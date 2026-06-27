package com.tommy.digitalbankkyc.presentation.viewmodel

import com.tommy.digitalbankkyc.domain.model.PermissionUiState

data class CameraUiState(
    val permissionState: PermissionUiState = PermissionUiState.UNKNOWN,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
