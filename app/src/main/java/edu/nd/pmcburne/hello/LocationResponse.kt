package edu.nd.pmcburne.hello

// making class to match JSON so Retrofit can read
data class LocationResponse(
    val id: Int,
    val name: String,
    val tag_list: List<String>,
    val description: String,
    val visual_center: VisualCenter
)

data class VisualCenter(
    val latitude: Double,
    val longitude: Double
)
