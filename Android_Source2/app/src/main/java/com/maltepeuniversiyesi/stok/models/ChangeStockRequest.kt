package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChangeStockRequest(
    @SerializedName("action"    ) var action   : String? = null,
    @SerializedName("new_query" ) var newQuery : String? = null,
    @SerializedName("new_name"  ) var newName  : String? = null,
    @SerializedName("token"     ) var token    : String? = null
)