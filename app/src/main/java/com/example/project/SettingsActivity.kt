package com.example.project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.project.ui.theme.ProjectTheme

class SettingsActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        setContent {
            ProjectTheme {

                val videoOnByDefault = remember { mutableStateOf(sharedPreferences.getBoolean("VideoOnByDefault", false)) }
                val audioOnByDefault = remember { mutableStateOf(sharedPreferences.getBoolean("AudioOnByDefault", false)) }
                val audioOnly = remember { mutableStateOf(sharedPreferences.getBoolean("AudioOnly", false)) }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Video On by default")
                    Switch(checked = videoOnByDefault.value, onCheckedChange = {
                        sharedPreferences.edit().putBoolean("VideoOnByDefault", it).apply()
                        videoOnByDefault.value = it
                    })

                    Text("Audio On by default")
                    Switch(checked = audioOnByDefault.value, onCheckedChange = {
                        sharedPreferences.edit().putBoolean("AudioOnByDefault", it).apply()
                        audioOnByDefault.value = it
                    })

                    Text("Audio only")
                    Switch(checked = audioOnly.value, onCheckedChange = {
                        sharedPreferences.edit().putBoolean("AudioOnly", it).apply()
                        audioOnly.value = it
                    })

                    Button(onClick = {
                        finish()
                    }) {
                        Text("Exit")
                    }
                }
            }
        }
    }
}