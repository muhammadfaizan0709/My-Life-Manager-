package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.models.AlarmRecord
import com.example.data.models.Goal
import com.example.data.models.Habit
import com.example.data.models.JournalEntry
import com.example.data.models.MoodRecord
import com.example.data.models.Note
import com.example.data.models.PrivateMedia
import com.example.data.models.SleepRecord
import com.example.data.models.Task
import com.example.data.models.WaterRecord
import com.example.data.models.WeightRecord

@Database(
    entities = [
        Note::class, Task::class, WaterRecord::class, Habit::class, 
        WeightRecord::class, MoodRecord::class, JournalEntry::class, 
        Goal::class, SleepRecord::class, PrivateMedia::class, AlarmRecord::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_life_manager.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
