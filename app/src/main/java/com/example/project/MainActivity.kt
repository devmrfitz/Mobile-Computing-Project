package com.example.project

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.project.ui.theme.ProjectTheme
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var connectionsClient: ConnectionsClient
    private val meetingRoomNames = mutableStateListOf<String>()
    private val connectedEndpoints = mutableSetOf<String>() // Maintain a list of connected endpoints
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val isMoving = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionsClient = Nearby.getConnectionsClient(this)

        startDiscovery()
        setContent {
            ProjectTheme {
                // Create a state for the room name
                val roomName = remember { mutableStateOf("") }
                // Create a state for the meeting room names

                Column {
                    OutlinedTextField(
                        value = roomName.value,
                        onValueChange = { roomName.value = it },
                        label = { Text("Room Name") }
                    )
                    Button(onClick = {
                        val payload = Payload.fromBytes(roomName.value.toByteArray())
                        connectedEndpoints.forEach { endpointId ->
                            connectionsClient.sendPayload(endpointId, payload)
                        }
                        // Create the intent and put the room name as an extra
                        val intent = Intent(this@MainActivity, VideoConferenceActivity::class.java).apply {
                            putExtra("ROOM_NAME", roomName.value)
                        }
                        startActivity(intent)
                    },
                        enabled = roomName.value.isNotEmpty(),
                        modifier = Modifier.padding(bottom = 30.dp)

                        ) {
                        Text("Start Video Conference")
                    }

                    Row {
                        Button(onClick = {
                            // Create the intent to start HistoryActivity
                            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                            startActivity(intent)
                        },
                            modifier = Modifier.padding(end = 10.dp)
                            ) {
                            Text("View Meeting History")
                        }

                        Button(onClick = {
                            // Create the intent to start SettingsActivity
                            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text("Settings")
                        }
                    }
                    if (isMoving.value) {
                        AlertDialog(
                            onDismissRequest = {
                            },
                            title = {
                                Text("Warning")
                            },
                            text = {
                                Text("You are moving. Please stop moving to proceed.")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                    // Display the meeting room names
                    LazyColumn {
                        items(meetingRoomNames) { meetingRoomName ->
                            Text(meetingRoomName)
                        }
                    }
                }
            }
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        connectionsClient.startDiscovery(packageName, object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                connectionsClient.requestConnection("MyName", endpointId, object : ConnectionLifecycleCallback() {
                    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                        // Automatically accept the connection on both sides.
                        connectionsClient.acceptConnection(endpointId, object : PayloadCallback() {
                            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                                // Payload received.
                                val meetingRoomName = String(payload.asBytes()!!)
                                // Add the meeting room name to the list
                                meetingRoomNames.add(meetingRoomName)
                            }

                            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                                // Payload transfer update.
                            }
                        })
                    }

                    override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                        if (result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                            // We're connected! Can now start sending and receiving data.
                            connectedEndpoints.add(endpointId) // Add the endpoint to the list
                        }
                    }

                    override fun onDisconnected(endpointId: String) {
                        // We've been disconnected from this endpoint. No more data can be sent or received.
                        connectedEndpoints.remove(endpointId) // Remove the endpoint from the list
                    }
                })
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
            }
        }, discoveryOptions)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH
        if (acceleration > 1.7) { // The threshold is subjective and may need to be adjusted
            isMoving.value = true
        } else {
            isMoving.value = false
            println("Acceleration: $acceleration")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // You can implement this method if you want to react to changes in sensor accuracy
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
}