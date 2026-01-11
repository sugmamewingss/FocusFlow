package com.focusflow.app.data.local.dao

import androidx.room.*
import com.focusflow.app.data.local.entity.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert
    suspend fun insertSession(session: FocusSession): Long

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getUserSessions(userId: Long): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND status = 'Completed' ORDER BY startTime DESC LIMIT 7")
    fun getLastWeekSessions(userId: Long): Flow<List<FocusSession>>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE userId = :userId AND status = 'Completed'")
    fun getTotalFocusMinutes(userId: Long): Flow<Int?>

    @Query("SELECT * FROM focus_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: Long): FocusSession?
}