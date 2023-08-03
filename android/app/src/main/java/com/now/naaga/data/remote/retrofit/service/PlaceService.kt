package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.PlaceDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceService {
    @GET("/places")
    fun getMyPlace(
        @Query("sort-by") sort: String,
        @Query("order") order: String,
    ): Call<List<PlaceDto>>

    @GET("/places/{placeId}")
    fun getPlace(
        @Path("placeId") placeId: Long,
    ): Call<PlaceDto>

    @Multipart
    @POST("/places")
    fun submitPlace(
        @Part placeDto: PlaceDto,
        @Part file: MultipartBody.Part,
    ): Call<PlaceDto>
}
