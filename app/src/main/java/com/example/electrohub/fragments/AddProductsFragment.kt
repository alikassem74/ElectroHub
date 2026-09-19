package com.example.electrohub.fragments

import android.R.attr.country
import android.R.attr.name
import android.icu.util.TimeZone.getRegion
import android.icu.util.ULocale.getCountry
import android.R as AndroidR
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electrohub.R
import com.example.electrohub.adapters.ProductImagesAdapter
import com.example.electrohub.databinding.FragmentAddProductsBinding
import com.example.electrohub.models.CloudinaryImage
import com.example.electrohub.models.Products
import com.example.electrohub.viewmodels.AddProductViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class AddProductsFragment : Fragment() {
    private lateinit var _binding: FragmentAddProductsBinding
    private lateinit var _imagesAdapter: ProductImagesAdapter

    // Images deleted from Cloudinary
    private val _deletedImagePublicIds = ArrayList<String>()

    // New images selected from device
    private val _selectedImages = ArrayList<Uri>()

    // Existing Cloudinary URLs
    private val _existingImageUrls = ArrayList<String>()

    // Existing Cloudinary public IDs
    private val _existingImagePublicIds = ArrayList<String>()

    // Product ID when editing
    private var _editingProductId: String? = null

    // Owner information
    private var _name = ""
    private var _id = ""

    // Selected category
    private var _selectedCategory = ""

    private val _viewModel: AddProductViewModel by viewModels()

    // IMAGE PICKER
    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { images ->

            for (image in images) {
                _viewModel.addImage(image)
            }
        }


    // CREATE VIEW
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAddProductsBinding.inflate(
                inflater,
                container,
                false
            )

        requireActivity()
            .window
            .insetsController
            ?.hide(
                WindowInsets.Type.statusBars() or
                        WindowInsets.Type.navigationBars()
            )

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )

        return _binding.root
    }

    // VIEW CREATED

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        // Check if we are editing
        _editingProductId =
            arguments?.getString("productId")

        setButtonName()

        setImagesRecyclerView()

        setViews()

        setObservers()

        // Get owner information
        _viewModel.getOwnerData(
            requireContext()
        )

        // Get categories
        _viewModel.getCategories(
            requireContext()
        )

        // If editing, load the existing product
        _editingProductId?.let { productId ->

            _viewModel.getProductById(
                productId
            )
        }
    }

    // BUTTON NAME
    private fun setButtonName() {

        if (_editingProductId != null) {

            _binding.btnAddProduct.text =
                "SAVE CHANGES"

        } else {

            _binding.btnAddProduct.text =
                "ADD PRODUCT"
        }
    }

    // VIEWS
    private fun setViews() {
        // BACK
        _binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // ADD / SAVE
        _binding.btnAddProduct.setOnClickListener {
            saveProduct()
        }

        // CATEGORY
        _binding.actxtProductCategory.setOnClickListener {

            _binding.actxtProductCategory.showDropDown()
        }


        _binding.actxtProductCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                _binding.actxtProductCategory.showDropDown()
            }
        }


        _binding.actxtProductCategory.setOnItemClickListener { parent,
                                                               _,
                                                               position,
                                                               _ ->

            _selectedCategory =
                parent
                    .getItemAtPosition(position)
                    .toString()
        }

        // ADD IMAGES
        _binding.btnAddImages.setOnClickListener {

            imagePickerLauncher.launch(
                "image/*"
            )
        }
    }

    // SAVE PRODUCT
    private fun saveProduct() {

        if (!validateInputs()) {
            return
        }


        /*
         * IMPORTANT:
         *
         * ADD MODE:
         * We must upload the selected images.
         *
         * EDIT MODE:
         *
         * If the user selected new images:
         *     upload them first.
         *
         * If the user did NOT select new images:
         *     update the product immediately.
         *
         * This fixes the problem where deleting an image
         * or editing text without adding a photo did nothing.
         */

        if (_editingProductId == null) {
            // ADD PRODUCT
            _viewModel.uploadSelectedImages()
        } else {
            // EDIT PRODUCT
            if (_selectedImages.isEmpty()) {
                // No new photos.
                // Update immediately.
                createOrUpdateProduct(emptyList())
            } else {
                // New photos exist.
                // Upload them first.
                _viewModel.uploadSelectedImages()
            }
        }
    }

    // OBSERVERS
    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                // PRODUCT ADDED / UPDATED
                launch {

                    _viewModel.productAdded.collect { success ->

                        when (success) {

                            true -> {

                                Toast.makeText(
                                    requireContext(),
                                    if (_editingProductId == null)
                                        "Product Added"
                                    else
                                        "Product Updated",
                                    Toast.LENGTH_SHORT
                                ).show()

                                findNavController()
                                    .navigate(
                                        R.id.action_AddProductsFragment_to_HomeFragment
                                    )
                            }

                            false -> {

                                Toast.makeText(
                                    requireContext(),
                                    if (_editingProductId == null)
                                        "Failed To Add Product"
                                    else
                                        "Failed To Update Product",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            null -> {
                                // Nothing yet
                            }
                        }
                    }
                }

                // OWNER NAME
                launch {

                    _viewModel.name.collect { name ->
                        _name = name
                    }
                }

                // OWNER ID
                launch {
                    _viewModel.id.collect { id ->
                        _id = id
                    }
                }

                // LOADING
                launch {

                    _viewModel.isLoading.collect { isLoading ->

                        if (isLoading) {
                            _binding.progressBar.visibility = View.VISIBLE

                            _binding.btnAddProduct.isEnabled = false

                            _binding.btnAddImages.isEnabled = false

                        } else {

                            _binding.progressBar.visibility = View.GONE

                            _binding.btnAddProduct.isEnabled = true

                            _binding.btnAddImages.isEnabled = true
                        }
                    }
                }

                // CATEGORIES
                launch {

                    _viewModel.categories.collect { categories ->

                        val adapter =
                            ArrayAdapter(
                                requireContext(),
                                AndroidR.layout
                                    .simple_dropdown_item_1line,
                                categories
                            )

                        _binding
                            .actxtProductCategory
                            .setAdapter(adapter)
                    }
                }


                // ==================================================
                // SELECTED IMAGES
                // ==================================================

                launch {

                    _viewModel.selectedImages.collect { images ->

                        _selectedImages.clear()

                        _selectedImages.addAll(
                            images
                        )

                        updateDisplayedImages()
                    }
                }


                // ==================================================
                // EXISTING PRODUCT WHEN EDITING
                // ==================================================

                launch {

                    _viewModel.editProduct.collect { product ->

                        product ?: return@collect


                        // Product information

                        _binding
                            .etxtProductName
                            .setText(product.name)

                        _binding
                            .etxtProductDescription
                            .setText(product.description)

                        _binding
                            .etxtProductPrice
                            .setText(
                                product.price.toString()
                            )

                        _binding
                            .actxtProductCategory
                            .setText(
                                product.category,
                                false
                            )


                        _selectedCategory =
                            product.category


                        // Existing image URLs

                        _existingImageUrls.clear()

                        _existingImageUrls.addAll(
                            product.imageUrls
                        )


                        // Existing image public IDs

                        _existingImagePublicIds.clear()

                        _existingImagePublicIds.addAll(
                            product.imagePublicIds
                        )


                        updateDisplayedImages()
                    }
                }


                // ==================================================
                // UPLOADED IMAGES
                // ==================================================

                launch {

                    _viewModel.uploadedImages.collect { uploadedImages ->

                        /*
                         * If no images were uploaded, we do NOT
                         * return here anymore for the edit case.
                         *
                         * The edit-without-new-photo case is
                         * already handled directly inside saveProduct().
                         */

                        if (
                            uploadedImages.isEmpty()
                        ) {
                            return@collect
                        }


                        createOrUpdateProduct(
                            uploadedImages
                        )
                    }
                }

                // Country
                launch {
                    _viewModel.country.collect { _country ->
                        _binding.txtVCountry.text = _country
                    }
                }
                // REGION
                launch {
                    _viewModel.region.collect { _region ->
                        _binding.txtVRegion.text = _region
                    }
                }
            }
        }
    }


    // ============================================================
    // CREATE OR UPDATE PRODUCT
    // ============================================================

    private fun createOrUpdateProduct(
        uploadedImages: List<CloudinaryImage>
    ) {

        val name =
            _binding
                .etxtProductName
                .text
                .toString()
                .trim()


        val description =
            _binding
                .etxtProductDescription
                .text
                .toString()
                .trim()


        val price =
            _binding
                .etxtProductPrice
                .text
                .toString()
                .trim()
                .toDouble()


        val category =
            _binding
                .actxtProductCategory
                .text
                .toString()
                .trim()


        // New Cloudinary URLs

        val newUrls =
            uploadedImages.map {
                it.url
            }


        // New Cloudinary public IDs

        val newPublicIds =
            uploadedImages.map {
                it.publicId
            }


        // ========================================================
        // ADD PRODUCT
        // ========================================================

        if (_editingProductId == null) {

            val product =
                Products(

                    name = name,

                    description = description,

                    price = price,

                    category = category,

                    imageUrls = newUrls,

                    imagePublicIds =
                        newPublicIds,

                    ownerId = _id,

                    ownerName = _name,

                    createdAt = Timestamp.now(),
                    country = _binding.txtVCountry.text.toString(),
                    region = _binding.txtVRegion.text.toString()
                )


            _viewModel.addProduct(
                product
            )

            return
        }


        // ========================================================
        // EDIT PRODUCT
        // ========================================================

        /*
         * Existing images are already the CURRENT images.
         *
         * When the user deletes an image,
         * deleteImageFromEdit() removes it from
         * _existingImageUrls and _existingImagePublicIds.
         *
         * Therefore these lists already represent the
         * remaining images.
         */

        val finalUrls =
            ArrayList<String>()

        finalUrls.addAll(
            _existingImageUrls
        )

        finalUrls.addAll(
            newUrls
        )


        val finalPublicIds =
            ArrayList<String>()

        finalPublicIds.addAll(
            _existingImagePublicIds
        )

        finalPublicIds.addAll(
            newPublicIds
        )


        val oldProduct =
            _viewModel.editProduct.value


        /*
         * IMPORTANT:
         *
         * Keep the original ownerId and ownerName.
         *
         * We don't want the owner information to accidentally
         * change just because DataStore is loaded differently.
         */

        val updatedProduct =
            Products(

                productId =
                    _editingProductId!!,

                name =
                    name,

                description =
                    description,

                price =
                    price,

                category =
                    category,

                imageUrls =
                    finalUrls,

                imagePublicIds =
                    finalPublicIds,

                ownerId =
                    oldProduct?.ownerId
                        ?: _id,

                ownerName =
                    oldProduct?.ownerName
                        ?: _name,

                createdAt =
                    oldProduct?.createdAt
                        ?: Timestamp.now()
            )


        _viewModel.updateProduct(
            updatedProduct,
            _deletedImagePublicIds
        )
    }


    // ============================================================
    // DISPLAY IMAGES
    // ============================================================

    private fun updateDisplayedImages() {

        val displayedImages =
            ArrayList<Uri>()


        // Existing Cloudinary images

        for (url in _existingImageUrls) {

            displayedImages.add(
                Uri.parse(url)
            )
        }


        // New local images

        displayedImages.addAll(
            _selectedImages
        )


        _imagesAdapter.updateImages(
            displayedImages
        )
    }


    // ============================================================
    // IMAGE RECYCLER VIEW
    // ============================================================

    private fun setImagesRecyclerView() {

        _imagesAdapter =
            ProductImagesAdapter(
                ArrayList<Uri>(),
                true
            ) { uri ->

                deleteImageFromEdit(
                    uri
                )
            }


        _binding
            .rvProductImages
            .adapter =
            _imagesAdapter
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    private fun validateInputs(): Boolean {

        // --------------------------------------------------------
        // NAME
        // --------------------------------------------------------

        if (
            _binding
                .etxtProductName
                .text
                .toString()
                .trim()
                .isEmpty()
        ) {

            _binding
                .etxtProductName
                .error =
                "Enter product name"

            return false
        }


        // --------------------------------------------------------
        // DESCRIPTION
        // --------------------------------------------------------

        if (
            _binding
                .etxtProductDescription
                .text
                .toString()
                .trim()
                .isEmpty()
        ) {

            _binding
                .etxtProductDescription
                .error =
                "Enter description"

            return false
        }


        // --------------------------------------------------------
        // PRICE
        // --------------------------------------------------------

        val price =
            _binding
                .etxtProductPrice
                .text
                .toString()
                .trim()


        if (price.isEmpty()) {

            _binding
                .etxtProductPrice
                .error =
                "Enter price"

            return false
        }


        if (
            price.toDoubleOrNull() == null ||
            price.toDouble() <= 0
        ) {

            _binding
                .etxtProductPrice
                .error =
                "Enter a valid price"

            return false
        }


        // --------------------------------------------------------
        // CATEGORY
        // --------------------------------------------------------

        if (
            _selectedCategory.isEmpty()
        ) {

            Toast.makeText(
                requireContext(),
                "Select a category",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }


        // --------------------------------------------------------
        // ADD MODE
        // --------------------------------------------------------

        if (
            _editingProductId == null &&
            _selectedImages.isEmpty()
        ) {

            Toast.makeText(
                requireContext(),
                "Select at least one image",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }


        // --------------------------------------------------------
        // EDIT MODE
        // --------------------------------------------------------

        /*
         * The product must still contain at least one image.
         */

        if (
            _editingProductId != null &&
            _existingImageUrls.isEmpty() &&
            _selectedImages.isEmpty()
        ) {

            Toast.makeText(
                requireContext(),
                "Product must have at least one image",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }


        return true
    }


    // ============================================================
    // DELETE IMAGE
    // ============================================================

    private fun deleteImageFromEdit(
        uri: Uri
    ) {

        val uriString =
            uri.toString()


        // --------------------------------------------------------
        // EXISTING CLOUDINARY IMAGE
        // --------------------------------------------------------

        val existingIndex = _existingImageUrls.indexOf(uriString)


        if (existingIndex != -1) {

            val publicId =
                _existingImagePublicIds
                    .getOrNull(
                        existingIndex
                    )


            if (publicId != null) {

                _deletedImagePublicIds.add(
                    publicId
                )
            }


            // Remove URL

            _existingImageUrls.removeAt(
                existingIndex
            )


            // Remove matching public ID

            if (
                existingIndex <
                _existingImagePublicIds.size
            ) {

                _existingImagePublicIds.removeAt(
                    existingIndex
                )
            }

        } else {

            // ----------------------------------------------------
            // NEW LOCAL IMAGE
            // ----------------------------------------------------

            _selectedImages.remove(
                uri
            )

            _viewModel.removeImage(
                uri
            )
        }


        updateDisplayedImages()
    }


    // ============================================================
    // DESTROY VIEW
    // ============================================================

    override fun onDestroyView() {

        super.onDestroyView()
    }
}