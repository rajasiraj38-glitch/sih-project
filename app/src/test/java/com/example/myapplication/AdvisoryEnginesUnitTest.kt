package com.example.myapplication

import com.example.myapplication.data.models.BusinessCategory
import com.example.myapplication.data.models.LocationData
import com.example.myapplication.data.models.PreferredLanguage
import com.example.myapplication.data.models.SocialCategory
import com.example.myapplication.data.models.UserInputs
import com.example.myapplication.domain.DataSourcingGuideProvider
import com.example.myapplication.domain.FeasibilityEngine
import com.example.myapplication.domain.FinancialCalculatorEngine
import com.example.myapplication.domain.SchemeRouterEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvisoryEnginesUnitTest {

    @Test
    fun testPreferredLanguages_22ScheduledIndicLanguagesPlusEnglish() {
        val languages = PreferredLanguage.entries
        assertEquals(22, languages.size)

        // Verify key Indic languages and Sarvam ISO code mappings
        val hindi = PreferredLanguage.HINDI
        assertEquals("Hindi", hindi.englishName)
        assertEquals("हिन्दी", hindi.nativeName)
        assertEquals("hi-IN", hindi.sarvamCode)

        val english = PreferredLanguage.ENGLISH
        assertEquals("English", english.englishName)
        assertEquals("en-IN", english.sarvamCode)

        val bengali = PreferredLanguage.BENGALI
        assertEquals("Bengali", bengali.englishName)
        assertEquals("bn-IN", bengali.sarvamCode)

        val sanskrit = PreferredLanguage.SANSKRIT
        assertEquals("Sanskrit", sanskrit.englishName)
        assertEquals("sa-IN", sanskrit.sarvamCode)

        val santali = PreferredLanguage.SANTALI
        assertEquals("Santali", santali.englishName)
        assertEquals("sat-IN", santali.sarvamCode)

        val bodo = PreferredLanguage.BODO
        assertEquals("Bodo", bodo.englishName)
        assertEquals("brx-IN", bodo.sarvamCode)
    }

    @Test
    fun testLocationData_LgdCodes() {
        val location = LocationData(
            village = "Rampur",
            block = "Chhatarpur",
            district = "Chhatarpur",
            state = "Madhya Pradesh",
            lgdVillageCode = "216890",
            lgdBlockCode = "1820"
        )

        assertEquals("216890", location.lgdVillageCode)
        assertEquals("1820", location.lgdBlockCode)
    }

    @Test
    fun testFinancialStructuring_MicroFinanceScheme() {
        // ₹12,000 margin capital -> ₹1,20,000 project cost (<= ₹1,40,000)
        val marginCapital = 12000.0
        val plan = FinancialCalculatorEngine.calculateFinancialPlan(
            availableMarginCapital = marginCapital,
            socialCategory = SocialCategory.SC,
            businessCategory = BusinessCategory.HANDICRAFTS_ARTISANS
        )

        assertEquals(120000.0, plan.projectCost, 0.01)
        assertEquals(108000.0, plan.maxLoanAmount, 0.01)
        assertEquals(12000.0, plan.marginContribution, 0.01)

        val scheme = plan.schemeInfo
        assertEquals("Micro Finance Scheme", scheme.schemeName)
        assertEquals(6.5, scheme.interestRatePerAnnum, 0.01)
        assertEquals(3, scheme.tenureYears)
        assertEquals(12, scheme.tenureQuarters)
        assertEquals(3, scheme.moratoriumMonths)
        assertEquals(1, scheme.moratoriumQuarters)

        // Agency Routing for SC (NSCFDC / NSFDC)
        assertEquals("NSCFDC", scheme.nodalAgency.agencyCode)

        // Repayment schedule check
        assertEquals(12, plan.repaymentSchedule.size)
        val firstQuarter = plan.repaymentSchedule[0]
        assertTrue(firstQuarter.isMoratoriumQuarter)
        assertEquals(0.0, firstQuarter.principalPayment, 0.01)
        assertTrue(firstQuarter.interestPayment > 0.0)

        // Last quarter closing balance must be 0.0
        val lastQuarter = plan.repaymentSchedule.last()
        assertEquals(0.0, lastQuarter.closingBalance, 0.01)

        // Check deterministic calculations separation from AI narrative
        assertTrue(plan.aiNarrativeExplanation.contains("Deterministic Financial Engine"))
        assertTrue(plan.aiNarrativeExplanation.contains("10:90 Margin-to-Loan ratio"))
    }

    @Test
    fun testFinancialStructuring_TermLoanScheme() {
        // ₹1,00,000 margin capital -> ₹10,00,000 project cost (> ₹1,40,000 and <= ₹50,00,000)
        val marginCapital = 100000.0
        val plan = FinancialCalculatorEngine.calculateFinancialPlan(
            availableMarginCapital = marginCapital,
            socialCategory = SocialCategory.OBC,
            businessCategory = BusinessCategory.DAIRY_LIVESTOCK
        )

        assertEquals(1000000.0, plan.projectCost, 0.01)
        assertEquals(900000.0, plan.maxLoanAmount, 0.01)
        assertEquals(100000.0, plan.marginContribution, 0.01)

        val scheme = plan.schemeInfo
        assertEquals("Term Loan Scheme", scheme.schemeName)
        assertEquals(8.0, scheme.interestRatePerAnnum, 0.01)
        assertEquals(7, scheme.tenureYears)
        assertEquals(28, scheme.tenureQuarters)
        assertEquals(6, scheme.moratoriumMonths)
        assertEquals(2, scheme.moratoriumQuarters)

        // Agency Routing for OBC
        assertEquals("NBCFDC", scheme.nodalAgency.agencyCode)

        // Moratorium quarters check (Q1 & Q2)
        assertEquals(28, plan.repaymentSchedule.size)
        assertTrue(plan.repaymentSchedule[0].isMoratoriumQuarter)
        assertTrue(plan.repaymentSchedule[1].isMoratoriumQuarter)
        assertEquals(0.0, plan.repaymentSchedule[0].principalPayment, 0.01)
        assertEquals(0.0, plan.repaymentSchedule[1].principalPayment, 0.01)

        // Post-moratorium Q3
        assertTrue(!plan.repaymentSchedule[2].isMoratoriumQuarter)
        assertTrue(plan.repaymentSchedule[2].principalPayment > 0.0)

        // Cost Breakdown Structure Sum Check
        val cbs = plan.costBreakdown
        val cbsTotal = cbs.capitalExpenditure.amount +
                cbs.rawMaterialsAndInventory.amount +
                cbs.workingCapitalBuffer.amount +
                cbs.marketingAndDistribution.amount +
                cbs.reserveContingency.amount

        assertEquals(plan.projectCost, cbsTotal, 0.01)
    }

    @Test
    fun testSocialCategoryAgencyRouting() {
        assertEquals("NBCFDC", SchemeRouterEngine.resolveNodalAgency(SocialCategory.OBC).agencyCode)
        assertEquals("NSCFDC", SchemeRouterEngine.resolveNodalAgency(SocialCategory.SC).agencyCode)
        assertEquals("NSTFDC", SchemeRouterEngine.resolveNodalAgency(SocialCategory.ST).agencyCode)
        assertEquals("NSKFDC", SchemeRouterEngine.resolveNodalAgency(SocialCategory.SAFAI_KARAMCHARI_SANITATION).agencyCode)
        assertEquals("MUDRA/PMEGP", SchemeRouterEngine.resolveNodalAgency(SocialCategory.GENERAL).agencyCode)
    }

    @Test
    fun testFeasibilityEngine_StatutoryDataAndConfidenceScore() {
        val inputs = UserInputs(
            location = LocationData(
                village = "Rampur",
                block = "Chhatarpur",
                district = "Chhatarpur",
                state = "Madhya Pradesh",
                lgdVillageCode = "216890",
                lgdBlockCode = "1820"
            ),
            availableMarginCapital = 100000.0,
            proposedBusinessCategory = BusinessCategory.DAIRY_LIVESTOCK,
            socialCategory = SocialCategory.OBC
        )

        val report = FeasibilityEngine.generateReport(inputs)

        assertNotNull(report)
        assertTrue(report.overallFeasibilityScore >= 5.0)

        // Verify Statutory Datasets Integration
        assertTrue(report.missionAntyodaya.gpInfrastructureScore >= 0.0)
        assertTrue(report.censusShrug.totalHouseholdCount > 0)
        assertNotNull(report.agmarknetMandi.nearestMandiName)
        assertNotNull(report.osmPoi.footfallDensityLevel)

        // Verify Dynamic Feasibility Confidence Score
        assertEquals(88.0, report.confidenceScore.scorePercentage, 0.01)
        assertEquals("High Data Confidence", report.confidenceScore.confidenceLevel)
        assertTrue(report.confidenceScore.validationSources.any { it.contains("Mission Antyodaya") })

        // Verify Data Sparsity Risk Mitigation
        assertTrue(report.dataSparsityMitigation.isNotEmpty())
        assertTrue(report.dataSparsityMitigation.any { it.contains("Gram Panchayat") || it.contains("LGD") })
    }

    @Test
    fun testDataSourcingGuideProvider() {
        val guide = DataSourcingGuideProvider.getGuide(
            location = LocationData("Rampur", "Chhatarpur", "Chhatarpur", "Madhya Pradesh"),
            category = BusinessCategory.RETAIL_SHOP
        )

        assertEquals(5, guide.items.size)
        val ids = guide.items.map { it.id }
        assertTrue(ids.contains("GPER"))
        assertTrue(ids.contains("DIC"))
        assertTrue(ids.contains("CENSUS"))
        assertTrue(ids.contains("NABARD_PLP"))
        assertTrue(ids.contains("PM_GEMINI_GATI_SHAKTI"))
    }
}
