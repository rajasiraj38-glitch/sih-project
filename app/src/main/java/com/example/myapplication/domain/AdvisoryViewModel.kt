package com.example.myapplication.domain

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SarvamTtsManager
import com.example.myapplication.data.models.BusinessCategory
import com.example.myapplication.data.models.DataSourcingGuide
import com.example.myapplication.data.models.FeasibilityReport
import com.example.myapplication.data.models.FinancialPlan
import com.example.myapplication.data.models.LocationData
import com.example.myapplication.data.models.PreferredLanguage
import com.example.myapplication.data.models.SocialCategory
import com.example.myapplication.data.models.UserInputs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State holding user inputs, feasibility analysis, financial plan, roadmap progress, data sourcing guide, and TTS playback state.
 */
data class AdvisoryUiState(
    val userInputs: UserInputs = UserInputs(),
    val feasibilityReport: FeasibilityReport = FeasibilityReport(),
    val financialPlan: FinancialPlan = FinancialCalculatorEngine.calculateFinancialPlan(
        availableMarginCapital = 100000.0,
        socialCategory = SocialCategory.OBC,
        businessCategory = BusinessCategory.DAIRY_LIVESTOCK
    ),
    val dataSourcingGuide: DataSourcingGuide = DataSourcingGuideProvider.getGuide(
        location = LocationData(),
        category = BusinessCategory.DAIRY_LIVESTOCK
    ),
    val completedRoadmapSteps: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSpeaking: Boolean = false,
    val ttsErrorMessage: String? = null,
    val speakingText: String? = null,
    val isOfflineCacheReady: Boolean = true,
    val offlineStatusMessage: String = "Offline Cache Ready - LGD Code Synced"
)

/**
 * ViewModel managing reactive business advisory, financial structuring, hyper-local feasibility calculation, and Sarvam AI TTS state.
 */
class AdvisoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdvisoryUiState())
    val uiState: StateFlow<AdvisoryUiState> = _uiState.asStateFlow()

    init {
        // Initial report evaluation
        evaluateAll()
    }

    /**
     * Synthesizes and speaks text using Sarvam AI TTS engine or native TTS fallback.
     * Toggles off if the same text is currently playing.
     */
    fun speakText(context: Context, text: String) {
        val currentState = _uiState.value
        if (currentState.isSpeaking && currentState.speakingText == text) {
            stopSpeaking()
            return
        }

        _uiState.update { current ->
            current.copy(
                isSpeaking = true,
                speakingText = text,
                ttsErrorMessage = null
            )
        }

        SarvamTtsManager.speak(
            context = context,
            text = text,
            language = currentState.userInputs.preferredLanguage,
            scope = viewModelScope,
            onStart = {
                _uiState.update { current ->
                    current.copy(
                        isSpeaking = true,
                        speakingText = text,
                        ttsErrorMessage = null
                    )
                }
            },
            onCompletion = {
                _uiState.update { current ->
                    current.copy(
                        isSpeaking = false,
                        speakingText = null
                    )
                }
            },
            onError = { errorMsg ->
                _uiState.update { current ->
                    current.copy(
                        isSpeaking = false,
                        speakingText = null,
                        ttsErrorMessage = errorMsg
                    )
                }
            }
        )
    }

    /**
     * Stops any active speech synthesis and resets speaking state.
     */
    fun stopSpeaking() {
        SarvamTtsManager.stopSpeaking()
        _uiState.update { current ->
            current.copy(
                isSpeaking = false,
                speakingText = null
            )
        }
    }

    /**
     * Updates complete UserInputs object and recalculates report.
     */
    fun updateUserInputs(newInputs: UserInputs) {
        _uiState.update { it.copy(userInputs = newInputs) }
        evaluateAll()
    }

    /**
     * Updates location details.
     */
    fun updateLocation(village: String, block: String, district: String, state: String) {
        _uiState.update { current ->
            val updatedLocation = current.userInputs.location.copy(
                village = village.ifBlank { "Rampur" },
                block = block.ifBlank { "Chhatarpur" },
                district = district.ifBlank { "Chhatarpur" },
                state = state.ifBlank { "Madhya Pradesh" }
            )
            current.copy(userInputs = current.userInputs.copy(location = updatedLocation))
        }
        evaluateAll()
    }

    /**
     * Updates available margin capital (e.g. ₹1,00,000).
     */
    fun updateMarginCapital(marginCapital: Double) {
        val safeMargin = if (marginCapital < 1000.0) 1000.0 else marginCapital
        _uiState.update { current ->
            current.copy(userInputs = current.userInputs.copy(availableMarginCapital = safeMargin))
        }
        evaluateAll()
    }

    /**
     * Updates proposed business category.
     */
    fun updateBusinessCategory(category: BusinessCategory) {
        _uiState.update { current ->
            current.copy(userInputs = current.userInputs.copy(proposedBusinessCategory = category))
        }
        evaluateAll()
    }

    /**
     * Updates social category.
     */
    fun updateSocialCategory(socialCategory: SocialCategory) {
        _uiState.update { current ->
            current.copy(userInputs = current.userInputs.copy(socialCategory = socialCategory))
        }
        evaluateAll()
    }

    /**
     * Updates preferred language choice.
     */
    fun updateLanguage(language: PreferredLanguage) {
        _uiState.update { current ->
            current.copy(userInputs = current.userInputs.copy(preferredLanguage = language))
        }
    }

    /**
     * Updates business unit name.
     */
    fun updateBusinessName(name: String) {
        _uiState.update { current ->
            current.copy(userInputs = current.userInputs.copy(businessName = name.ifBlank { "Gramin Enterprise" }))
        }
    }

    /**
     * Toggles roadmap step completion status.
     */
    fun toggleRoadmapStep(stepNumber: Int) {
        _uiState.update { current ->
            val updatedSteps = if (current.completedRoadmapSteps.contains(stepNumber)) {
                current.completedRoadmapSteps - stepNumber
            } else {
                current.completedRoadmapSteps + stepNumber
            }
            current.copy(completedRoadmapSteps = updatedSteps)
        }
    }

    /**
     * Explicitly triggers recalculation of FeasibilityReport, FinancialPlan, and DataSourcingGuide.
     */
    fun recalculate() {
        evaluateAll()
    }

    private fun evaluateAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentInputs = _uiState.value.userInputs

                val feasibilityReport = FeasibilityEngine.generateReport(currentInputs)

                val financialPlan = FinancialCalculatorEngine.calculateFinancialPlan(
                    availableMarginCapital = currentInputs.availableMarginCapital,
                    socialCategory = currentInputs.socialCategory,
                    businessCategory = currentInputs.proposedBusinessCategory
                )

                val guide = DataSourcingGuideProvider.getGuide(
                    location = currentInputs.location,
                    category = currentInputs.proposedBusinessCategory
                )

                _uiState.update { current ->
                    current.copy(
                        feasibilityReport = feasibilityReport,
                        financialPlan = financialPlan,
                        dataSourcingGuide = guide,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Error evaluating business feasibility report."
                    )
                }
            }
        }
    }

    /**
     * Generates a WhatsApp share string for quick field distribution.
     */
    fun getWhatsAppShareText(): String {
        val state = _uiState.value
        val inputs = state.userInputs
        val plan = state.financialPlan
        val report = state.feasibilityReport
        val scheme = plan.schemeInfo

        return """
            📊 *GramAdvisor Feasibility & Detailed Project Summary*
            🏆 *SIH 2026 Innovation Platform*
            
            🏢 *Enterprise*: ${inputs.businessName}
            🌾 *Category*: ${inputs.proposedBusinessCategory.displayName}
            📍 *Location*: ${inputs.location.getFormattedLocation()}
            
            💰 *Financial Structure*:
            - Project Cost: ₹${formatCurrency(plan.projectCost)}
            - Eligible Loan (90%): ₹${formatCurrency(plan.maxLoanAmount)}
            - Equity Margin (10%): ₹${formatCurrency(plan.availableMarginCapital)}
            - Concessional Scheme: ${scheme.schemeName} (${scheme.nodalAgency.agencyCode})
            - Interest Rate: ${scheme.interestRatePerAnnum}% p.a.
            - Post-Moratorium Quarterly EMI: ₹${formatCurrency(plan.quarterlyEmiPostMoratorium)}
            
            📈 *Feasibility Score*: ${report.overallFeasibilityScore}/10 (${report.feasibilityGrade})
            🎯 *Data Confidence*: ${report.confidenceScore.scorePercentage}% (${report.confidenceScore.confidenceLevel})
            
            Generated via GramAdvisor Offline Sync Engine.
        """.trimIndent()
    }

    /**
     * Exports a formal Detailed Project Report (DPR) summary string for loan application submission.
     */
    fun exportDprSummaryText(): String {
        val state = _uiState.value
        val inputs = state.userInputs
        val plan = state.financialPlan
        val report = state.feasibilityReport
        val scheme = plan.schemeInfo
        val agency = scheme.nodalAgency

        return """
            ========================================================================
            GRAMADVISOR - DETAILED PROJECT REPORT (DPR) & FEASIBILITY SUMMARY
            SMART INDIA HACKATHON (SIH) 2026 - RURAL ENTERPRISE SOLUTION
            ========================================================================
            Project Name: ${inputs.businessName}
            Business Category: ${inputs.proposedBusinessCategory.displayName}
            Location: ${inputs.location.getFormattedLocation()}
            Social Category: ${inputs.socialCategory.displayName}
            Preferred Language: ${inputs.preferredLanguage.englishName} (${inputs.preferredLanguage.nativeName})
            
            ------------------------------------------------------------------------
            1. FINANCIAL STRUCTURING & LOAN SUMMARY (DETERMINISTIC ENGINE)
            ------------------------------------------------------------------------
            Promoter Margin Capital: ₹${formatCurrency(plan.availableMarginCapital)} (10% Contribution)
            Calculated Total Project Cost: ₹${formatCurrency(plan.projectCost)}
            Maximum Eligible Bank Loan: ₹${formatCurrency(plan.maxLoanAmount)} (90% Financed)
            Applicable Capital Subsidy: ₹${formatCurrency(plan.subsidyAmount)}
            Selected Scheme: ${scheme.schemeName}
            Applicable Interest Rate: ${scheme.interestRatePerAnnum}% p.a.
            Loan Tenure: ${scheme.tenureYears} Years (${scheme.tenureQuarters} Quarters)
            Moratorium Period: ${scheme.moratoriumMonths} Months (${scheme.moratoriumQuarters} Quarter(s) Interest-Only)
            Post-Moratorium Quarterly EMI: ₹${formatCurrency(plan.quarterlyEmiPostMoratorium)}
            Total Interest Payable Over Tenure: ₹${formatCurrency(plan.totalInterestPayable)}
            Total Repayment Amount: ₹${formatCurrency(plan.totalAmountRepaid)}
            
            ------------------------------------------------------------------------
            2. NODAL AGENCY ROUTING RECOMMENDATION (MoSJE / APEX CORPORATIONS)
            ------------------------------------------------------------------------
            Recommended Agency: ${agency.agencyFullName} (${agency.agencyCode})
            Target Beneficiary Group: ${agency.targetGroup}
            Official Portal: ${agency.websiteUrl} | Helpline: ${agency.helplineNumber}
            
            ------------------------------------------------------------------------
            3. COST BREAKDOWN STRUCTURE (CAPITAL ALLOCATION)
            ------------------------------------------------------------------------
            - Capital Expenditure (Machinery/Shed): ₹${formatCurrency(plan.costBreakdown.capitalExpenditure.amount)} (${plan.costBreakdown.capitalExpenditure.percentage}%)
            - Raw Materials & Initial Inventory: ₹${formatCurrency(plan.costBreakdown.rawMaterialsAndInventory.amount)} (${plan.costBreakdown.rawMaterialsAndInventory.percentage}%)
            - Working Capital Buffer: ₹${formatCurrency(plan.costBreakdown.workingCapitalBuffer.amount)} (${plan.costBreakdown.workingCapitalBuffer.percentage}%)
            - Marketing & Local Distribution: ₹${formatCurrency(plan.costBreakdown.marketingAndDistribution.amount)} (${plan.costBreakdown.marketingAndDistribution.percentage}%)
            - Reserve / Contingency Cushion: ₹${formatCurrency(plan.costBreakdown.reserveContingency.amount)} (${plan.costBreakdown.reserveContingency.percentage}%)
            
            ------------------------------------------------------------------------
            4. STATUTORY DATASETS & HYPER-LOCAL FEASIBILITY EVALUATION
            ------------------------------------------------------------------------
            Overall Feasibility Score: ${report.overallFeasibilityScore} / 10.0 (${report.feasibilityGrade})
            Feasibility Data Confidence: ${report.confidenceScore.scorePercentage}% (${report.confidenceScore.confidenceLevel})
            Mission Antyodaya GP Infra Score: ${report.missionAntyodaya.gpInfrastructureScore} / 100.0
            Census 2011 / SHRUG Literacy Rate: ${report.censusShrug.literacyRatePercent}%
            Agmarknet APMC Mandi Benchmark Price: ₹${formatCurrency(report.agmarknetMandi.benchmarkPricePerUnit)} / Quintal
            OSM Commercial POIs Count (5km): ${report.osmPoi.commercialPoiCount5kmRadius} POIs
            Estimated Population Reach: ${report.marketReach.estimatedPopulationReach} residents
            Active Buyer Density: ${report.marketReach.activeBuyerDensityPerSqKm} buyers/sq.km
            Expected Net Profit Margin: ${report.pricingAndValue.expectedProfitMarginPercent}%
            Estimated Break-Even Horizon: ${report.pricingAndValue.BreakEvenPointMonths} Months
            
            Verdict: ${report.summaryVerdict}
            ========================================================================
        """.trimIndent()
    }

    override fun onCleared() {
        super.onCleared()
        SarvamTtsManager.stopSpeaking()
    }

    private fun formatCurrency(amount: Double): String {
        return "%,.2f".format(amount)
    }
}
