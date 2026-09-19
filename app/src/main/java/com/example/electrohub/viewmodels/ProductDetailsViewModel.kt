package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Chat
import com.example.electrohub.models.Products
import com.example.electrohub.models.Users
import com.example.electrohub.repositories.ChatRepository
import com.example.electrohub.repositories.ProductDetailsRepository
import com.example.electrohub.repositories.ProductsRepository
import com.example.electrohub.repositories.SavedProductsRepository
import com.example.electrohub.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel : ViewModel() {

    private val _repository =
        ProductDetailsRepository()

    private val _productRepository = ProductsRepository()

    private val _chatRepository = ChatRepository()

    private val _userRepository = UsersRepository()

    private val _savedRepository = SavedProductsRepository()

    // PRODUCT
    private val _product = MutableStateFlow<Products?>(null)
    val product: StateFlow<Products?> = _product.asStateFlow()

    // OWNER
    private val _owner = MutableStateFlow<Users?>(null)
    val owner: StateFlow<Users?> = _owner.asStateFlow()

    // SAVED
    private val _isSaved = MutableStateFlow(false)

    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    // DELETE RESULT
    private val _deleteResult = MutableStateFlow<Boolean?>(null)
    val deleteResult: StateFlow<Boolean?> = _deleteResult.asStateFlow()


    // LOAD PRODUCT
    fun loadProduct(
        productId: String
    ) {

        viewModelScope.launch {

            val product = _repository.getProduct(productId)

            _product.value =
                product

            product?.let {
                loadOwnerData(
                    it.ownerId
                )
            }
        }
    }


    // =========================================================
    // LOAD OWNER
    // =========================================================

    private fun loadOwnerData(
        ownerId: String
    ) {

        viewModelScope.launch {

            val user = _userRepository.getUserById(ownerId)

            _owner.value = user
        }
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    fun getCurrentUserId(): String {

        return _userRepository.getCurrentUserId()
    }


    // =========================================================
    // OPEN CHAT
    // =========================================================

    fun openChat(
        productId: String,
        productName: String,
        sellerId: String,
        sellerName: String,
        onResult: (String) -> Unit
    ) {

        viewModelScope.launch {

            val buyerId =
                getCurrentUserId()

            val existingChatId =
                _chatRepository.getExistingChat(
                    productId,
                    sellerId,
                    buyerId
                )

            if (existingChatId != null) {

                onResult(
                    existingChatId
                )

                return@launch
            }


            val chat =
                Chat(
                    productId = productId,
                    productName = productName,
                    sellerId = sellerId,
                    buyerId = buyerId
                )


            val newChatId =
                _chatRepository.createChat(
                    chat
                )

            newChatId?.let {

                onResult(it)
            }
        }
    }

    // SAVE PRODUCT
    fun saveProduct(
        productId: String
    ) {

        viewModelScope.launch {

            val success =
                _savedRepository.saveProduct(productId)
            if (success) {
                _isSaved.value = true
            }
        }
    }

    // REMOVE SAVED PRODUCT
    fun removeSavedProduct(
        productId: String
    ) {

        viewModelScope.launch {

            val success =
                _savedRepository.removeSavedProduct(
                    productId
                )

            if (success) {
                _isSaved.value = false
            }
        }
    }


    // =========================================================
    // CHECK IF SAVED
    // =========================================================

    fun checkIfSaved(
        productId: String
    ) {

        viewModelScope.launch {

            val saved =
                _savedRepository.isProductSaved(
                    productId
                )

            _isSaved.value =
                saved
        }
    }


    // =========================================================
    // TOGGLE SAVED
    // =========================================================

    fun toggleSavedProduct(
        productId: String
    ) {

        viewModelScope.launch {

            val saved =
                _savedRepository.toggleSavedProduct(
                    productId
                )

            _isSaved.value =
                saved
        }
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    fun deleteProduct(
        product: Products
    ) {
        viewModelScope.launch {

            val result =
                _productRepository.deleteProduct(product)

            _deleteResult.value =
                result.isSuccess
        }
    }
}