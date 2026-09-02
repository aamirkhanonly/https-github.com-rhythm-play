package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.model.Playlist
import com.example.model.Song

@Database(entities = [Song::class, Playlist::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RhythmDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: RhythmDatabase? = null

        fun getDatabase(context: Context): RhythmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RhythmDatabase::class.java,
                    "rhythm_play_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
