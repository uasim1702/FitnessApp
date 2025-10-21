package com.bnkt.f106024.staniterminator

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Fragment that displays the exercise list and cycles through each exercise every 30 seconds.
 */
class ExerciseFragment : Fragment() {

    private lateinit var exerciseListLayout: LinearLayout
    private lateinit var currentExerciseText: TextView

    private var currentIndex = 0
    private var exercises: List<String> = emptyList()
    private var cycleTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        currentExerciseText = view.findViewById(R.id.exerciseText)
        exerciseListLayout = requireActivity().findViewById(R.id.exerciseListLayout)
        return view
    }

    override fun onResume() {
        super.onResume()

        // Load exercise list based on workout type.
        val workoutType = arguments?.getString("workout_type") ?: "Cardio"
        exercises = when (workoutType) {
            "Cardio" -> listOf("Jumping Jacks", "High Knees", "Mountain Climbers", "Burpees")
            "Strength" -> listOf("Push-ups", "Squats", "Plank")
            else -> listOf("Stretching")
        }

        currentExerciseText.text = "$workoutType exercises"
        buildExerciseList()
        currentIndex = 0
        startCycling()
    }

    override fun onPause() {
        super.onPause()
        cycleTimer?.cancel()
        cycleTimer = null
    }

    /** Starts a 30-second cycle that highlights exercises one by one. */
    private fun startCycling() {
        if (exercises.isEmpty()) return
        cycleTimer?.cancel()
        cycleTimer = object : CountDownTimer(Long.MAX_VALUE, 30_000L) {
            override fun onTick(millisUntilFinished: Long) {
                highlightExercise(currentIndex)
                currentIndex = (currentIndex + 1) % exercises.size
            }
            override fun onFinish() {}
        }.start()
    }

    /** Builds and displays the exercise list. */
    private fun buildExerciseList() {
        exerciseListLayout.removeAllViews()
        exercises.forEachIndexed { index, name ->
            val tv = TextView(requireContext()).apply {
                text = "• $name"
                textSize = 18f
                setPadding(20, 14, 20, 14)
                setTextColor(Color.BLACK)
                setBackgroundColor(Color.WHITE)
                tag = "exercise_$index"
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
            tv.layoutParams = lp
            exerciseListLayout.addView(tv)
        }
    }

    /** Highlights the current exercise and resets colors for others. */
    private fun highlightExercise(position: Int) {
        for (i in exercises.indices) {
            val tv = exerciseListLayout.findViewWithTag<TextView>("exercise_$i")
            if (i == position) {
                tv?.setBackgroundColor(Color.RED)
                tv?.setTextColor(Color.WHITE)
            } else {
                tv?.setBackgroundColor(Color.WHITE)
                tv?.setTextColor(Color.BLACK)
            }
        }
    }
}
