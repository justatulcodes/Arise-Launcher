package com.expeknow.ariselauncher.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart
            val isUpdate = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

            Log.d("PackageReceiver", "App installed: $packageName | Update: $isUpdate")
            Toast.makeText(
                context,
                "Installed: $packageName (Update: $isUpdate)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
