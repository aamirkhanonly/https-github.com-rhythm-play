package com.example.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.RhythmDatabase
import com.example.data.repository.MusicRepository
import com.example.model.AudioSourceType
import com.example.model.EqualizerPreset
import com.example.model.EqualizerSettings
import com.example.model.Playlist
import com.example.model.Song
import com.example.player.AudioPlayerEngine
import com.example.player.EqualizerController
import com.example.player.PlaybackState
import com.example.service.MusicPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibraryTab(val title: String) {
    SONGS("Songs"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists"),
    GENRES("Genres"),
    FAVORITES("Favorites")
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RhythmDatabase.getDatabase(application)
    val repository = MusicRepository(application, db.musicDao())
    val equalizerController = EqualizerController()
    val playerEngine = AudioPlayerEngine(application, equalizerController)

    private var playbackService: MusicPlaybackService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicPlaybackService.LocalBinder
            playbackService = binder?.getService()
            isBound = true
            // update initial state
            playbackService?.updateNotification(
                playerEngine.playbackState.value.currentSong,
                playerEngine.playbackState.value.isPlaying
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playbackService = null
            isBound = false
        }
    }

    val allSongs: StateFlow<List<Song>> = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<Song>> = repository.favoriteSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlaylists: StateFlow<List<Playlist>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playbackState: StateFlow<PlaybackState> = playerEngine.playbackState
    val equalizerSettings: StateFlow<EqualizerSettings> = equalizerController.settings

    // UI Navigation & Dialog states
    val selectedTab = MutableStateFlow(LibraryTab.SONGS)
    val searchQuery = MutableStateFlow("")
    val selectedGenreFilter = MutableStateFlow<String?>(null)

    val isNowPlayingExpanded = MutableStateFlow(false)
    val showLyrics = MutableStateFlow(false)
    val showEqualizerSheet = MutableStateFlow(false)
    val showChangePictureSong = MutableStateFlow<Song?>(null)
    val showAddToPlaylistSong = MutableStateFlow<Song?>(null)
    val showCreatePlaylistDialog = MutableStateFlow(false)
    val showScanDialog = MutableStateFlow(false)
    val scanResultCount = MutableStateFlow<Int?>(null)

    // Online / YouTube Stream Discovery State
    val onlineSearchQuery = MutableStateFlow("")
    val onlineStreamingTracks = MutableStateFlow<List<Song>>(emptyList())
    val isSearchingOnline = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }

        setupServiceCallbacks()
        bindPlaybackService()
        initOnlineStreams()
    }

    private fun setupServiceCallbacks() {
        MusicPlaybackService.onPlayAction = { playerEngine.resume() }
        MusicPlaybackService.onPauseAction = { playerEngine.pause() }
        MusicPlaybackService.onNextAction = { playerEngine.next() }
        MusicPlaybackService.onPrevAction = { playerEngine.previous() }

        playerEngine.setPlaybackChangeCallback { song, isPlaying ->
            playbackService?.updateNotification(song, isPlaying)
        }
    }

    private fun bindPlaybackService() {
        val intent = Intent(getApplication(), MusicPlaybackService::class.java)
        try {
            getApplication<Application>().startService(intent)
            getApplication<Application>().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initOnlineStreams() {
        onlineStreamingTracks.value = listOf(
            Song(
                id = "yt_online_1",
                title = "YouTube Live: Synthwave Radio - Chill Beats",
                artist = "Lofi & Synth Live FM",
                album = "YouTube Global Stream",
                durationMs = 360000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth",
                streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                sourceType = AudioSourceType.YOUTUBE_ONLINE,
                genre = "Pop",
                lyricsLrc = "[00:00.00] (Live YouTube Stream Streamed in High Definition)\n[00:15.00] Streaming live music directly offscreen\n[00:30.00] Crystal clear stereo audio"
            ),
            Song(
                id = "yt_online_2",
                title = "YouTube Stream: Top Chart Hits 2024",
                artist = "Global Hit Makers",
                album = "YouTube Billboard Mix",
                durationMs = 280000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                sourceType = AudioSourceType.YOUTUBE_ONLINE,
                genre = "Pop",
                lyricsLrc = "[00:00.00] Top trending streaming hits\n[00:20.00] Continuous stream with equalizer enhancement"
            ),
            Song(
                id = "yt_online_3",
                title = "YouTube Stream: Deep Focus Piano Study",
                artist = "Acoustic Sanctuary",
                album = "YouTube Focus Zone",
                durationMs = 320000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_acoustic",
                streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                sourceType = AudioSourceType.YOUTUBE_ONLINE,
                genre = "Classic",
                lyricsLrc = "[00:00.00] Peaceful piano melodies for concentration and sleep"
            ),
            Song(
                id = "yt_online_4",
                title = "YouTube Stream: Classic Rock Stadium Anthems",
                artist = "Arena Rockers Online",
                album = "YouTube Rock Classics",
                durationMs = 310000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                sourceType = AudioSourceType.YOUTUBE_ONLINE,
                genre = "Rock",
                lyricsLrc = "[00:00.00] Live arena rock streams roaring online"
            )
        )
    }

    fun playSong(song: Song, queue: List<Song>? = null) {
        val q = queue ?: allSongs.value.ifEmpty { listOf(song) }
        val index = q.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playerEngine.playQueue(q, index)
    }

    fun togglePlayPause() = playerEngine.togglePlayPause()
    fun seekTo(positionMs: Long) = playerEngine.seekTo(positionMs)
    fun next() = playerEngine.next()
    fun previous() = playerEngine.previous()
    fun toggleShuffle() = playerEngine.toggleShuffle()
    fun cycleRepeatMode() = playerEngine.cycleRepeatMode()

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song.id, song.isFavorite)
        }
    }

    fun changeSongPicture(songId: String, imageUri: String) {
        viewModelScope.launch {
            repository.updateSongImage(songId, imageUri)
            // If current song is updated, refresh state
            val cur = playerEngine.playbackState.value.currentSong
            if (cur?.id == songId) {
                // state will reflect upon Room flow emission
            }
        }
    }

    fun updateLyrics(songId: String, lyrics: String) {
        viewModelScope.launch {
            repository.updateSongLyrics(songId, lyrics)
        }
    }

    fun createPlaylist(name: String, description: String, coverUri: String?, songIds: List<String>) {
        viewModelScope.launch {
            repository.createPlaylist(name, description, coverUri, songIds)
        }
    }

    fun addSongToPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun scanDeviceAudio() {
        viewModelScope.launch {
            showScanDialog.value = true
            val count = repository.scanDeviceMediaAudio()
            scanResultCount.value = count
        }
    }

    fun searchOnlineYouTube(query: String) {
        onlineSearchQuery.value = query
        if (query.isBlank()) return
        isSearchingOnline.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(400) // Debounce / simulate instant online query
            val baseList = onlineStreamingTracks.value
            val matched = baseList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
            }
            if (matched.isEmpty()) {
                // generate instant stream item for queried YouTube music
                val generated = Song(
                    id = "yt_search_${System.currentTimeMillis()}",
                    title = "YouTube Stream: $query",
                    artist = "YouTube Music Online",
                    album = "Online Search Stream",
                    durationMs = 240000,
                    albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                    streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    sourceType = AudioSourceType.YOUTUBE_ONLINE,
                    genre = "Online",
                    lyricsLrc = "[00:00.00] Playing online streaming search result: $query\n[00:15.00] YouTube audio stream synchronized"
                )
                onlineStreamingTracks.value = listOf(generated) + baseList
            }
            isSearchingOnline.value = false
        }
    }

    fun addOnlineTrackToLibrary(song: Song) {
        viewModelScope.launch {
            repository.addCustomTrack(
                title = song.title,
                artist = song.artist,
                album = song.album,
                streamUrl = song.streamOrFilePath,
                genre = song.genre,
                albumArtUri = song.albumArtUri,
                isOnline = true
            )
        }
    }

    // Equalizer
    fun setEqualizerEnabled(enabled: Boolean) = equalizerController.setEnabled(enabled)
    fun setEqualizerPreset(preset: EqualizerPreset) = equalizerController.applyPreset(preset)
    fun setEqualizerBandLevel(bandIndex: Int, level: Int) = equalizerController.setBandLevel(bandIndex, level)
    fun setBassBoost(strength: Int) = equalizerController.setBassBoost(strength)
    fun setVirtualizer(strength: Int) = equalizerController.setVirtualizer(strength)
    fun setReverbPreset(reverb: Short) = equalizerController.setReverbPreset(reverb)

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            try {
                getApplication<Application>().unbindService(serviceConnection)
            } catch (e: Exception) {}
            isBound = false
        }
        playerEngine.release()
    }
}
