package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    val userId: Long,
    val category: String,
    val startTime: Long,
    val durationMinutes: Int,
    val status: String, // "Completed" atau "Failed"
    val coinsEarned: Int,
    val distractions: Int = 0,
    val mode: String // "Soft" atau "Hard"
)