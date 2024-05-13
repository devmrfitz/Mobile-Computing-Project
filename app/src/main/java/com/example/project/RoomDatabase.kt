package com.example.project

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MeetingRoom::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meetingRoomDao(): MeetingRoomDao
}