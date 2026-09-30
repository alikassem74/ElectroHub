package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electrohub.R
import com.example.electrohub.adapters.ProductDetailImagesAdapter
import com.example.electrohub.databinding.FragmentProductDetailsBinding
import com.example.electrohub.models.Products
import com.example.electrohub.viewmodels.ProductDetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class ProductDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentProductDetailsBinding

    private var currentProduct: Products? = null

    private val _viewModel: ProductDetailsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        return _binding.root
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setViews()
        setObservers()
        getProduct()
    }

    // GET PRODUCT
    private fun getProduct() {

        val productId =
            arguments?.getString("productId")

        productId?.let {

            _viewModel.loadProduct(it)
        }
    }



    // OBSERVERS
    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    _viewModel.product.collect { product ->

                        product?.let {

                            currentProduct = it

                            _viewModel.checkIfSaved(
                                it.productId
                            )
                            _binding.txtVProductName.text = it.name

                            _binding.txtVPrice.text = "${it.price} $"

                            _binding.txtVCategory.text = it.category

                            _binding.txtVDescription.text = it.description




                            // PRODUCT IMAGES
                            val imageAdapter =
                                ProductDetailImagesAdapter(
                                    ArrayList(
                                        it.imageUrls
                                    )
                                )

                            _binding.vpImages.adapter =
                                imageAdapter


                            TabLayoutMediator(
                                _binding.imageIndicator,
                                _binding.vpImages
                            ) { _, _ ->
                            }.attach()


                            checkOwnerActions()
                        }
                    }
                }

                // SAVED PRODUCT
                launch {

                    _viewModel.isSaved.collect { isSaved ->

                        if (isSaved) {

                            _binding.btnSave.setImageResource(
                                R.drawable.ic_favorite
                            )

                        } else {

                            _binding.btnSave.setImageResource(
                                R.drawable.ic_favorite_border
                            )
                        }
                    }
                }

                // DELETE RESULT
                launch {

                    _viewModel.deleteResult.collect { result ->

                        result?.let { success ->

                            if (success) {

                                Toast.makeText(
                                    requireContext(),
                                    "Product deleted",
                                    Toast.LENGTH_SHORT
                                ).show()

                                findNavController()
                                    .navigateUp()
                            } else {

                                Toast.makeText(
                                    requireContext(),
                                    "Failed to delete product",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }


                // OWNER
                launch {
                    _viewModel.owner.collect { user ->

                        user?.let {
                            // Seller name
                            _binding.txtVSellerName.text = it.name

                            // Member since
                            it.createdAt?.let { timestamp ->

                                val date = timestamp.toDate()

                                val sdf =
                                    SimpleDateFormat(
                                        "MMMM yyyy",
                                        Locale.getDefault()
                                    )

                                val formattedDate = sdf.format(date)
                                _binding.txtVMemberSince.text = "Member since $formattedDate"
                                _binding.txtVSellerCountry.text=it.country
                                _binding.txtVSellerRegion.text=it.region
                            } ?: run {
                                _binding.txtVMemberSince.text = "Member since unknown"
                            }
                        }
                    }
                }
            }
        }
    }

    // OWNER ACTIONS
    private fun checkOwnerActions() {

        val currentUserId = _viewModel.getCurrentUserId()

        currentProduct?.let { product ->

            if (currentUserId == product.ownerId) {

                _binding.btnMessageSeller.visibility =
                    View.GONE

                _binding.btnDelete.visibility =
                    View.VISIBLE

                _binding.btnEdit.visibility =
                    View.VISIBLE

            } else {

                _binding.btnMessageSeller.visibility =
                    View.VISIBLE

                _binding.btnDelete.visibility =
                    View.GONE

                _binding.btnEdit.visibility =
                    View.GONE
            }
        }
    }



    // VIEWS
    private fun setViews() {

        // Back
        _binding.btnBack.setOnClickListener {

            findNavController()
                .navigateUp()
        }


        // Message Seller
        _binding.btnMessageSeller.setOnClickListener {

            currentProduct?.let { product ->

                _viewModel.openChat(
                    productId = product.productId,
                    productName = product.name,
                    sellerId = product.ownerId,
                    sellerName = product.ownerName
                ) { chatId ->

                    val bundle =
                        Bundle().apply {

                            putString(
                                "chatId",
                                chatId
                            )
                        }

                    findNavController().navigate(
                        R.id.action_ProductDetailsFragment_to_ChatFragment,
                        bundle
                    )
                }
            }
        }


        // Save Product
        _binding.btnSave.setOnClickListener {

            currentProduct?.let { product ->

                _viewModel.toggleSavedProduct(
                    product.productId
                )
            }
        }


        // Report
        _binding.btnReport.setOnClickListener {

            currentProduct?.let { product ->

                val bundle =
                    Bundle().apply {

                        putString(
                            "productId",
                            product.productId
                        )

                        putString(
                            "sellerId",
                            product.ownerId
                        )

                        putString(
                            "productName",
                            product.name
                        )
                    }

                findNavController().navigate(
                    R.id.action_ProductDetailsFragment_to_ReportFragment,
                    bundle
                )
            }
        }


        // Delete
        _binding.btnDelete.setOnClickListener {

            currentProduct?.let { product ->

                showDeleteDialog(product)
            }
        }

        // Edit
        _binding.btnEdit.setOnClickListener {

            currentProduct?.let { product ->

                val bundle =
                    Bundle().apply {

                        putString(
                            "productId",
                            product.productId
                        )
                    }

                findNavController().navigate(
                    R.id.action_ProductDetailsFragment_to_AddProductsFragment,
                    bundle
                )
            }
        }
    }

    // DELETE DIALOG
    private fun showDeleteDialog(
        product: Products
    ) {

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage(
                "Are you sure you want to delete this product?"
            )
            .setPositiveButton("Delete") { _, _ ->

                _viewModel.deleteProduct(
                    product
                )
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}