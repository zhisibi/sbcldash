package com.example.ui

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrashLogDialog
import com.example.ui.components.ThemeSelectionSheet
import com.example.ui.screens.BackendsScreen
import com.example.ui.screens.ConnectionsScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.ProxiesScreen
import com.example.ui.screens.RulesAndLogsScreen
import com.example.ui.theme.DelayGreen
import com.example.ui.theme.ZashboardTheme
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

    val selectedTheme by viewModel.selectedThemePreset.collectAsState()
    val selectedWallpaper by viewModel.selectedWallpaperPreset.collectAsState()
    val wallpaperOpacity by viewModel.wallpaperOpacity.collectAsState()
    val showThemeSheet by viewModel.showThemeSheet.collectAsState()

    val showCrashLogDialog by viewModel.showCrashLogDialog.collectAsState()
    val hasUnreadCrashLog by viewModel.hasUnreadCrashLog.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ZashboardTheme(themePreset = selectedTheme) {
        Box(modifier = modifier.fillMaxSize()) {
            // Render Background Wallpaper if selected
            if (selectedWallpaper.drawableRes != null) {
                Image(
                    painter = painterResource(id = selectedWallpaper.drawableRes!!),
                    contentDescription = "Background Wallpaper",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(wallpaperOpacity),
                    contentScale = ContentScale.Crop
                )
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = if (selectedWallpaper.drawableRes != null) Color.Transparent else MaterialTheme.colorScheme.background,
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
                            }
                        },
                        actions = {
                            // Small connection indicator dot + chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isDemoMode) MaterialTheme.colorScheme.tertiaryContainer
                                        else if (isConnected) DelayGreen.copy(alpha = 0.18f)
                                        else MaterialTheme.colorScheme.errorContainer
                                    )
                                    .clickable { selectedTab = 4 }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
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
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isChinese) {
                                            if (isDemoMode) "演示" else if (isConnected) "在线" else "离线"
                                        } else {
                                            if (isDemoMode) "DEMO" else if (isConnected) "ONLINE" else "OFFLINE"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDemoMode) MaterialTheme.colorScheme.onTertiaryContainer
                                        else if (isConnected) DelayGreen
                                        else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { viewModel.openCrashLogDialog() },
                                modifier = Modifier.testTag("top_app_bar_crash_log")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (hasUnreadCrashLog) {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = if (isChinese) "崩溃与运行日志" else "Crash Logs",
                                        tint = if (hasUnreadCrashLog) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.openThemeSheet() },
                                modifier = Modifier.testTag("top_app_bar_theme_palette")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = if (isChinese) "主题与壁纸设置" else "Theme & Wallpaper",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable { viewModel.toggleLanguage() }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("top_app_bar_lang_toggle"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isChinese) "中文" else "EN",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

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
                            containerColor = if (selectedWallpaper.drawableRes != null) MaterialTheme.colorScheme.surface.copy(alpha = 0.88f) else MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = if (selectedWallpaper.drawableRes != null) MaterialTheme.colorScheme.surface.copy(alpha = 0.88f) else MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("main_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                            label = { 
                                Text(
                                    text = if (isChinese) "概览" else "Overview", 
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            modifier = Modifier.testTag("nav_item_overview")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.Dns, contentDescription = "Proxies") },
                            label = { 
                                Text(
                                    text = if (isChinese) "节点" else "Proxies", 
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            modifier = Modifier.testTag("nav_item_proxies")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.Link, contentDescription = "Conns") },
                            label = { 
                                Text(
                                    text = if (isChinese) "连接" else "Conns", 
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            modifier = Modifier.testTag("nav_item_connections")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Rules & Logs") },
                            label = { 
                                Text(
                                    text = if (isChinese) "规则日志" else "Rules/Logs", 
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            modifier = Modifier.testTag("nav_item_rules")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 4,
                            onClick = { selectedTab = 4 },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Backends") },
                            label = { 
                                Text(
                                    text = if (isChinese) "后端" else "Backends", 
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
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

            // Theme Customization Sheet
            if (showThemeSheet) {
                ThemeSelectionSheet(
                    selectedTheme = selectedTheme,
                    selectedWallpaper = selectedWallpaper,
                    wallpaperOpacity = wallpaperOpacity,
                    isChinese = isChinese,
                    onThemeSelect = { viewModel.setThemePreset(it) },
                    onWallpaperSelect = { viewModel.setWallpaperPreset(it) },
                    onOpacityChange = { viewModel.setWallpaperOpacity(it) },
                    onResetDefault = { viewModel.resetThemeToDefault() },
                    onDismiss = { viewModel.closeThemeSheet() }
                )
            }

            // Crash Log Dialog
            if (showCrashLogDialog) {
                CrashLogDialog(
                    isChinese = isChinese,
                    onDismiss = { viewModel.closeCrashLogDialog() },
                    onLogsCleared = { viewModel.refreshCrashLogStatus() }
                )
            }
        }
    }
}
