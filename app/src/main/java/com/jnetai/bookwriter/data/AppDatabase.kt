package com.jnetai.bookwriter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jnetai.bookwriter.data.converter.Converters
import com.jnetai.bookwriter.data.dao.*
import com.jnetai.bookwriter.data.entity.*

@Database(
    entities = [Book::class, Chapter::class, Character::class, WorldbuildingNote::class, WritingSession::class, DailyGoal::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun characterDao(): CharacterDao
    abstract fun worldbuildingDao(): WorldbuildingDao
    abstract fun writingSessionDao(): WritingSessionDao
    abstract fun dailyGoalDao(): DailyGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookwriter_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}