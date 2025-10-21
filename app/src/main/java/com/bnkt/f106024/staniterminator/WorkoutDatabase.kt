package com.bnkt.f106024.staniterminator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// SQLite helper class for storing and retrieving completed workouts.
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
//not used but we need two abstract methods because of SQLite helper
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        onCreate(db)
    }

    /** Inserts a new workout record into the database. */
    fun saveWorkout(type: String, durationSeconds: Int): Long {
        val values = ContentValues().apply {
            put(COLUMN_TYPE, type)
            put(COLUMN_DURATION, durationSeconds)
            put(COLUMN_DATE, System.currentTimeMillis().toString())
        }
        return writableDatabase.insert(TABLE_WORKOUTS, null, values)
    }

    /** Returns all saved workouts sorted by date (newest first). */
    fun getAllWorkouts(): List<WorkoutSession> {
        val workouts = mutableListOf<WorkoutSession>()
        val cursor = readableDatabase.query(
            TABLE_WORKOUTS, null, null, null, null, null, "$COLUMN_DATE DESC"
        )
        cursor.use { //use closes the cursor after we are done
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

/** Data model representing a single workout entry. */
data class WorkoutSession(
    val id: Int,
    val type: String,
    val durationSeconds: Int,
    val date: String
)
