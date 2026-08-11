package com.example.data.api

import com.example.data.model.ConfigUpdateRequest
import com.example.data.model.ConfigsResponse
import com.example.data.model.ConnectionsResponse
import com.example.data.model.DelayResponse
import com.example.data.model.MemoryResponse
import com.example.data.model.ProxiesResponse
import com.example.data.model.RulesResponse
import com.example.data.model.SelectProxyRequest
import com.example.data.model.TrafficResponse
import com.example.data.model.VersionResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ClashApiService {

    @GET("version")
    suspend fun getVersion(): Response<VersionResponse>

    @GET("traffic")
    suspend fun getTraffic(): Response<TrafficResponse>

    @GET("memory")
    suspend fun getMemory(): Response<MemoryResponse>

    @GET("configs")
    suspend fun getConfigs(): Response<ConfigsResponse>

    @PATCH("configs")
    suspend fun updateConfigs(
        @Body request: ConfigUpdateRequest
    ): Response<ResponseBody>

    @GET("proxies")
    suspend fun getProxies(): Response<ProxiesResponse>

    @PUT("proxies/{group}")
    suspend fun selectProxy(
        @Path("group") groupName: String,
        @Body request: SelectProxyRequest
    ): Response<ResponseBody>

    @GET("proxies/{name}/delay")
    suspend fun getProxyDelay(
        @Path("name") proxyName: String,
        @Query("url") url: String = "http://www.gstatic.com/generate_204",
        @Query("timeout") timeout: Int = 5000
    ): Response<DelayResponse>

    @GET("group/{name}/delay")
    suspend fun getGroupDelay(
        @Path("name") groupName: String,
        @Query("url") url: String = "http://www.gstatic.com/generate_204",
        @Query("timeout") timeout: Int = 5000
    ): Response<Map<String, Int>>

    @GET("connections")
    suspend fun getConnections(): Response<ConnectionsResponse>

    @DELETE("connections/{id}")
    suspend fun closeConnection(
        @Path("id") id: String
    ): Response<ResponseBody>

    @DELETE("connections")
    suspend fun closeAllConnections(): Response<ResponseBody>

    @GET("rules")
    suspend fun getRules(): Response<RulesResponse>

    @GET("logs")
    @Streaming
    suspend fun getLogsStream(
        @Query("level") level: String = "info"
    ): Response<ResponseBody>
}
