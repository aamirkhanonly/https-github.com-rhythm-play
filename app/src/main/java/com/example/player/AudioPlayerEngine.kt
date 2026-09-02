package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.example.model.AudioSourceType
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val isShuffle: Boolean = false,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = -1,
    val errorMessage: String? = null
)

class AudioPlayerEngine(
    private val context: Context,
    val equalizerController: EqualizerController
) {
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressTrackerJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var onPlaybackChangeCallback: ((Song?, Boolean) -> Unit)? = null

    fun setPlaybackChangeCallback(callback: (Song?, Boolean) -> Unit) {
        onPlaybackChangeCallback = callback
    }

    private fun getOrCreateMediaPlayer(): MediaPlayer {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { mp ->
                    _playbackState.value = _playbackState.value.copy(
                        isBuffering = false,
                        durationMs = mp.duration.toLong().coerceAtLeast(1L)
                    )
                    equalizerController.bindAudioSession(mp.audioSessionId)
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(isPlaying = true)
                    startProgressTracker()
                    notifyCallback()
                }
                setOnCompletionListener {
                    handleTrackCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerEngine", "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = _playbackState.value.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Playback error ($what, $extra)"
                    )
                    true
                }
            }
        }
        return mediaPlayer!!
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        val clampedIndex = startIndex.coerceIn(0, songs.size - 1)
        _playbackState.value = _playbackState.value.copy(
            queue = songs,
            queueIndex = clampedIndex
        )
        playSong(songs[clampedIndex])
    }

    fun playSong(song: Song) {
        scope.launch {
            _playbackState.value = _playbackState.value.copy(
                currentSong = song,
                isBuffering = true,
                errorMessage = null,
                currentPositionMs = 0L,
                durationMs = song.durationMs
            )
            notifyCallback()

            val mp = getOrCreateMediaPlayer()
            try {
                mp.reset()

                val dataSourcePath = withContext(Dispatchers.IO) {
                    when (song.sourceType) {
                        AudioSourceType.SYNTHESIZED_OFFLINE -> {
                            val synthFile = AudioSynthesizer.generateMelodicAudioFile(
                                context,
                                song.id,
                                song.genre,
                                (song.durationMs / 1000).toInt().coerceAtLeast(60)
                            )
                            synthFile.absolutePath
                        }
                        AudioSourceType.LOCAL_OFFLINE -> song.streamOrFilePath
                        AudioSourceType.ONLINE_STREAM, AudioSourceType.YOUTUBE_ONLINE -> song.streamOrFilePath
                    }
                }

                if (dataSourcePath.startsWith("content://") || dataSourcePath.startsWith("android.resource://")) {
                    mp.setDataSource(context, Uri.parse(dataSourcePath))
                } else if (dataSourcePath.startsWith("http://") || dataSourcePath.startsWith("https://")) {
                    mp.setDataSource(dataSourcePath)
                } else {
                    mp.setDataSource(dataSourcePath)
                }

                mp.prepareAsync()
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Failed to play song: ${song.title}", e)
                _playbackState.value = _playbackState.value.copy(
                    isBuffering = false,
                    isPlaying = false,
                    errorMessage = "Cannot play track: ${e.message}"
                )
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
            stopProgressTracker()
        } else {
            mp.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            startProgressTracker()
        }
        notifyCallback()
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
                stopProgressTracker()
                notifyCallback()
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            startProgressTracker()
            notifyCallback()
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let { mp ->
            try {
                mp.seekTo(positionMs.toInt())
                _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun next() {
        val q = _playbackState.value.queue
        if (q.isEmpty()) return
        val currentIndex = _playbackState.value.queueIndex
        val isShuffle = _playbackState.value.isShuffle

        val nextIndex = if (isShuffle) {
            (q.indices).random()
        } else {
            (currentIndex + 1) % q.size
        }

        _playbackState.value = _playbackState.value.copy(queueIndex = nextIndex)
        playSong(q[nextIndex])
    }

    fun previous() {
        val mp = mediaPlayer
        if (mp != null && mp.currentPosition > 3000) {
            seekTo(0L)
            return
        }
        val q = _playbackState.value.queue
        if (q.isEmpty()) return
        val currentIndex = _playbackState.value.queueIndex
        val prevIndex = if (currentIndex - 1 < 0) q.size - 1 else currentIndex - 1

        _playbackState.value = _playbackState.value.copy(queueIndex = prevIndex)
        playSong(q[prevIndex])
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(isShuffle = !_playbackState.value.isShuffle)
    }

    fun cycleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
    }

    private fun handleTrackCompletion() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0L)
                mediaPlayer?.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
                startProgressTracker()
            }
            RepeatMode.ALL -> {
                next()
            }
            RepeatMode.OFF -> {
                val q = _playbackState.value.queue
                val currentIndex = _playbackState.value.queueIndex
                if (currentIndex < q.size - 1) {
                    next()
                } else {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPositionMs = 0L
                    )
                    stopProgressTracker()
                    notifyCallback()
                }
            }
        }
    }

    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = mp.currentPosition.toLong(),
                            durationMs = mp.duration.toLong().coerceAtLeast(1L)
                        )
                    }
                }
                delay(250L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
    }

    private fun notifyCallback() {
        onPlaybackChangeCallback?.invoke(
            _playbackState.value.currentSong,
            _playbackState.value.isPlaying
        )
    }

    fun release() {
        stopProgressTracker()
        equalizerController.releaseEffects()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
