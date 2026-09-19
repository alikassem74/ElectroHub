package com.example.electrohub.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.R
import com.example.electrohub.models.CloudinaryImage
import com.example.electrohub.models.Products
import com.example.electrohub.repositories.ProductsRepository
import com.example.electrohub.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddProductViewModel : ViewModel() {

    private val repository = ProductsRepository()

    private val userRepository = UsersRepository()

    // PRODUCT RESULT
    private val _productAdded = MutableStateFlow<Boolean?>(null)
    val productAdded = _productAdded.asStateFlow()

    // OWNER NAME
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()


    // OWNER ID
    private val _id = MutableStateFlow("")
    val id = _id.asStateFlow()

    // CATEGORIES
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories = _categories.asStateFlow()

    // SELECTED IMAGES
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())

    val selectedImages = _selectedImages.asStateFlow()

    // EDIT PRODUCT
    private val _editProduct = MutableStateFlow<Products?>(null)
    val editProduct = _editProduct.asStateFlow()

    // LOADING
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // UPLOADED IMAGES
    private val _uploadedImages = MutableStateFlow<List<CloudinaryImage>>(emptyList())
    val uploadedImages = _uploadedImages.asStateFlow()

// OWNER COUNTRY
    private val _country = MutableStateFlow("")
    val country = _country.asStateFlow()

// OWNER REGION
    private val _region = MutableStateFlow("")
    val region = _region.asStateFlow()

    // ADD PRODUCT
    fun addProduct(
        product: Products
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _productAdded.value = null

            val result = repository.addProduct(product)

            _productAdded.value = result.isSuccess

            _isLoading.value = false
        }
    }
    // UPDATE PRODUCT
    fun updateProduct(
        product: Products,
        deletedImagePublicIds: List<String>
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _productAdded.value = null

            val result = repository.updateProduct(product, deletedImagePublicIds)

            _productAdded.value = result.isSuccess

            _isLoading.value = false
        }
    }

    // GET PRODUCT
    fun getProductById(
        productId: String
    ) {
        viewModelScope.launch {

            val product = repository.getProductById(productId)
            _editProduct.value = product
        }
    }

    // GET OWNER DATA
    fun getOwnerData(
        context: Context
    ) {
        viewModelScope.launch {
            val (id, name) = repository.getOwnerData(context)

            _id.value = id

            _name.value = name
            val user = userRepository.getUserById(id)
            _country.value = user?.country?:""
            _region.value= user?.region?:""
        }
    }

    // GET CATEGORIES
    fun getCategories(
        context: Context
    ) {
        val categories = context.resources.getStringArray(R.array.product_categories)
            .toList()

        Log.d("Categories", categories.toString())
        _categories.value = categories
    }

    // ADD IMAGE
    fun addImage(
        uri: Uri
    ) {
        _selectedImages.value = _selectedImages.value + uri
    }

    // REMOVE IMAGE
    fun removeImage(
        uri: Uri
    ) {
        _selectedImages.value = _selectedImages.value - uri
    }


    // ============================================================
    // UPLOAD SELECTED IMAGES
    // ============================================================

    fun uploadSelectedImages() {

        viewModelScope.launch {

            _isLoading.value = true

            // Clear previous upload result
            _uploadedImages.value =
                emptyList()


            val uploadedImages =
                repository.uploadImages(
                    _selectedImages.value
                )


            _uploadedImages.value =
                uploadedImages


            _isLoading.value = false
        }
    }
}