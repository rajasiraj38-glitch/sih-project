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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.BusinessCategory
import com.example.myapplication.data.models.LocationData
import com.example.myapplication.data.models.PreferredLanguage
import com.example.myapplication.data.models.SocialCategory
import com.example.myapplication.data.models.UserInputs
import com.example.myapplication.domain.AdvisoryUiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    uiState: AdvisoryUiState,
    onUpdateInputs: (UserInputs) -> Unit,
    onNavigateToFeasibility: () -> Unit,
    onSpeakText: ((String) -> Unit)? = null,
    onStopSpeaking: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val userInputs = uiState.userInputs
    val scrollState = rememberScrollState()

    var isVoiceActive by remember { mutableStateOf(false) }
    var langDropdownExpanded by remember { mutableStateOf(false) }

    // Predefined capital margin quick options
    val capitalPresets = listOf(25000.0, 50000.0, 100000.0, 200000.0, 500000.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Header Card with Voice Assistant Prompt
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            shadowElevation = 4.dp
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Psychology,
                                    contentDescription = "Rural AI Advisor",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Gramin Shakti AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Rural Enterprise Advisory Engine",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Language Selector Badge
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { langDropdownExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Language,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = userInputs.preferredLanguage.nativeName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = langDropdownExpanded,
                            onDismissRequest = { langDropdownExpanded = false }
                        ) {
                            PreferredLanguage.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = lang.nativeName, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "(${lang.englishName})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onUpdateInputs(userInputs.copy(preferredLanguage = lang))
                                        langDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = getLanguagePromptText(userInputs.preferredLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 20.sp
                )

                // Voice Assistant Input Bar
                VoiceAssistantPromptBar(
                    isListening = isVoiceActive,
                    isPlayingAudio = uiState.isSpeaking,
                    onToggleMic = { isVoiceActive = !isVoiceActive },
                    onToggleAudio = {
                        val onboardingText = getLanguagePromptText(userInputs.preferredLanguage)
                        if (uiState.isSpeaking) {
                            onStopSpeaking?.invoke()
                        } else {
                            onSpeakText?.invoke(onboardingText)
                        }
                    }
                )
            }
        }

        // Section 1: Business Profile & Location
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
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
                        imageVector = Icons.Rounded.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "1. Enterprise Profile & Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Business Unit Name
                OutlinedTextField(
                    value = userInputs.businessName,
                    onValueChange = { onUpdateInputs(userInputs.copy(businessName = it)) },
                    label = { Text("Proposed Business Unit Name") },
                    placeholder = { Text("e.g. Gramin Shakti Dairy Unit") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Rounded.BusinessCenter, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                // Location Fields Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = userInputs.location.village,
                        onValueChange = { newVillage ->
                            onUpdateInputs(
                                userInputs.copy(location = userInputs.location.copy(village = newVillage))
                            )
                        },
                        label = { Text("Village / Town") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = userInputs.location.block,
                        onValueChange = { newBlock ->
                            onUpdateInputs(
                                userInputs.copy(location = userInputs.location.copy(block = newBlock))
                            )
                        },
                        label = { Text("Block") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = userInputs.location.district,
                        onValueChange = { newDistrict ->
                            onUpdateInputs(
                                userInputs.copy(location = userInputs.location.copy(district = newDistrict))
                            )
                        },
                        label = { Text("District") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = userInputs.location.state,
                        onValueChange = { newState ->
                            onUpdateInputs(
                                userInputs.copy(location = userInputs.location.copy(state = newState))
                            )
                        },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                // Quick Location Auto-fill Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Sample Locations:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = {
                            onUpdateInputs(
                                userInputs.copy(
                                    location = LocationData(
                                        village = "Rampur",
                                        block = "Chhatarpur",
                                        district = "Chhatarpur",
                                        state = "Madhya Pradesh"
                                    )
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("M.P. Sample", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Section 2: Available Promoter Margin Capital
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
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
                        imageVector = Icons.Rounded.CurrencyRupee,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "2. Available Promoter Margin Capital (₹)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Bank loans finance up to 90% of total project cost. Your available margin capital acts as the 10% promoter contribution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Capital Amount TextField
                var capitalInputText by remember(userInputs.availableMarginCapital) {
                    mutableStateOf(userInputs.availableMarginCapital.toLong().toString())
                }

                OutlinedTextField(
                    value = capitalInputText,
                    onValueChange = { newValue ->
                        capitalInputText = newValue.filter { it.isDigit() }
                        val parsed = capitalInputText.toDoubleOrNull() ?: 10000.0
                        onUpdateInputs(userInputs.copy(availableMarginCapital = parsed.coerceIn(10000.0, 2000000.0)))
                    },
                    label = { Text("Available Capital (₹)") },
                    leadingIcon = {
                        Text("₹", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    },
                    trailingIcon = {
                        Text(
                            text = "Project Cap: ₹${formatRupees(userInputs.availableMarginCapital * 10)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                // Quick Capital Presets Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    capitalPresets.forEach { amount ->
                        val isSelected = userInputs.availableMarginCapital == amount
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onUpdateInputs(userInputs.copy(availableMarginCapital = amount))
                            },
                            label = { Text("₹${formatRupeesShort(amount)}") },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Interactive Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("₹10k", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "Selected: ₹${formatRupees(userInputs.availableMarginCapital)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("₹10 Lakhs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Slider(
                        value = userInputs.availableMarginCapital.toFloat(),
                        onValueChange = { newVal ->
                            onUpdateInputs(userInputs.copy(availableMarginCapital = newVal.toDouble()))
                        },
                        valueRange = 10000f..1000000f,
                        steps = 99,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Section 3: Proposed Business Category
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
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
                        imageVector = Icons.Rounded.BusinessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "3. Proposed Business Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BusinessCategory.entries.forEach { category ->
                        val isSelected = userInputs.proposedBusinessCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onUpdateInputs(userInputs.copy(proposedBusinessCategory = category))
                            },
                            label = { Text(category.displayName) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Selected Category Description & Margin Details
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = userInputs.proposedBusinessCategory.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = userInputs.proposedBusinessCategory.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Est. Profit Margin Range: ${userInputs.proposedBusinessCategory.defaultProfitMarginRange}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "CapEx Ratio: ${(userInputs.proposedBusinessCategory.capExRatio * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Social Category & MoSJE Scheme Routing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
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
                        imageVector = Icons.Rounded.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "4. Social Category & MoSJE Nodal Agency Match",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Selecting your category routes loan applications directly to specialized Ministry of Social Justice & Empowerment (MoSJE) concessional finance corporations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Social Category Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SocialCategory.entries.forEach { social ->
                        val isSelected = userInputs.socialCategory == social
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onUpdateInputs(userInputs.copy(socialCategory = social))
                            },
                            label = { Text(social.displayName) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }

                // MoSJE Nodal Agency Match Card Preview
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userInputs.socialCategory.primaryNodalAgencyCode,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nodal Agency Match: ${userInputs.socialCategory.primaryNodalAgencyCode}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = userInputs.socialCategory.primaryNodalAgencyName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Primary Action Button
        Button(
            onClick = {
                onNavigateToFeasibility()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Evaluate Feasibility & Calculate Loan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VoiceAssistantPromptBar(
    isListening: Boolean,
    isPlayingAudio: Boolean,
    onToggleMic: () -> Unit,
    onToggleAudio: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    IconButton(
                        onClick = onToggleMic,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isListening) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Rounded.Mic else Icons.Rounded.MicOff,
                            contentDescription = "Mic Toggle",
                            tint = if (isListening) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = if (isListening) "सुन रहे हैं... (Listening)" else "बोलकर भरें (Voice Input)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onToggleAudio,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (isPlayingAudio) Icons.Rounded.Pause else Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Audio Guide Toggle",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Waveform Visualizer
            AnimatedVisibility(
                visible = isListening || isPlayingAudio,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AudioWaveformVisualizer(
                    isAnimating = isListening || isPlayingAudio,
                    barColor = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun AudioWaveformVisualizer(
    isAnimating: Boolean,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(18) { index ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index * 45) % 400,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            val heightMultiplier = if (isAnimating) animatedHeight else 0.2f

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((24 * heightMultiplier).dp)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

private fun getLanguagePromptText(language: PreferredLanguage): String {
    return when (language) {
        PreferredLanguage.HINDI -> "नमस्ते! अपने ग्रामीण व्यवसाय की जानकारी भरें या नीचे माइक दबाकर बोलें।"
        PreferredLanguage.ENGLISH -> "Welcome! Enter your rural micro-enterprise details or tap the mic to speak."
        PreferredLanguage.TAMIL -> "வணக்கம்! உங்கள் கிராமப்புற தொழில் விவரங்களை உள்ளிடவும்."
        PreferredLanguage.TELUGU -> "స్వాగతం! మీ గ్రామీణ వ్యాపార వివరాలను నమోదు చేయండి."
        PreferredLanguage.MARATHI -> "नमस्कार! तुमच्या ग्रामीण उद्योगाची माहिती नोंदवा."
        PreferredLanguage.BENGALI -> "স্বাগতম! আপনার গ্রামীণ ব্যবসার তথ্য প্রদান করুন।"
        PreferredLanguage.KANNADA -> "ಸ್ವಾಗತ! ನಿಮ್ಮ ಗ್ರಾಮೀಣ ಉದ್ಯಮದ ವಿವರಗಳನ್ನು ನಮೂದಿಸಿ."
        PreferredLanguage.GUJARATI -> "સ્વાગત છે! તમારા ગ્રામીણ વ્યવસાયની વિગતો આપો."
        else -> "Welcome! Enter your rural micro-enterprise details or tap the mic to speak."
    }
}

private fun formatRupees(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return formatter.format(amount.toLong())
}

private fun formatRupeesShort(amount: Double): String {
    return when {
        amount >= 100000 -> "${(amount / 100000).toInt()} Lakh"
        amount >= 1000 -> "${(amount / 1000).toInt()}k"
        else -> amount.toInt().toString()
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MaterialTheme {
        OnboardingScreen(
            uiState = AdvisoryUiState(),
            onUpdateInputs = {},
            onNavigateToFeasibility = {}
        )
    }
}
