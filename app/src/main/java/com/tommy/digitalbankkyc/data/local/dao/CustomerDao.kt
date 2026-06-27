package com.tommy.digitalbankkyc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommy.digitalbankkyc.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY first_name ASC, last_name ASC")
    fun observeCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun observeCustomer(customerId: Int): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers")
    suspend fun getCustomers(): List<CustomerEntity>

    @Query("SELECT MAX(updated_at) FROM customers")
    suspend fun getLatestUpdatedAt(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomers(customers: List<CustomerEntity>)

    @Query("UPDATE customers SET verified = 1, selfie_path = :selfiePath WHERE id = :customerId")
    suspend fun updateVerification(customerId: Int, selfiePath: String)
}
