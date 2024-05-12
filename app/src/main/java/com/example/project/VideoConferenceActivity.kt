package com.example.project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class VideoConferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the room name from the intent
        val roomName = intent.getStringExtra("ROOM_NAME") ?: "default"
        val serverURL = URL("https://meet.jit.si")
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setRoom(roomName) // Use the room name here
            .build()
        JitsiMeetActivity.launch(this, defaultOptions)
    }
}