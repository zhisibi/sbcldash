package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backends")
data class BackendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val host: String,
    val port: Int = 9090,
    val secret: String = "",
    val isHttps: Boolean = false,
    val delayTestUrl: String = "http://www.gstatic.com/generate_204",
    val isActive: Boolean = false
)
