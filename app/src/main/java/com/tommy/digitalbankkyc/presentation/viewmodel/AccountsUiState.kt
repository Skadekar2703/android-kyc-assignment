package com.tommy.digitalbankkyc.presentation.viewmodel

import com.tommy.digitalbankkyc.domain.model.Customer
import com.tommy.digitalbankkyc.domain.model.CustomerTab

data class AccountsUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val query: String = "",
    val debouncedQuery: String = "",
    val selectedTab: CustomerTab = CustomerTab.PENDING,
    val customers: List<Customer> = emptyList()
) {
    val filteredCustomers: List<Customer>
        get() {
            val normalizedQuery = debouncedQuery.trim().lowercase()
            return customers
                .filter { customer ->
                    if (selectedTab == CustomerTab.PENDING) !customer.isVerified else customer.isVerified
                }
                .filter { customer ->
                    normalizedQuery.isBlank() ||
                        customer.firstName.lowercase().contains(normalizedQuery) ||
                        customer.lastName.lowercase().contains(normalizedQuery) ||
                        customer.fullName.lowercase().contains(normalizedQuery) ||
                        customer.accountNumber.contains(normalizedQuery)
                }
        }
}
