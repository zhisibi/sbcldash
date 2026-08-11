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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.BackendEntity
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun BackendsScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val backends by viewModel.backends.collectAsState()
    val isDemoMode by viewModel.isDemoMode.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingBackend by remember { mutableStateOf<BackendEntity?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingBackend = null
                    showAddDialog = true
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_backend")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Core Backend"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Demo Mode Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Demo / Simulation Mode",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Simulate live Clash/Sing-box core traffic & proxies when no local core is running.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isDemoMode,
                            onCheckedChange = { viewModel.toggleDemoMode(it) },
                            modifier = Modifier.testTag("demo_mode_switch")
                        )
                    }
                }
            }

            // Section Title
            item {
                Text(
                    text = "Saved Core Backends (${backends.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(
                items = backends,
                key = { it.id }
            ) { backend ->
                BackendCardItem(
                    backend = backend,
                    isChinese = isChinese,
                    onSelectActive = { viewModel.selectActiveBackend(backend.id) },
                    onEdit = {
                        editingBackend = backend
                        showAddDialog = true
                    },
                    onDelete = { viewModel.deleteBackendProfile(backend) },
                    onTestConnection = { viewModel.refreshData() }
                )
            }
        }
    }

    if (showAddDialog) {
        BackendAddEditDialog(
            initialBackend = editingBackend,
            onDismiss = { showAddDialog = false },
            onSave = { name, host, port, secret, isHttps, setActive ->
                if (editingBackend != null) {
                    viewModel.updateBackendProfile(
                        editingBackend!!.copy(
                            name = name,
                            host = host,
                            port = port,
                            secret = secret,
                            isHttps = isHttps,
                            isActive = setActive
                        )
                    )
                } else {
                    viewModel.addBackendProfile(
                        name = name,
                        host = host,
                        port = port,
                        secret = secret,
                        isHttps = isHttps,
                        setActive = setActive
                    )
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BackendCardItem(
    backend: BackendEntity,
    isChinese: Boolean,
    onSelectActive: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestConnection: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("backend_card_${backend.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (backend.isActive)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        border = if (backend.isActive)
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
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
                    RadioButton(
                        selected = backend.isActive,
                        onClick = onSelectActive,
                        modifier = Modifier.testTag("radio_active_backend_${backend.id}")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = backend.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (backend.isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isChinese) "已激活" else "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        val scheme = if (backend.isHttps) "https" else "http"
                        Text(
                            text = "$scheme://${backend.host}:${backend.port}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Backend",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Backend",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                        imageVector = Icons.Default.Key,
                        contentDescription = "Secret",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (backend.secret.isBlank()) 
                            (if (isChinese) "无密钥保护" else "No Secret Token") 
                        else 
                            (if (isChinese) "密钥保护中" else "Secret Token Protected"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onTestConnection,
                    modifier = Modifier.testTag("btn_test_backend_${backend.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = "Test",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isChinese) "测试连接" else "Test Connection", 
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
fun BackendAddEditDialog(
    initialBackend: BackendEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, host: String, port: Int, secret: String, isHttps: Boolean, setActive: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(initialBackend?.name ?: "Local Clash Core") }
    var host by remember { mutableStateOf(initialBackend?.host ?: "127.0.0.1") }
    var portText by remember { mutableStateOf((initialBackend?.port ?: 9090).toString()) }
    var secret by remember { mutableStateOf(initialBackend?.secret ?: "") }
    var isHttps by remember { mutableStateOf(initialBackend?.isHttps ?: false) }
    var setActive by remember { mutableStateOf(initialBackend?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialBackend == null) "Add Core Backend" else "Edit Core Backend",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Profile Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_backend_name")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text("Host / IP") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.8f)
                            .testTag("input_backend_host")
                    )

                    OutlinedTextField(
                        value = portText,
                        onValueChange = { portText = it },
                        label = { Text("Port") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_backend_port")
                    )
                }

                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text("Secret Token (Optional)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_backend_secret")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("HTTPS Encrypted", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isHttps, onCheckedChange = { isHttps = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { setActive = !setActive }
                ) {
                    Checkbox(checked = setActive, onCheckedChange = { setActive = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Set as Active Core Backend", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val port = portText.toIntOrNull() ?: 9090
                    if (name.isNotBlank() && host.isNotBlank()) {
                        onSave(name, host, port, secret, isHttps, setActive)
                    }
                },
                modifier = Modifier.testTag("btn_save_backend")
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
