package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.project.ui.theme.ProjectTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import com.example.project.ui.theme.Purple80
import com.example.project.ui.theme.PurpleGrey80

class HistoryActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var meetingRooms: MutableList<MeetingRoom>

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        val dao = db.meetingRoomDao()
        val viewModel = MeetingRoomViewModel(MeetingRoomRepository(dao))





        setContent {
            meetingRooms = remember { mutableStateListOf() }
            // Retrieve the list of previous meetings from the database
            GlobalScope.launch {
                val _meetingRooms = viewModel.getAll()

                // Display the list of previous meetings
                // This is a placeholder. Replace this with your actual UI code.
                _meetingRooms.forEach { room ->
                    println(room.roomName)
                }
                meetingRooms.removeAll { true }
                meetingRooms.addAll(_meetingRooms)
            }
            ProjectTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Display the list of previous meetings
                    // This is a placeholder. Replace this with your actual UI code.
                    meetingRooms.forEachIndexed { idx, room ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .background(PurpleGrey80)
                        ) {

                            Button(
                                onClick = {
                                    // Create the intent and put the room name as an extra
                                    // Create the intent and put the room name as an extra
                                    val intent = Intent(this@HistoryActivity, VideoConferenceActivity::class.java).apply {
                                        putExtra("ROOM_NAME", room.roomName)
                                    }
                                    startActivity(intent)
                                },
                                modifier = Modifier.padding(10.dp).fillMaxWidth()
                            ) {
                                Text("${idx + 1}: ${room.roomName}",)
                            }
                        }
                    }
                }
            }
        }
    }
}