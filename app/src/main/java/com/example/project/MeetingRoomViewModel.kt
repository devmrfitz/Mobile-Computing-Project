package com.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MeetingRoomViewModel(private val repository: MeetingRoomRepository) : ViewModel() {

    suspend fun insert(room: MeetingRoom) {
        repository.insert(room)
    }

    suspend fun getAll(): List<MeetingRoom> {
        return repository.getAll()
    }
}