package com.bnkt.f106024.staniterminator

//object that holds the current workout state.
//* Shared across activities, fragments, and services.
object WorkoutState {
    @Volatile var isPaused: Boolean = false
    @Volatile var isStopped: Boolean = false

//Elapsed workout time in seconds.
//Volatile gives safe access through all threads
    @Volatile var seconds: Int = 0
}
