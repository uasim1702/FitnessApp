package com.bnkt.f106024.staniterminator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Workout screen with timer and exercises
class WorkoutActivity : AppCompatActivity() {

    private lateinit var database: WorkoutDatabase
    private var workoutStartTime: Long = 0L
    private var workoutType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        database = WorkoutDatabase(this)
        workoutType = intent.getStringExtra("workout_type") ?: "Cardio"
        workoutStartTime = System.currentTimeMillis()

        // Load timer and exercise fragments
        supportFragmentManager.beginTransaction()
            .replace(R.id.timer_container, TimerFragment())
            .replace(R.id.exercise_container, ExerciseFragment().apply {
                arguments = Bundle().apply { putString("workout_type", workoutType) }
            })
            .commit()

        // Start timer service
        startService(Intent(this, WorkoutTimerService::class.java))

        // Setup control buttons
        setupButtons()
    }

    private fun setupButtons() {
        val pauseButton = findViewById<Button>(R.id.btnPause)
        val continueButton = findViewById<Button>(R.id.btnContinue)
        val stopButton = findViewById<Button>(R.id.btnStop)

        pauseButton.setOnClickListener {
            WorkoutState.isPaused = true
            Toast.makeText(this, "Workout paused", Toast.LENGTH_SHORT).show()
        }

        continueButton.setOnClickListener {
            when {
                WorkoutState.isStopped -> {
                    // Start new workout
                    WorkoutState.isStopped = false
                    WorkoutState.isPaused = false
                    workoutStartTime = System.currentTimeMillis()
                    startService(Intent(this, WorkoutTimerService::class.java))

                    // Restart exercises
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_container, ExerciseFragment().apply {
                            arguments = Bundle().apply { putString("workout_type", workoutType) }
                        })
                        .commit()
                    Toast.makeText(this, "Workout started!", Toast.LENGTH_SHORT).show()
                }
                WorkoutState.isPaused -> {
                    // Resume paused workout
                    WorkoutState.isPaused = false
                    Toast.makeText(this, "Workout resumed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        stopButton.setOnClickListener {
            if (!WorkoutState.isStopped) {
                // Calculate workout duration
                val durationSeconds = ((System.currentTimeMillis() - workoutStartTime) / 1000).toInt()
                if (durationSeconds > 0) {
                    // Save workout to database
                    database.saveWorkout(workoutType, durationSeconds)
                    val minutes = durationSeconds / 60
                    val seconds = durationSeconds % 60
                    Toast.makeText(this, "Workout saved! Duration: ${minutes}m ${seconds}s", Toast.LENGTH_LONG).show()
                }
            }
            WorkoutState.isStopped = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Save workout if app is closed during exercise
        if (!WorkoutState.isStopped) {
            val durationSeconds = ((System.currentTimeMillis() - workoutStartTime) / 1000).toInt()
            if (durationSeconds > 0) {
                database.saveWorkout(workoutType, durationSeconds)
            }
        }
        stopService(Intent(this, WorkoutTimerService::class.java))
    }
}
