package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class VideoConferenceActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        val dao = db.meetingRoomDao()
        val viewModel = MeetingRoomViewModel(MeetingRoomRepository(dao))

        // Retrieve the room name from the intent
        val roomName = intent.getStringExtra("ROOM_NAME") ?: "default"
        // Store the room name in the database
        GlobalScope.launch {
            viewModel.insert(MeetingRoom(roomName = roomName))
        }

        val serverURL = URL("https://meet.jit.si")
//        val defaultOptions = JitsiMeetConferenceOptions.Builder()
//            .setServerURL(serverURL)
//            .setRoom(roomName) // Use the room name here
//            .build()
//        JitsiMeetActivity.launch(this, defaultOptions)


    }
}