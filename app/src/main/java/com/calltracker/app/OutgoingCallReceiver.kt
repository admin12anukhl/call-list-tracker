package com.calltracker.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.calltracker.app.data.CallRecord
import com.calltracker.app.data.CallDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Tries to catch outgoing numbers
class OutgoingCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val number = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER) ?: return
        // Save an outgoing record with current timestamp; location may be added by service on OFFHOOK
        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val db = CallDatabase.getInstance(it.applicationContext)
                val record = CallRecord(number = number, type = "outgoing", timestamp = System.currentTimeMillis(), latitude = null, longitude = null, address = null)
                db.callDao().insert(record)
            }
        }
    }
}
