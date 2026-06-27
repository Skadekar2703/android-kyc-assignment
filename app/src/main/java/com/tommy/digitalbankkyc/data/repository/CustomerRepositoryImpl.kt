package com.tommy.digitalbankkyc.data.repository

import androidx.room.withTransaction
import com.tommy.digitalbankkyc.data.local.AppDatabase
import com.tommy.digitalbankkyc.data.local.dao.CustomerDao
import com.tommy.digitalbankkyc.data.mapper.toDomain
import com.tommy.digitalbankkyc.data.mapper.toEntity
import com.tommy.digitalbankkyc.data.remote.api.DummyJsonApiService
import com.tommy.digitalbankkyc.data.remote.api.IfscApiService
import com.tommy.digitalbankkyc.domain.model.BankInfo
import com.tommy.digitalbankkyc.domain.model.Customer
import com.tommy.digitalbankkyc.domain.repository.CustomerRepository
import com.tommy.digitalbankkyc.utils.isCacheExpired
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val dummyJsonApiService: DummyJsonApiService,
    private val ifscApiService: IfscApiService,
    private val appDatabase: AppDatabase,
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun observeCustomers(): Flow<List<Customer>> {
        return customerDao.observeCustomers().map { customers -> customers.map { it.toDomain() } }
    }

    override fun observeCustomer(customerId: Int): Flow<Customer?> {
        return customerDao.observeCustomer(customerId).map { it?.toDomain() }
    }

    override suspend fun refreshCustomers(force: Boolean): Result<Unit> = runCatching {
        val cachedCustomers = customerDao.getCustomers()
        val latestUpdatedAt = customerDao.getLatestUpdatedAt()
        val shouldRefresh = force || cachedCustomers.isEmpty() || isCacheExpired(latestUpdatedAt)
        if (!shouldRefresh) return@runCatching

        val syncTime = System.currentTimeMillis()
        val existingCustomers = cachedCustomers.associateBy { it.id }
        val users = dummyJsonApiService.getUsers().users
        val entities = users.map { user ->
            user.toEntity(existingCustomer = existingCustomers[user.id], syncedAt = syncTime)
        }

        appDatabase.withTransaction {
            customerDao.upsertCustomers(entities)
        }
    }

    override suspend fun resolveIfsc(ifscCode: String): Result<BankInfo> = runCatching {
        ifscApiService.getBankInfo(ifscCode).toDomain()
    }

    override suspend fun updateCustomerVerification(customerId: Int, selfiePath: String): Result<Unit> =
        runCatching {
            customerDao.updateVerification(customerId, selfiePath)
        }
}
