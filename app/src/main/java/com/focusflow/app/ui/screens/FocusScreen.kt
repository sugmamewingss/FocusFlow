package com.focusflow.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusflow.app.ui.viewmodel.FocusViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showModeDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var customDuration by remember { mutableStateOf(25) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Animation for zen atmosphere
    val infiniteTransition = rememberInfiniteTransition(label = "zen")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (uiState.isSessionActive) {
                        showExitDialog = true
                    } else {
                        onNavigateBack()
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }

                Text(
                    text = "Fokus Mode",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Distraction counter
                if (uiState.isSessionActive) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "${uiState.distractionCount}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!uiState.isSessionActive) {
                // Setup Phase
                SetupPhase(
                    sessionType = uiState.sessionType,
                    mode = uiState.sessionMode,
                    category = uiState.sessionCategory,
                    duration = customDuration,
                    breakDuration = uiState.breakDuration,
                    onModeClick = { showModeDialog = true },
                    onCategoryClick = { showCategoryDialog = true },
                    onDurationChange = { customDuration = it },
                    onStart = {
                        if (uiState.sessionType == "Custom") {
                            viewModel.setTargetDuration(customDuration)
                        }
                        viewModel.startFocusSession()
                    }
                )
            } else {
                // Active Session Phase
                ActiveSessionPhase(
                    sessionType = uiState.sessionType,
                    elapsedTime = uiState.elapsedTime,
                    targetDuration = if (uiState.isBreakTime) uiState.breakDuration else uiState.targetDuration,
                    isPaused = uiState.isPaused,
                    isBreakTime = uiState.isBreakTime,
                    pomodoroRound = uiState.pomodoroRound,
                    waveOffset = waveOffset,
                    onPause = { viewModel.pauseSession() },
                    onResume = { viewModel.resumeSession() },
                    onComplete = { viewModel.completeFocusSession() },
                    onCancel = { showExitDialog = true }
                )
            }
        }
    }

    // Mode Selection Dialog
    if (showModeDialog) {
        ModeSelectionDialog(
            currentMode = uiState.sessionMode,
            onDismiss = { showModeDialog = false },
            onModeSelected = { mode ->
                viewModel.setSessionMode(mode)
                showModeDialog = false
            }
        )
    }

    // Category Selection Dialog
    if (showCategoryDialog) {
        CategorySelectionDialog(
            currentCategory = uiState.sessionCategory,
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = { category ->
                viewModel.setSessionCategory(category)
                showCategoryDialog = false
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                viewModel.cancelFocusSession()
                showExitDialog = false
                onNavigateBack()
            }
        )
    }
}

@Composable
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Keluar dari Sesi?") },
        text = {
            Text("Jika keluar sekarang, progres fokus Anda akan hilang dan tanaman akan layu. Yakin ingin keluar?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Ya, Keluar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tetap Fokus")
            }
        }
    )
}

@Composable
fun SetupPhase(
    sessionType: String,
    mode: String,
    category: String,
    duration: Int,
    breakDuration: Int,
    onModeClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onDurationChange: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Session Type Info
        if (sessionType != "Custom") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (sessionType == "Pomodoro") Icons.Default.Timer else Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (sessionType == "Pomodoro") "Mode Pomodoro" else "Mode Deep Work",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (sessionType == "Pomodoro")
                                "4 sesi × $duration menit + break $breakDuration menit"
                            else
                                "Fokus panjang tanpa break",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Mode Selection
        SettingCard(
            title = "Mode",
            value = mode,
            icon = if (mode == "Hard") Icons.Default.Lock else Icons.Default.LockOpen,
            onClick = onModeClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selection
        SettingCard(
            title = "Kategori",
            value = category,
            icon = Icons.Default.Category,
            onClick = onCategoryClick
        )

        // Duration Selector (only for Custom mode)
        if (sessionType == "Custom") {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(), // Pastikan kolom mengisi lebar kartu
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Durasi Fokus",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // GUNAKAN INI:
                    Row(
                        modifier = Modifier.fillMaxWidth(), // Baris memenuhi lebar
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Pusatkan konten secara horizontal
                    ) {
                        IconButton(
                            onClick = { if (duration > 5) onDurationChange(duration - 5) }
                        ) {
                            Icon(Icons.Default.Remove, "Kurangi")
                        }

                        // Gunakan Box dengan weight atau Spacer agar angka benar-benar di titik tengah
                        Column(
                            modifier = Modifier.widthIn(min = 100.dp), // Beri ruang minimum agar stabil
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$duration",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "menit",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        IconButton(
                            onClick = { if (duration < 120) onDurationChange(duration + 5) }
                        ) {
                            Icon(Icons.Default.Add, "Tambah")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start Button
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mulai Fokus",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActiveSessionPhase(
    sessionType: String,
    elapsedTime: Long,
    targetDuration: Int,
    isPaused: Boolean,
    isBreakTime: Boolean,
    pomodoroRound: Int,
    waveOffset: Float,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime).toInt()
    val elapsedSeconds = (TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60).toInt()
    val progress = (elapsedTime.toFloat() / (targetDuration * 60 * 1000))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pomodoro Round Indicator
        if (sessionType == "Pomodoro") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                for (i in 1..4) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (i <= pomodoroRound && !isBreakTime)
                                    MaterialTheme.colorScheme.primary
                                else if (i < pomodoroRound)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.primaryContainer
                            )
                    )
                }
            }

            Text(
                text = if (isBreakTime) "⏸ Waktu Istirahat" else "🎯 Sesi Fokus $pomodoroRound/4",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isBreakTime) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Animated Timer Circle
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background animated waves
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                for (i in 0..2) {
                    val radius = 100f + (waveOffset + i * 30f) % 140f
                    val alpha = 1f - ((waveOffset + i * 30f) % 140f) / 140f

                    drawCircle(
                        color = Color(0xFF9CAF88).copy(alpha = alpha * 0.3f),
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // Progress Circle
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(240.dp),
                strokeWidth = 12.dp,
                color = if (isBreakTime) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )

            // Time Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isBreakTime) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "dari $targetDuration menit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Control Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cancel Button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Batal")
            }

            // Pause/Resume Button
            if (!isPaused) {
                Button(
                    onClick = onPause,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Jeda")
                }
            } else {
                Button(
                    onClick = onResume,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lanjut")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ModeSelectionDialog(
    currentMode: String,
    onDismiss: () -> Unit,
    onModeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Mode Fokus") },
        text = {
            Column {
                ModeOption(
                    mode = "Soft",
                    description = "Notifikasi reminder jika keluar aplikasi",
                    isSelected = currentMode == "Soft",
                    onClick = { onModeSelected("Soft") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ModeOption(
                    mode = "Hard",
                    description = "Blokir akses ke aplikasi lain (1.5x koin)",
                    isSelected = currentMode == "Hard",
                    onClick = { onModeSelected("Hard") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun CategorySelectionDialog(
    currentCategory: String,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Study", "Coding", "Reading", "Work", "Writing", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Kategori") },
        text = {
            Column {
                categories.forEach { category ->
                    CategoryOption(
                        category = category,
                        isSelected = currentCategory == category,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeOption(
    mode: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = mode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun CategoryOption(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}