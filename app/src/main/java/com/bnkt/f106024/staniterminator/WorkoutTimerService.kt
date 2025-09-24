package com.bnkt.f106024.staniterminator

import android.app.Service
import android.content.Intent
import android.os.IBinder

// Background service that counts seconds and sends updates
class WorkoutTimerService : Service() {

    private var isRunning = false
    private lateinit var timerThread: Thread

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true

        timerThread = Thread {
            var seconds = 0

            while (isRunning) {
                if (WorkoutState.isStopped) break

                if (!WorkoutState.isPaused) {
                    // Send timer update to TimerFragment
                    val timerIntent = Intent("com.bnkt.f106024.TIMER_UPDATE")
                    timerIntent.setPackage(packageName)
                    timerIntent.putExtra("seconds", seconds)
                    sendBroadcast(timerIntent)
                    seconds++
                }

                Thread.sleep(1000) // Wait 1 second
            }
            stopSelf()
        }
        timerThread.start()

        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        if (this::timerThread.isInitialized) {
            timerThread.interrupt()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
