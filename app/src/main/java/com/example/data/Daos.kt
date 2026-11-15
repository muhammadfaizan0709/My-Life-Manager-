package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.Goal
import com.example.data.models.Habit
import com.example.data.models.JournalEntry
import com.example.data.models.MoodRecord
import com.example.data.models.Note
import com.example.data.models.SleepRecord
import com.example.data.models.Task
import com.example.data.models.WaterRecord
import com.example.data.models.WeightRecord
import com.example.data.models.PrivateMedia
import com.example.data.models.AlarmRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Notes
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, timestamp DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    // Water
    @Query("SELECT SUM(amountMl) FROM water_records WHERE dateString = :date")
    fun getWaterConsumed(date: String): Flow<Int?>

    @Query("SELECT * FROM water_records ORDER BY id ASC")
    fun getAllWaterRecords(): Flow<List<WaterRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterRecord(record: WaterRecord)

    @Query("DELETE FROM water_records")
    suspend fun clearWaterRecords()

    // Habits
    @Query("SELECT * FROM habits ORDER BY timestamp DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Int)

    // Weight
    @Query("SELECT * FROM weight_records ORDER BY timestamp DESC")
    fun getAllWeightRecords(): Flow<List<WeightRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightRecord(record: WeightRecord)

    // Mood
    @Query("SELECT * FROM mood_records ORDER BY timestamp DESC")
    fun getAllMoodRecords(): Flow<List<MoodRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodRecord(record: MoodRecord)

    // Journal
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntryById(id: Int)

    // Goals
    @Query("SELECT * FROM goals ORDER BY timestamp DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)

    // Sleep
    @Query("SELECT * FROM sleep_records ORDER BY timestamp DESC")
    fun getAllSleepRecords(): Flow<List<SleepRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepRecord(record: SleepRecord)

    // Private Media Vault
    @Query("SELECT * FROM private_media ORDER BY timestamp DESC")
    fun getAllPrivateMedia(): Flow<List<PrivateMedia>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateMedia(media: PrivateMedia)

    @Query("DELETE FROM private_media WHERE id = :id")
    suspend fun deletePrivateMediaById(id: Int)

    // Alarms
    @Query("SELECT * FROM alarms ORDER BY timestamp DESC")
    fun getAllAlarms(): Flow<List<AlarmRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmRecord): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmRecord)

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarmById(id: Int)
}
