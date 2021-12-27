package com.maltepeuniversiyesi.stok.helpers

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileHelper {
    public fun createImageFile(context: Context,bitmap: Bitmap?): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
           val currentPhotoPath = absolutePath
            try {
                if(bitmap == null)return@apply
                FileOutputStream(currentPhotoPath).use { out ->
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        out
                    ) // bmp is your Bitmap instance
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
}