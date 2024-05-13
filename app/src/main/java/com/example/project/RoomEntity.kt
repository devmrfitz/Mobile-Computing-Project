package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeetingRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomName: String
)