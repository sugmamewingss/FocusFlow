package com.focusflow.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusflow.app.data.local.entity.VirtualAsset
import com.focusflow.app.data.repository.FocusFlowRepository
import com.focusflow.app.ui.viewmodel.FocusViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    repository: FocusFlowRepository,
    viewModel: FocusViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val scope = rememberCoroutineScope()

    val assets by repository.getAllAssets().collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Semua", "Flora", "Building", "Animal", "Weather")

    var showPurchaseDialog by remember { mutableStateOf(false) }
    var selectedAsset by remember { mutableStateOf<VirtualAsset?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Toko") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Zen Coins",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.totalZenCoins ?: 0}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Asset List
            val filteredAssets = when (tabs[selectedTab]) {
                "Semua" -> assets
                else -> assets.filter { it.assetType == tabs[selectedTab] }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAssets) { asset ->
                    AssetItem(
                        asset = asset,
                        userCoins = user?.totalZenCoins ?: 0,
                        onPurchase = {
                            selectedAsset = asset
                            showPurchaseDialog = true
                        }
                    )
                }
            }
        }
    }

    // Purchase Confirmation Dialog
    if (showPurchaseDialog && selectedAsset != null) {
        PurchaseConfirmationDialog(
            asset = selectedAsset!!,
            userCoins = user?.totalZenCoins ?: 0,
            onDismiss = { showPurchaseDialog = false },
            onConfirm = {
                scope.launch {
                    val success = repository.purchaseAsset(
                        userId = user?.userId ?: 0,
                        assetId = selectedAsset?.assetId ?: 0
                    )
                    showPurchaseDialog = false
                }
            }
        )
    }
}

@Composable
fun PurchaseConfirmationDialog(
    asset: VirtualAsset,
    userCoins: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Pembelian") },
        text = {
            Column {
                Text("Apakah Anda yakin ingin membeli ${asset.assetName}?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Harga: ${asset.price} Zen Coins",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = userCoins >= asset.price
            ) {
                Text("Beli")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun AssetItem(
    asset: VirtualAsset,
    userCoins: Int,
    onPurchase: () -> Unit
) {
    val canAfford = userCoins >= asset.price
    val icon = when (asset.assetType) {
        "Flora" -> when {
            asset.assetName.contains("Sakura") -> "🌸"
            asset.assetName.contains("Lotus") -> "🪷"
            asset.assetName.contains("Lavender") -> "🪻"
            asset.assetName.contains("Taman Bunga") -> "💐"
            asset.assetName.contains("Bambu") -> "🎋"
            asset.assetName.contains("Bonsai") -> "🌿"
            asset.assetName.contains("Pinus") -> "🌲"
            asset.assetName.contains("Oak") -> "🌳"
            asset.assetName.contains("Apel") -> "🍎"
            asset.assetName.contains("Sawit") -> "🌴"

            else -> "🌱"
        }
        "Building" -> when {
            asset.assetName.contains("Kolam") -> "⛲"
            asset.assetName.contains("Batu") -> "🪨"
            asset.assetName.contains("Jembatan") -> "🌉"
            asset.assetName.contains("Lentera") -> "🏮"
            asset.assetName.contains("Pagoda") -> "🛕"
            asset.assetName.contains("Taman") -> "🗻"
            asset.assetName.contains("Terjun") -> "🌊"
            asset.assetName.contains("Gazebo") -> "⛩️"
            asset.assetName.contains("Moai") -> "🗿"
            else -> "🏠"
        }
        "Animal" -> when {
            asset.assetName.contains("Tropik") -> "🐠"
            asset.assetName.contains("Blowfish") -> "🐡"
            asset.assetName.contains("Flamingo") -> "🦩"
            asset.assetName.contains("Kupu") -> "🦋"
            asset.assetName.contains("Burung Kolibri") -> "🐦"
            asset.assetName.contains("Rusa") -> "🦌"
            asset.assetName.contains("Kelinci") -> "🐰"
            asset.assetName.contains("Burung Hantu") -> "🦉"
            asset.assetName.contains("Rubah") -> "🦊"
            else -> "🐾"
        }
        "Weather" -> when {
            asset.assetName.contains("Hujan") -> "🌧️"
            asset.assetName.contains("Kabut") -> "🌫️"
            asset.assetName.contains("Pelangi") -> "🌈"
            asset.assetName.contains("Salju") -> "❄️"
            asset.assetName.contains("Kunang") -> "✨"
            else -> "☁️"
        }
        else -> "📦"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Asset Icon
            Text(
                text = icon,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Asset Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.assetName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = asset.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${asset.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Purchase Button
            Button(
                onClick = onPurchase,
                enabled = canAfford,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Beli")
            }
        }
    }
}