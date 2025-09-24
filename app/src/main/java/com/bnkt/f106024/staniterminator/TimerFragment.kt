package com.bnkt.f106024.staniterminator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

// Fragment that shows the workout timer
class TimerFragment : Fragment() {

    private lateinit var timerText: TextView

    // Receiver to get timer updates from service
    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (WorkoutState.isStopped || WorkoutState.isPaused) return

            val seconds = intent?.getIntExtra("seconds", 0) ?: 0
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            timerText.text = String.format("Timer: %02d:%02d", minutes, remainingSeconds)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        timerText = view.findViewById(R.id.timerText)
        timerText.text = "Timer: 00:00"
        return view
    }

    override fun onResume() {
        super.onResume()
        // Register to receive timer updates
        requireActivity().registerReceiver(
            timerReceiver,
            IntentFilter("com.bnkt.f106024.TIMER_UPDATE"),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        // Stop receiving timer updates
        requireActivity().unregisterReceiver(timerReceiver)
    }
}
