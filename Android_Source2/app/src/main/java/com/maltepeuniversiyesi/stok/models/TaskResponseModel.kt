package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TaskResponseModel(
        @SerializedName("status") val status : String,
        @SerializedName("data") val data : List<TaskModel>,
        @SerializedName("message") val message : String
)
