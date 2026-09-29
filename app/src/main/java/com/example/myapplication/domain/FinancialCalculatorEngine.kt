package com.example.myapplication.domain

import com.example.myapplication.data.models.BusinessCategory
import com.example.myapplication.data.models.CostBreakdownItem
import com.example.myapplication.data.models.CostBreakdownStructure
import com.example.myapplication.data.models.FinancialPlan
import com.example.myapplication.data.models.QuarterlyRepaymentEntry
import com.example.myapplication.data.models.SocialCategory
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Engine for Financial Structuring, Repayment Schedule Generation, and Cost Breakdown Structure calculations.
 * Ensures strict separation between deterministic financial formulas and AI narrative guidance.
 */
object FinancialCalculatorEngine {

    /**
     * Calculates the full financial plan given available margin capital, social category, and proposed business category.
     *
     * Formula rules:
     * - Project Cost = Available Margin Capital / 0.10 (since margin contribution is 10%)
     * - Maximum Loan Amount = 90% of Project Cost
     * - Margin Contribution = 10% of Project Cost (equals Available Margin Capital)
     */
    fun calculateFinancialPlan(
        availableMarginCapital: Double,
        socialCategory: SocialCategory,
        businessCategory: BusinessCategory
    ): FinancialPlan {
        // Handle positive margin capital safeguard
        val safeMargin = if (availableMarginCapital < 1000.0) 1000.0 else availableMarginCapital

        // 10% contribution -> Project Cost = Margin / 0.10
        val projectCost = roundToTwoDecimals(safeMargin / 0.10)
        val maxLoanAmount = roundToTwoDecimals(projectCost * 0.90)
        val marginContribution = roundToTwoDecimals(projectCost * 0.10)

        // Scheme Selection and Nodal Agency Routing
        val schemeInfo = SchemeRouterEngine.selectScheme(projectCost, socialCategory)

        // Subsidy calculation based on MoSJE corporate norms
        val subsidyPct = if (socialCategory != SocialCategory.GENERAL) 25.0 else 15.0
        val subsidyAmount = roundToTwoDecimals(projectCost * (subsidyPct / 100.0))
        val netBankLoanAmount = roundToTwoDecimals((maxLoanAmount - subsidyAmount).coerceAtLeast(0.0))

        // Repayment Schedule Calculation
        val (schedule, quarterlyEmiPostMoratorium, totalInterestPayable) = generateRepaymentSchedule(
            loanAmount = maxLoanAmount,
            interestRatePAnnum = schemeInfo.interestRatePerAnnum,
            totalQuarters = schemeInfo.tenureQuarters,
            moratoriumQuarters = schemeInfo.moratoriumQuarters
        )

        val totalAmountRepaid = roundToTwoDecimals(maxLoanAmount + totalInterestPayable)

        // Cost Breakdown Structure based on Business Category
        val costBreakdown = generateCostBreakdownStructure(projectCost, businessCategory)

        // AI Narrative Explanation mode text (separated from deterministic math)
        val narrative = "DETERMINISTIC FINANCIAL ENGINE EXPLANATION:\n" +
                "1. Total Project Cost calculated strictly at ₹${projectCost.toInt()} based on 10% promoter equity margin (₹${safeMargin.toInt()}).\n" +
                "2. Concessional Institutional Loan structured at 90% (₹${maxLoanAmount.toInt()}) routed via ${schemeInfo.nodalAgency.agencyCode} (${schemeInfo.nodalAgency.agencyFullName}).\n" +
                "3. Applicable Capital Subsidy: ${subsidyPct.toInt()}% (₹${subsidyAmount.toInt()}), reducing effective loan burden.\n" +
                "4. Moratorium Period: First ${schemeInfo.moratoriumMonths} months (${schemeInfo.moratoriumQuarters} quarter/s) requires interest-only servicing.\n" +
                "5. Post-moratorium EMI: Exact quarterly reducing balance payment of ₹${quarterlyEmiPostMoratorium.toInt()} over ${schemeInfo.tenureYears} years."

        return FinancialPlan(
            availableMarginCapital = safeMargin,
            marginPercentage = 10.0,
            projectCost = projectCost,
            maxLoanAmount = maxLoanAmount,
            marginContribution = marginContribution,
            subsidyAmount = subsidyAmount,
            netBankLoanAmount = netBankLoanAmount,
            schemeInfo = schemeInfo,
            costBreakdown = costBreakdown,
            repaymentSchedule = schedule,
            quarterlyEmiPostMoratorium = quarterlyEmiPostMoratorium,
            totalInterestPayable = totalInterestPayable,
            totalAmountRepaid = totalAmountRepaid,
            aiNarrativeExplanation = narrative
        )
    }

    /**
     * Generates quarterly repayment schedule with interest-only moratorium and reducing balance EMI.
     */
    private fun generateRepaymentSchedule(
        loanAmount: Double,
        interestRatePAnnum: Double,
        totalQuarters: Int,
        moratoriumQuarters: Int
    ): Triple<List<QuarterlyRepaymentEntry>, Double, Double> {
        val schedule = mutableListOf<QuarterlyRepaymentEntry>()
        val quarterlyInterestRate = (interestRatePAnnum / 100.0) / 4.0
        val remainingQuarters = totalQuarters - moratoriumQuarters

        // Post-moratorium quarterly EMI formula: PMT = P * [r * (1+r)^n] / [(1+r)^n - 1]
        val postMoratoriumEmi = if (remainingQuarters > 0 && quarterlyInterestRate > 0) {
            val factor = (1.0 + quarterlyInterestRate).pow(remainingQuarters.toDouble())
            loanAmount * (quarterlyInterestRate * factor) / (factor - 1.0)
        } else {
            0.0
        }

        var currentOpeningBalance = loanAmount
        var cumulativeInterestPayable = 0.0

        // Phase 1: Moratorium Quarters (Interest-Only)
        for (q in 1..moratoriumQuarters) {
            val interestPayment = roundToTwoDecimals(currentOpeningBalance * quarterlyInterestRate)
            val principalPayment = 0.0
            val totalPayment = interestPayment
            val closingBalance = currentOpeningBalance

            cumulativeInterestPayable += interestPayment

            schedule.add(
                QuarterlyRepaymentEntry(
                    quarterNumber = q,
                    quarterLabel = "Q$q (Moratorium)",
                    isMoratoriumQuarter = true,
                    openingBalance = currentOpeningBalance,
                    interestPayment = interestPayment,
                    principalPayment = principalPayment,
                    totalQuarterlyPayment = totalPayment,
                    closingBalance = closingBalance
                )
            )
        }

        // Phase 2: Repayment Quarters Post-Moratorium
        for (q in (moratoriumQuarters + 1)..totalQuarters) {
            val interestPayment = roundToTwoDecimals(currentOpeningBalance * quarterlyInterestRate)

            val principalPayment: Double
            val closingBalance: Double

            if (q == totalQuarters) {
                // Final quarter adjustment to ensure exact 0.0 closing balance
                principalPayment = currentOpeningBalance
                closingBalance = 0.0
            } else {
                val rawPrincipal = postMoratoriumEmi - interestPayment
                principalPayment = roundToTwoDecimals(rawPrincipal.coerceAtLeast(0.0).coerceAtMost(currentOpeningBalance))
                closingBalance = roundToTwoDecimals((currentOpeningBalance - principalPayment).coerceAtLeast(0.0))
            }

            val totalPayment = roundToTwoDecimals(principalPayment + interestPayment)
            cumulativeInterestPayable += interestPayment

            schedule.add(
                QuarterlyRepaymentEntry(
                    quarterNumber = q,
                    quarterLabel = "Q$q",
                    isMoratoriumQuarter = false,
                    openingBalance = currentOpeningBalance,
                    interestPayment = interestPayment,
                    principalPayment = principalPayment,
                    totalQuarterlyPayment = totalPayment,
                    closingBalance = closingBalance
                )
            )

            currentOpeningBalance = closingBalance
        }

        return Triple(
            schedule,
            roundToTwoDecimals(postMoratoriumEmi),
            roundToTwoDecimals(cumulativeInterestPayable)
        )
    }

    /**
     * Generates Cost Breakdown Structure (CBS) tailored to the proposed business category.
     */
    private fun generateCostBreakdownStructure(
        projectCost: Double,
        category: BusinessCategory
    ): CostBreakdownStructure {
        val capExPct = category.capExRatio * 100
        val rawMatPct = category.rawMaterialRatio * 100
        val workCapPct = category.workingCapRatio * 100
        val mktgPct = category.marketingRatio * 100
        val reservePct = category.reserveRatio * 100

        val capExAmt = roundToTwoDecimals(projectCost * category.capExRatio)
        val rawMatAmt = roundToTwoDecimals(projectCost * category.rawMaterialRatio)
        val workCapAmt = roundToTwoDecimals(projectCost * category.workingCapRatio)
        val mktgAmt = roundToTwoDecimals(projectCost * category.marketingRatio)
        val reserveAmt = roundToTwoDecimals(projectCost * category.reserveRatio)

        val capExDesc = when (category) {
            BusinessCategory.DAIRY_LIVESTOCK -> "High-yield cattle purchase, shed construction, automated milking/chilling equipment"
            BusinessCategory.RETAIL_SHOP -> "Store shelving, refrigeration, billing machine, digital signage & display counters"
            BusinessCategory.TEXTILES_APPAREL -> "Heavy-duty sewing machines, interlocking units, cutting tables & steam irons"
            BusinessCategory.HANDICRAFTS_ARTISANS -> "Artisan toolkits, pottery wheels, kilns, woodcarving machinery & display units"
            BusinessCategory.FOOD_PROCESSING -> "Pulse/grain mill, packaging & sealing machinery, stainless steel processing vessels"
            BusinessCategory.MICRO_MANUFACTURING -> "Die-cutting machine, hydraulic press, welding set, toolings & safety gear"
        }

        val rawMatDesc = when (category) {
            BusinessCategory.DAIRY_LIVESTOCK -> "Initial cattle feed stock, green fodder seeds, mineral mixtures & veterinary medicines"
            BusinessCategory.RETAIL_SHOP -> "Initial stock of fast-moving consumer goods (FMCG), grains, spices & packaged goods"
            BusinessCategory.TEXTILES_APPAREL -> "Initial fabric rolls, threads, zips, buttons, lining cloth & packaging bags"
            BusinessCategory.HANDICRAFTS_ARTISANS -> "Raw clay, timber blocks, natural dyes, varnishes & protective packaging materials"
            BusinessCategory.FOOD_PROCESSING -> "Bulk raw pulses, spices, oil, food-grade packaging film & jars"
            BusinessCategory.MICRO_MANUFACTURING -> "Sheet metal, paper rolls, bamboo stocks, chemicals & hardware consumables"
        }

        return CostBreakdownStructure(
            capitalExpenditure = CostBreakdownItem("Capital Expenditure (Machinery/Shed)", capExAmt, capExPct, capExDesc),
            rawMaterialsAndInventory = CostBreakdownItem("Raw Materials & Initial Inventory", rawMatAmt, rawMatPct, rawMatDesc),
            workingCapitalBuffer = CostBreakdownItem("Working Capital Buffer", workCapAmt, workCapPct, "Cash buffer for 2-3 months operational expenses including rent, electricity, wages & logistics"),
            marketingAndDistribution = CostBreakdownItem("Marketing & Local Distribution", mktgAmt, mktgPct, "Local signage, WhatsApp/flyer campaigns, sample distribution & transport to weekly markets"),
            reserveContingency = CostBreakdownItem("Reserve / Contingency Cushion", reserveAmt, reservePct, "Emergency financial cushion for unexpected price escalation or seasonal delays")
        )
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToLong() / 100.0
    }
}
