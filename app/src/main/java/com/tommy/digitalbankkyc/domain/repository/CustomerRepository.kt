package com.tommy.digitalbankkyc.domain.repository

import com.tommy.digitalbankkyc.domain.model.BankInfo
import com.tommy.digitalbankkyc.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun observeCustomers(): Flow<List<Customer>>
    fun observeCustomer(customerId: Int): Flow<Customer?>
    suspend fun refreshCustomers(force: Boolean = false): Result<Unit>
    suspend fun resolveIfsc(ifscCode: String): Result<BankInfo>
    suspend fun updateCustomerVerification(customerId: Int, selfiePath: String): Result<Unit>
}
