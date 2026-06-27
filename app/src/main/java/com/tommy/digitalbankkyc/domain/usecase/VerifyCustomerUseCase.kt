package com.tommy.digitalbankkyc.domain.usecase

import com.tommy.digitalbankkyc.domain.repository.CustomerRepository
import javax.inject.Inject

class VerifyCustomerUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(customerId: Int, selfiePath: String) =
        repository.updateCustomerVerification(customerId, selfiePath)
}
