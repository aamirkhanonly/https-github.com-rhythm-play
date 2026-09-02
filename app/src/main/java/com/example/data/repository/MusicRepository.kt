package com.example.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.database.MusicDao
import com.example.data.defaultdata.DefaultMusicData
import com.example.model.AudioSourceType
import com.example.model.Playlist
import com.example.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class MusicRepository(
    private val context: Context,
    private val musicDao: MusicDao
) {
    val allSongs: Flow<List<Song>> = musicDao.getAllSongs()
    val favoriteSongs: Flow<List<Song>> = musicDao.getFavoriteSongs()
    val allPlaylists: Flow<List<Playlist>> = musicDao.getAllPlaylists()

    suspend fun initializeDefaultDataIfNeeded() {
        withContext(Dispatchers.IO) {
            val existing = musicDao.getAllSongs().first()
            if (existing.isEmpty()) {
                musicDao.insertSongs(DefaultMusicData.getDefaultSongs())
                val defaultPlaylists = DefaultMusicData.getDefaultPlaylists()
                for (pl in defaultPlaylists) {
                    musicDao.insertPlaylist(pl)
                }
            }
        }
    }

    suspend fun toggleFavorite(songId: String, currentStatus: Boolean) {
        withContext(Dispatchers.IO) {
            musicDao.updateFavoriteStatus(songId, !currentStatus)
        }
    }

    suspend fun updateSongImage(songId: String, imageUri: String?) {
        withContext(Dispatchers.IO) {
            musicDao.updateSongImage(songId, imageUri)
        }
    }

    suspend fun updateSongLyrics(songId: String, lyrics: String) {
        withContext(Dispatchers.IO) {
            musicDao.updateSongLyrics(songId, lyrics)
        }
    }

    suspend fun addCustomTrack(
        title: String,
        artist: String,
        album: String,
        streamUrl: String,
        genre: String,
        albumArtUri: String?,
        isOnline: Boolean = true
    ) {
        withContext(Dispatchers.IO) {
            val song = Song(
                id = "custom_" + UUID.randomUUID().toString().take(8),
                title = title.ifBlank { "Untitled Track" },
                artist = artist.ifBlank { "Unknown Artist" },
                album = album.ifBlank { "Single" },
                durationMs = 210000,
                albumArtUri = albumArtUri ?: "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth",
                streamOrFilePath = streamUrl,
                sourceType = if (isOnline) AudioSourceType.YOUTUBE_ONLINE else AudioSourceType.LOCAL_OFFLINE,
                genre = genre,
                year = 2024,
                lyricsLrc = "[00:00.00] Enjoying $title by $artist\n[00:15.00] Rhythm Play online streaming active\n[00:30.00] Continuous offscreen audio playback"
            )
            musicDao.insertSong(song)
        }
    }

    suspend fun createPlaylist(name: String, description: String, coverUri: String?, songIds: List<String>) {
        withContext(Dispatchers.IO) {
            val pl = Playlist(
                id = "pl_" + UUID.randomUUID().toString().take(8),
                name = name,
                description = description,
                coverUri = coverUri ?: "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth",
                songIds = songIds
            )
            musicDao.insertPlaylist(pl)
        }
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        withContext(Dispatchers.IO) {
            val playlists = musicDao.getAllPlaylists().first()
            val pl = playlists.find { it.id == playlistId } ?: return@withContext
            if (!pl.songIds.contains(songId)) {
                val updated = pl.copy(songIds = pl.songIds + songId)
                musicDao.updatePlaylist(updated)
            }
        }
    }

    suspend fun deletePlaylist(playlistId: String) {
        withContext(Dispatchers.IO) {
            musicDao.deletePlaylist(playlistId)
        }
    }

    suspend fun deleteSong(songId: String) {
        withContext(Dispatchers.IO) {
            musicDao.deleteSong(songId)
        }
    }

    suspend fun scanDeviceMediaAudio(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

            val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            try {
                context.contentResolver.query(
                    queryUri,
                    projection,
                    selection,
                    null,
                    "${MediaStore.Audio.Media.TITLE} ASC"
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    val scannedSongs = mutableListOf<Song>()
                    while (cursor.moveToNext()) {
                        val mediaId = cursor.getLong(idCol)
                        val title = cursor.getString(titleCol) ?: "Unknown"
                        val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                        val album = cursor.getString(albumCol) ?: "Unknown Album"
                        val duration = cursor.getLong(durationCol)
                        val albumId = cursor.getLong(albumIdCol)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            mediaId
                        ).toString()

                        val albumArtUri = "content://media/external/audio/albumart/$albumId"

                        scannedSongs.add(
                            Song(
                                id = "local_$mediaId",
                                title = title,
                                artist = artist,
                                album = album,
                                durationMs = if (duration > 0) duration else 180000,
                                albumArtUri = albumArtUri,
                                streamOrFilePath = contentUri,
                                sourceType = AudioSourceType.LOCAL_OFFLINE,
                                genre = "Offline",
                                lyricsLrc = "[00:00.00] Local device audio: $title\n[00:15.00] Enjoy your high quality offline music"
                            )
                        )
                    }
                    if (scannedSongs.isNotEmpty()) {
                        musicDao.insertSongs(scannedSongs)
                        count = scannedSongs.size
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            count
        }
    }
}
