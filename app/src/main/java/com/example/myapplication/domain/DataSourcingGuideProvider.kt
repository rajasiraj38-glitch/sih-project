package com.example.myapplication.domain

import com.example.myapplication.data.models.BusinessCategory
import com.example.myapplication.data.models.DataSourcingGuide
import com.example.myapplication.data.models.DataSourcingGuideItem
import com.example.myapplication.data.models.LocationData

/**
 * Provider for generating formal hyper-local rural data sourcing strategy guides.
 */
object DataSourcingGuideProvider {

    /**
     * Builds a tailored DataSourcingGuide for the specified location and business category.
     */
    fun getGuide(location: LocationData, category: BusinessCategory): DataSourcingGuide {
        val items = listOf(
            DataSourcingGuideItem(
                id = "GPER",
                sourceName = "Gram Panchayat Enterprise Register (GPER)",
                nodalBody = "Department of Panchayati Raj & Gram Panchayat Office",
                summary = "Statutory village commercial register maintained at ${location.village} Gram Panchayat office listing local trade licenses, shops, artisans, and active enterprises.",
                dataPointsProvided = listOf(
                    "Active commercial licenses in ${location.village}",
                    "Local trader and artisan inventory",
                    "Gram Sabha approved local market spaces & weekly Haat stall allocations",
                    "Panchayat tax assessments & commercial asset holdings"
                ),
                usageForMicroEntrepreneur = "Verify existing local enterprise density in ${location.village} to identify direct competitors, partner with licensed village shopkeepers, and secure Gram Panchayat NOC for setup.",
                accessMethod = "Visit Gram Panchayat Office or access e-GramSwaraj portal (egramswaraj.gov.in)",
                officialPortalUrl = "https://egramswaraj.gov.in"
            ),

            DataSourcingGuideItem(
                id = "DIC",
                sourceName = "District Industrial Centre (DIC) Micro-Cluster Data",
                nodalBody = "Ministry of Micro, Small and Medium Enterprises (MSME) & ${location.district} DIC",
                summary = "Official district-level repository of registered MSME units, One District One Product (ODOP) clusters, industrial land availability, and technology subsidy approvals.",
                dataPointsProvided = listOf(
                    "Udyam registration records for ${location.district} district",
                    "ODOP cluster maps and focus products",
                    "Capital subsidy & margin money reimbursement status",
                    "Common Facility Centre (CFC) locations and raw material supply banks"
                ),
                usageForMicroEntrepreneur = "Check eligibility for 15-25% PMEGP capital subsidy, verify ${category.displayName} cluster incentive policies in ${location.district}, and register under Udyam for priority bank lending.",
                accessMethod = "General Manager (GM) DIC Office, ${location.district} District Collectorate, or Udyam Registration Portal",
                officialPortalUrl = "https://udyamregistration.gov.in"
            ),

            DataSourcingGuideItem(
                id = "CENSUS",
                sourceName = "Census Demographics & Socio-Economic Survey",
                nodalBody = "Office of the Registrar General & Census Commissioner of India / SECC Data",
                summary = "Block and Gram Panchayat level demographic metrics including household counts, literacy rates, occupational distribution, and working population profile.",
                dataPointsProvided = listOf(
                    "Total working-age population in ${location.block} Block",
                    "Household purchasing power proxies and housing type classification",
                    "Female labor force participation and youth skill profile",
                    "Village electrification and telecommunication penetration rate"
                ),
                usageForMicroEntrepreneur = "Estimate local consumer demand, calculate addressable target population within 10km radius of ${location.village}, and assess local purchasing capacity.",
                accessMethod = "District Statistical Handbook or Census India Data Portal",
                officialPortalUrl = "https://censusindia.gov.in"
            ),

            DataSourcingGuideItem(
                id = "NABARD_PLP",
                sourceName = "NABARD Sectoral Potential Linked Credit Plan (PLP)",
                nodalBody = "National Bank for Agriculture and Rural Development (NABARD)",
                summary = "Annual statutory credit plan mapping physical potential, infrastructure gaps, and sector-wise credit absorption capacity for ${location.district} district.",
                dataPointsProvided = listOf(
                    "District credit potential for ${category.displayName} sector in ${location.district}",
                    "Recommended bank loan targets per block",
                    "Critical infrastructure gaps (cold storages, raw material depots, testing labs)",
                    "Subsidized refinance scheme terms for regional rural banks"
                ),
                usageForMicroEntrepreneur = "Include official NABARD PLP credit estimates in bank project report (DPR) to accelerate loan appraisal by local Regional Rural Banks (RRBs) and Lead Bank.",
                accessMethod = "NABARD District Development Manager (DDM) Office or NABARD PLP Downloads Portal",
                officialPortalUrl = "https://www.nabard.org"
            ),

            DataSourcingGuideItem(
                id = "PM_GEMINI_GATI_SHAKTI",
                sourceName = "PM Gati Shakti / PM GEMINI Rural Infrastructure Data",
                nodalBody = "BISAG-N / Ministry of Commerce & Industry / PM Gati Shakti NMP",
                summary = "National Master Plan GIS spatial platform integrating rural road networks, PMGSY connectivity, freight corridors, power grid sub-stations, and local mandi locations.",
                dataPointsProvided = listOf(
                    "All-weather PMGSY road connectivity score for ${location.village}",
                    "Proximity to nearest railway siding, cold chain, and primary agricultural market (Mandi)",
                    "Power feeder availability and 4G/5G telecom tower spatial coverage",
                    "Supply chain transit time estimates to district commercial hubs"
                ),
                usageForMicroEntrepreneur = "Optimize raw material transport routes, evaluate market transport cost to ${location.block} mandi, and select the most accessible location for production/retail.",
                accessMethod = "PM Gati Shakti Portal (gatishakti.gov.in) & State Remote Sensing Application Centre",
                officialPortalUrl = "https://gatishakti.gov.in"
            )
        )

        return DataSourcingGuide(
            items = items,
            guidanceOverview = "Authoritative rural data sources for ${location.village} (${location.block} Block, ${location.district} District). Leveraging statutory registers, NABARD PLPs, and PM Gati Shakti GIS spatial mapping significantly strengthens bank loan appraisal and operational planning for ${category.displayName}."
        )
    }
}
