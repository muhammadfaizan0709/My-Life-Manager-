package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            val scheduler = AlarmScheduler(context)
            val db = AppDatabase.getDatabase(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                val alarms = db.appDao().getAllAlarms().first()
                alarms.filter { it.isEnabled }.forEach { alarm ->
                    scheduler.schedule(alarm)
                }
                pendingResult.finish()
            }
        }
    }
}
