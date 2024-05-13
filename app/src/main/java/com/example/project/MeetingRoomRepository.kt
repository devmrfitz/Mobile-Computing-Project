package com.example.project

class MeetingRoomRepository(private val meetingRoomDao: MeetingRoomDao) {
    suspend fun insert(room: MeetingRoom) {
        meetingRoomDao.insert(room)
    }

    suspend fun getAll(): List<MeetingRoom> {
        return meetingRoomDao.getAll()
    }
}