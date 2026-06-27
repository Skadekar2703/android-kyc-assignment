package com.tommy.digitalbankkyc.presentation.screens.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.digitalbankkyc.domain.model.PermissionUiState
import com.tommy.digitalbankkyc.presentation.components.ErrorState
import com.tommy.digitalbankkyc.presentation.components.LoadingState
import com.tommy.digitalbankkyc.presentation.ui.theme.Mint
import com.tommy.digitalbankkyc.presentation.viewmodel.CameraViewModel
import com.tommy.digitalbankkyc.utils.createSelfieFile
import com.tommy.digitalbankkyc.utils.findActivity

@Composable
fun CameraRoute(
    customerId: Int,
    onCompleted: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CameraScreen(
        customerId = customerId,
        uiState = uiState,
        onCompleted = onCompleted,
        onImageSaved = viewModel::onImageSaved,
        onPermissionResolved = viewModel::onPermissionResolved,
        onCaptureError = viewModel::onCaptureError
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CameraScreen(
    customerId: Int,
    uiState: com.tommy.digitalbankkyc.presentation.viewmodel.CameraUiState,
    onCompleted: () -> Unit,
    onImageSaved: (Int, String, () -> Unit) -> Unit,
    onPermissionResolved: (Boolean, Boolean) -> Unit,
    onCaptureError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context.findActivity()
    var requestedOnce by rememberSaveable { mutableStateOf(false) }
    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            setEnabledUseCases(androidx.camera.view.CameraController.IMAGE_CAPTURE)
        }
    }

    DisposableEffect(controller, lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose {
            controller.unbind()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val permanentlyDenied = activity?.let {
            !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
        } == true && !granted
        onPermissionResolved(granted, permanentlyDenied)
    }

    LaunchedEffect(Unit) {
        val granted = PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PermissionChecker.PERMISSION_GRANTED
        if (granted) {
            onPermissionResolved(true, false)
        } else if (!requestedOnce) {
            requestedOnce = true
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capture KYC Selfie", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCompleted) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        when (uiState.permissionState) {
            PermissionUiState.GRANTED -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(innerPadding)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { previewContext ->
                            PreviewView(previewContext).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                this.controller = controller
                            }
                        }
                    )
                    
                    FaceScannerOverlay()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Position your face in the target area",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    if (uiState.isSaving) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    LargeFloatingActionButton(
                        onClick = {
                            val outputFile = createSelfieFile(context, customerId)
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
                            controller.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(
                                        outputFileResults: ImageCapture.OutputFileResults
                                    ) {
                                        onImageSaved(customerId, outputFile.absolutePath, onCompleted)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        onCaptureError(exception.message ?: "Unable to capture selfie.")
                                    }
                                }
                            )
                        },
                        shape = CircleShape,
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = "Capture selfie",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            PermissionUiState.PERMANENTLY_DENIED -> {
                ErrorState(
                    title = "Camera permission blocked",
                    subtitle = "Open app settings and allow camera access to complete the KYC selfie step.",
                    actionLabel = "Open settings",
                    onAction = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            PermissionUiState.DENIED -> {
                ErrorState(
                    title = "Camera permission needed",
                    subtitle = "We need camera access to capture a live selfie for KYC.",
                    actionLabel = "Grant permission",
                    onAction = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            PermissionUiState.UNKNOWN -> LoadingState(
                modifier = Modifier.padding(innerPadding),
                message = "Preparing camera..."
            )
        }
    }
}

@Composable
private fun FaceScannerOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        // Circular cutout size (70% of screen width)
        val diameter = canvasWidth * 0.7f
        val radius = diameter / 2f
        // Position circle in the center of preview view
        val center = androidx.compose.ui.geometry.Offset(canvasWidth / 2f, canvasHeight / 2.2f)

        val path = Path().apply {
            addOval(Rect(center, radius))
        }

        // Draw translucent overlay outside circle cutout
        clipPath(path = path, clipOp = ClipOp.Difference) {
            drawRect(
                color = Color.Black.copy(alpha = 0.65f),
                size = Size(canvasWidth, canvasHeight)
            )
        }

        // Draw scanner target border
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = radius,
            center = center,
            style = Stroke(
                width = 3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f), 0f)
            )
        )
    }
}
