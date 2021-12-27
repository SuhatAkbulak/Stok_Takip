package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProductRequestModel(
    @SerializedName("barkod") var barkod: String? = null,
    @SerializedName("urun_ismi") var urunIsmi: String? = null,
    @SerializedName("stok_adet") var stokAdet: String? = null,
    @SerializedName("token") var token: String? = null
)