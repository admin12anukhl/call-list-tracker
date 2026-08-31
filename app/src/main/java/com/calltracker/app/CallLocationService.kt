package com.calltracker.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.calltracker.app.data.CallRecord
import com.calltracker.app.data.CallDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallLocationService : Service() {
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var fusedClient: FusedLocationProviderClient

    private val listener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            super.onCallStateChanged(state, incomingNumber)
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    captureLocationAndSave(incomingNumber ?: "unknown", "incoming")
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    // call active - may be outgoing or answered incoming
                    // For outgoing, OutgoingCallReceiver tries to capture number
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    // idle
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        startForegroundIfNeeded()
    }

    private fun startForegroundIfNeeded() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "calltracker_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Call Tracker", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(ch)
        }
        val notif: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("CallTracker running")
            .setContentText("Listening for call events and logging location")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
        startForeground(1, notif)
    }

    private fun captureLocationAndSave(number: String, type: String) {
        // try last location first
        fusedClient.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    saveCall(number, type, loc)
                } else {
                    // fallback: request current location (simple approach)
                    fusedClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { l: Location? ->
                            saveCall(number, type, l)
                        }
                }
            }
            .addOnFailureListener {
                // ignore
            }
    }

    private fun saveCall(number: String, type: String, loc: Location?) {
        val record = CallRecord(
            number = number,
            type = type,
            timestamp = System.currentTimeMillis(),
            latitude = loc?.latitude,
            longitude = loc?.longitude,
            address = null
        )
        CoroutineScope(Dispatchers.IO).launch {
            val db = CallDatabase.getInstance(applicationContext)
            db.callDao().insert(record)
        }
    }

    override fun onDestroy() {
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
