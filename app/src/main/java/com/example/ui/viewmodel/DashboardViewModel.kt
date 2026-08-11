package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.BackendEntity
import com.example.data.model.ConfigUpdateRequest
import com.example.data.model.ConfigsResponse
import com.example.data.model.ConnectionsResponse
import com.example.data.model.LogItem
import com.example.data.model.MemoryResponse
import com.example.data.model.ProxiesResponse
import com.example.data.model.RulesResponse
import com.example.data.model.TrafficResponse
import com.example.data.model.VersionResponse
import com.example.data.repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DashboardRepository
    val backends: StateFlow<List<BackendEntity>>
    val activeBackend: StateFlow<BackendEntity?>

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese.asStateFlow()

    fun toggleLanguage() {
        _isChinese.value = !_isChinese.value
    }

    private val _versionInfo = MutableStateFlow<VersionResponse?>(null)
    val versionInfo: StateFlow<VersionResponse?> = _versionInfo.asStateFlow()

    private val _trafficCurrent = MutableStateFlow(TrafficResponse(0, 0))
    val trafficCurrent: StateFlow<TrafficResponse> = _trafficCurrent.asStateFlow()

    // Real-time speed history for Compose Canvas chart (last 30 seconds)
    private val _trafficHistory = MutableStateFlow<List<Pair<Long, Long>>>(emptyList())
    val trafficHistory: StateFlow<List<Pair<Long, Long>>> = _trafficHistory.asStateFlow()

    private val _memoryInfo = MutableStateFlow(MemoryResponse(0, 0))
    val memoryInfo: StateFlow<MemoryResponse> = _memoryInfo.asStateFlow()

    private val _configsInfo = MutableStateFlow(ConfigsResponse())
    val configsInfo: StateFlow<ConfigsResponse> = _configsInfo.asStateFlow()

    private val _proxiesState = MutableStateFlow(ProxiesResponse())
    val proxiesState: StateFlow<ProxiesResponse> = _proxiesState.asStateFlow()

    private val _connectionsState = MutableStateFlow(ConnectionsResponse())
    val connectionsState: StateFlow<ConnectionsResponse> = _connectionsState.asStateFlow()

    private val _rulesState = MutableStateFlow(RulesResponse())
    val rulesState: StateFlow<RulesResponse> = _rulesState.asStateFlow()

    private val _logsList = MutableStateFlow<List<LogItem>>(emptyList())
    val logsList: StateFlow<List<LogItem>> = _logsList.asStateFlow()

    // Search and Filters
    val proxySearchQuery = MutableStateFlow("")
    val connectionSearchQuery = MutableStateFlow("")
    val ruleSearchQuery = MutableStateFlow("")
    val logFilterLevel = MutableStateFlow("ALL")

    // UI Toast Events
    private val _toastEvents = MutableSharedFlow<String>()
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    private var pollingJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DashboardRepository(database.backendDao())

        backends = repository.allBackends.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeBackend = repository.activeBackend.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            repository.initDefaultBackendIfEmpty()
        }

        viewModelScope.launch {
            activeBackend.collect { backend ->
                restartPolling(backend)
            }
        }
    }

    fun toggleDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
        repository.setDemoMode(enabled)
        viewModelScope.launch {
            _toastEvents.emit(if (enabled) "Demo Mode Enabled (Simulated Core)" else "Connecting to Core API...")
            refreshData()
        }
    }

    private fun restartPolling(backend: BackendEntity?) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            // Initial version check
            checkVersion(backend)
            while (true) {
                pollMetrics(backend)
                delay(1000) // 1 second refresh cycle
            }
        }
    }

    private suspend fun checkVersion(backend: BackendEntity?) {
        val versionRes = repository.fetchVersion(backend)
        if (versionRes.isSuccess) {
            _versionInfo.value = versionRes.getOrNull()
            _isConnected.value = true
        } else {
            _isConnected.value = repository.isDemoMode()
            if (!repository.isDemoMode()) {
                // If core is offline and not demo mode, show warning
            }
        }
    }

    private suspend fun pollMetrics(backend: BackendEntity?) {
        // Traffic
        val trafficRes = repository.fetchTraffic(backend)
        if (trafficRes.isSuccess) {
            val traffic = trafficRes.getOrNull() ?: TrafficResponse(0, 0)
            _trafficCurrent.value = traffic
            val currentList = _trafficHistory.value.toMutableList()
            currentList.add(Pair(traffic.down, traffic.up))
            if (currentList.size > 30) {
                currentList.removeAt(0)
            }
            _trafficHistory.value = currentList
            _isConnected.value = true
        } else {
            if (!_isDemoMode.value) {
                _isConnected.value = false
            }
        }

        // Memory
        val memoryRes = repository.fetchMemory(backend)
        if (memoryRes.isSuccess) {
            _memoryInfo.value = memoryRes.getOrNull() ?: MemoryResponse(0, 0)
        }

        // Periodically poll Configs, Proxies, Connections
        val configsRes = repository.fetchConfigs(backend)
        if (configsRes.isSuccess) {
            _configsInfo.value = configsRes.getOrNull() ?: ConfigsResponse()
        }

        val proxiesRes = repository.fetchProxies(backend)
        if (proxiesRes.isSuccess) {
            _proxiesState.value = proxiesRes.getOrNull() ?: ProxiesResponse()
        }

        val connRes = repository.fetchConnections(backend)
        if (connRes.isSuccess) {
            _connectionsState.value = connRes.getOrNull() ?: ConnectionsResponse()
        }

        val rulesRes = repository.fetchRules(backend)
        if (rulesRes.isSuccess) {
            _rulesState.value = rulesRes.getOrNull() ?: RulesResponse()
        }

        // Simulated Logs stream
        val newLogs = repository.generateSimulatedLogs()
        val currentLogs = _logsList.value.toMutableList()
        currentLogs.addAll(0, newLogs)
        if (currentLogs.size > 100) {
            _logsList.value = currentLogs.take(100)
        } else {
            _logsList.value = currentLogs
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val currentBackend = activeBackend.value
            checkVersion(currentBackend)
            pollMetrics(currentBackend)
            _toastEvents.emit("Dashboard Refreshed")
        }
    }

    // Backend management actions
    fun addBackendProfile(name: String, host: String, port: Int, secret: String, isHttps: Boolean, setActive: Boolean) {
        viewModelScope.launch {
            val entity = BackendEntity(
                name = name,
                host = host,
                port = port,
                secret = secret,
                isHttps = isHttps,
                isActive = setActive
            )
            repository.addBackend(entity)
            _toastEvents.emit("Backend profile '$name' saved")
        }
    }

    fun updateBackendProfile(entity: BackendEntity) {
        viewModelScope.launch {
            repository.updateBackend(entity)
            _toastEvents.emit("Backend profile updated")
        }
    }

    fun deleteBackendProfile(entity: BackendEntity) {
        viewModelScope.launch {
            repository.deleteBackend(entity)
            _toastEvents.emit("Profile deleted")
        }
    }

    fun selectActiveBackend(id: Long) {
        viewModelScope.launch {
            repository.setActiveBackend(id)
            _toastEvents.emit("Switched Active Core Backend")
        }
    }

    // Proxy actions
    fun selectProxyForGroup(groupName: String, proxyName: String) {
        viewModelScope.launch {
            val ok = repository.selectProxy(activeBackend.value, groupName, proxyName)
            if (ok) {
                _toastEvents.emit("Switched $groupName -> $proxyName")
                val proxiesRes = repository.fetchProxies(activeBackend.value)
                if (proxiesRes.isSuccess) {
                    _proxiesState.value = proxiesRes.getOrNull() ?: ProxiesResponse()
                }
            } else {
                _toastEvents.emit("Failed to select proxy")
            }
        }
    }

    fun testProxyDelay(proxyName: String, testUrl: String = "http://www.gstatic.com/generate_204") {
        viewModelScope.launch {
            val delayMs = repository.testProxyDelay(activeBackend.value, proxyName, testUrl)
            if (delayMs > 0) {
                _toastEvents.emit("Latency $proxyName: $delayMs ms")
            } else {
                _toastEvents.emit("$proxyName Timeout")
            }
            val proxiesRes = repository.fetchProxies(activeBackend.value)
            if (proxiesRes.isSuccess) {
                _proxiesState.value = proxiesRes.getOrNull() ?: ProxiesResponse()
            }
        }
    }

    // Config actions
    fun setCoreMode(mode: String) {
        viewModelScope.launch {
            val ok = repository.updateConfigs(activeBackend.value, ConfigUpdateRequest(mode = mode))
            if (ok) {
                _configsInfo.value = _configsInfo.value.copy(mode = mode)
                _toastEvents.emit("Core Mode set to $mode")
            }
        }
    }

    fun setAllowLan(allow: Boolean) {
        viewModelScope.launch {
            val ok = repository.updateConfigs(activeBackend.value, ConfigUpdateRequest(allowLan = allow))
            if (ok) {
                _configsInfo.value = _configsInfo.value.copy(allowLan = allow)
                _toastEvents.emit("Allow LAN ${if (allow) "Enabled" else "Disabled"}")
            }
        }
    }

    fun setLogLevel(level: String) {
        viewModelScope.launch {
            val ok = repository.updateConfigs(activeBackend.value, ConfigUpdateRequest(logLevel = level))
            if (ok) {
                _configsInfo.value = _configsInfo.value.copy(logLevel = level)
                _toastEvents.emit("Log level set to $level")
            }
        }
    }

    // Connection actions
    fun closeConnection(id: String) {
        viewModelScope.launch {
            val ok = repository.closeConnection(activeBackend.value, id)
            if (ok) {
                _toastEvents.emit("Connection closed")
                val connRes = repository.fetchConnections(activeBackend.value)
                if (connRes.isSuccess) {
                    _connectionsState.value = connRes.getOrNull() ?: ConnectionsResponse()
                }
            }
        }
    }

    fun closeAllConnections() {
        viewModelScope.launch {
            val ok = repository.closeAllConnections(activeBackend.value)
            if (ok) {
                _toastEvents.emit("All connections closed")
                val connRes = repository.fetchConnections(activeBackend.value)
                if (connRes.isSuccess) {
                    _connectionsState.value = connRes.getOrNull() ?: ConnectionsResponse()
                }
            }
        }
    }

    fun clearLogs() {
        _logsList.value = emptyList()
    }
}
