package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.BackendsScreen
import com.example.ui.screens.ConnectionsScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.ProxiesScreen
import com.example.ui.screens.RulesAndLogsScreen
import com.example.ui.theme.DelayGreen
import com.example.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZashboardApp(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val activeBackend by viewModel.activeBackend.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isDemoMode by viewModel.isDemoMode.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Z",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Zashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Connection / Demo Badge Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isDemoMode) MaterialTheme.colorScheme.tertiaryContainer
                                    else if (isConnected) DelayGreen.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.errorContainer
                                )
                                .clickable { selectedTab = 4 }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDemoMode) MaterialTheme.colorScheme.tertiary
                                            else if (isConnected) DelayGreen
                                            else MaterialTheme.colorScheme.error
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isDemoMode) "DEMO" else activeBackend?.name ?: "127.0.0.1",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDemoMode) MaterialTheme.colorScheme.onTertiaryContainer
                                    else if (isConnected) DelayGreen
                                    else MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("top_app_bar_lang_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isChinese) "中文" else "EN",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.refreshData() },
                        modifier = Modifier.testTag("top_app_bar_refresh")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = if (isChinese) "刷新内核数据" else "Refresh Core Data",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                    label = { Text(if (isChinese) "概览" else "Overview", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_overview")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Dns, contentDescription = "Proxies") },
                    label = { Text(if (isChinese) "节点" else "Proxies", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_proxies")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Link, contentDescription = "Conns") },
                    label = { Text(if (isChinese) "连接" else "Conns", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_connections")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Rules & Logs") },
                    label = { Text(if (isChinese) "规则/日志" else "Rules/Logs", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_rules")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Backends") },
                    label = { Text(if (isChinese) "后端" else "Backends", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_backends")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> OverviewScreen(
                    viewModel = viewModel,
                    onNavigateToBackends = { selectedTab = 4 }
                )
                1 -> ProxiesScreen(viewModel = viewModel)
                2 -> ConnectionsScreen(viewModel = viewModel)
                3 -> RulesAndLogsScreen(viewModel = viewModel)
                4 -> BackendsScreen(viewModel = viewModel)
            }
        }
    }
}
