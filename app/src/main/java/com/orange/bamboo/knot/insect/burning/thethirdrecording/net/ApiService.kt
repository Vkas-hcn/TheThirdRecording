package com.orange.bamboo.knot.insect.burning.thethirdrecording.net

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
    @GET
    fun getMapData(
        @Url url: String,
        @QueryMap options: Map<String, String>
    ): Call<String>
}
