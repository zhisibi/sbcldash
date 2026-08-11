package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionItem
import com.example.ui.components.formatBytes
import com.example.ui.theme.DownloadColor
import com.example.ui.theme.UploadColor
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun ConnectionsScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val connectionsState by viewModel.connectionsState.collectAsState()
    val searchQuery by viewModel.connectionSearchQuery.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()

    var selectedConnectionDetail by remember { mutableStateOf<ConnectionItem?>(null) }

    val filteredConnections = remember(connectionsState, searchQuery) {
        connectionsState.connections.filter { conn ->
            val host = conn.metadata.host ?: ""
            val destIp = conn.metadata.destinationIP ?: ""
            val process = conn.metadata.processPath ?: ""
            val rule = conn.rulePayload ?: ""
            searchQuery.isBlank() ||
                    host.contains(searchQuery, ignoreCase = true) ||
                    destIp.contains(searchQuery, ignoreCase = true) ||
                    process.contains(searchQuery, ignoreCase = true) ||
                    rule.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Summary Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("connections_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isChinese) "活跃连接 (${connectionsState.connections.size})" else "Active Connections (${connectionsState.connections.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.closeAllConnections() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_close_all_connections_screen")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isChinese) "断开全部" else "Close All", 
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isChinese) "总下载量" else "Total Downloaded",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatBytes(connectionsState.downloadTotal),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DownloadColor
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isChinese) "总上传量" else "Total Uploaded",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatBytes(connectionsState.uploadTotal),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = UploadColor
                                )
                            )
                        }
                    }
                }
            }
        }

        // Search Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.connectionSearchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("connection_search_field"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                placeholder = { 
                    Text(
                        text = if (isChinese) "搜索主机、IP、规则或进程..." else "Search host, IP, rule or process...",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        if (filteredConnections.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isChinese) "暂无活跃网络连接" else "No Connections Tracked",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isChinese) "新的网络请求将在这里实时显示" else "Active network requests will appear here in real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(
                items = filteredConnections,
                key = { it.id }
            ) { item ->
                ConnectionCardItem(
                    item = item,
                    isChinese = isChinese,
                    onClose = { viewModel.closeConnection(item.id) },
                    onDetailClick = { selectedConnectionDetail = item }
                )
            }
        }
    }

    // Connection Details Modal Dialog
    selectedConnectionDetail?.let { conn ->
        AlertDialog(
            onDismissRequest = { selectedConnectionDetail = null },
            title = {
                Text(
                    text = if (isChinese) "网络连接元数据" else "Connection Metadata",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (isChinese) "目标主机: ${conn.metadata.host ?: "无"}" else "Host: ${conn.metadata.host ?: "N/A"}")
                    Text(if (isChinese) "目标 IP/端口: ${conn.metadata.destinationIP}:${conn.metadata.destinationPort}" else "Destination: ${conn.metadata.destinationIP}:${conn.metadata.destinationPort}")
                    Text(if (isChinese) "源 IP 地址: ${conn.metadata.sourceIP}" else "Source IP: ${conn.metadata.sourceIP}")
                    Text(if (isChinese) "网络协议类型: ${conn.metadata.network?.uppercase()} / ${conn.metadata.type}" else "Network Protocol: ${conn.metadata.network?.uppercase()} / ${conn.metadata.type}")
                    Text(if (isChinese) "应用进程路径: ${conn.metadata.processPath ?: "未知进程"}" else "Process: ${conn.metadata.processPath ?: "Unknown"}")
                    Text(if (isChinese) "匹配路由规则: ${conn.rule} (${conn.rulePayload ?: "完全匹配"})" else "Matched Rule: ${conn.rule} (${conn.rulePayload ?: "Match"})")
                    Text(if (isChinese) "代理节点链路: ${conn.chains.joinToString(" ➔ ")}" else "Chains: ${conn.chains.joinToString(" ➔ ")}")
                    Text(if (isChinese) "已下载数据: ${formatBytes(conn.download)}" else "Download: ${formatBytes(conn.download)}")
                    Text(if (isChinese) "已上传数据: ${formatBytes(conn.upload)}" else "Upload: ${formatBytes(conn.upload)}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedConnectionDetail = null }) {
                    Text(if (isChinese) "关闭" else "Close")
                }
            }
        )
    }
}

@Composable
fun ConnectionCardItem(
    item: ConnectionItem,
    isChinese: Boolean,
    onClose: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
            .testTag("connection_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.metadata.network?.uppercase() ?: "TCP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.metadata.host?.ifBlank { item.metadata.destinationIP ?: (if (isChinese) "未知主机" else "Unknown Host") } ?: (if (isChinese) "未知" else "Unknown"),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("close_conn_btn_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close connection",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Proxy Chain path
            Text(
                text = item.chains.joinToString(" ➔ ").ifBlank { if (isChinese) "直连" else "Direct" },
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (if (isChinese) "规则: " else "Rule: ") + "${item.rule ?: "Match"} (${item.rulePayload ?: "*"})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "↓ " + formatBytes(item.download),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = DownloadColor
                    )
                    Text(
                        text = "↑ " + formatBytes(item.upload),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = UploadColor
                    )
                }
            }
        }
    }
}
