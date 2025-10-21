package com.bnkt.f106024.staniterminator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Fragment responsible for displaying and updating the workout timer.
 */
class TimerFragment : Fragment() {
    private lateinit var timerText: TextView
    private val handler = Handler(Looper.getMainLooper())


    // Periodically updates the timer display every second
    private val updater = object : Runnable {
        override fun run() {
            if (!WorkoutState.isStopped) {
                val total = WorkoutState.seconds
                val minutes = total / 60
                val seconds = total % 60
                timerText.text = String.format("Timer: %02d:%02d", minutes, seconds)
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        timerText = view.findViewById(R.id.timerText)
        timerText.text = "Timer: 00:00"
        return view
    }

    override fun onResume() {
        super.onResume()
        handler.post(updater)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updater)
    }
}
