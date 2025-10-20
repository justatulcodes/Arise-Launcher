package com.expeknow.ariselauncher.utils

import android.content.Context
import android.content.Intent
import com.expeknow.ariselauncher.service.AppMonitorService

object AppTimerManager {

    fun startMonitoring(context: Context) {
        val intent = Intent(context, AppMonitorService::class.java)
        context.startService(intent)
    }

    fun stopMonitoring(context: Context) {
        val intent = Intent(context, AppMonitorService::class.java)
        context.stopService(intent)
    }
}

