package com.tommy.digitalbankkyc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.digitalbankkyc.domain.model.CustomerTab
import com.tommy.digitalbankkyc.domain.usecase.ObserveCustomersUseCase
import com.tommy.digitalbankkyc.domain.usecase.RefreshCustomersUseCase
import com.tommy.digitalbankkyc.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class AccountsViewModel @Inject constructor(
    observeCustomersUseCase: ObserveCustomersUseCase,
    private val refreshCustomersUseCase: RefreshCustomersUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val loading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow("")
    private val selectedTab = MutableStateFlow(CustomerTab.PENDING)

    private val queryAndDebounced = combine(query, query.debounce(300)) { q, dq -> Pair(q, dq) }

    private val viewState = combine(
        queryAndDebounced,
        selectedTab,
        loading,
        errorMessage,
        networkMonitor.isOnline
    ) { queryPair, currentTab, isLoading, message, isOnline ->
        AccountsUiState(
            isLoading = isLoading,
            isOffline = !isOnline,
            errorMessage = message,
            query = queryPair.first,
            debouncedQuery = queryPair.second,
            selectedTab = currentTab
        )
    }

    val uiState: StateFlow<AccountsUiState> = combine(
        observeCustomersUseCase(),
        viewState
    ) { customers, state ->
        state.copy(customers = customers)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AccountsUiState()
    )

    init {
        refreshCustomers()
    }

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun onTabSelected(tab: CustomerTab) {
        selectedTab.value = tab
    }

    fun refreshCustomers(force: Boolean = false) {
        viewModelScope.launch {
            loading.value = true
            errorMessage.value = null
            val result = refreshCustomersUseCase(force)
            if (result.isFailure) {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Unable to load customers."
            }
            loading.value = false
        }
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}
