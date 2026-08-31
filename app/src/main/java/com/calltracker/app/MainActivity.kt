package com.calltracker.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calltracker.app.data.CallDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CallAdapter

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        // start service if permissions granted
        val ok = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                perms[Manifest.permission.READ_PHONE_STATE] == true
        if (ok) startCallService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = CallAdapter()
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        if (hasPermissions()) {
            startCallService()
        } else {
            permissionsLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            ))
        }

        // load existing calls
        CoroutineScope(Dispatchers.IO).launch {
            val dao = CallDatabase.getInstance(applicationContext).callDao()
            val list = dao.getAll()
            CoroutineScope(Dispatchers.Main).launch {
                adapter.submitList(list)
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCallService() {
        val intent = Intent(this, CallLocationService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}
