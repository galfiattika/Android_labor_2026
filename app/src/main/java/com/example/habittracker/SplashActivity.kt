package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.databinding.ActivitySplashBinding
import com.example.habittracker.ui.StartupViewModel
import com.example.habittracker.ui.UiState

class SplashActivity : AppCompatActivity() {
    private val viewModel: StartupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.destination.observe(this) { state ->
            if (state is UiState.Success) {
                viewModel.consumeDestination()
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra(MainActivity.EXTRA_HAS_SESSION, state.data)
                )
                finish()
            }
        }
        viewModel.start()
    }
}
