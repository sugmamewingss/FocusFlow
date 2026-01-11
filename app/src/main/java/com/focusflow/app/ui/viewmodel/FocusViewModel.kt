package com.focusflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.data.local.entity.FocusSession
import com.focusflow.app.data.local.entity.User
import com.focusflow.app.data.repository.FocusFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class FocusUiState(
    val user: User? = null,
    val isSessionActive: Boolean = false,
    val currentSessionId: Long? = null,
    val sessionMode: String = "Soft", // "Soft" or "Hard"
    val sessionCategory: String = "Study",
    val sessionType: String = "Custom", // "Pomodoro", "DeepWork", or "Custom"
    val targetDuration: Int = 25, // minutes
    val breakDuration: Int = 5, // minutes (for Pomodoro)
    val isBreakTime: Boolean = false,
    val pomodoroRound: Int = 1, // Current pomodoro round
    val elapsedTime: Long = 0, // milliseconds
    val distractionCount: Int = 0,
    val isPaused: Boolean = false,
    val recentSessions: List<FocusSession> = emptyList()
)

class FocusViewModel(
    private val repository: FocusFlowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null
    private var sessionStartTime: Long = 0

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(user = user) }
                    loadRecentSessions(user.userId)
                } else {
                    // Create default user
                    val userId = repository.createUser("Fii")
                    repository.getCurrentUser().collect { newUser ->
                        _uiState.update { it.copy(user = newUser) }
                    }
                }
            }
        }
    }

    private fun loadRecentSessions(userId: Long) {
        viewModelScope.launch {
            repository.getLastWeekSessions(userId).collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions) }
            }
        }
    }

    fun setSessionMode(mode: String) {
        _uiState.update { it.copy(sessionMode = mode) }
    }

    fun setSessionCategory(category: String) {
        _uiState.update { it.copy(sessionCategory = category) }
    }

    fun setTargetDuration(minutes: Int) {
        _uiState.update { it.copy(targetDuration = minutes) }
    }

    fun setSessionType(type: String, duration: Int, breakDuration: Int = 5) {
        _uiState.update {
            it.copy(
                sessionType = type,
                targetDuration = duration,
                breakDuration = breakDuration
            )
        }
    }

    fun startFocusSession() {
        val user = _uiState.value.user ?: return

        viewModelScope.launch {
            sessionStartTime = System.currentTimeMillis()

            val session = FocusSession(
                userId = user.userId,
                category = _uiState.value.sessionCategory,
                startTime = sessionStartTime,
                durationMinutes = _uiState.value.targetDuration,
                status = "In Progress",
                coinsEarned = 0,
                mode = _uiState.value.sessionMode
            )

            val sessionId = repository.startFocusSession(session)

            _uiState.update {
                it.copy(
                    isSessionActive = true,
                    currentSessionId = sessionId,
                    elapsedTime = 0,
                    distractionCount = 0,
                    isPaused = false,
                    isBreakTime = false,
                    pomodoroRound = 1
                )
            }

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isSessionActive && !_uiState.value.isPaused) {
                kotlinx.coroutines.delay(1000)
                _uiState.update {
                    it.copy(elapsedTime = System.currentTimeMillis() - sessionStartTime)
                }

                // Check if target duration reached
                val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(_uiState.value.elapsedTime)
                val currentTargetMinutes = if (_uiState.value.isBreakTime) {
                    _uiState.value.breakDuration.toLong()
                } else {
                    _uiState.value.targetDuration.toLong()
                }

                if (elapsedMinutes >= currentTargetMinutes) {
                    // For Pomodoro, switch between work and break
                    if (_uiState.value.sessionType == "Pomodoro" && !_uiState.value.isBreakTime) {
                        startBreakTime()
                    } else if (_uiState.value.sessionType == "Pomodoro" && _uiState.value.isBreakTime) {
                        // After break, start next pomodoro round or complete
                        if (_uiState.value.pomodoroRound < 4) {
                            startNextPomodoroRound()
                        } else {
                            completeFocusSession()
                        }
                    } else {
                        completeFocusSession()
                    }
                }
            }
        }
    }

    private fun startBreakTime() {
        _uiState.update {
            it.copy(
                isBreakTime = true,
                elapsedTime = 0
            )
        }
        sessionStartTime = System.currentTimeMillis()
    }

    private fun startNextPomodoroRound() {
        _uiState.update {
            it.copy(
                isBreakTime = false,
                elapsedTime = 0,
                pomodoroRound = it.pomodoroRound + 1
            )
        }
        sessionStartTime = System.currentTimeMillis()
    }

    fun pauseSession() {
        _uiState.update { it.copy(isPaused = true) }
        timerJob?.cancel()
    }

    fun resumeSession() {
        _uiState.update { it.copy(isPaused = false) }
        sessionStartTime = System.currentTimeMillis() - _uiState.value.elapsedTime
        startTimer()
    }

    fun recordDistraction() {
        _uiState.update { it.copy(distractionCount = it.distractionCount + 1) }
    }

    fun completeFocusSession() {
        val sessionId = _uiState.value.currentSessionId ?: return

        // Calculate total duration (for Pomodoro, sum all rounds)
        val durationMinutes = if (_uiState.value.sessionType == "Pomodoro") {
            _uiState.value.targetDuration * _uiState.value.pomodoroRound
        } else {
            TimeUnit.MILLISECONDS.toMinutes(_uiState.value.elapsedTime).toInt()
        }

        viewModelScope.launch {
            val coinsEarned = repository.completeSession(
                sessionId = sessionId,
                durationMinutes = durationMinutes,
                distractions = _uiState.value.distractionCount,
                mode = _uiState.value.sessionMode
            )

            _uiState.update {
                it.copy(
                    isSessionActive = false,
                    currentSessionId = null,
                    elapsedTime = 0,
                    distractionCount = 0,
                    isBreakTime = false,
                    pomodoroRound = 1
                )
            }

            // Reload user to get updated coins
            loadUserData()
        }

        timerJob?.cancel()
    }

    fun cancelFocusSession() {
        val sessionId = _uiState.value.currentSessionId ?: return

        viewModelScope.launch {
            repository.failSession(sessionId)

            _uiState.update {
                it.copy(
                    isSessionActive = false,
                    currentSessionId = null,
                    elapsedTime = 0,
                    distractionCount = 0,
                    isBreakTime = false,
                    pomodoroRound = 1
                )
            }
        }

        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}