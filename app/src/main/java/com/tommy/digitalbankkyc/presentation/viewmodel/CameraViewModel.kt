package com.tommy.digitalbankkyc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.digitalbankkyc.domain.model.PermissionUiState
import com.tommy.digitalbankkyc.domain.usecase.VerifyCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val verifyCustomerUseCase: VerifyCustomerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onPermissionResolved(granted: Boolean, permanentlyDenied: Boolean) {
        _uiState.update {
            it.copy(
                permissionState = when {
                    granted -> PermissionUiState.GRANTED
                    permanentlyDenied -> PermissionUiState.PERMANENTLY_DENIED
                    else -> PermissionUiState.DENIED
                }
            )
        }
    }

    fun onImageSaved(customerId: Int, selfiePath: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = verifyCustomerUseCase(customerId, selfiePath)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
            if (result.isSuccess) onComplete()
        }
    }

    fun onCaptureError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}
