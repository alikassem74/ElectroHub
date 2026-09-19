package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Products
import com.example.electrohub.repositories.SavedProductsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {

    private val _savedRepository =
        SavedProductsRepository()


    // =========================================================
    // SAVED PRODUCTS
    // =========================================================

    private val _savedProducts =
        MutableStateFlow<List<Products>>(emptyList())

    val savedProducts: StateFlow<List<Products>> =
        _savedProducts.asStateFlow()


    // =========================================================
    // LOAD SAVED PRODUCTS
    // =========================================================

    fun loadSavedProducts() {

        viewModelScope.launch {

            _savedRepository
                .getSavedProducts()
                .collect { products ->

                    _savedProducts.value =
                        products
                }
        }
    }
}