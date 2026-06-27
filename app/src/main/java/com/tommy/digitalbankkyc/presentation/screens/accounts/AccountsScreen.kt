package com.tommy.digitalbankkyc.presentation.screens.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.digitalbankkyc.domain.model.CustomerTab
import com.tommy.digitalbankkyc.presentation.components.CustomerCard
import com.tommy.digitalbankkyc.presentation.components.EmptyState
import com.tommy.digitalbankkyc.presentation.components.ErrorState
import com.tommy.digitalbankkyc.presentation.components.LoadingState
import com.tommy.digitalbankkyc.presentation.components.SearchField
import com.tommy.digitalbankkyc.presentation.viewmodel.AccountsViewModel

@Composable
fun AccountsRoute(
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onCustomerSelected: (Int) -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AccountsScreen(
        uiState = uiState,
        darkTheme = darkTheme,
        onToggleDarkTheme = onToggleDarkTheme,
        onRetry = { viewModel.refreshCustomers(force = true) },
        onQueryChanged = viewModel::onQueryChanged,
        onTabSelected = viewModel::onTabSelected,
        onCustomerSelected = onCustomerSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountsScreen(
    uiState: com.tommy.digitalbankkyc.presentation.viewmodel.AccountsUiState,
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onRetry: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onTabSelected: (CustomerTab) -> Unit,
    onCustomerSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "RM KYC DESK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = onToggleDarkTheme) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Toggle theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            WelcomeCard()
            Spacer(modifier = Modifier.height(16.dp))
            SearchField(query = uiState.query, onQueryChanged = onQueryChanged)
            Spacer(modifier = Modifier.height(16.dp))
            CapsuleTabRow(selectedTab = uiState.selectedTab, onTabSelected = onTabSelected)
            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading && uiState.customers.isEmpty() -> {
                    LoadingState(
                        modifier = Modifier.fillMaxWidth(),
                        message = "Loading customer accounts..."
                    )
                }

                uiState.errorMessage != null && uiState.customers.isEmpty() && uiState.isOffline -> {
                    ErrorState(
                        title = "You're offline",
                        subtitle = "Unable to fetch customers right now. Connect to the internet or retry to use a cached copy.",
                        actionLabel = "Retry",
                        onAction = onRetry
                    )
                }

                uiState.errorMessage != null && uiState.customers.isEmpty() -> {
                    ErrorState(
                        title = "Unable to load customers",
                        subtitle = uiState.errorMessage,
                        actionLabel = "Retry",
                        onAction = onRetry
                    )
                }

                uiState.filteredCustomers.isEmpty() -> {
                    EmptyState(
                        title = "No customers found",
                        subtitle = if (uiState.query.isBlank()) {
                            "There are no ${uiState.selectedTab.title.lowercase()} KYC customers right now."
                        } else {
                            "Try another name or account number."
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.isOffline) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        text = "Offline Mode: Showing cached data from database.",
                                        color = MaterialTheme.colorScheme.secondary,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        items(uiState.filteredCustomers, key = { it.id }) { customer ->
                            CustomerCard(
                                customer = customer,
                                onViewDetails = onCustomerSelected
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Welcome, Relationship Manager",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Use this portal to review customer details, perform KYC checks via selfie scanning, and resolve branch routing details.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CapsuleTabRow(
    selectedTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomerTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val tabBg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
            val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(tabBg)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}


