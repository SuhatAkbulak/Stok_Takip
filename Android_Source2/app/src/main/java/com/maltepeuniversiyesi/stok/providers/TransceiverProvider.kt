package com.maltepeuniversiyesi.stok.providers


import com.maltepeuniversiyesi.stok.Constants
import com.maltepeuniversiyesi.stok.models.*
import com.maltepeuniversiyesi.stok.retrofitcoroutines.remote.NetworkResponse
import com.maltepeuniversiyesi.stok.services.ApiClient
import com.maltepeuniversiyesi.stok.services.TransceiverService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File


class TransceiverProvider {

    suspend fun token(request: TokenRequestModel): TokenResponseModel {
        val service = getBaseClient().create(TransceiverService::class.java)
        return service.token(request)
    }

    private fun getBaseClient(): Retrofit = ApiClient.getClient(Constants.API_BASE_URL)
    private fun getApiClient(): Retrofit = ApiClient.getClientBearer(Constants.API_URL, Constants.ACCESS_TOKEN)

    suspend fun tasks(): NetworkResponse<TaskResponseModel, RetrofitError> {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.tasks()
    }

    suspend fun changeStockCount(id:Int,request: ChangeStockRequest): NetworkResponse<TaskResponseModel, RetrofitError> {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.changeStockCount(id,request)
    }


    suspend fun addProduct(request: ProductRequestModel): NetworkResponse<TaskResponseModel, RetrofitError> {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.addProduct(request)
    }


}