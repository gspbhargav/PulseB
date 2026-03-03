package com.pulseb.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pulseb.app.data.db.AppDatabase
import com.pulseb.app.data.repository.ActivityRepository
import com.pulseb.app.ui.timeline.TimelineScreen
import com.pulseb.app.ui.timeline.TimelineViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(this)
        val repository = ActivityRepository(database.activityEntryDao())

        setContent {

            val viewModel: TimelineViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TimelineViewModel(repository) as T
                    }
                }
            )

            TimelineScreen(viewModel = viewModel)
        }
    }
}

//
//
//
// package com.pulseb.app
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            PulseBApp()
//        }
//    }
//}
//
//@Composable
//fun PulseBApp() {
//    MaterialTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("Foundation + Core Logic Ready")
//
//            }
//        }
//    }
//}
