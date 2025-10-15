package com.bnkt.f106024.staniterminator

object WorkoutState {
    @Volatile var isPaused: Boolean = false
    @Volatile var isStopped: Boolean = false
    @Volatile var seconds: Int = 0
}
