package com.example.myapplication.data.models

import kotlinx.serialization.Serializable

/**
 * Single item in Cost Breakdown Structure (for visual pie charts/diagrams).
 */
@Serializable
data class CostBreakdownItem(
    val categoryName: String,
    val amount: Double,
    val percentage: Double,
    val description: String
)

/**
 * Cost Breakdown Structure of Project Cost.
 */
@Serializable
data class CostBreakdownStructure(
    val capitalExpenditure: CostBreakdownItem,
    val rawMaterialsAndInventory: CostBreakdownItem,
    val workingCapitalBuffer: CostBreakdownItem,
    val marketingAndDistribution: CostBreakdownItem,
    val reserveContingency: CostBreakdownItem
) {
    fun toList(): List<CostBreakdownItem> = listOf(
        capitalExpenditure,
        rawMaterialsAndInventory,
        workingCapitalBuffer,
        marketingAndDistribution,
        reserveContingency
    )
}

/**
 * Single quarter entry in repayment schedule.
 */
@Serializable
data class QuarterlyRepaymentEntry(
    val quarterNumber: Int,
    val quarterLabel: String,
    val isMoratoriumQuarter: Boolean,
    val openingBalance: Double,
    val interestPayment: Double,
    val principalPayment: Double,
    val totalQuarterlyPayment: Double,
    val closingBalance: Double
)

/**
 * Nodal Agency details for social category routing.
 */
@Serializable
data class NodalAgencyInfo(
    val agencyCode: String,
    val agencyFullName: String,
    val description: String,
    val targetGroup: String,
    val websiteUrl: String,
    val helplineNumber: String = "1800-180-1122"
)

/**
 * Scheme details auto-selected based on project cost and social category.
 */
@Serializable
data class SchemeInfo(
    val schemeName: String,
    val interestRatePerAnnum: Double,
    val tenureYears: Int,
    val tenureQuarters: Int,
    val moratoriumMonths: Int,
    val moratoriumQuarters: Int,
    val nodalAgency: NodalAgencyInfo,
    val summaryDescription: String,
    val maxSubsidyPercentage: Double = 35.0
)

/**
 * Complete Financial Plan model holding loan structuring, scheme info, repayment schedule, and CBS.
 * Deterministic financial calculations are strictly stored here, separated from AI narrative guidance.
 */
@Serializable
data class FinancialPlan(
    val availableMarginCapital: Double,
    val marginPercentage: Double = 10.0, // 10% promoter contribution
    val projectCost: Double,
    val maxLoanAmount: Double,
    val marginContribution: Double,
    val subsidyAmount: Double = 0.0,
    val netBankLoanAmount: Double = 0.0,
    val schemeInfo: SchemeInfo,
    val costBreakdown: CostBreakdownStructure,
    val repaymentSchedule: List<QuarterlyRepaymentEntry>,
    val quarterlyEmiPostMoratorium: Double,
    val totalInterestPayable: Double,
    val totalAmountRepaid: Double,
    val aiNarrativeExplanation: String = "Deterministic calculation based on MoSJE corporate norms (90% loan, 10% promoter margin)."
)
