package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenResponseModel(
    @SerializedName("exp") var exp: Double? = null,
    @SerializedName("login_time") var loginTime: Double? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("yetki") var yetki: Int? = null
)