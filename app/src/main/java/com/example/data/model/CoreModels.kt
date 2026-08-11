package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VersionResponse(
    @Json(name = "version") val version: String? = null,
    @Json(name = "meta") val meta: Boolean? = false,
    @Json(name = "premium") val premium: Boolean? = false,
    @Json(name = "singbox") val singbox: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class TrafficResponse(
    @Json(name = "up") val up: Long = 0,
    @Json(name = "down") val down: Long = 0
)

@JsonClass(generateAdapter = true)
data class MemoryResponse(
    @Json(name = "inuse") val inuse: Long = 0,
    @Json(name = "osbound") val osbound: Long = 0
)

@JsonClass(generateAdapter = true)
data class ConfigsResponse(
    @Json(name = "port") val port: Int = 0,
    @Json(name = "socks-port") val socksPort: Int = 0,
    @Json(name = "redir-port") val redirPort: Int = 0,
    @Json(name = "tproxy-port") val tproxyPort: Int = 0,
    @Json(name = "mixed-port") val mixedPort: Int = 0,
    @Json(name = "allow-lan") val allowLan: Boolean = false,
    @Json(name = "mode") val mode: String = "rule",
    @Json(name = "log-level") val logLevel: String = "info"
)

@JsonClass(generateAdapter = true)
data class ConfigUpdateRequest(
    @Json(name = "mode") val mode: String? = null,
    @Json(name = "allow-lan") val allowLan: Boolean? = null,
    @Json(name = "log-level") val logLevel: String? = null
)

@JsonClass(generateAdapter = true)
data class DelayHistory(
    @Json(name = "time") val time: String? = null,
    @Json(name = "delay") val delay: Int = 0
)

@JsonClass(generateAdapter = true)
data class ProxyNode(
    @Json(name = "name") val name: String = "",
    @Json(name = "type") val type: String = "Unknown",
    @Json(name = "now") val now: String? = null,
    @Json(name = "all") val all: List<String>? = null,
    @Json(name = "history") val history: List<DelayHistory>? = null,
    @Json(name = "udp") val udp: Boolean? = null,
    @Json(name = "xudp") val xudp: Boolean? = null,
    @Json(name = "alive") val alive: Boolean? = true
)

@JsonClass(generateAdapter = true)
data class ProxiesResponse(
    @Json(name = "proxies") val proxies: Map<String, ProxyNode> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class SelectProxyRequest(
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class DelayResponse(
    @Json(name = "delay") val delay: Int = 0,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ConnectionMetadata(
    @Json(name = "network") val network: String? = "tcp",
    @Json(name = "type") val type: String? = "HTTP",
    @Json(name = "sourceIP") val sourceIP: String? = "",
    @Json(name = "destinationIP") val destinationIP: String? = "",
    @Json(name = "destinationPort") val destinationPort: String? = "",
    @Json(name = "host") val host: String? = "",
    @Json(name = "processPath") val processPath: String? = null
)

@JsonClass(generateAdapter = true)
data class ConnectionItem(
    @Json(name = "id") val id: String,
    @Json(name = "metadata") val metadata: ConnectionMetadata,
    @Json(name = "upload") val upload: Long = 0,
    @Json(name = "download") val download: Long = 0,
    @Json(name = "start") val start: String? = null,
    @Json(name = "chains") val chains: List<String> = emptyList(),
    @Json(name = "rule") val rule: String? = null,
    @Json(name = "rulePayload") val rulePayload: String? = null
)

@JsonClass(generateAdapter = true)
data class ConnectionsResponse(
    @Json(name = "downloadTotal") val downloadTotal: Long = 0,
    @Json(name = "uploadTotal") val uploadTotal: Long = 0,
    @Json(name = "connections") val connections: List<ConnectionItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RuleItem(
    @Json(name = "type") val type: String = "",
    @Json(name = "payload") val payload: String = "",
    @Json(name = "proxy") val proxy: String = ""
)

@JsonClass(generateAdapter = true)
data class RulesResponse(
    @Json(name = "rules") val rules: List<RuleItem> = emptyList()
)

data class LogItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)
