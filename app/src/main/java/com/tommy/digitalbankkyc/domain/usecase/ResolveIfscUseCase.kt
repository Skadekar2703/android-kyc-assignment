package com.tommy.digitalbankkyc.domain.usecase

import com.tommy.digitalbankkyc.domain.repository.CustomerRepository
import javax.inject.Inject

class ResolveIfscUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(ifscCode: String) = repository.resolveIfsc(ifscCode)
}
