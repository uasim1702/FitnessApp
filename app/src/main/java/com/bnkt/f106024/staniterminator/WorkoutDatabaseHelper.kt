package com.bnkt.f106024.staniterminator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WorkoutDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "workouts.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_WORKOUTS = "workouts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_WORKOUTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_DURATION INTEGER NOT NULL,
                $COLUMN_DATE TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        onCreate(db)
    }

    fun saveWorkout(type: String, durationSeconds: Int): Long {
        val values = ContentValues().apply {
            put(COLUMN_TYPE, type)
            put(COLUMN_DURATION, durationSeconds)
            put(COLUMN_DATE, System.currentTimeMillis().toString())
        }
        return writableDatabase.insert(TABLE_WORKOUTS, null, values)
    }

    fun getAllWorkouts(): List<WorkoutSession> {
        val workouts = mutableListOf<WorkoutSession>()
        val cursor = readableDatabase.query(
            TABLE_WORKOUTS, null, null, null, null, null, "$COLUMN_DATE DESC"
        )
        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                workouts.add(WorkoutSession(id, type, duration, date))
            }
        }
        return workouts
    }
}

data class WorkoutSession(
    val id: Int,
    val type: String,
    val durationSeconds: Int,
    val date: String
)
