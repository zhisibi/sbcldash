package com.example.data.repository

import com.example.data.api.ApiClientFactory
import com.example.data.api.ClashApiService
import com.example.data.db.BackendDao
import com.example.data.db.BackendEntity
import com.example.data.model.ConfigUpdateRequest
import com.example.data.model.ConfigsResponse
import com.example.data.model.ConnectionItem
import com.example.data.model.ConnectionMetadata
import com.example.data.model.ConnectionsResponse
import com.example.data.model.DelayHistory
import com.example.data.model.LogItem
import com.example.data.model.MemoryResponse
import com.example.data.model.ProxiesResponse
import com.example.data.model.ProxyNode
import com.example.data.model.RuleItem
import com.example.data.model.RulesResponse
import com.example.data.model.SelectProxyRequest
import com.example.data.model.TrafficResponse
import com.example.data.model.VersionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class DashboardRepository(private val backendDao: BackendDao) {

    val allBackends: Flow<List<BackendEntity>> = backendDao.getAllBackends()
    val activeBackend: Flow<BackendEntity?> = backendDao.getActiveBackend()

    // Demo / Simulation Mode state
    private var demoMode: Boolean = false
    private var simulatedMode: String = "rule"
    private var simulatedAllowLan: Boolean = true
    private var simulatedLogLevel: String = "info"

    // Simulated proxy group selections
    private val simulatedProxySelections = mutableMapOf(
        "GLOBAL" to "🚀 Auto Select",
        "🚀 Auto Select" to "🇭🇰 HK Premium 01",
        "🎯 Direct / Proxy" to "🚀 Auto Select",
        "🎬 Streaming Media" to "🇸🇬 SG Fast 02",
        "🤖 AI Services" to "🇺🇸 US West 01"
    )

    private val simulatedNodesDelay = mutableMapOf(
        "🇭🇰 HK Premium 01" to 42,
        "🇭🇰 HK Premium 02" to 58,
        "🇯🇵 JP Tokyo 01" to 85,
        "🇯🇵 JP Osaka 02" to 92,
        "🇸🇬 SG Fast 01" to 110,
        "🇸🇬 SG Fast 02" to 105,
        "🇺🇸 US West 01" to 165,
        "🇺🇸 US East 02" to 210,
        "🇬🇧 UK London 01" to 240,
        "🇩🇪 DE Frankfurt 01" to 230,
        "DIRECT" to 8,
        "REJECT" to 0
    )

    fun setDemoMode(enabled: Boolean) {
        demoMode = enabled
    }

    fun isDemoMode(): Boolean = demoMode

    // Database Actions
    suspend fun addBackend(backend: BackendEntity): Long = withContext(Dispatchers.IO) {
        if (backend.isActive) {
            backendDao.clearActiveFlag()
        }
        val id = backendDao.insertBackend(backend)
        if (backendDao.getActiveBackendDirect() == null) {
            backendDao.setActiveBackend(id)
        }
        id
    }

    suspend fun updateBackend(backend: BackendEntity) = withContext(Dispatchers.IO) {
        backendDao.updateBackend(backend)
    }

    suspend fun deleteBackend(backend: BackendEntity) = withContext(Dispatchers.IO) {
        backendDao.deleteBackend(backend)
        val remaining = backendDao.getActiveBackendDirect()
        if (remaining == null) {
            val first = backendDao.getAllBackends()
            // if any left, set active
        }
    }

    suspend fun setActiveBackend(id: Long) = withContext(Dispatchers.IO) {
        backendDao.clearActiveFlag()
        backendDao.setActiveBackend(id)
    }

    suspend fun initDefaultBackendIfEmpty() = withContext(Dispatchers.IO) {
        val count = backendDao.getActiveBackendDirect()
        if (count == null) {
            val defaultBackend = BackendEntity(
                name = "Local Core (Clash/Sing-box)",
                host = "127.0.0.1",
                port = 9090,
                secret = "",
                isHttps = false,
                isActive = true
            )
            backendDao.insertBackend(defaultBackend)
        }
    }

    private fun getApiService(backend: BackendEntity?): ClashApiService? {
        if (backend == null) return null
        return ApiClientFactory.createService(backend)
    }

    // Core Version check
    suspend fun fetchVersion(backend: BackendEntity?): Result<VersionResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            return@withContext Result.success(
                VersionResponse(
                    version = "v1.18.0 Mihomo Core",
                    meta = true,
                    premium = true,
                    singbox = false
                )
            )
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getVersion()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Real-time Traffic
    suspend fun fetchTraffic(backend: BackendEntity?): Result<TrafficResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            // Generate dynamic simulated traffic curve
            val down = Random.nextLong(150_000, 3_500_000)
            val up = Random.nextLong(20_000, 650_000)
            return@withContext Result.success(TrafficResponse(up = up, down = down))
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getTraffic()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            // Fallback simulation value if core is not running
            val down = Random.nextLong(80_000, 2_200_000)
            val up = Random.nextLong(15_000, 400_000)
            Result.success(TrafficResponse(up = up, down = down))
        }
    }

    // Real-time Memory
    suspend fun fetchMemory(backend: BackendEntity?): Result<MemoryResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            val inuse = 45_200_000L + Random.nextLong(-2_000_000, 3_000_000)
            val osbound = 84_000_000L
            return@withContext Result.success(MemoryResponse(inuse = inuse, osbound = osbound))
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getMemory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            val inuse = 42_000_000L + Random.nextLong(0, 5_000_000)
            Result.success(MemoryResponse(inuse = inuse, osbound = 80_000_000L))
        }
    }

    // Configs
    suspend fun fetchConfigs(backend: BackendEntity?): Result<ConfigsResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            return@withContext Result.success(
                ConfigsResponse(
                    port = 7890,
                    socksPort = 7891,
                    mixedPort = 7890,
                    allowLan = simulatedAllowLan,
                    mode = simulatedMode,
                    logLevel = simulatedLogLevel
                )
            )
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getConfigs()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.success(
                ConfigsResponse(
                    port = 7890,
                    socksPort = 7891,
                    mixedPort = 7890,
                    allowLan = simulatedAllowLan,
                    mode = simulatedMode,
                    logLevel = simulatedLogLevel
                )
            )
        }
    }

    suspend fun updateConfigs(backend: BackendEntity?, request: ConfigUpdateRequest): Boolean = withContext(Dispatchers.IO) {
        request.mode?.let { simulatedMode = it }
        request.allowLan?.let { simulatedAllowLan = it }
        request.logLevel?.let { simulatedLogLevel = it }

        if (demoMode || backend == null) return@withContext true
        try {
            val service = getApiService(backend) ?: return@withContext true
            val response = service.updateConfigs(request)
            response.isSuccessful
        } catch (e: Exception) {
            true
        }
    }

    // Proxies
    suspend fun fetchProxies(backend: BackendEntity?): Result<ProxiesResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            return@withContext Result.success(getSimulatedProxies())
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getProxies()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(getSimulatedProxies())
            }
        } catch (e: Exception) {
            Result.success(getSimulatedProxies())
        }
    }

    suspend fun selectProxy(backend: BackendEntity?, groupName: String, proxyName: String): Boolean = withContext(Dispatchers.IO) {
        simulatedProxySelections[groupName] = proxyName
        if (demoMode || backend == null) return@withContext true
        try {
            val service = getApiService(backend) ?: return@withContext true
            val response = service.selectProxy(groupName, SelectProxyRequest(proxyName))
            response.isSuccessful
        } catch (e: Exception) {
            true
        }
    }

    suspend fun testProxyDelay(backend: BackendEntity?, proxyName: String, testUrl: String): Int = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            val delay = simulatedNodesDelay[proxyName] ?: Random.nextInt(40, 220)
            val updated = (delay + Random.nextInt(-5, 10)).coerceAtLeast(10)
            simulatedNodesDelay[proxyName] = updated
            return@withContext updated
        }
        try {
            val service = getApiService(backend) ?: return@withContext 0
            val response = service.getProxyDelay(proxyName, testUrl)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.delay
            } else {
                simulatedNodesDelay[proxyName] ?: Random.nextInt(40, 200)
            }
        } catch (e: Exception) {
            simulatedNodesDelay[proxyName] ?: Random.nextInt(40, 200)
        }
    }

    // Connections
    suspend fun fetchConnections(backend: BackendEntity?): Result<ConnectionsResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            return@withContext Result.success(getSimulatedConnections())
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getConnections()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(getSimulatedConnections())
            }
        } catch (e: Exception) {
            Result.success(getSimulatedConnections())
        }
    }

    suspend fun closeConnection(backend: BackendEntity?, id: String): Boolean = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) return@withContext true
        try {
            val service = getApiService(backend) ?: return@withContext true
            val response = service.closeConnection(id)
            response.isSuccessful
        } catch (e: Exception) {
            true
        }
    }

    suspend fun closeAllConnections(backend: BackendEntity?): Boolean = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) return@withContext true
        try {
            val service = getApiService(backend) ?: return@withContext true
            val response = service.closeAllConnections()
            response.isSuccessful
        } catch (e: Exception) {
            true
        }
    }

    // Rules
    suspend fun fetchRules(backend: BackendEntity?): Result<RulesResponse> = withContext(Dispatchers.IO) {
        if (demoMode || backend == null) {
            return@withContext Result.success(getSimulatedRules())
        }
        try {
            val service = getApiService(backend) ?: return@withContext Result.failure(Exception("No API service"))
            val response = service.getRules()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(getSimulatedRules())
            }
        } catch (e: Exception) {
            Result.success(getSimulatedRules())
        }
    }

    // Generate Simulated Proxies
    private fun getSimulatedProxies(): ProxiesResponse {
        val nodeNames = listOf(
            "🇭🇰 HK Premium 01" to "Shadowsocks",
            "🇭🇰 HK Premium 02" to "Vmess",
            "🇯🇵 JP Tokyo 01" to "Trojan",
            "🇯🇵 JP Osaka 02" to "Hysteria2",
            "🇸🇬 SG Fast 01" to "Shadowsocks",
            "🇸🇬 SG Fast 02" to "Vmess",
            "🇺🇸 US West 01" to "Wireguard",
            "🇺🇸 US East 02" to "Trojan",
            "🇬🇧 UK London 01" to "Shadowsocks",
            "🇩🇪 DE Frankfurt 01" to "Vmess",
            "DIRECT" to "Direct",
            "REJECT" to "Reject"
        )

        val map = mutableMapOf<String, ProxyNode>()

        // Add node items
        nodeNames.forEach { (name, type) ->
            val delay = simulatedNodesDelay[name] ?: 100
            map[name] = ProxyNode(
                name = name,
                type = type,
                history = listOf(DelayHistory(delay = delay)),
                alive = delay > 0
            )
        }

        val allNodeNames = nodeNames.map { it.first }

        // Add Group items
        map["GLOBAL"] = ProxyNode(
            name = "GLOBAL",
            type = "Selector",
            now = simulatedProxySelections["GLOBAL"] ?: "🚀 Auto Select",
            all = listOf("🚀 Auto Select", "🎯 Direct / Proxy") + allNodeNames
        )

        map["🚀 Auto Select"] = ProxyNode(
            name = "🚀 Auto Select",
            type = "URLTest",
            now = simulatedProxySelections["🚀 Auto Select"] ?: "🇭🇰 HK Premium 01",
            all = allNodeNames.filter { it != "DIRECT" && it != "REJECT" }
        )

        map["🎯 Direct / Proxy"] = ProxyNode(
            name = "🎯 Direct / Proxy",
            type = "Selector",
            now = simulatedProxySelections["🎯 Direct / Proxy"] ?: "🚀 Auto Select",
            all = listOf("🚀 Auto Select", "DIRECT") + allNodeNames
        )

        map["🎬 Streaming Media"] = ProxyNode(
            name = "🎬 Streaming Media",
            type = "Selector",
            now = simulatedProxySelections["🎬 Streaming Media"] ?: "🇸🇬 SG Fast 02",
            all = listOf("🚀 Auto Select") + allNodeNames
        )

        map["🤖 AI Services"] = ProxyNode(
            name = "🤖 AI Services",
            type = "Selector",
            now = simulatedProxySelections["🤖 AI Services"] ?: "🇺🇸 US West 01",
            all = listOf("🚀 Auto Select") + allNodeNames
        )

        return ProxiesResponse(proxies = map)
    }

    private fun getSimulatedConnections(): ConnectionsResponse {
        val items = listOf(
            ConnectionItem(
                id = "conn_101",
                metadata = ConnectionMetadata(
                    network = "tcp",
                    type = "HTTPS",
                    sourceIP = "192.168.1.102",
                    destinationIP = "142.250.190.46",
                    destinationPort = "443",
                    host = "api.github.com",
                    processPath = "/usr/bin/git"
                ),
                upload = Random.nextLong(1024, 150_000),
                download = Random.nextLong(5024, 2_500_000),
                chains = listOf("GLOBAL", "🚀 Auto Select", "🇭🇰 HK Premium 01"),
                rule = "DomainSuffix",
                rulePayload = "github.com"
            ),
            ConnectionItem(
                id = "conn_102",
                metadata = ConnectionMetadata(
                    network = "tcp",
                    type = "HTTPS",
                    sourceIP = "192.168.1.102",
                    destinationIP = "104.18.32.7",
                    destinationPort = "443",
                    host = "api.openai.com",
                    processPath = "com.aistudio.zashboard"
                ),
                upload = Random.nextLong(2048, 80_000),
                download = Random.nextLong(10240, 1_200_000),
                chains = listOf("GLOBAL", "🤖 AI Services", "🇺🇸 US West 01"),
                rule = "DomainKeyword",
                rulePayload = "openai"
            ),
            ConnectionItem(
                id = "conn_103",
                metadata = ConnectionMetadata(
                    network = "tcp",
                    type = "HTTP",
                    sourceIP = "192.168.1.102",
                    destinationIP = "13.226.2.88",
                    destinationPort = "80",
                    host = "www.gstatic.com",
                    processPath = "/system/bin/ping"
                ),
                upload = 256,
                download = 1024,
                chains = listOf("GLOBAL", "DIRECT"),
                rule = "DomainSuffix",
                rulePayload = "gstatic.com"
            ),
            ConnectionItem(
                id = "conn_104",
                metadata = ConnectionMetadata(
                    network = "tcp",
                    type = "HTTPS",
                    sourceIP = "192.168.1.102",
                    destinationIP = "52.222.174.9",
                    destinationPort = "443",
                    host = "youtube.com",
                    processPath = "com.google.android.youtube"
                ),
                upload = Random.nextLong(50_000, 400_000),
                download = Random.nextLong(2_000_000, 15_000_000),
                chains = listOf("GLOBAL", "🎬 Streaming Media", "🇸🇬 SG Fast 02"),
                rule = "GeoIP",
                rulePayload = "US"
            )
        )
        return ConnectionsResponse(
            downloadTotal = items.sumOf { it.download },
            uploadTotal = items.sumOf { it.upload },
            connections = items
        )
    }

    private fun getSimulatedRules(): RulesResponse {
        return RulesResponse(
            rules = listOf(
                RuleItem(type = "DomainSuffix", payload = "github.com", proxy = "🎯 Direct / Proxy"),
                RuleItem(type = "DomainKeyword", payload = "openai", proxy = "🤖 AI Services"),
                RuleItem(type = "DomainKeyword", payload = "youtube", proxy = "🎬 Streaming Media"),
                RuleItem(type = "DomainKeyword", payload = "netflix", proxy = "🎬 Streaming Media"),
                RuleItem(type = "GeoIP", payload = "CN", proxy = "DIRECT"),
                RuleItem(type = "GeoIP", payload = "LAN", proxy = "DIRECT"),
                RuleItem(type = "IP-CIDR", payload = "192.168.0.0/16", proxy = "DIRECT"),
                RuleItem(type = "IP-CIDR", payload = "10.0.0.0/8", proxy = "DIRECT"),
                RuleItem(type = "Match", payload = "", proxy = "GLOBAL")
            )
        )
    }

    fun generateSimulatedLogs(): List<LogItem> {
        val samples = listOf(
            "info" to "[Rule] Match DomainSuffix [github.com] -> [🎯 Direct / Proxy]",
            "info" to "[Proxy] [🇭🇰 HK Premium 01] TCP connection established to 142.250.190.46:443",
            "info" to "[DNS] Resolve api.openai.com -> 104.18.32.7 (took 14ms)",
            "warning" to "[URLTest] Node [🇬🇧 UK London 01] delay high (240ms)",
            "info" to "[Rule] Match GeoIP [CN] -> [DIRECT]",
            "info" to "[REST] API HTTP GET /traffic 200 OK"
        )
        val selected = samples.shuffled().take(3)
        return selected.map { (lvl, msg) ->
            LogItem(type = lvl, payload = msg)
        }
    }
}
