package com.maltepeuniversiyesi.stok.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CompanyModel (

	@SerializedName("id") val id : Int,
	@SerializedName("title") val title : String,
	@SerializedName("tax_no") val tax_no : Int,
	@SerializedName("city") val city : String,
	@SerializedName("district") val district : String,
	@SerializedName("created_at") val created_at : String,
	@SerializedName("updated_at") val updated_at : String,
	@SerializedName("deleted_at") val deleted_at : String,
	@SerializedName("sms_username") val sms_username : String,
	@SerializedName("sms_password") val sms_password : Int,
	@SerializedName("barcode_reader_active") val barcode_reader_active : Boolean,
	@SerializedName("nfc_reader_active") val nfc_reader_active : Boolean
)