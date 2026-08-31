package com.calltracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_records")
data class CallRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val number: String,
    val type: String,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?
)
