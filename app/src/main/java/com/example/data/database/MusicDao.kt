package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Playlist
import com.example.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: String): Song?

    @Query("SELECT * FROM songs WHERE genre = :genre")
    fun getSongsByGenre(genre: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE album = :album")
    fun getSongsByAlbum(album: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE artist = :artist")
    fun getSongsByArtist(artist: String): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Update
    suspend fun updateSong(song: Song)

    @Query("UPDATE songs SET customImageUri = :imageUri WHERE id = :songId")
    suspend fun updateSongImage(songId: String, imageUri: String?)

    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :songId")
    suspend fun updateFavoriteStatus(songId: String, isFavorite: Boolean)

    @Query("UPDATE songs SET lyricsLrc = :lyrics WHERE id = :songId")
    suspend fun updateSongLyrics(songId: String, lyrics: String)

    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteSong(songId: String)

    // Playlists
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)
}
