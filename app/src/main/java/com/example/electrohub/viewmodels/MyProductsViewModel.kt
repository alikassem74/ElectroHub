package com.example.electrohub.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Products
import com.example.electrohub.repositories.MyProductsRepository
import kotlinx.coroutines.launch

class MyProductsViewModel : ViewModel() {

    private val _repository = MyProductsRepository()

    private val _liveMyProducts =
        MutableLiveData<List<Products>>()

    fun liveMyProducts() = _liveMyProducts


    fun loadMyProducts() {

        viewModelScope.launch {

            val products =
                _repository.getMyProducts()

            _liveMyProducts.value =
                products
        }
    }
}