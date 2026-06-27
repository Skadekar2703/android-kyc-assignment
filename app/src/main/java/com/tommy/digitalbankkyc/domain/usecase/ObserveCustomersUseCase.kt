package com.tommy.digitalbankkyc.domain.usecase

import com.tommy.digitalbankkyc.domain.repository.CustomerRepository
import javax.inject.Inject

class ObserveCustomersUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    operator fun invoke() = repository.observeCustomers()
}
