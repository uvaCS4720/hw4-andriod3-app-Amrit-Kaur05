package edu.nd.pmcburne.hello

import retrofit2.http.GET

// working with UVA server

interface ApiUVAService {
    @GET("~wxt4gm/placemarks.json")
    suspend fun getPlacemarks(): List<LocationResponse>
}