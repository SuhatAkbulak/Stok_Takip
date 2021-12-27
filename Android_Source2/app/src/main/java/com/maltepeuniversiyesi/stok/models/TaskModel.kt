package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TaskModel(
    @SerializedName("stok_sayi") val stok_sayi : Int,
    @SerializedName("urun_barkod") val urun_barkod : String,
    @SerializedName("urun_id") val urun_id : Int,
    @SerializedName("urun_isim") val urun_isim : String
) : BaseModel {


}
