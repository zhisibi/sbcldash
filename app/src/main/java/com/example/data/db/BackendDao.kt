package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BackendDao {
    @Query("SELECT * FROM backends ORDER BY id ASC")
    fun getAllBackends(): Flow<List<BackendEntity>>

    @Query("SELECT * FROM backends WHERE isActive = 1 LIMIT 1")
    fun getActiveBackend(): Flow<BackendEntity?>

    @Query("SELECT * FROM backends WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveBackendDirect(): BackendEntity?

    @Query("SELECT * FROM backends WHERE id = :id")
    suspend fun getBackendById(id: Long): BackendEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackend(backend: BackendEntity): Long

    @Update
    suspend fun updateBackend(backend: BackendEntity)

    @Delete
    suspend fun deleteBackend(backend: BackendEntity)

    @Query("UPDATE backends SET isActive = 0")
    suspend fun clearActiveFlag()

    @Query("UPDATE backends SET isActive = (CASE WHEN id = :activeId THEN 1 ELSE 0 END)")
    suspend fun setActiveBackend(activeId: Long)
}
