package com.pulseb.app.ui.popup

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pulseb.app.util.LogTags
import androidx.compose.ui.Alignment
class PopupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LogTags.APP, "PopupActivity opened")

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        setContent {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {

                    var text by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text("What did you do in the last 15 minutes?")

                        Spacer(Modifier.height(16.dp))

                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { finish() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }

    }
}

