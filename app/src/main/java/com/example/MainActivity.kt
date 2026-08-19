package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.local.database.TukTukDatabase
import com.example.data.repository.TukTukRepository
import com.example.ui.TukTukApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TukTukBackground
import com.example.ui.viewmodel.TukTukViewModel
import com.example.ui.viewmodel.TukTukViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: TukTukViewModel by viewModels {
        val db = TukTukDatabase.getDatabase(applicationContext)
        val repo = TukTukRepository(db.tukTukDao())
        TukTukViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TukTukBackground
                ) {
                    TukTukApp(viewModel = viewModel)
                }
            }
        }
    }
}

