package com.bnkt.f106024.staniterminator

import android.app.Service
import android.content.Intent
import android.os.IBinder

class WorkoutTimerService : Service() {

    private var isRunning = false
    private lateinit var timerThread: Thread

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        WorkoutState.seconds = 0

        timerThread = Thread {
            try {
                while (isRunning) {
                    if (WorkoutState.isStopped) break
                    if (!WorkoutState.isPaused) {
                        WorkoutState.seconds += 1
                    }
                    Thread.sleep(1000)
                }
            } catch (_: InterruptedException) {
            } finally {
                stopSelf()
            }
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
