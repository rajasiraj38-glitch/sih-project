package com.example.myapplication.data.models

import kotlinx.serialization.Serializable

/**
 * Data model representing rural location parameters.
 */
@Serializable
data class LocationData(
    val village: String = "Rampur",
    val block: String = "Chhatarpur",
    val district: String = "Chhatarpur",
    val state: String = "Madhya Pradesh",
    val lgdVillageCode: String = "LGD-582910",
    val lgdBlockCode: String = "LGD-BLK-3849"
) {
    fun getFormattedLocation(): String = "$village, $block Block, $district, $state (LGD: $lgdVillageCode)"
}

/**
 * Proposed business category for micro-enterprise advisory.
 */
@Serializable
enum class BusinessCategory(
    val displayName: String,
    val description: String,
    val defaultProfitMarginRange: String,
    val capExRatio: Double,
    val rawMaterialRatio: Double,
    val workingCapRatio: Double,
    val marketingRatio: Double,
    val reserveRatio: Double
) {
    DAIRY_LIVESTOCK(
        displayName = "Dairy & Livestock",
        description = "Cattle farming, milk collection, ghee/butter processing, and livestock management.",
        defaultProfitMarginRange = "18% - 24%",
        capExRatio = 0.55,
        rawMaterialRatio = 0.20,
        workingCapRatio = 0.15,
        marketingRatio = 0.05,
        reserveRatio = 0.05
    ),
    RETAIL_SHOP(
        displayName = "Retail Shop & Grocery",
        description = "Kirana store, general provision shop, daily essentials, and local retail trade.",
        defaultProfitMarginRange = "12% - 16%",
        capExRatio = 0.30,
        rawMaterialRatio = 0.45,
        workingCapRatio = 0.15,
        marketingRatio = 0.05,
        reserveRatio = 0.05
    ),
    TEXTILES_APPAREL(
        displayName = "Textiles & Apparel",
        description = "Garment tailoring, boutique, handloom weaving, embroidery, and ready-made clothing.",
        defaultProfitMarginRange = "22% - 30%",
        capExRatio = 0.40,
        rawMaterialRatio = 0.35,
        workingCapRatio = 0.15,
        marketingRatio = 0.05,
        reserveRatio = 0.05
    ),
    HANDICRAFTS_ARTISANS(
        displayName = "Handicrafts & Artisans",
        description = "Pottery, woodwork, terracotta, traditional crafts, and artisan product creation.",
        defaultProfitMarginRange = "28% - 38%",
        capExRatio = 0.25,
        rawMaterialRatio = 0.40,
        workingCapRatio = 0.20,
        marketingRatio = 0.10,
        reserveRatio = 0.05
    ),
    FOOD_PROCESSING(
        displayName = "Food Processing & Bakery",
        description = "Papad, pickle making, grain milling, bakery items, spices packaging, and snack units.",
        defaultProfitMarginRange = "20% - 28%",
        capExRatio = 0.45,
        rawMaterialRatio = 0.30,
        workingCapRatio = 0.15,
        marketingRatio = 0.05,
        reserveRatio = 0.05
    ),
    MICRO_MANUFACTURING(
        displayName = "Micro Manufacturing",
        description = "Paper cups, bricks/blocks, small machine parts, bamboo items, and light fabrication.",
        defaultProfitMarginRange = "20% - 26%",
        capExRatio = 0.50,
        rawMaterialRatio = 0.25,
        workingCapRatio = 0.15,
        marketingRatio = 0.05,
        reserveRatio = 0.05
    );

    companion object {
        fun fromDisplayName(name: String): BusinessCategory {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: DAIRY_LIVESTOCK
        }
    }
}

/**
 * Social categories for agency routing and scheme eligibility.
 */
@Serializable
enum class SocialCategory(
    val code: String,
    val displayName: String,
    val primaryNodalAgencyCode: String,
    val primaryNodalAgencyName: String
) {
    OBC(
        code = "OBC",
        displayName = "Other Backward Classes (OBC)",
        primaryNodalAgencyCode = "NBCFDC",
        primaryNodalAgencyName = "National Backward Classes Finance & Development Corporation"
    ),
    SC(
        code = "SC",
        displayName = "Scheduled Castes (SC)",
        primaryNodalAgencyCode = "NSCFDC",
        primaryNodalAgencyName = "National Scheduled Castes Finance & Development Corporation"
    ),
    ST(
        code = "ST",
        displayName = "Scheduled Tribes (ST)",
        primaryNodalAgencyCode = "NSTFDC",
        primaryNodalAgencyName = "National Scheduled Tribes Finance & Development Corporation"
    ),
    SAFAI_KARAMCHARI_SANITATION(
        code = "NSKFDC",
        displayName = "Safai Karamcharis / Sanitation Workers",
        primaryNodalAgencyCode = "NSKFDC",
        primaryNodalAgencyName = "National Safai Karamcharis Finance & Development Corporation"
    ),
    GENERAL(
        code = "GEN",
        displayName = "General Category",
        primaryNodalAgencyCode = "MUDRA/PMEGP",
        primaryNodalAgencyName = "Pradhan Mantri MUDRA Yojana / PMEGP / SRLM"
    );

    companion object {
        fun fromCode(code: String): SocialCategory {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: GENERAL
        }
    }
}

/**
 * Preferred user language choices.
 */
@Serializable
enum class PreferredLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String
) {
    HINDI(code = "hi", englishName = "Hindi", nativeName = "हिन्दी"),
    ENGLISH(code = "en", englishName = "English", nativeName = "English"),
    BENGALI(code = "bn", englishName = "Bengali", nativeName = "বাংলা"),
    MARATHI(code = "mr", englishName = "Marathi", nativeName = "मराठी"),
    TELUGU(code = "te", englishName = "Telugu", nativeName = "తెలుగు"),
    TAMIL(code = "ta", englishName = "Tamil", nativeName = "தமிழ்"),
    GUJARATI(code = "gu", englishName = "Gujarati", nativeName = "ગુજરાતી"),
    URDU(code = "ur", englishName = "Urdu", nativeName = "اردو"),
    KANNADA(code = "kn", englishName = "Kannada", nativeName = "ಕನ್ನಡ"),
    ODIA(code = "or", englishName = "Odia", nativeName = "ଓଡ଼ିଆ"),
    MALAYALAM(code = "ml", englishName = "Malayalam", nativeName = "മലയാളം"),
    PUNJABI(code = "pa", englishName = "Punjabi", nativeName = "ਪੰਜਾਬੀ"),
    ASSAMESE(code = "as", englishName = "Assamese", nativeName = "অসমীয়া"),
    MAITHILI(code = "mai", englishName = "Maithili", nativeName = "मैथिली"),
    SANTALI(code = "sat", englishName = "Santali", nativeName = "ᱥᱟᱱᱛᱟᱲᱤ"),
    KASHMIRI(code = "ks", englishName = "Kashmiri", nativeName = "कॉशुर"),
    NEPALI(code = "ne", englishName = "Nepali", nativeName = "नेपाली"),
    KONKANI(code = "kok", englishName = "Konkani", nativeName = "कोंकणी"),
    DOGRI(code = "doi", englishName = "Dogri", nativeName = "डोगरी"),
    MANIPURI(code = "mni", englishName = "Manipuri", nativeName = "মৈতৈলোন্"),
    BODO(code = "brx", englishName = "Bodo", nativeName = "बर'"),
    SANSKRIT(code = "sa", englishName = "Sanskrit", nativeName = "संस्कृतम्");

    val sarvamCode: String get() = "${code}-IN"
}

/**
 * User inputs aggregate data model for the advisory engine.
 */
@Serializable
data class UserInputs(
    val location: LocationData = LocationData(),
    val availableMarginCapital: Double = 100000.0, // Default ₹1,00,000
    val proposedBusinessCategory: BusinessCategory = BusinessCategory.DAIRY_LIVESTOCK,
    val socialCategory: SocialCategory = SocialCategory.OBC,
    val preferredLanguage: PreferredLanguage = PreferredLanguage.HINDI,
    val businessName: String = "Gramin Shakti Micro-Enterprise"
)
