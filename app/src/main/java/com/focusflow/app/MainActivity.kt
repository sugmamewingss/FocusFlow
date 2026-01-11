package com.focusflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.focusflow.app.data.local.AppDatabase
import com.focusflow.app.data.repository.FocusFlowRepository
import com.focusflow.app.ui.navigation.FocusFlowNavigation
import com.focusflow.app.ui.theme.FocusFlowTheme
import com.focusflow.app.ui.viewmodel.FocusViewModel

class MainActivity : ComponentActivity() {

    private lateinit var repository: FocusFlowRepository
    private lateinit var viewModel: FocusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val database = AppDatabase.getDatabase(applicationContext)
        repository = FocusFlowRepository(
            userDao = database.userDao(),
            sessionDao = database.focusSessionDao(),
            assetDao = database.virtualAssetDao(),
            inventoryDao = database.userInventoryDao(),
            whitelistDao = database.appWhitelistDao()
        )

        viewModel = FocusViewModel(repository)

        setContent {
            FocusFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FocusFlowNavigation(viewModel = viewModel, repository = repository)
                }
            }
        }
    }
}