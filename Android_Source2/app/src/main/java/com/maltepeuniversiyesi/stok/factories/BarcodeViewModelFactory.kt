package com.maltepeuniversiyesi.stok.factories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.maltepeuniversiyesi.stok.TransceiverViewModel
import com.maltepeuniversiyesi.stok.repositories.TransceiverRepository

class BarcodeViewModelFactory(private val repository: TransceiverRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransceiverViewModel(
            repository
        ) as T
    }
}