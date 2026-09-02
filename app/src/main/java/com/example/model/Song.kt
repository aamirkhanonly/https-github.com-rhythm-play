package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

enum class AudioSourceType {
    LOCAL_OFFLINE,
    SYNTHESIZED_OFFLINE,
    ONLINE_STREAM,
    YOUTUBE_ONLINE
}

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val albumArtUri: String? = null,
    val customImageUri: String? = null,
    val streamOrFilePath: String,
    val sourceType: AudioSourceType = AudioSourceType.LOCAL_OFFLINE,
    val lyricsLrc: String = "",
    val genre: String = "Pop",
    val year: Int = 2024,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val dateAdded: Long = System.currentTimeMillis()
) : Serializable {
    val displayImageUri: String?
        get() = customImageUri ?: albumArtUri
}

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val coverUri: String? = null,
    val songIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class LyricsLine(
    val timestampMs: Long,
    val text: String
)

enum class EqualizerPreset(val displayName: String) {
    NORMAL("Normal"),
    POP("Pop"),
    CLASSIC("Classic"),
    JAZZ("Jazz"),
    ROCK("Rock"),
    HALL("Hall"),
    CONCERT("Concert"),
    CUSTOM("Custom")
}

data class EqualizerSettings(
    val isEnabled: Boolean = true,
    val currentPreset: EqualizerPreset = EqualizerPreset.NORMAL,
    val bandLevels: List<Int> = listOf(0, 0, 0, 0, 0), // 5 bands in milliBels or %
    val bassBoost: Int = 0, // 0 to 1000
    val virtualizer: Int = 0, // 0 to 1000
    val reverbPreset: Short = 0 // 0 = none, 1 = smallroom, 2 = mediumroom, 3 = largeroom, 4 = mediumhall, 5 = largehall, 6 = plate
)
