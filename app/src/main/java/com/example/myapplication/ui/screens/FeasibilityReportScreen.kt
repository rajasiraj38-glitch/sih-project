package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Dataset
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PriceCheck
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.FeasibilityReport
import com.example.myapplication.data.models.PreferredLanguage
import com.example.myapplication.domain.AdvisoryUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeasibilityReportScreen(
    uiState: AdvisoryUiState,
    onSpeakText: ((String) -> Unit)? = null,
    onStopSpeaking: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val report = uiState.feasibilityReport
    val userInputs = uiState.userInputs
    val scrollState = rememberScrollState()

    val feasibilityBrief = remember(report, userInputs.preferredLanguage) {
        "Feasibility Report Brief for ${report.businessCategory.displayName} in ${report.location.getFormattedLocation()}. Overall Feasibility Score: ${"%.1f".format(report.overallFeasibilityScore)} out of 10. Grade: ${report.feasibilityGrade}. Verdict: ${report.summaryVerdict}."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Voice Guidance Equalizer Header
        VoiceGuidancePlayerCard(
            isPlaying = uiState.isSpeaking,
            language = userInputs.preferredLanguage,
            onTogglePlay = {
                if (uiState.isSpeaking) {
                    onStopSpeaking?.invoke()
                } else {
                    onSpeakText?.invoke(feasibilityBrief)
                }
            }
        )

        // 1. Feasibility Summary Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = report.businessCategory.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = report.location.getFormattedLocation(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Gauge Score Indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { (report.overallFeasibilityScore / 10.0).toFloat() },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 7.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f".format(report.overallFeasibilityScore),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "/10",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Grade Chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Verdict Grade: ${report.feasibilityGrade}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Text(
                    text = report.summaryVerdict,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 20.sp
                )
            }
        }

        // 1B. Feasibility Data Confidence Score & Statutory Validation Card
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Data Confidence Score: ${report.confidenceScore.scorePercentage.toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = report.confidenceScore.confidenceLevel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { (report.confidenceScore.scorePercentage / 100.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Text(
                    text = "Statutory Datasets Integration Sources:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                report.confidenceScore.validationSources.forEach { source ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Dataset,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = source,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 1C. Statutory Datasets Breakdown Card
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
                Text(
                    text = "Statutory Dataset Benchmarks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(
                        title = "Mission Antyodaya",
                        value = "${report.missionAntyodaya.gpInfrastructureScore}/100",
                        subtitle = "GP Infra Score",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Census / SHRUG",
                        value = "${report.censusShrug.literacyRatePercent}%",
                        subtitle = "Literacy Rate",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Agmarknet Mandi",
                        value = "₹${report.agmarknetMandi.benchmarkPricePerUnit.toInt()}",
                        subtitle = "Price/Quintal",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "LGD Village Code: ${report.location.lgdVillageCode} | Block Code: ${report.location.lgdBlockCode}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Data Sparsity Risk Mitigation Advisory:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                report.dataSparsityMitigation.forEach { advice ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = advice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Market Reach Card
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
                        imageVector = Icons.Rounded.Radar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Market Reach & Radius Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(
                        title = "Radius Reach",
                        value = "${report.marketReach.radiusKm} km",
                        subtitle = "Catchment Area",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Est. Population",
                        value = formatNumber(report.marketReach.estimatedPopulationReach),
                        subtitle = "Residents",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Buyer Density",
                        value = "${report.marketReach.activeBuyerDensityPerSqKm}/sq.km",
                        subtitle = report.marketReach.buyerDensityLevel,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Recommended Distribution Channels:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    report.marketReach.distributionChannels.forEach { channel ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "• $channel",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Hyper-Local Opportunity Analysis
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
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Hyper-Local Micro-Niche Opportunities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = report.opportunityAnalysis.marketGapSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Unserved Micro Niches
                if (report.opportunityAnalysis.unservedMicroNiches.isNotEmpty()) {
                    Text(
                        text = "Unserved High-Margin Micro Niches:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        report.opportunityAnalysis.unservedMicroNiches.forEach { niche ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = niche,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Demand Growth Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Demand Growth Potential",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${report.opportunityAnalysis.demandGrowthPotentialScore}/10.0",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (report.opportunityAnalysis.demandGrowthPotentialScore / 10.0).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // 4. SWOT Matrix 2x2 Grid
        Text(
            text = "4-Quadrant SWOT Matrix Analysis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SwotQuadrantCard(
                title = "Strengths (S)",
                items = report.swotBreakdown.strengths,
                containerColor = Color(0xFFE8F5E9),
                titleColor = Color(0xFF1B5E20),
                modifier = Modifier.weight(1f)
            )
            SwotQuadrantCard(
                title = "Weaknesses (W)",
                items = report.swotBreakdown.weaknesses,
                containerColor = Color(0xFFFFF3E0),
                titleColor = Color(0xFFE65100),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SwotQuadrantCard(
                title = "Opportunities (O)",
                items = report.swotBreakdown.opportunities,
                containerColor = Color(0xFFE1F5FE),
                titleColor = Color(0xFF01579B),
                modifier = Modifier.weight(1f)
            )
            SwotQuadrantCard(
                title = "Threats (T)",
                items = report.swotBreakdown.threats,
                containerColor = Color(0xFFFFEBEE),
                titleColor = Color(0xFFB71C1C),
                modifier = Modifier.weight(1f)
            )
        }

        // 5. Threat Identification & Mitigation Strategy Card
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
                        imageVector = Icons.Rounded.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Key Threats & Risk Mitigation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (report.threatIdentification.supplyChainBottlenecks.isNotEmpty()) {
                    Text(
                        text = "Supply Chain Vulnerabilities:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    report.threatIdentification.supplyChainBottlenecks.forEach { bottleneck ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = bottleneck,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Text(
                    text = "Seasonal Demand & Buyer Risk: ${report.threatIdentification.seasonalDemandFluctuations} (${report.threatIdentification.singleBuyerDependencyRisk})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Actionable Mitigation Plan:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                report.threatIdentification.mitigationStrategies.forEach { strategy ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strategy,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 6. Competitor Density Mapping Card
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
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Block-Level Competitor Density",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(
                        title = "Est. Competitors",
                        value = "${report.competitorMapping.estimatedCompetitorCountPerBlock} Units",
                        subtitle = "Per Block Level",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Intensity Level",
                        value = report.competitorMapping.competitionIntensityLevel,
                        subtitle = "Market Saturation",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Price Benchmarking: ${report.competitorMapping.competitorPriceRangeText}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Key Insight: ${report.competitorMapping.keyCompetitorInsights}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 7. Product Market Value & Optimal Pricing Card
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
                        imageVector = Icons.Rounded.PriceCheck,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Product Pricing & Profitability Model",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(
                        title = "Pricing Tier",
                        value = report.pricingAndValue.suggestedPricingTier,
                        subtitle = "Target Strategy",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Est. Profit Margin",
                        value = "${report.pricingAndValue.expectedProfitMarginPercent}%",
                        subtitle = "Net Margin",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricBox(
                        title = "Break-Even Horizon",
                        value = "${report.pricingAndValue.BreakEvenPointMonths} Months",
                        subtitle = "Payback Time",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Local Purchasing Power Index: ${report.pricingAndValue.localPurchasingPowerIndex} / 100",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = report.pricingAndValue.suggestedRetailPriceNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VoiceGuidancePlayerCard(
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
                    contentDescription = "Audio Summary",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPlaying) "Playing Feasibility Brief (${language.englishName})" else "Read Aloud Feasibility Brief (${language.nativeName})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Tap to listen to personalized rural advisory commentary.",
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
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(24) { idx ->
            val heightPercent by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 300 + (idx * 35) % 350,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$idx"
            )

            val h = if (isAnimating) heightPercent else 0.2f

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((16 * h).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SwotQuadrantCard(
    title: String,
    items: List<String>,
    containerColor: Color,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = modifier.height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items.take(3).forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = titleColor.copy(alpha = 0.85f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

private fun formatNumber(number: Int): String {
    return "%,d".format(number)
}

@Preview(showBackground = true)
@Composable
fun FeasibilityReportScreenPreview() {
    MaterialTheme {
        FeasibilityReportScreen(
            uiState = AdvisoryUiState()
        )
    }
}
