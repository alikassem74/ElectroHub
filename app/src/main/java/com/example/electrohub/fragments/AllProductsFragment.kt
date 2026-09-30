package com.example.electrohub.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electrohub.R
import com.example.electrohub.adapters.ProductsAdapter
import com.example.electrohub.databinding.FragmentAllProductsBinding
import com.example.electrohub.interfaces.IProductClickListener
import com.example.electrohub.models.Products
import com.example.electrohub.viewmodels.AllProductsViewModel
import kotlinx.coroutines.launch

class AllProductsFragment : Fragment(), IProductClickListener {
    private lateinit var _binding: FragmentAllProductsBinding

    var isFilterVisible = false
    var minPrice: Double? =null
    var maxPrice: Double? =null

    var category: String?=null

    private val _viewModel: AllProductsViewModel by viewModels()

    private lateinit var _productsAdapter: ProductsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllProductsBinding.inflate(
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
         category = arguments?.getString("categoryId")

        setViews()

        setupSearch()

        setObservers()

        _viewModel.loadProducts(category,minPrice,maxPrice)
    }

    // OBSERVE STATEFLOW
    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                _viewModel.products.collect { products ->

                    _productsAdapter.updateProducts(
                        ArrayList(products)
                    )
                }
            }
        }
    }

    // SEARCH
    private fun setupSearch() {

        _binding.etxtSearch.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    _viewModel.searchProducts(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    // VIEWS
    private fun setViews() {
        _binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    findNavController().navigate(
                        R.id.action_AllProductsFragment_to_HomeFragment
                    )
                    true
                }

                R.id.nav_SavedForLater -> {
                    findNavController().navigate(
                        R.id.action_AllProductsFragment_to_SaveForLaterFragment
                    )
                    true
                }

                R.id.nav_ShopAll -> {
                    true
                }

                R.id.nav_Profile -> {
                    findNavController().navigate(
                        R.id.action_AllProductsFragment_to_ProfileFragment
                    )
                    true
                }

                else -> false
            }
        }
        _binding.bottomNavigation.selectedItemId = R.id.nav_ShopAll

        _binding.rvProducts.layoutManager =
            GridLayoutManager(
                requireContext(),
                2
            )

        _productsAdapter =
            ProductsAdapter(ArrayList()).apply {

                inter = this@AllProductsFragment
            }


        _binding.rvProducts.adapter = _productsAdapter

        _binding.imgViewCategories.setOnClickListener {

            if (_binding.drawerLayout2.isDrawerOpen(GravityCompat.END)) {
                _binding.drawerLayout2.closeDrawer(GravityCompat.END)
                _binding.imgViewCategories.setImageResource(R.drawable.ic_menu)
            } else {
                _binding.drawerLayout2.openDrawer(GravityCompat.END)
                _binding.imgViewCategories.setImageResource(R.drawable.ic_close)
            }
        }
        _binding.imgFilter.setOnClickListener {
            isFilterVisible = !isFilterVisible

            if (isFilterVisible) {
                _binding.priceScroll.visibility = View.VISIBLE
                _binding.imgFilter.setImageResource(R.drawable.ic_close)
            } else {
                _binding.priceScroll.visibility = View.GONE
                _binding.imgFilter.setImageResource(R.drawable.ic_menu)
            }
        }
        //PONES
        _binding.categoryPhones.setOnClickListener {
            if (category == "Phones") {

                _binding.categoryPhones.isChecked = false
                category = null

            } else {
                _binding.categoryPhones.isChecked = true
                _binding.categoryLaptops.isChecked = false
                _binding.categoryAccessories.isChecked = false
                _binding.categoryGaming.isChecked = false
                _binding.categoryMonitors.isChecked = false
                _binding.categoryTablets.isChecked = false
                category = "Phones"
            }

            _binding.drawerLayout2.closeDrawer(GravityCompat.END)
            _viewModel.loadProducts(category, minPrice, maxPrice)
        }
        //TABLETS
        _binding.categoryTablets.setOnClickListener {
            if (category == "Tablets") {

                _binding.categoryTablets.isChecked = false
                category = null

            } else {

                _binding.categoryTablets.isChecked = true

                _binding.categoryLaptops.isChecked = false
                _binding.categoryAccessories.isChecked = false
                _binding.categoryGaming.isChecked = false
                _binding.categoryMonitors.isChecked = false
                _binding.categoryPhones.isChecked = false
                category = "Tablets"
            }

            _binding.drawerLayout2.closeDrawer(GravityCompat.END)
            _viewModel.loadProducts(category, minPrice, maxPrice)
        }
        //ACCESSORIES
        _binding.categoryAccessories.setOnClickListener {
            if (category == "Accessories") {

                _binding.categoryAccessories.isChecked = false
                category = null

            } else {

                _binding.categoryAccessories.isChecked = true

                _binding.categoryTablets.isChecked = false
                _binding.categoryLaptops.isChecked = false
                _binding.categoryGaming.isChecked = false
                _binding.categoryMonitors.isChecked = false
                _binding.categoryPhones.isChecked = false
                category = "Accessories"
            }

            _binding.drawerLayout2.closeDrawer(GravityCompat.END)
            _viewModel.loadProducts(category, minPrice, maxPrice)
        }
        //MONITORS
        _binding.categoryMonitors.setOnClickListener {
            if (category == "Monitors") {

                _binding.categoryMonitors.isChecked = false
                category = null

            } else {

                _binding.categoryMonitors.isChecked = true

                _binding.categoryTablets.isChecked = false
                _binding.categoryAccessories.isChecked = false
                _binding.categoryGaming.isChecked = false
                _binding.categoryLaptops.isChecked = false
                _binding.categoryPhones.isChecked = false
                category = "Monitors"
            }

            _binding.drawerLayout2.closeDrawer(GravityCompat.END)
            _viewModel.loadProducts(category, minPrice, maxPrice)
        }
        //LAPTOPS
        _binding.categoryLaptops.setOnClickListener {
            if (category == "Laptops") {

                _binding.categoryLaptops.isChecked = false
                category = null

            } else {

                _binding.categoryLaptops.isChecked = true

                _binding.categoryTablets.isChecked = false
                _binding.categoryAccessories.isChecked = false
                _binding.categoryGaming.isChecked = false
                _binding.categoryMonitors.isChecked = false
                _binding.categoryPhones.isChecked = false
                category = "Laptops"
            }

            _binding.drawerLayout2.closeDrawer(GravityCompat.END)
            _viewModel.loadProducts(category, minPrice, maxPrice)
        }
        //GAMING
        _binding.categoryGaming.setOnClickListener {

            if (category == "Gaming") {

                _binding.categoryGaming.isChecked = false
                category = null

            } else {

                _binding.categoryGaming.isChecked = true

                _binding.categoryTablets.isChecked = false
                _binding.categoryAccessories.isChecked = false
                _binding.categoryLaptops.isChecked = false
                _binding.categoryMonitors.isChecked = false
                _binding.categoryPhones.isChecked = false
                category = "Gaming"
            }

            _binding.drawerLayout2.closeDrawer(GravityCompat.END)
            _viewModel.loadProducts(category, minPrice, maxPrice)
        }


        //CHIPS
        _binding.chipUnder100.setOnClickListener {
            minPrice =0.0
            maxPrice =100.0
            _viewModel.loadProducts(category,minPrice,maxPrice)
        }
        _binding.chip5001000.setOnClickListener {
            minPrice =500.0
            maxPrice =1000.0
            _viewModel.loadProducts(category,minPrice,maxPrice)
        }

        _binding.chip100500.setOnClickListener {
            minPrice =100.0
            maxPrice =500.0
            _viewModel.loadProducts(category,minPrice,maxPrice)
        }
        _binding.chipOver1000.setOnClickListener {
            minPrice=1000.0
            maxPrice=null
            _viewModel.loadProducts(category,minPrice,maxPrice)
        }
    }

    // PRODUCT CLICK
    override fun onProductClick(
        product: Products
    ) {

        val bundle = Bundle()

        bundle.putString(
            "productId",
            product.productId
        )

        findNavController().navigate(
            R.id.action_AllProductsFragment_to_ProductDetailsFragment,
            bundle
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}