package com.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MeetingRoomViewModel(private val repository: MeetingRoomRepository) : ViewModel() {

    suspend fun insert(room: MeetingRoom) {
        val current = repository.getAll()
        val exists = current.any { it.roomName == room.roomName }
        for (i in current) {
            println(i.roomName)
        }
        println("exists: $exists")
        println("room: ${room.roomName}")
        if (!exists) {
            repository.insert(room)
        }
    }

    suspend fun getAll(): List<MeetingRoom> {
        return repository.getAll()
    }
}