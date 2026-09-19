package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Products
import com.example.electrohub.models.Reports
import com.example.electrohub.repositories.ProductsRepository
import com.example.electrohub.repositories.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminReportDetailsViewModel : ViewModel() {

    private val repository =
        ReportRepository()

    private val productsRepository =
        ProductsRepository()


    // =========================================================
    // REPORT
    // =========================================================

    private val _report =
        MutableStateFlow<Reports?>(null)

    val report =
        _report.asStateFlow()


    // =========================================================
    // ACTION COMPLETE
    // =========================================================

    private val _actionCompleted =
        MutableStateFlow(false)

    val actionCompleted =
        _actionCompleted.asStateFlow()


    // =========================================================
    // MESSAGE
    // =========================================================

    private val _message =
        MutableStateFlow<String?>(null)

    val message =
        _message.asStateFlow()


    // =========================================================
    // PRODUCT
    // =========================================================

    private val _product =
        MutableStateFlow<Products?>(null)

    val product =
        _product.asStateFlow()


    // =========================================================
    // LOADING
    // =========================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading =
        _isLoading.asStateFlow()


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableStateFlow<String?>(null)

    val error =
        _error.asStateFlow()


    // =========================================================
    // LOAD REPORT
    // =========================================================

    fun loadReport(
        reportId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _error.value = null

            val result =
                repository.getReportById(
                    reportId
                )

            if (result.isSuccess) {

                _report.value =
                    result.getOrNull()

            } else {

                _error.value =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Failed to load report"
            }

            _isLoading.value = false
        }
    }


    // =========================================================
    // DISMISS REPORT
    // =========================================================

    fun dismissReport(
        reportId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val result =
                repository.dismissReport(
                    reportId
                )

            if (result.isSuccess) {

                val deleteResult =
                    repository.deleteReport(
                        reportId
                    )

                if (deleteResult.isSuccess) {

                    _message.value =
                        "Report dismissed successfully"

                    _actionCompleted.value =
                        true

                } else {

                    _message.value =
                        "Report dismissed, but failed to delete report"
                }

            } else {

                _message.value =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Failed to dismiss report"
            }

            _isLoading.value = false
        }
    }


    // =========================================================
    // WARN SELLER
    // =========================================================

    // =========================================================
// WARN SELLER
// =========================================================

    // =========================================================
// WARN SELLER
// =========================================================

    fun warnSeller(
        userId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val report =
                _report.value

            if (report == null) {

                _message.value =
                    "Report not found"

                _isLoading.value = false

                return@launch
            }


            val reportId =
                report.reportId

            if (reportId.isNullOrEmpty()) {

                _message.value =
                    "Report ID not found"

                _isLoading.value = false

                return@launch
            }


            val productName =
                report.productName.ifEmpty {
                    "Unknown Product"
                }


            val result =
                repository.warnUser(

                    userId =
                        userId,

                    reportId =
                        reportId,

                    productName =
                        productName,

                    message =
                        "⚠️ Warning from ElectroHub\n\n" +
                                "Your product \"$productName\" has received a report. " +
                                "Please review your listing and make sure all information is accurate.\n\n" +
                                "⚠️ تحذير من ElectroHub\n\n" +
                                "تم الإبلاغ عن منتجك \"$productName\". " +
                                "يرجى مراجعة إعلانك والتأكد من أن جميع المعلومات دقيقة."
                )


            if (result.isSuccess) {

                val deleteResult =
                    repository.deleteReport(
                        reportId
                    )


                if (deleteResult.isSuccess) {

                    _message.value =
                        "Seller warned successfully"

                    _actionCompleted.value =
                        true

                } else {

                    _message.value =
                        "Seller warned, but failed to delete report"
                }

            } else {

                _message.value =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Failed to warn seller"
            }


            _isLoading.value = false
        }
    }


// =========================================================
// WARN REPORTER
// =========================================================

    fun warnReporter(
        userId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val report =
                _report.value

            if (report == null) {

                _message.value =
                    "Report not found"

                _isLoading.value = false

                return@launch
            }


            val reportId =
                report.reportId

            if (reportId.isNullOrEmpty()) {

                _message.value =
                    "Report ID not found"

                _isLoading.value = false

                return@launch
            }


            val result =
                repository.warnUser(

                    userId =
                        userId,

                    reportId =
                        reportId,

                    productName =
                        report.productName,

                    message =
                        "⚠️ Warning from ElectroHub\n\n" +
                                "Your report has been reviewed by the administrator. " +
                                "Please make sure that reports you submit are accurate and legitimate.\n\n" +
                                "⚠️ تحذير من ElectroHub\n\n" +
                                "تمت مراجعة بلاغك من قبل المسؤول. " +
                                "يرجى التأكد من أن البلاغات التي تقدمها دقيقة وموثوقة."
                )


            if (result.isSuccess) {

                val deleteResult =
                    repository.deleteReport(
                        reportId
                    )


                if (deleteResult.isSuccess) {

                    _message.value =
                        "Reporter warned successfully"

                    _actionCompleted.value =
                        true

                } else {

                    _message.value =
                        "Reporter warned, but failed to delete report"
                }

            } else {

                _message.value =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Failed to warn reporter"
            }


            _isLoading.value = false
        }
    }


    // =========================================================
    // BLOCK USER
    // =========================================================

    fun blockUser(
        userId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val reportId =
                _report.value?.reportId

            if (reportId.isNullOrEmpty()) {

                _message.value =
                    "Report not found"

                _isLoading.value = false

                return@launch
            }


            val result =
                repository.blockUser(
                    userId
                )


            if (result.isSuccess) {

                val deleteResult =
                    repository.deleteReport(
                        reportId
                    )

                if (deleteResult.isSuccess) {

                    _message.value =
                        "User blocked successfully"

                    _actionCompleted.value =
                        true

                } else {

                    _message.value =
                        "User blocked, but failed to delete report"
                }

            } else {

                _message.value =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Failed to block user"
            }

            _isLoading.value = false
        }
    }


    // =========================================================
    // REMOVE PRODUCT
    // =========================================================

    fun removeProduct(
        product: Products
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val reportId =
                _report.value?.reportId

            if (reportId.isNullOrEmpty()) {

                _message.value =
                    "Report not found"

                _isLoading.value = false

                return@launch
            }


            val result =
                productsRepository.deleteProduct(
                    product
                )


            if (result.isSuccess) {

                val deleteResult =
                    repository.deleteReport(
                        reportId
                    )

                if (deleteResult.isSuccess) {

                    _message.value =
                        "Product removed successfully"

                    _actionCompleted.value =
                        true

                } else {

                    _message.value =
                        "Product removed, but failed to delete report"
                }

            } else {

                _message.value =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Failed to remove product"
            }

            _isLoading.value = false
        }
    }


    // =========================================================
    // GET PRODUCT
    // =========================================================

    fun getProductById(
        productId: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val product =
                productsRepository.getProductById(
                    productId
                )

            _product.value =
                product

            _isLoading.value = false
        }
    }
}