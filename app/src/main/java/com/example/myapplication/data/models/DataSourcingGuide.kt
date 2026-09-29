package com.example.myapplication.data.models

import kotlinx.serialization.Serializable

/**
 * Item representing a formal rural data source reference.
 */
@Serializable
data class DataSourcingGuideItem(
    val id: String,
    val sourceName: String,
    val nodalBody: String,
    val summary: String,
    val dataPointsProvided: List<String>,
    val usageForMicroEntrepreneur: String,
    val accessMethod: String,
    val officialPortalUrl: String,
    val reliabilityLevel: String = "High (Government / Statutory Data)"
)

/**
 * Formal data sources reference guide for rural micro-entrepreneurs.
 */
@Serializable
data class DataSourcingGuide(
    val items: List<DataSourcingGuideItem>,
    val guidanceOverview: String = "Formal government registers, sectoral potential plans, and spatial GIS infrastructure platforms provide authoritative ground-truth data for rural business planning."
)
