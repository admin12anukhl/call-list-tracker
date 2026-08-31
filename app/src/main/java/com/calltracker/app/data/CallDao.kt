package com.calltracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CallDao {
    @Insert
    suspend fun insert(record: CallRecord)

    @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
    suspend fun getAll(): List<CallRecord>
}
