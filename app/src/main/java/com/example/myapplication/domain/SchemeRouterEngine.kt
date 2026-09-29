package com.example.myapplication.domain

import com.example.myapplication.data.models.NodalAgencyInfo
import com.example.myapplication.data.models.SchemeInfo
import com.example.myapplication.data.models.SocialCategory

/**
 * Engine for Scheme Auto-Selection and Social Category Nodal Agency Routing.
 */
object SchemeRouterEngine {

    /**
     * Resolves the nodal agency based on social category.
     */
    fun resolveNodalAgency(socialCategory: SocialCategory): NodalAgencyInfo {
        return when (socialCategory) {
            SocialCategory.OBC -> NodalAgencyInfo(
                agencyCode = "NBCFDC",
                agencyFullName = "National Backward Classes Finance & Development Corporation",
                description = "Provides concessional financial assistance to eligible beneficiaries belonging to Backward Classes for income-generating micro-enterprises.",
                targetGroup = "Other Backward Classes (OBC) with annual family income up to ₹3,00,000 in rural areas",
                websiteUrl = "https://nbcfdc.gov.in",
                helplineNumber = "1800-11-2500"
            )

            SocialCategory.SC -> NodalAgencyInfo(
                agencyCode = "NSCFDC",
                agencyFullName = "National Scheduled Castes Finance & Development Corporation",
                description = "Provides concessional loans, skill development training, and margin money support for socio-economic empowerment of Scheduled Castes.",
                targetGroup = "Scheduled Caste (SC) micro-entrepreneurs across rural and semi-urban districts",
                websiteUrl = "https://nscfdc.nic.in",
                helplineNumber = "1800-11-0080"
            )

            SocialCategory.ST -> NodalAgencyInfo(
                agencyCode = "NSTFDC",
                agencyFullName = "National Scheduled Tribes Finance & Development Corporation",
                description = "Offers concessional loan schemes (Adivasi Mahila Sashaktikaran Yojana, Micro Credit) for economic upliftment of Scheduled Tribes.",
                targetGroup = "Scheduled Tribe (ST) individuals, SHGs, and cooperatives in tribal and rural blocks",
                websiteUrl = "https://nstfdc.tribal.gov.in",
                helplineNumber = "1800-11-7788"
            )

            SocialCategory.SAFAI_KARAMCHARI_SANITATION -> NodalAgencyInfo(
                agencyCode = "NSKFDC",
                agencyFullName = "National Safai Karamcharis Finance & Development Corporation",
                description = "Dedicated apex corporation providing concessional loans and skill training for rehabilitation of Safai Karamcharis, Manual Scavengers, and their dependents.",
                targetGroup = "Safai Karamcharis, Sanitation Workers, and Liberated Manual Scavengers",
                websiteUrl = "https://nskfdc.nic.in",
                helplineNumber = "1800-11-0123"
            )

            SocialCategory.GENERAL -> NodalAgencyInfo(
                agencyCode = "MUDRA/PMEGP",
                agencyFullName = "Pradhan Mantri MUDRA Yojana / PMEGP / SRLM",
                description = "Provides collateral-free micro credits via public sector banks, Regional Rural Banks (RRBs), and State Rural Livelihood Missions (SRLM).",
                targetGroup = "All eligible rural micro-entrepreneurs and non-farm micro enterprises",
                websiteUrl = "https://www.mudra.org.in",
                helplineNumber = "1800-180-1111"
            )
        }
    }

    /**
     * Auto-selects scheme and assigns nodal agency based on project cost and social category.
     * Rules:
     * - Micro Finance Scheme: Project Cost <= ₹1,40,000 -> 6.5% interest p.a., 3-year tenure (12 quarters), 3-month moratorium (1st quarter).
     * - Term Loan Scheme: Project Cost > ₹1,40,000 and <= ₹50,00,000 -> 8.0% interest p.a., 7-year tenure (28 quarters), 6-month moratorium (1st & 2nd quarters).
     * - Large Term Loan Scheme: Project Cost > ₹50,00,000 -> 8.5% interest p.a., 10-year tenure (40 quarters), 12-month moratorium (1st-4th quarters).
     */
    fun selectScheme(projectCost: Double, socialCategory: SocialCategory): SchemeInfo {
        val nodalAgency = resolveNodalAgency(socialCategory)

        return if (projectCost <= 140000.0) {
            SchemeInfo(
                schemeName = "Micro Finance Scheme",
                interestRatePerAnnum = 6.5,
                tenureYears = 3,
                tenureQuarters = 12,
                moratoriumMonths = 3,
                moratoriumQuarters = 1,
                nodalAgency = nodalAgency,
                summaryDescription = "Concessional Micro Finance Scheme for projects up to ₹1,40,000. Features low 6.5% p.a. interest, 3-year tenure (12 quarters), and 3-month moratorium (1st quarter interest-only)."
            )
        } else if (projectCost <= 5000000.0) {
            SchemeInfo(
                schemeName = "Term Loan Scheme",
                interestRatePerAnnum = 8.0,
                tenureYears = 7,
                tenureQuarters = 28,
                moratoriumMonths = 6,
                moratoriumQuarters = 2,
                nodalAgency = nodalAgency,
                summaryDescription = "Term Loan Scheme for project costs between ₹1,40,000 and ₹50,00,000. Features 8.0% p.a. interest rate, 7-year tenure (28 quarters), and 6-month moratorium (Q1 & Q2 interest-only)."
            )
        } else {
            SchemeInfo(
                schemeName = "Large Enterprise Term Loan Scheme",
                interestRatePerAnnum = 8.5,
                tenureYears = 10,
                tenureQuarters = 40,
                moratoriumMonths = 12,
                moratoriumQuarters = 4,
                nodalAgency = nodalAgency,
                summaryDescription = "Specialized Large Term Loan Scheme for projects above ₹50,00,000. Features 8.5% p.a. interest rate, 10-year tenure (40 quarters), and 12-month moratorium (Q1-Q4 interest-only)."
            )
        }
    }
}
