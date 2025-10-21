package com.bnkt.f106024.staniterminator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Entry activity of the app.
 * Displays workout options, motivational quotes, and workout history.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var database: WorkoutDatabase
    private lateinit var quoteService: ApiService
    private lateinit var motivationText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = WorkoutDatabase(this)
        quoteService = ApiService()

        val cardioButton = findViewById<Button>(R.id.btnCardio)
        val strengthButton = findViewById<Button>(R.id.btnStrength)
        val historyButton = findViewById<Button>(R.id.btnHistory)
        motivationText = findViewById(R.id.motivationText)

        cardioButton.setOnClickListener { startWorkout("Cardio") }
        strengthButton.setOnClickListener { startWorkout("Strength") }
        historyButton.setOnClickListener { showHistory() }

        loadMotivationalQuote()
    }

    /** Launches the workout screen for the selected workout type. */
    private fun startWorkout(workoutType: String) {
        val intent = Intent(this, WorkoutActivity::class.java)
        intent.putExtra("workout_type", workoutType)
        startActivity(intent)
    }

    /** Opens a dialog showing the last saved workout sessions. */
    private fun showHistory() {
        val workouts = database.getAllWorkouts()
        if (workouts.isEmpty()) {
            Toast.makeText(this, "No workouts yet. Start exercising!", Toast.LENGTH_LONG).show()
        } else {
            showHistoryDialog(workouts)
        }
    }

    /** Builds and displays the workout history dialog. */
    private fun showHistoryDialog(workouts: List<WorkoutSession>) {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_workout_history, null)

        val historyContent = dialogView.findViewById<TextView>(R.id.historyContent)
        val closeButton = dialogView.findViewById<Button>(R.id.btnCloseHistory)

        val historyText = StringBuilder()
        historyText.append("Total workouts: ${workouts.size}\n\n")
        workouts.take(10).forEachIndexed { index, workout ->
            val minutes = workout.durationSeconds / 60
            val seconds = workout.durationSeconds % 60
            val date = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(workout.date.toLong()))
            historyText.append("${index + 1}. ${workout.type} - ${minutes}m ${seconds}s ($date)\n")
        }
        historyContent.text = historyText.toString()

        val alertDialog = dialog.setView(dialogView).create()
        closeButton.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    /** Loads a random motivational quote from the API. */
    private fun loadMotivationalQuote() {
        motivationText.text = "Loading motivation..."
        quoteService.getRandomQuote(object : ApiService.QuoteCallback {
            override fun onQuoteReceived(quote: String, author: String) {
                motivationText.text = "\"$quote\" — $author"
            }
            override fun onQuoteFailed(error: String) {
                motivationText.text = "Stay motivated and keep exercising!"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadMotivationalQuote()
    }
}
