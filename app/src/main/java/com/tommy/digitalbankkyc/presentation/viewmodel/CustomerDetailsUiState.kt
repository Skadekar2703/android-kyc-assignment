package com.tommy.digitalbankkyc.presentation.viewmodel

import com.tommy.digitalbankkyc.domain.model.BankInfo
import com.tommy.digitalbankkyc.domain.model.Customer

data class CustomerDetailsUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val customer: Customer? = null,
    val bankInfo: BankInfo? = null,
    val bankLoading: Boolean = false,
    val bankError: String? = null
)
