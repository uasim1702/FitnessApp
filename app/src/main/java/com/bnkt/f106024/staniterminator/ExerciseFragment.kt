package com.bnkt.f106024.staniterminator

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ExerciseFragment : Fragment() {

    private lateinit var exerciseListLayout: LinearLayout
    private lateinit var currentExerciseText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var exerciseIndex = 0
    private var completedRounds = 0
    private val maxRounds = 5
    private lateinit var exercises: List<String>

    private val exerciseUpdater = object : Runnable {
        override fun run() {
            if (WorkoutState.isStopped) {
                exerciseIndex = 0
                completedRounds = 0
                updateExerciseDisplay(0)
                return
            }

            if (!WorkoutState.isPaused) {
                if (exerciseIndex >= exercises.size) {
                    completedRounds++
                    if (completedRounds >= maxRounds) {
                        currentExerciseText.text = "🏁 Workout Complete!"
                        return
                    }
                    exerciseIndex = 0
                }
                updateExerciseDisplay(exerciseIndex)
                exerciseIndex++
            }
            handler.postDelayed(this, 20_000)
        }
    }

    private fun updateExerciseDisplay(position: Int) {
        currentExerciseText.text = exercises[position]

        for (i in exercises.indices) {
            val exerciseView = exerciseListLayout.findViewWithTag<TextView>("exercise_$i")
            if (i == position) {
                exerciseView?.setTextColor(Color.WHITE)
                exerciseView?.setBackgroundColor(Color.RED)
            } else {
                exerciseView?.setTextColor(Color.BLACK)
                exerciseView?.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        currentExerciseText = view.findViewById(R.id.exerciseText)
        exerciseListLayout = requireActivity().findViewById(R.id.exerciseListLayout)
        return view
    }

    override fun onResume() {
        super.onResume()
        val workoutType = arguments?.getString("workout_type") ?: "Cardio"

        exercises = when (workoutType) {
            "Cardio" -> listOf("🏃 Jumping Jacks", "🤸 High Knees", "🧗 Mountain Climbers", "💥 Burpees")
            "Strength" -> listOf("💪 Push-ups", "🦵 Squats", "🧍 Plank")
            else -> listOf("🧘 Stretching")
        }

        exerciseListLayout.removeAllViews()
        exercises.forEachIndexed { index, exerciseName ->
            val exerciseView = TextView(requireContext()).apply {
                text = exerciseName
                textSize = 18f
                setPadding(20, 10, 20, 10)
                setBackgroundColor(Color.WHITE)
                setTextColor(Color.BLACK)
                tag = "exercise_$index"
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
            exerciseView.layoutParams = layoutParams
            exerciseListLayout.addView(exerciseView)
        }

        handler.post(exerciseUpdater)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(exerciseUpdater)
    }
}