package com.example.myapplication.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.AdvisoryUiState
import java.text.NumberFormat
import java.util.Locale

/**
 * Single milestone step item in the Loan Application Roadmap.
 */
data class RoadmapMilestoneItem(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val estimatedDuration: String,
    val icon: ImageVector,
    val checklistDocs: List<String>,
    val guidanceNotes: String,
    val actionButtonText: String? = null,
    val portalUrl: String? = null
)

/**
 * Actionable Loan Roadmap Tracker Screen featuring 5 step-by-step milestones with completion checkboxes.
 */
@Composable
fun LoanRoadmapScreen(
    uiState: AdvisoryUiState,
    onToggleMilestone: (Int) -> Unit = {},
    onExportDpr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val completedSteps = uiState.completedRoadmapSteps
    val plan = uiState.financialPlan
    val scheme = plan.schemeInfo
    val agency = scheme.nodalAgency
    val userInputs = uiState.userInputs

    val milestones = remember(agency) {
        listOf(
            RoadmapMilestoneItem(
                stepNumber = 1,
                title = "Document Preparation & Eligibility Verification",
                subtitle = "Assemble required KYC, social category certificates, and land/site NOCs",
                estimatedDuration = "1 - 3 Days",
                icon = Icons.Rounded.Description,
                checklistDocs = listOf(
                    "Aadhaar Card & PAN / Voter ID Proof",
                    "Caste / Social Category Certificate (${userInputs.socialCategory.code} proof)",
                    "Gram Panchayat NOC / Trade License Registration",
                    "Bank Account Passbook (Active Savings/Current Account with Aadhaar seeded)",
                    "Income Certificate / Ration Card Proof"
                ),
                guidanceNotes = "Verify that your name on Aadhaar matches bank account details exactly. For ${agency.agencyCode} subvention, ${userInputs.socialCategory.displayName} certificate must be certified by Tehsildar or competent district authority."
            ),
            RoadmapMilestoneItem(
                stepNumber = 2,
                title = "DPR & Hyper-Local Feasibility Report Generation",
                subtitle = "Generate formal Detailed Project Report with Cost Breakdown & Amortization Schedule",
                estimatedDuration = "Instant (Auto-Generated)",
                icon = Icons.Rounded.AssignmentTurnedIn,
                checklistDocs = listOf(
                    "Project Capital Cost Allocation (CapEx, Raw Materials, Working Capital)",
                    "Market Feasibility Score (${uiState.feasibilityReport.overallFeasibilityScore}/10.0)",
                    "10km Radius Population Reach Estimate (${uiState.feasibilityReport.marketReach.estimatedPopulationReach} residents)",
                    "Quarterly Repayment Schedule (${scheme.tenureYears} Years, ${scheme.moratoriumMonths}m Moratorium)",
                    "Financial Break-Even Horizon (${uiState.feasibilityReport.pricingAndValue.BreakEvenPointMonths} Months)"
                ),
                guidanceNotes = "This app auto-generates your official DPR. Click 'Share / Download DPR Summary' below to export the formatted text or share directly with your District Nodal Officer.",
                actionButtonText = "Share / Download DPR Summary"
            ),
            RoadmapMilestoneItem(
                stepNumber = 3,
                title = "Channelizing Agency Portal Submission (${agency.agencyCode})",
                subtitle = "Submit online application on ${agency.agencyCode} portal or via District Nodal Office",
                estimatedDuration = "3 - 7 Days",
                icon = Icons.Rounded.AccountBalance,
                checklistDocs = listOf(
                    "Online Application Submission on ${agency.websiteUrl}",
                    "Upload DPR, Aadhaar, Caste Certificate & Gram Panchayat NOC",
                    "Select Preferred Partner Bank Branch (${userInputs.location.district} District)",
                    "Acknowledgement Receipt & Application Tracking ID Generation"
                ),
                guidanceNotes = "Submit your application on ${agency.agencyFullName} (${agency.agencyCode}) portal. You can also visit your District Collectorate / DIC Office to submit physical copies to the ${agency.agencyCode} District Manager.",
                actionButtonText = "Visit ${agency.agencyCode} Portal",
                portalUrl = agency.websiteUrl
            ),
            RoadmapMilestoneItem(
                stepNumber = 4,
                title = "Bank Credit Sanction & Concessional Loan Disbursement",
                subtitle = "Bank appraisal, site verification, subvention agreement signing & funds release",
                estimatedDuration = "7 - 14 Days",
                icon = Icons.Rounded.Verified,
                checklistDocs = listOf(
                    "Lead Bank / Regional Rural Bank (RRB) Branch Manager Site Appraisal",
                    "Credit Sanction Letter Issuance (Loan Amount: ₹${formatCurrency(plan.maxLoanAmount)})",
                    "Promoter Margin Capital Deposit (10%: ₹${formatCurrency(plan.availableMarginCapital)})",
                    "Interest Subvention Agreement Signing (${scheme.interestRatePerAnnum}% p.a. Concessional Rate)",
                    "Direct Account Disbursement of Loan Capital"
                ),
                guidanceNotes = "During bank manager inspection, present your Gram Panchayat NOC and DPR. Highlight that ${agency.agencyCode} provides concessional interest subvention, reducing credit risk."
            ),
            RoadmapMilestoneItem(
                stepNumber = 5,
                title = "Business Operations Kickoff & Moratorium Tracking",
                subtitle = "Machinery acquisition, raw material procurement, trial runs & moratorium management",
                estimatedDuration = "Ongoing (${scheme.moratoriumMonths}m Moratorium)",
                icon = Icons.Rounded.BusinessCenter,
                checklistDocs = listOf(
                    "Machinery & Equipment Acquisition from Verified Suppliers",
                    "Initial Raw Material Sourcing & Stocking",
                    "Trial Production Run & Quality Checks",
                    "Moratorium Period Tracking (Interest-only payment for first ${scheme.moratoriumQuarters} Quarter(s))",
                    "Transition to Post-Moratorium EMI (₹${formatCurrency(plan.quarterlyEmiPostMoratorium)}/Qtr)"
                ),
                guidanceNotes = "During the ${scheme.moratoriumMonths}-month moratorium, you only pay nominal interest (approx ₹${formatCurrency(plan.repaymentSchedule.firstOrNull()?.interestPayment ?: 0.0)}/quarter), allowing you to build working capital reserves before principal repayment begins."
            )
        )
    }

    val progressPercent = (completedSteps.size.toFloat() / milestones.size.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Roadmap Header & Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Actionable Loan Roadmap",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "${completedSteps.size} of 5 Completed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "5-Step guided milestone tracker for ${scheme.schemeName} application via ${agency.agencyCode} (${agency.agencyFullName}). Track document preparation, DPR export, agency portal submission, bank sanction, and moratorium tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Roadmap Completion Progress",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "${(progressPercent * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }

        // Reusable ApplicationRoadmapCard showing 5 Milestones
        ApplicationRoadmapCard(
            milestones = milestones,
            completedSteps = completedSteps,
            onToggleStep = onToggleMilestone,
            onExportDpr = onExportDpr
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Reusable Application Roadmap Card component displaying the 5 milestone steps.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationRoadmapCard(
    milestones: List<RoadmapMilestoneItem>,
    completedSteps: Set<Int>,
    onToggleStep: (Int) -> Unit,
    onExportDpr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Application & Disbursement Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            milestones.forEachIndexed { index, item ->
                val isCompleted = completedSteps.contains(item.stepNumber)
                var isExpanded by remember { mutableStateOf(index == 0 || isCompleted) }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .animateContentSize()
                    ) {
                        // Header Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                        ) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = { onToggleStep(item.stepNumber) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCompleted) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${item.stepNumber}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Duration: ${item.estimatedDuration}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { isExpanded = !isExpanded }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = "Expand details"
                                )
                            }
                        }

                        // Expanded Content Details
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 8.dp, end = 8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "Required Documentation & Deliverables:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                item.checklistDocs.forEach { doc ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Text(
                                            text = doc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // Guidance Notes Card
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = item.guidanceNotes,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // CTA Action Button if present
                                if (item.actionButtonText != null) {
                                    if (item.stepNumber == 2) {
                                        Button(
                                            onClick = { onExportDpr() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Share,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = item.actionButtonText,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else if (item.portalUrl != null) {
                                        OutlinedButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(item.portalUrl))
                                                context.startActivity(intent)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.OpenInNew,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = item.actionButtonText,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return formatter.format(amount.toLong())
}

@Preview(showBackground = true)
@Composable
fun LoanRoadmapScreenPreview() {
    MaterialTheme {
        LoanRoadmapScreen(uiState = AdvisoryUiState())
    }
}
