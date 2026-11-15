package com.example.data

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(private val appDao: AppDao) {
    
    // Notes
    val allNotes: Flow<List<Note>> = appDao.getAllNotes()
    suspend fun insertNote(note: Note) = appDao.insertNote(note)
    suspend fun updateNote(note: Note) = appDao.updateNote(note)
    suspend fun deleteNoteById(id: Int) = appDao.deleteNoteById(id)

    // Tasks
    val allTasks: Flow<List<Task>> = appDao.getAllTasks()
    suspend fun insertTask(task: Task) = appDao.insertTask(task)
    suspend fun updateTask(task: Task) = appDao.updateTask(task)
    suspend fun deleteTaskById(id: Int) = appDao.deleteTaskById(id)

    // Water
    val allWaterRecords: Flow<List<WaterRecord>> = appDao.getAllWaterRecords()
    
    fun getWaterConsumedToday(): Flow<Int?> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return appDao.getWaterConsumed(today)
    }
    
    suspend fun addWater(amountMl: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        appDao.insertWaterRecord(WaterRecord(amountMl = amountMl, dateString = today))
    }

    // Habits
    val allHabits: Flow<List<Habit>> = appDao.getAllHabits()
    suspend fun insertHabit(habit: Habit) = appDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = appDao.updateHabit(habit)
    suspend fun deleteHabitById(id: Int) = appDao.deleteHabitById(id)

    // Weight
    val allWeightRecords: Flow<List<WeightRecord>> = appDao.getAllWeightRecords()
    suspend fun insertWeightRecord(record: WeightRecord) = appDao.insertWeightRecord(record)

    // Mood
    val allMoodRecords: Flow<List<MoodRecord>> = appDao.getAllMoodRecords()
    suspend fun insertMoodRecord(record: MoodRecord) = appDao.insertMoodRecord(record)

    // Journal
    val allJournalEntries: Flow<List<JournalEntry>> = appDao.getAllJournalEntries()
    suspend fun insertJournalEntry(entry: JournalEntry) = appDao.insertJournalEntry(entry)
    suspend fun deleteJournalEntryById(id: Int) = appDao.deleteJournalEntryById(id)

    // Goals
    val allGoals: Flow<List<Goal>> = appDao.getAllGoals()
    suspend fun insertGoal(goal: Goal) = appDao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = appDao.updateGoal(goal)
    suspend fun deleteGoalById(id: Int) = appDao.deleteGoalById(id)

    // Sleep
    val allSleepRecords: Flow<List<SleepRecord>> = appDao.getAllSleepRecords()
    suspend fun insertSleepRecord(record: SleepRecord) = appDao.insertSleepRecord(record)

    // Vault
    val allPrivateMedia: Flow<List<PrivateMedia>> = appDao.getAllPrivateMedia()
    suspend fun insertPrivateMedia(media: PrivateMedia) = appDao.insertPrivateMedia(media)
    suspend fun deletePrivateMediaById(id: Int) = appDao.deletePrivateMediaById(id)

    // Alarms
    val allAlarms: Flow<List<AlarmRecord>> = appDao.getAllAlarms()
    suspend fun insertAlarm(alarm: AlarmRecord): Long = appDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmRecord) = appDao.updateAlarm(alarm)
    suspend fun deleteAlarmById(id: Int) = appDao.deleteAlarmById(id)
}
