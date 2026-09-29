package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.OfflinePin
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.AdvisoryViewModel
import com.example.myapplication.ui.screens.DataSourcingGuideScreen
import com.example.myapplication.ui.screens.FeasibilityReportScreen
import com.example.myapplication.ui.screens.FinancialPlanScreen
import com.example.myapplication.ui.screens.LoanRoadmapScreen
import com.example.myapplication.ui.screens.OnboardingScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RuralEnterpriseAdvisoryApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuralEnterpriseAdvisoryApp(
    viewModel: AdvisoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabTitles = listOf("Onboarding", "Feasibility", "Financial Plan", "Loan Roadmap", "Data Sourcing")
    val tabIcons = listOf(
        Icons.Rounded.EditNote,
        Icons.Rounded.Assessment,
        Icons.Rounded.Calculate,
        Icons.Rounded.AssignmentTurnedIn,
        Icons.Rounded.Storage
    )

    fun shareWhatsApp() {
        val shareText = viewModel.getWhatsAppShareText()
        val whatsappIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        try {
            whatsappIntent.setPackage("com.whatsapp")
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            val chooserIntent = Intent.createChooser(whatsappIntent, "Share Summary via")
            context.startActivity(chooserIntent)
        }
    }

    fun downloadDprReport() {
        val dprText = viewModel.exportDprSummaryText()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, dprText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Download Detailed Project Report (DPR)"))
        Toast.makeText(context, "DPR Document Ready for Download / Save", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "GramAdvisor",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "SIH 2026",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Text(
                                text = "${uiState.userInputs.businessName} • ${uiState.userInputs.location.village} (LGD: ${uiState.userInputs.location.lgdVillageCode})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { shareWhatsApp() }) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = "Share via WhatsApp"
                            )
                        }
                        IconButton(onClick = { downloadDprReport() }) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = "Download DPR"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Offline Sync Connectivity Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.OfflinePin,
                        contentDescription = "Offline Sync Status",
                        tint = Color(0xFF2E7D32)
                    )
                    Text(
                        text = uiState.offlineStatusMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTabIndex > 0) {
                FloatingActionButton(
                    onClick = { shareWhatsApp() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "WhatsApp Share"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Navigation Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if (selectedTabIndex != index) {
                                viewModel.stopSpeaking()
                            }
                            selectedTabIndex = index
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tabIcons[index],
                                contentDescription = title
                            )
                        }
                    )
                }
            }

            // Tab Content Body
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> OnboardingScreen(
                        uiState = uiState,
                        onUpdateInputs = { newInputs ->
                            viewModel.updateUserInputs(newInputs)
                        },
                        onNavigateToFeasibility = {
                            viewModel.recalculate()
                            selectedTabIndex = 1
                        },
                        onSpeakText = { text ->
                            viewModel.speakText(context, text)
                        },
                        onStopSpeaking = {
                            viewModel.stopSpeaking()
                        }
                    )
                    1 -> FeasibilityReportScreen(
                        uiState = uiState,
                        onSpeakText = { text ->
                            viewModel.speakText(context, text)
                        },
                        onStopSpeaking = {
                            viewModel.stopSpeaking()
                        }
                    )
                    2 -> FinancialPlanScreen(
                        uiState = uiState,
                        onUpdateMarginCapital = { margin ->
                            viewModel.updateMarginCapital(margin)
                        },
                        onSpeakText = { text ->
                            viewModel.speakText(context, text)
                        },
                        onStopSpeaking = {
                            viewModel.stopSpeaking()
                        }
                    )
                    3 -> LoanRoadmapScreen(
                        uiState = uiState,
                        onToggleMilestone = { step ->
                            viewModel.toggleRoadmapStep(step)
                        },
                        onExportDpr = {
                            downloadDprReport()
                        }
                    )
                    4 -> DataSourcingGuideScreen(
                        uiState = uiState
                    )
                }
            }
        }
    }
}
