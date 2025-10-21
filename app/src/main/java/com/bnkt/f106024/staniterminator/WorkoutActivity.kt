package com.bnkt.f106024.staniterminator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity that manages the workout session.
 * Handles timer, exercise sequence, and user actions (pause, continue, stop).
 */
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

        // Load timer and exercise fragments.
        supportFragmentManager.beginTransaction()
            .replace(R.id.timer_container, TimerFragment())
            .replace(R.id.exercise_container, ExerciseFragment().apply {
                arguments = Bundle().apply { putString("workout_type", workoutType) }
            })
            .commit()

        // Start background timer service.
        startService(Intent(this, WorkoutTimerService::class.java))

        setupButtons()
    }

    /** Initializes button actions for controlling the workout. */
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
                // Restart a stopped workout.
                WorkoutState.isStopped -> {
                    WorkoutState.isStopped = false
                    WorkoutState.isPaused = false
                    workoutStartTime = System.currentTimeMillis()
                    startService(Intent(this, WorkoutTimerService::class.java))

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_container, ExerciseFragment().apply {
                            arguments = Bundle().apply { putString("workout_type", workoutType) }
                        })
                        .commit()
                    Toast.makeText(this, "Workout started!", Toast.LENGTH_SHORT).show()
                }

                // Resume a paused workout.
                WorkoutState.isPaused -> {
                    WorkoutState.isPaused = false
                    Toast.makeText(this, "Workout resumed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        stopButton.setOnClickListener {
            if (!WorkoutState.isStopped) {
                val durationSeconds = WorkoutState.seconds
                if (durationSeconds > 0) {
                    database.saveWorkout(workoutType, durationSeconds)
                    val minutes = durationSeconds / 60
                    val seconds = durationSeconds % 60
                    Toast.makeText(this, "Workout saved! Duration: ${minutes}m ${seconds}s", Toast.LENGTH_LONG).show()
                }
            }
            WorkoutState.isStopped = true
        }
    }

    /** Ensures workout is saved and timer service stopped when activity is destroyed. */
    override fun onDestroy() {
        super.onDestroy()
        if (!WorkoutState.isStopped) {
            val durationSeconds = WorkoutState.seconds
            if (durationSeconds > 0) {
                database.saveWorkout(workoutType, durationSeconds)
            }
        }
        stopService(Intent(this, WorkoutTimerService::class.java))
    }
}
