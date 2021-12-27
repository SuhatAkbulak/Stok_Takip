package com.maltepeuniversiyesi.stok.services


import com.maltepeuniversiyesi.stok.models.*
import com.maltepeuniversiyesi.stok.retrofitcoroutines.remote.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface TransceiverService {

    @POST("api/v1/login")
    suspend fun token(@Body request: TokenRequestModel): TokenResponseModel

    @GET("list_stock_for_app")
    suspend fun tasks(): NetworkResponse<TaskResponseModel, RetrofitError>

    @POST("edit_urun/{id}")
    suspend fun changeStockCount(@Path("id") id: Int?,@Body request: ChangeStockRequest): NetworkResponse<TaskResponseModel, RetrofitError>

    @POST("add_stock")
    suspend fun addProduct(@Body request: ProductRequestModel): NetworkResponse<TaskResponseModel, RetrofitError>
}