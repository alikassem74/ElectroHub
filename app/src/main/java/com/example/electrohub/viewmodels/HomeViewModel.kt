package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Products
import com.example.electrohub.repositories.ProductsRepository
import com.example.electrohub.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository =
        ProductsRepository()

    private val userRepository =
        UsersRepository()


    private var allProducts =
        listOf<Products>()

    private var sortedProducts =
        listOf<Products>()


    private val _products =
        MutableStateFlow<List<Products>>(
            emptyList()
        )

    val products =
        _products.asStateFlow()


    // =========================================================
    // LOAD PRODUCTS
    // =========================================================

    fun loadProducts() {

        viewModelScope.launch {

            val currentUserId =
                userRepository.getCurrentUserId()

            val currentUser =
                userRepository.getUserById(
                    currentUserId
                )


            repository.getProducts()
                .collect { products ->

                    allProducts =
                        products


                    // IMPORTANT:
                    // Assign the CLASS property.
                    // Do NOT use "val sortedProducts" here.

                    sortedProducts =
                        if (currentUser != null) {

                            products.sortedBy { product ->

                                when {

                                    product.country ==
                                            currentUser.country &&
                                            product.region ==
                                            currentUser.region -> 0

                                    product.country ==
                                            currentUser.country -> 1

                                    else -> 2
                                }
                            }

                        } else {

                            products
                        }


                    _products.value =
                        sortedProducts
                }
        }
    }


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    fun searchProducts(
        query: String
    ) {

        val searchQuery =
            query.trim()


        if (searchQuery.isEmpty()) {

            _products.value =
                sortedProducts

            return
        }


        val filteredProducts =
            sortedProducts.filter { product ->

                product.name.contains(
                    searchQuery,
                    ignoreCase = true
                ) ||

                        product.category.contains(
                            searchQuery,
                            ignoreCase = true
                        )
            }


        _products.value =
            filteredProducts
    }
}