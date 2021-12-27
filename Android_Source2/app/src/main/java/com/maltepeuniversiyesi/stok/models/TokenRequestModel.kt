package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TokenRequestModel (

        @SerializedName("username") val username : String,
        @SerializedName("password") val password : String
)