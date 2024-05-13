package com.example.project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class VideoConferenceActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var videoOnByDefault: Boolean = false
    private var audioOnByDefault: Boolean = false
    private var audioOnly: Boolean = false

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

        // Load the settings
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        videoOnByDefault = sharedPreferences.getBoolean("VideoOnByDefault", false)
        audioOnByDefault = sharedPreferences.getBoolean("AudioOnByDefault", false)
        audioOnly = sharedPreferences.getBoolean("AudioOnly", false)

        val serverURL = URL("https://jitsi.member.fsf.org/")
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setRoom(roomName) // Use the room name here
            .setAudioMuted(!audioOnByDefault)
            .setVideoMuted(!videoOnByDefault)
            .setAudioOnly(audioOnly)
            .build()
        JitsiMeetActivity.launch(this, defaultOptions)
    }
}