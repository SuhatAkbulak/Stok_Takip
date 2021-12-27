package com.maltepeuniversiyesi.stok.repositories

import com.maltepeuniversiyesi.stok.models.*
import com.maltepeuniversiyesi.stok.providers.TransceiverProvider


class TransceiverRepository : BaseRepository {
    private var provider = TransceiverProvider()
    suspend fun token(request: TokenRequestModel) = provider.token(request)
    suspend fun tasks() = provider.tasks()
    suspend fun changeStockCount(id: Int,request: ChangeStockRequest) = provider.changeStockCount(id,request)
    suspend fun addProduct(request: ProductRequestModel) = provider.addProduct(request)
}