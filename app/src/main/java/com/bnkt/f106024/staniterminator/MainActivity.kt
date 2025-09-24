package com.bnkt.f106024.staniterminator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Main screen - choose workout type and see motivational quote
class MainActivity : AppCompatActivity() {

    private lateinit var database: WorkoutDatabase
    private lateinit var quoteService: ApiService
    private lateinit var motivationText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database and quote service
        database = WorkoutDatabase(this)
        quoteService = ApiService()

        // Find all buttons and text views
        val cardioButton = findViewById<Button>(R.id.btnCardio)
        val strengthButton = findViewById<Button>(R.id.btnStrength)
        val historyButton = findViewById<Button>(R.id.btnHistory)
        motivationText = findViewById(R.id.motivationText)

        // Set button click listeners
        cardioButton.setOnClickListener { startWorkout("Cardio") }
        strengthButton.setOnClickListener { startWorkout("Strength") }
        historyButton.setOnClickListener { showHistory() }

        // Get motivational quote when app starts
        loadMotivationalQuote()
    }

    private fun startWorkout(workoutType: String) {
        // Create intent to go to workout screen
        val intent = Intent(this, WorkoutActivity::class.java)
        intent.putExtra("workout_type", workoutType)
        startActivity(intent)
    }

    private fun showHistory() {
        val workouts = database.getAllWorkouts()
        if (workouts.isEmpty()) {
            Toast.makeText(this, "No workouts yet. Start exercising!", Toast.LENGTH_LONG).show()
        } else {
            showHistoryDialog(workouts)
        }
    }

    private fun showHistoryDialog(workouts: List<WorkoutSession>) {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_workout_history, null)

        val historyContent = dialogView.findViewById<TextView>(R.id.historyContent)
        val closeButton = dialogView.findViewById<Button>(R.id.btnCloseHistory)

        // Build history text
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

    private fun loadMotivationalQuote() {
        motivationText.text = "Loading motivation..."

        quoteService.getRandomQuote(object : ApiService.QuoteCallback {
            override fun onQuoteReceived(quote: String, author: String) {
                motivationText.text = "\"$quote\" — $author"
            }

            override fun onQuoteFailed(error: String) {
                motivationText.text = "Stay motivated and keep exercising! 💪"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Load new quote when returning to main screen
        loadMotivationalQuote()
    }
}