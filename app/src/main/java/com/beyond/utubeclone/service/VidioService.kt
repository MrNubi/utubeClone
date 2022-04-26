package com.beyond.utubeclone.service

import com.beyond.utubeclone.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VidioService {
    @GET("/v3/5f42ed33-ed40-4543-9aaf-e9fdecbb2710")
    fun getVidioList():Call<VideoDto>
}