package com.tommy.digitalbankkyc.domain.usecase

import com.tommy.digitalbankkyc.domain.repository.CustomerRepository
import javax.inject.Inject

class RefreshCustomersUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(force: Boolean = false) = repository.refreshCustomers(force)
}
