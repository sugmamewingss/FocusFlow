package com.focusflow.app.data.local.dao

import androidx.room.*
import com.focusflow.app.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET totalZenCoins = totalZenCoins + :coins WHERE userId = :userId")
    suspend fun addCoins(userId: Long, coins: Int)

    @Query("UPDATE users SET totalFocusMinutes = totalFocusMinutes + :minutes WHERE userId = :userId")
    suspend fun addFocusMinutes(userId: Long, minutes: Int)
}