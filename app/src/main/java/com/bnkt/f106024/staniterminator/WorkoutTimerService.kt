package com.bnkt.f106024.staniterminator

import android.app.Service
import android.content.Intent
import android.os.IBinder


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
                    val timerIntent = Intent("com.bnkt.f106024.TIMER_UPDATE")
                    timerIntent.setPackage(packageName)
                    timerIntent.putExtra("seconds", seconds)
                    sendBroadcast(timerIntent)
                    seconds++
                }
                try {
                    Thread.sleep(1000)
                } catch (_: InterruptedException) { break }
            }
            stopSelf()
        }
        timerThread.start()

        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        if (this::timerThread.isInitialized) timerThread.interrupt()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
