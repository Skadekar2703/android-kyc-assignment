package com.tommy.digitalbankkyc.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.digitalbankkyc.domain.usecase.ObserveCustomerUseCase
import com.tommy.digitalbankkyc.domain.usecase.ResolveIfscUseCase
import com.tommy.digitalbankkyc.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCustomerUseCase: ObserveCustomerUseCase,
    private val resolveIfscUseCase: ResolveIfscUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val customerId: Int = checkNotNull(savedStateHandle["customerId"])
    private val bankState = kotlinx.coroutines.flow.MutableStateFlow(CustomerDetailsUiState())

    val uiState: StateFlow<CustomerDetailsUiState> = combine(
        observeCustomerUseCase(customerId),
        bankState,
        networkMonitor.isOnline
    ) { customer, bankUiState, isOnline ->
        bankUiState.copy(
            isLoading = customer == null,
            isOffline = !isOnline,
            customer = customer
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CustomerDetailsUiState()
    )

    init {
        observeCustomerUseCase(customerId)
            .filterNotNull()
            .distinctUntilChanged { old, new -> old.ifscCode == new.ifscCode }
            .onEach { customer ->
                fetchBankInfo(customer.ifscCode)
            }
            .launchIn(viewModelScope)
    }

    fun fetchBankInfo(ifscCode: String) {
        viewModelScope.launch {
            bankState.update { it.copy(bankLoading = true, bankError = null) }
            val result = resolveIfscUseCase(ifscCode)
            bankState.update {
                it.copy(
                    bankLoading = false,
                    bankInfo = result.getOrNull(),
                    bankError = if (result.isFailure) "Unable to fetch bank details." else null
                )
            }
        }
    }
}
