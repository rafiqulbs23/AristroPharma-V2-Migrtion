package com.aristopharma.dev.v2.features.login.data.dataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LoginDao {

    // Observe current login/session row
    @Query("SELECT * FROM login WHERE id = 1 LIMIT 1")
    fun observeLogin(): Flow<LoginEntity?>

    // One-time fetch
    @Query("SELECT * FROM login WHERE id = 1 LIMIT 1")
    suspend fun getLogin(): LoginEntity?

    // Save/replace session (upsert behavior)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(login: LoginEntity)

    // Optional: update (works if row exists)
    @Update
    suspend fun update(login: LoginEntity)

    // Clear login table (logout)
    @Query("DELETE FROM login")
    suspend fun clear()
}
