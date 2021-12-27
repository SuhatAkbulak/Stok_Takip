package com.maltepeuniversiyesi.stok

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.maltepeuniversiyesi.stok.factories.BarcodeViewModelFactory
import com.maltepeuniversiyesi.stok.models.ChangeStockRequest
import com.maltepeuniversiyesi.stok.models.ProductRequestModel
import com.maltepeuniversiyesi.stok.models.Resource

import com.maltepeuniversiyesi.stok.repositories.TransceiverRepository
import android.widget.EditText

import android.widget.LinearLayout






class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnGroupMain: LinearLayout
    private lateinit var btnBarcode: Button
    private lateinit var btnRefresh: Button
    private var LAUNCH_NFC_ACTIVITY_FOR_BARCODE = 2

    private lateinit var transceiverViewModel: TransceiverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGroupMain = findViewById(R.id.btn_group_main)

        btnBarcode = findViewById(R.id.btn_barcode)

        btnBarcode.setOnClickListener {
            openReadBarcodeActivity()
        }
        btnRefresh = findViewById(R.id.btn_refresh)
        btnRefresh.setOnClickListener {
            transceiverViewModel.refresh()
        }
        imageView = findViewById(R.id.imageView)

        transceiverViewModel = ViewModelProvider(
            this,
            BarcodeViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )

        transceiverViewModel.refreshState.observe(this,
            Observer { loginFormState ->



                when (loginFormState) {
                    null -> return@Observer
                    "VISIBLE" -> {
                        btnGroupMain.visibility = View.VISIBLE;
                        imageView.setImageResource(R.drawable.logo)
                    }
                    "GONE" -> {
                        btnGroupMain.visibility = View.GONE;
                        imageView.setImageResource(R.drawable.logo)

                    }
                }
            })


    }

    private fun openReadBarcodeActivity() {
        startActivityForResult(
            Intent(this, ReadBarcodeActivity::class.java),
            LAUNCH_NFC_ACTIVITY_FOR_BARCODE
        )

    }

    override fun onDestroy() {
        super.onDestroy()

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_NFC_ACTIVITY_FOR_BARCODE && resultCode == RESULT_OK) {
            val result = data?.getStringExtra("result")
            if (result != null) {
                Log.i("DENGETELEKOM_NFC", result)

                barcodeRequest(result)
            }
            if (result == null) {
                Toast.makeText(this, R.string.barcode_read_cancelled, Toast.LENGTH_LONG)
                    .show()
                return
            }
        }
    }

    private fun barcodeRequest(barcode:String) {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Stok Sayısı Giriniz")
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val barcodeBox = EditText(this)
        barcodeBox.hint = "Barkod: $barcode"
        layout.addView(barcodeBox) // Another add method
        val nameBox = EditText(this)
        nameBox.hint = "Ürün adını giriniz"
        layout.addView(nameBox) // Notice this is an add method

        val stockBox = EditText(this)
        stockBox.hint = "Stok sayısını giriniz"
        stockBox.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(stockBox) // Another add method


        builder.setView(layout)

        builder.setPositiveButton("OK") { _, _ ->
            // Here you get get input text from the Edittext

            var name = nameBox.text.toString()
            var stockCount = stockBox.text.toString()
            val request = ProductRequestModel(
                barcode,
                name,
                stockCount,
                Constants.ACCESS_TOKEN
            )
            transceiverViewModel.addProduct(request)
                .observe(this, {
                    it?.let { resource ->
                        when (resource.status) {
                            Resource.Status.SUCCESS -> {
                                transceiverViewModel.refresh()
                                if(resource.message == null){
                                    Toast.makeText(this,
                                        resource.data.toString(),
                                        Toast.LENGTH_SHORT
                                    )  .show()

                                }else{
                                    Toast.makeText(this,
                                        resource.message,
                                        Toast.LENGTH_SHORT
                                    ) .show()

                                }
                            }
                            Resource.Status.API_ERROR -> {
                                Toast.makeText(this,
                                    resource.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }
                            Resource.Status.ERROR -> {
                                Toast.makeText(this,
                                    getString(R.string.unhandled_error),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            Resource.Status.LOADING -> {
                                // loadingProgressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                })
        }
        builder.setNegativeButton("İptal Et") { dialog, _ -> dialog.cancel() }
        builder.show()
    }


}