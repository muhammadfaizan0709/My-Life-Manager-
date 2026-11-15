package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
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
import com.example.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.util.AlarmScheduler

class MainViewModel(
    private val repository: AppRepository,
    private val settingsManager: SettingsManager,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterConsumed: StateFlow<Int> = repository.getWaterConsumedToday()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allWaterRecords: StateFlow<List<WaterRecord>> = repository.allWaterRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightRecords: StateFlow<List<WeightRecord>> = repository.allWeightRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val moodRecords: StateFlow<List<MoodRecord>> = repository.allMoodRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntry>> = repository.allJournalEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepRecords: StateFlow<List<SleepRecord>> = repository.allSleepRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val privateMedia: StateFlow<List<PrivateMedia>> = repository.allPrivateMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<AlarmRecord>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterGoal: StateFlow<Int> = settingsManager.waterGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    val heightCm: StateFlow<Float> = settingsManager.heightCm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 170f)

    val themeMode: StateFlow<Int> = settingsManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val vaultPin: StateFlow<String?> = settingsManager.vaultPin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName: StateFlow<String> = settingsManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "User")

    fun setThemeMode(mode: Int) {
        viewModelScope.launch { settingsManager.setThemeMode(mode) }
    }

    fun setVaultPin(pin: String?) {
        viewModelScope.launch { settingsManager.setVaultPin(pin) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { settingsManager.setUserName(name) }
    }

    // Actions
    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.insertNote(Note(title = title, content = content))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch { repository.deleteNoteById(id) }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(Task(title = title))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch { repository.deleteTaskById(id) }
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
            repository.addWater(amount)
        }
    }

    fun addHabit(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name))
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val completed = !habit.isCompletedToday
            val newStreak = if (completed) habit.streak + 1 else maxOf(0, habit.streak - 1)
            repository.updateHabit(habit.copy(isCompletedToday = completed, streak = newStreak))
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch { repository.deleteHabitById(id) }
    }

    fun setWaterGoal(goal: Int) {
        viewModelScope.launch { settingsManager.setWaterGoal(goal) }
    }

    fun addWeightRecord(weightKg: Float) {
        viewModelScope.launch {
            repository.insertWeightRecord(WeightRecord(weightKg = weightKg))
            settingsManager.setHeight(heightCm.value) // Just touching it for now, later we'll have a proper profile update
        }
    }

    fun addMoodRecord(mood: String, intensity: Int) {
        viewModelScope.launch {
            repository.insertMoodRecord(MoodRecord(mood = mood, intensity = intensity))
        }
    }

    fun addJournalEntry(title: String, content: String, mood: String = "") {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.insertJournalEntry(JournalEntry(title = title, content = content, mood = mood))
        }
    }

    fun deleteJournalEntry(id: Int) {
        viewModelScope.launch { repository.deleteJournalEntryById(id) }
    }

    fun addGoal(title: String, target: Int) {
        if (title.isBlank() || target <= 0) return
        viewModelScope.launch {
            repository.insertGoal(Goal(title = title, target = target))
        }
    }

    fun updateGoalProgress(goal: Goal, progress: Int) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(current = progress))
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch { repository.deleteGoalById(id) }
    }

    fun addSleepRecord(durationMinutes: Int, quality: String) {
        if (durationMinutes <= 0) return
        viewModelScope.launch {
            repository.insertSleepRecord(SleepRecord(durationMinutes = durationMinutes, quality = quality))
        }
    }

    // Vault
    fun addPrivateMedia(fileName: String, isImage: Boolean) {
        viewModelScope.launch {
            repository.insertPrivateMedia(PrivateMedia(fileName = fileName, isImage = isImage))
        }
    }

    fun deletePrivateMedia(id: Int) {
        viewModelScope.launch { repository.deletePrivateMediaById(id) }
    }

    // Alarms
    fun addAlarm(hour: Int, minute: Int, label: String) {
        viewModelScope.launch {
            val alarm = AlarmRecord(hour = hour, minute = minute, label = label, isEnabled = true)
            val id = repository.insertAlarm(alarm).toInt()
            alarmScheduler.schedule(alarm.copy(id = id))
        }
    }

    fun toggleAlarm(alarm: AlarmRecord) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            repository.updateAlarm(updated)
            if (updated.isEnabled) {
                alarmScheduler.schedule(updated)
            } else {
                alarmScheduler.cancel(updated.id)
            }
        }
    }

    fun deleteAlarm(id: Int) {
        viewModelScope.launch { 
            alarmScheduler.cancel(id)
            repository.deleteAlarmById(id) 
        }
    }
}
