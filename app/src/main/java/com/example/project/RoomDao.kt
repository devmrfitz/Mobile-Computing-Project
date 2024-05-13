package com.example.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeetingRoomDao {
    @Insert
    suspend fun insert(room: MeetingRoom)

    @Query("SELECT * FROM MeetingRoom")
    suspend fun getAll(): List<MeetingRoom>
}