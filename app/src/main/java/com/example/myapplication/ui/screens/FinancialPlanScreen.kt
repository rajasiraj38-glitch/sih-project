package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.CostBreakdownStructure
import com.example.myapplication.data.models.FinancialPlan
import com.example.myapplication.data.models.NodalAgencyInfo
import com.example.myapplication.data.models.PreferredLanguage
import com.example.myapplication.data.models.SchemeInfo
import com.example.myapplication.data.models.SocialCategory
import com.example.myapplication.domain.AdvisoryUiState
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Data item representing a slice in the custom project cost breakdown pie/ring chart.
 */
data class CostSliceData(
    val name: String,
    val amount: Double,
    val percentage: Double,
    val color: Color,
    val description: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialPlanScreen(
    uiState: AdvisoryUiState,
    onUpdateMarginCapital: (Double) -> Unit = {},
    onSpeakText: ((String) -> Unit)? = null,
    onStopSpeaking: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val plan = uiState.financialPlan
    val scheme = plan.schemeInfo
    val agency = scheme.nodalAgency
    val breakdown = plan.costBreakdown
    val userInputs = uiState.userInputs
    val scrollState = rememberScrollState()

    var isAmortizationExpanded by remember { mutableStateOf(false) }
    var selectedMarginCapital by remember(plan.availableMarginCapital) { mutableDoubleStateOf(plan.availableMarginCapital) }

    val commercialInterestRate = 12.5 // Commercial bank benchmark
    val interestSavings = calculateInterestSavings(
        loanAmount = plan.maxLoanAmount,
        concessionalRate = scheme.interestRatePerAnnum,
        commercialRate = commercialInterestRate,
        totalQuarters = scheme.tenureQuarters,
        moratoriumQuarters = scheme.moratoriumQuarters,
        concessionalTotalInterest = plan.totalInterestPayable
    )

    val financialSummaryText = remember(plan, userInputs.preferredLanguage) {
        val costStr = "%.0f".format(plan.projectCost)
        val loanStr = "%.0f".format(plan.maxLoanAmount)
        val marginStr = "%.0f".format(plan.marginContribution)
        val emiStr = "%.0f".format(plan.quarterlyEmiPostMoratorium)
        "Financial Summary for ${userInputs.businessName}: Total project cost is ₹$costStr, funded with ₹$marginStr promoter margin contribution and maximum eligible loan of ₹$loanStr under ${scheme.schemeName} channelized by ${agency.agencyCode}. Post-moratorium quarterly EMI is ₹$emiStr."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Read Aloud Financial Summary Header
        FinancialVoiceGuidanceCard(
            isPlaying = uiState.isSpeaking,
            language = userInputs.preferredLanguage,
            onTogglePlay = {
                if (uiState.isSpeaking) {
                    onStopSpeaking?.invoke()
                } else {
                    onSpeakText?.invoke(financialSummaryText)
                }
            }
        )

        // 1. CAPITAL RATIO HEADER (10% Margin vs 90% Loan vs Total Cost)
        CapitalRatioHeaderCard(
            plan = plan,
            selectedMarginCapital = selectedMarginCapital,
            onMarginChanged = { newMargin ->
                selectedMarginCapital = newMargin
                onUpdateMarginCapital(newMargin)
            }
        )

        // 2. SCHEME AUTO-SELECTION & COMPARISON CARD
        SchemeAutoSelectionCard(
            currentProjectCost = plan.projectCost,
            activeScheme = scheme
        )

        // 3. NODAL CHANNELIZING AGENCY (SCA) BADGE & APPLICATION DETAILS
        NodalAgencyBadgeCard(
            agency = agency,
            socialCategory = uiState.userInputs.socialCategory,
            schemeInterestRate = scheme.interestRatePerAnnum
        )

        // 3B. DETERMINISTIC FINANCIAL ENGINE & MoSJE AI GUIDANCE SEPARATION CARD
        DeterministicEngineExplanationCard(plan = plan)

        // 4. CUSTOM RING / PIE CANVAS CHART FOR PROJECT COST BREAKDOWN
        ProjectCostPieChartCard(breakdown = breakdown, projectCost = plan.projectCost)

        // 5. QUARTERLY EMI & MORATORIUM REPAYMENT SCHEDULE TABLE
        RepaymentScheduleTableCard(
            plan = plan,
            scheme = scheme,
            interestSavings = interestSavings,
            commercialRate = commercialInterestRate,
            isExpanded = isAmortizationExpanded,
            onToggleExpand = { isAmortizationExpanded = !isAmortizationExpanded }
        )

        // 6. WORKING CAPITAL & OPERATIONAL COST GUIDE CARDS
        WorkingCapitalGuideCards()

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FinancialVoiceGuidanceCard(
    isPlaying: Boolean,
    language: PreferredLanguage,
    onTogglePlay: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onTogglePlay,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = "Read Aloud Financial Summary",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPlaying) "Playing Financial Summary (${language.englishName})" else "Read Aloud Financial Summary (${language.nativeName})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Tap to listen to loan eligibility and quarterly EMI breakdown.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )

                AnimatedVisibility(
                    visible = isPlaying,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AudioWaveformEqualizer(isAnimating = isPlaying)
                }
            }
        }
    }
}

@Composable
private fun AudioWaveformEqualizer(isAnimating: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val barCount = 18
        for (i in 0 until barCount) {
            val duration = 400 + (i * 70) % 500
            val targetHeight = if (isAnimating) 1f else 0.2f

            val heightFactor by infiniteTransition.animateFloat(
                initialValue = 0.15f,
                targetValue = targetHeight,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((20 * if (isAnimating) heightFactor else 0.2f).dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

/**
 * 1. Capital Ratio Header Banner (Interactive 10% Margin vs 90% SCA Loan).
 */
@Composable
private fun CapitalRatioHeaderCard(
    plan: FinancialPlan,
    selectedMarginCapital: Double,
    onMarginChanged: (Double) -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        imageVector = Icons.Rounded.Calculate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "10:90 Capital Structure Ratio",
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
                        text = "10% Margin : 90% Loan",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Metric Overview Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinancialMetricItem(
                    title = "Promoter Margin (10%)",
                    value = "₹${formatCurrency(plan.marginContribution)}",
                    subtitle = "Self Contribution",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricItem(
                    title = "Bank Loan (90%)",
                    value = "₹${formatCurrency(plan.maxLoanAmount)}",
                    subtitle = "SCA Channel Loan",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricItem(
                    title = "Total Project Cost",
                    value = "₹${formatCurrency(plan.projectCost)}",
                    subtitle = "Capital Outlay",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            // Margin Capital Slider Adjustment
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Adjust Margin Contribution",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "₹${formatCurrency(selectedMarginCapital)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = selectedMarginCapital.toFloat(),
                    onValueChange = { onMarginChanged(it.toDouble()) },
                    valueRange = 10000f..500000f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

/**
 * 2. Scheme Auto-Selection Card.
 */
@Composable
private fun SchemeAutoSelectionCard(
    currentProjectCost: Double,
    activeScheme: SchemeInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Auto-Selected Concessional Loan Scheme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeScheme.schemeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = activeScheme.summaryDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FinancialMetricItem(
                            title = "Interest Rate",
                            value = "${activeScheme.interestRatePerAnnum}% p.a.",
                            subtitle = "Concessional",
                            modifier = Modifier.weight(1f)
                        )

                        FinancialMetricItem(
                            title = "Tenure",
                            value = "${activeScheme.tenureYears} Years",
                            subtitle = "${activeScheme.tenureQuarters} Quarters",
                            modifier = Modifier.weight(1f)
                        )

                        FinancialMetricItem(
                            title = "Moratorium",
                            value = "${activeScheme.moratoriumMonths} Months",
                            subtitle = "${activeScheme.moratoriumQuarters} Qtr Interest-Only",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. Nodal Agency Badge Card.
 */
@Composable
private fun NodalAgencyBadgeCard(
    agency: NodalAgencyInfo,
    socialCategory: SocialCategory,
    schemeInterestRate: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Target Social Category Channelizing Agency",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = agency.agencyCode,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = agency.agencyFullName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Category: ${socialCategory.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Target Beneficiaries: ${agency.targetGroup}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3B. Deterministic Financial Engine vs AI Guidance Mode Card.
 */
@Composable
private fun DeterministicEngineExplanationCard(plan: FinancialPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "MoSJE Deterministic Calculation Engine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Deterministic Formulas Separated from AI Narrative Guidance:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = plan.aiNarrativeExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * 4. Custom Ring / Pie Chart Card for Project Cost Breakdown.
 */
@Composable
private fun ProjectCostPieChartCard(
    breakdown: CostBreakdownStructure,
    projectCost: Double
) {
    val items = breakdown.toList()
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.outline
    )

    val slices = items.mapIndexed { index, item ->
        CostSliceData(
            name = item.categoryName,
            amount = item.amount,
            percentage = item.percentage,
            color = colors[index % colors.size],
            description = item.description
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    imageVector = Icons.Rounded.PieChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Capital Outlay Breakdown Structure",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Canvas Ring Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    var startAngle = -90f
                    slices.forEach { slice ->
                        val sweepAngle = (slice.percentage.toFloat() / 100f) * 360f
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Butt)
                        )
                        startAngle += sweepAngle
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Outlay",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatShortCurrency(projectCost),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Interactive Color-Coded Legend
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                slices.forEach { slice ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(slice.color)
                                    )
                                    Text(
                                        text = slice.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "₹${formatCurrency(slice.amount)} (${slice.percentage.toInt()}%)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = slice.description,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5. Quarterly EMI & Moratorium Repayment Schedule Table.
 */
@Composable
private fun RepaymentScheduleTableCard(
    plan: FinancialPlan,
    scheme: SchemeInfo,
    interestSavings: Double,
    commercialRate: Double,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Quarterly EMI Repayment Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.clickable { onToggleExpand() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Hide Table" else "View All Qtrs",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Key Repayment Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinancialMetricItem(
                    title = "Post-Moratorium EMI",
                    value = "₹${formatCurrency(plan.quarterlyEmiPostMoratorium)}",
                    subtitle = "Quarterly Payment",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricItem(
                    title = "Total Interest",
                    value = "₹${formatCurrency(plan.totalInterestPayable)}",
                    subtitle = "Over ${scheme.tenureYears} Years",
                    modifier = Modifier.weight(1f)
                )

                FinancialMetricItem(
                    title = "Total Repaid",
                    value = "₹${formatCurrency(plan.totalAmountRepaid)}",
                    subtitle = "Principal + Interest",
                    modifier = Modifier.weight(1f)
                )
            }

            // Concessional Savings Alert Banner vs Commercial Banks
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Savings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Estimated Interest Savings: ₹${formatCurrency(interestSavings)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Compared to commercial bank loan rates (${commercialRate}% p.a.), this concessional scheme saves substantial interest costs over the tenure.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Expandable Quarterly Table
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Table Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableHeaderCell(text = "Quarter", weight = 1.2f)
                        TableHeaderCell(text = "Principal", weight = 1f)
                        TableHeaderCell(text = "Interest", weight = 1f)
                        TableHeaderCell(text = "Total EMI", weight = 1.1f)
                        TableHeaderCell(text = "Closing Bal", weight = 1.2f)
                    }

                    // Table Data Rows
                    plan.repaymentSchedule.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.quarterLabel,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (entry.isMoratoriumQuarter) FontWeight.Bold else FontWeight.Normal,
                                color = if (entry.isMoratoriumQuarter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1.2f)
                            )
                            TableCell(text = "₹${formatShortCurrency(entry.principalPayment)}", weight = 1f)
                            TableCell(text = "₹${formatShortCurrency(entry.interestPayment)}", weight = 1f)
                            TableCell(text = "₹${formatShortCurrency(entry.totalQuarterlyPayment)}", weight = 1.1f, isBold = true)
                            TableCell(text = "₹${formatShortCurrency(entry.closingBalance)}", weight = 1.2f)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 6. Working Capital & Operational Cost Guide.
 */
@Composable
private fun WorkingCapitalGuideCards() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Working Capital & Cash Flow Management Advice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "• Reserve 15% of project cost explicitly as a 2-3 month liquid cash buffer for operational inventory and utility payments.\n" +
                        "• Utilize moratorium quarter(s) to establish local retail buyer relationships before principal repayment begins.\n" +
                        "• Maintain a strict 7-day credit cycle for local village retail buyers to avoid working capital lockup.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun FinancialMetricItem(
    title: String,
    value: String,
    subtitle: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(0.dp) // dummy for weight
    )
}

@Composable
private fun TableCell(text: String, weight: Float, isBold: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

private fun calculateInterestSavings(
    loanAmount: Double,
    concessionalRate: Double,
    commercialRate: Double,
    totalQuarters: Int,
    moratoriumQuarters: Int,
    concessionalTotalInterest: Double
): Double {
    val remainingQuarters = totalQuarters - moratoriumQuarters
    val commercialQuarterlyRate = (commercialRate / 100.0) / 4.0

    val commercialEmi = if (remainingQuarters > 0 && commercialQuarterlyRate > 0) {
        val factor = (1.0 + commercialQuarterlyRate).pow(remainingQuarters.toDouble())
        loanAmount * (commercialQuarterlyRate * factor) / (factor - 1.0)
    } else {
        0.0
    }

    var totalCommercialInterest = moratoriumQuarters * (loanAmount * commercialQuarterlyRate)
    var balance = loanAmount
    for (q in 1..remainingQuarters) {
        val interest = balance * commercialQuarterlyRate
        val principal = (commercialEmi - interest).coerceAtLeast(0.0)
        totalCommercialInterest += interest
        balance = (balance - principal).coerceAtLeast(0.0)
    }

    val savings = totalCommercialInterest - concessionalTotalInterest
    return (savings * 100.0).roundToLong() / 100.0
}

private fun formatCurrency(amount: Double): String {
    return "%,.2f".format(amount)
}

private fun formatShortCurrency(amount: Double): String {
    return if (amount >= 100000) {
        "%.1fL".format(amount / 100000.0)
    } else if (amount >= 1000) {
        "%.1fK".format(amount / 1000.0)
    } else {
        "%.0f".format(amount)
    }
}

@Preview(showBackground = true)
@Composable
fun FinancialPlanScreenPreview() {
    MaterialTheme {
        FinancialPlanScreen(
            uiState = AdvisoryUiState()
        )
    }
}
