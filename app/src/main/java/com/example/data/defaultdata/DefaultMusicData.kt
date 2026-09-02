package com.example.data.defaultdata

import com.example.R
import com.example.model.AudioSourceType
import com.example.model.Playlist
import com.example.model.Song

object DefaultMusicData {

    fun getDefaultSongs(): List<Song> {
        return listOf(
            Song(
                id = "song_1",
                title = "Amber Horizon",
                artist = "Neon Skyline",
                album = "Retro Waves 2016",
                durationMs = 195000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth",
                streamOrFilePath = "synth://song_1",
                sourceType = AudioSourceType.SYNTHESIZED_OFFLINE,
                genre = "Pop",
                year = 2016,
                isFavorite = true,
                lyricsLrc = """
                    [00:00.00] (Instrumental Intro - Warm Synth Wave)
                    [00:08.50] Driving through the amber glowing city streets
                    [00:16.00] Echoes of the rhythm in the summer heat
                    [00:24.00] Feel the bass line rise under neon skies
                    [00:32.00] Google Play nostalgia right before our eyes
                    [00:40.00] Golden memories in the stereo
                    [00:48.00] Turn it all the way up, let the music flow
                    [00:56.50] Amber horizon, take me far away
                    [01:05.00] Dancing till the dawn of another day
                    [01:14.00] (Melodic synth solo with bass groove)
                    [01:28.00] Every frequency vibrating in my soul
                    [01:36.50] Rhythm Play is taking total control
                    [01:45.00] Amber horizon, never let it fade
                    [01:54.00] In this golden symphony that we made
                """.trimIndent()
            ),
            Song(
                id = "song_2",
                title = "Midnight Acoustic Reverie",
                artist = "Evelyn Ross",
                album = "Wooden Strings & Memories",
                durationMs = 210000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_acoustic",
                streamOrFilePath = "synth://song_2",
                sourceType = AudioSourceType.SYNTHESIZED_OFFLINE,
                genre = "Classic",
                year = 2018,
                isFavorite = true,
                lyricsLrc = """
                    [00:00.00] (Acoustic Guitar Fingerpicking)
                    [00:10.00] Shadows dance upon the rustic floor
                    [00:19.00] Gentle chords I've heard so long before
                    [00:29.00] Whispers in the breeze of a quiet room
                    [00:39.00] Flowers of nostalgia starting to bloom
                    [00:48.50] Finger on the frets, feeling every note
                    [00:58.00] Simple verses that the midnight wrote
                    [01:08.00] Sing for the dreamers under starry light
                    [01:18.00] Rest your weary heart into the night
                    [01:28.00] (Warm acoustic harmonic bridge)
                    [01:40.00] Time slows down when the chords ring true
                    [01:50.00] Midnight reverie bringing me to you
                """.trimIndent()
            ),
            Song(
                id = "song_3",
                title = "Grand Symphony in D Minor",
                artist = "Vienna Philharmonic Ensemble",
                album = "Royal Concert Hall Masterpieces",
                durationMs = 240000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_orchestral",
                streamOrFilePath = "synth://song_3",
                sourceType = AudioSourceType.SYNTHESIZED_OFFLINE,
                genre = "Classic",
                year = 2020,
                isFavorite = false,
                lyricsLrc = """
                    [00:00.00] (Movement I: Allegro Maestoso)
                    [00:15.00] Strings resonate through the grand vaulted hall
                    [00:30.00] Chandelier reflections shimmering on the wall
                    [00:45.00] Timpani thunder as the brass ascends
                    [01:00.00] Where the timeless harmony never ends
                    [01:15.00] (Movement II: Andante Cantabile)
                    [01:30.00] Gentle woodwinds tell a story of the past
                    [01:45.00] Pure classical beauty built to last
                    [02:00.00] (Movement III: Presto Con Fuoco)
                    [02:15.00] The grand crescendo sweeps the listening crowd
                    [02:30.00] Standing ovation, resonant and proud
                """.trimIndent()
            ),
            Song(
                id = "song_4",
                title = "Electric Pulse overdrive",
                artist = "Volt & The Thunderbirds",
                album = "High Voltage Arena",
                durationMs = 185000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                streamOrFilePath = "synth://song_4",
                sourceType = AudioSourceType.SYNTHESIZED_OFFLINE,
                genre = "Rock",
                year = 2022,
                isFavorite = true,
                lyricsLrc = """
                    [00:00.00] (Heavy Guitar Riff & Drum Beats)
                    [00:09.00] Turned the amp to eleven, can you feel the heat?
                    [00:17.50] Stadium is shaking beneath our feet
                    [00:26.00] Distortion screaming in the monitor line
                    [00:34.00] Power chords crashing right on time
                    [00:43.00] Electric pulse! Screaming in the air!
                    [00:51.00] Hands up high, everywhere!
                    [01:00.00] (Lightning guitar shred solo)
                    [01:15.00] Rock and roll will never die
                    [01:23.00] Rhythm blazing across the sky!
                """.trimIndent()
            ),
            Song(
                id = "song_5",
                title = "Smooth Velvet Lounge",
                artist = "Miles Blue Quartet",
                album = "Late Night Manhattan Sessions",
                durationMs = 205000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_acoustic",
                streamOrFilePath = "synth://song_5",
                sourceType = AudioSourceType.SYNTHESIZED_OFFLINE,
                genre = "Jazz",
                year = 2021,
                isFavorite = false,
                lyricsLrc = """
                    [00:00.00] (Muted Trumpet & Upright Bass Intonation)
                    [00:12.00] Dim red lights in the basement bar
                    [00:24.00] Saxophone crying like a shooting star
                    [00:36.00] Complex syncopation swinging in the room
                    [00:48.00] Piano chords dispelling all the afternoon gloom
                    [01:00.00] Sip of bourbon on the vintage booth
                    [01:12.00] Jazz improvisation speaking the truth
                    [01:24.00] (Upright bass walking groove)
                    [01:38.00] Velvet midnight, silky and slow
                    [01:50.00] That's the only way the jazz cats go
                """.trimIndent()
            ),
            Song(
                id = "song_6",
                title = "Live Concert Starlight",
                artist = "The Arena Collective",
                album = "Live at Wembley 2023",
                durationMs = 230000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_orchestral",
                streamOrFilePath = "synth://song_6",
                sourceType = AudioSourceType.SYNTHESIZED_OFFLINE,
                genre = "Pop",
                year = 2023,
                isFavorite = true,
                lyricsLrc = """
                    [00:00.00] (Crowd Cheering & Drum intro)
                    [00:10.00] Eighty thousand phone lights shining bright
                    [00:19.00] Sing along with us into the night!
                    [00:28.00] Every voice united as one single sound
                    [00:37.00] Trembling the stadium and the ground
                    [00:46.00] We are alive under the starry dome
                    [00:55.00] In this concert hall we found our home!
                    [01:05.00] (Live crowd chorus chant)
                    [01:20.00] Starlight shining on our faces tonight
                    [01:35.00] Everything is gonna be alright!
                """.trimIndent()
            ),
            Song(
                id = "song_7",
                title = "YouTube Stream: Chill Lofi Beats to Relax",
                artist = "Lofi Girl Network (Online)",
                album = "24/7 Study Stream Mix",
                durationMs = 245000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth",
                streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                sourceType = AudioSourceType.YOUTUBE_ONLINE,
                genre = "Pop",
                year = 2024,
                isFavorite = false,
                lyricsLrc = """
                    [00:00.00] (Lofi Vinyl Crackle & Soft Rhodes Piano)
                    [00:15.00] Raindrops tapping gently on the windowpane
                    [00:30.00] Mellow study beats to ease the mental strain
                    [00:45.00] Warm tea steaming in the ceramic cup
                    [01:00.00] Never letting worry get you down or up
                    [01:15.00] Just relax and let the lofi loop unwind
                    [01:30.00] Peaceful tranquility in your state of mind
                """.trimIndent()
            ),
            Song(
                id = "song_8",
                title = "YouTube Stream: Synthwave Highway Nights",
                artist = "Cyberpunk FM (Online Stream)",
                album = "Tokyo Cyber Radiance",
                durationMs = 260000,
                albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                sourceType = AudioSourceType.YOUTUBE_ONLINE,
                genre = "Rock",
                year = 2024,
                isFavorite = true,
                lyricsLrc = """
                    [00:00.00] (Cybernetic Arpeggiator & 808 Beats)
                    [00:14.00] Gliding down the futuristic tollway fast
                    [00:28.00] Leaving all the troubles of the fading past
                    [00:42.00] Holographic billboards flashing in the rain
                    [00:56.00] Electric adrenaline running through the vein
                    [01:10.00] Tokyo nights, glowing in purple and chrome
                    [01:25.00] Fast speed music guiding us back home
                """.trimIndent()
            )
        )
    }

    fun getDefaultPlaylists(): List<Playlist> {
        return listOf(
            Playlist(
                id = "pl_favorites",
                name = "Thumbs Up Favorites",
                description = "Google Play Music style instant favorite mixes",
                coverUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth",
                songIds = listOf("song_1", "song_2", "song_4", "song_6", "song_8")
            ),
            Playlist(
                id = "pl_chill",
                name = "Late Night Chillout",
                description = "Relaxing acoustic vibes and smooth jazz sessions",
                coverUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_acoustic",
                songIds = listOf("song_2", "song_5", "song_7")
            ),
            Playlist(
                id = "pl_rock",
                name = "Pure High Voltage Rock",
                description = "Heavy electric riffs and adrenaline stadium anthems",
                coverUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                songIds = listOf("song_4", "song_1", "song_8")
            )
        )
    }
}
