package com.example.electrohub.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.electrohub.R
import com.example.electrohub.databinding.FragmentHomeBinding
import com.example.electrohub.viewmodels.HomeViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electrohub.adapters.ProductsAdapter
import com.example.electrohub.interfaces.IProductClickListener
import com.example.electrohub.models.Products
import kotlinx.coroutines.launch

class HomeFragment : Fragment(),IProductClickListener {

    private lateinit var _binding: FragmentHomeBinding

    private val _viewModel: HomeViewModel by viewModels()

    private  var _category: String = ""

    private lateinit var _productsAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setupSearch()
        setObservers()
        _viewModel.loadProducts()
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

    private fun setViews() {
        // Categories button
        _binding.imgViewCategories.setOnClickListener {

            if (_binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                _binding.drawerLayout.closeDrawer(GravityCompat.END)
                _binding.imgViewCategories.setImageResource(R.drawable.ic_menu)
            } else {
                _binding.drawerLayout.openDrawer(GravityCompat.END)
                _binding.imgViewCategories.setImageResource(R.drawable.ic_close)
            }
        }

        _binding.categoryPhones.setOnClickListener {
            _binding.drawerLayout.closeDrawer(GravityCompat.END)

            val bundle = Bundle()
            bundle.putString("categoryId","Phones")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        _binding.categoryLaptops.setOnClickListener {
            _binding.drawerLayout.closeDrawer(GravityCompat.END)

            val bundle = Bundle()
            bundle.putString("categoryId", "Laptops")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        _binding.categoryTablets.setOnClickListener {
            _binding.drawerLayout.closeDrawer(GravityCompat.END)
            val bundle = Bundle()
            bundle.putString("categoryId", "Tablets")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        _binding.categoryAccessories.setOnClickListener {
            _binding.drawerLayout.closeDrawer(GravityCompat.END)
            val bundle = Bundle()
            bundle.putString("categoryId", "Accessories")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        _binding.categoryMonitors.setOnClickListener {
            _binding.drawerLayout.closeDrawer(GravityCompat.END)
            val bundle = Bundle()
            bundle.putString("categoryId", "Monitors")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        _binding.categoryGaming.setOnClickListener {
            _binding.drawerLayout.closeDrawer(GravityCompat.END)
            val bundle = Bundle()
            bundle.putString("categoryId", "Gaming")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        // Search test
        _binding.etxtSearch.setOnEditorActionListener { _, _, _ ->
            Toast.makeText(
                requireContext(),
                "Search: ${_binding.etxtSearch.text}",
                Toast.LENGTH_SHORT
            ).show()

            true
        }

        _binding.cVPhones.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("categoryId", "Phones")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }
        _binding.cVAccessories.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("categoryId", "Accessories")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }
        _binding.cVGaming.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("categoryId", "Gaming")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }
        _binding.cVMonitors.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("categoryId", "Monitors")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )

        }
        _binding.cVLaptops.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("categoryId", "Laptops")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )

        }
        _binding.cVTablets.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("categoryId", "Tablets")

            findNavController().navigate(
                R.id.action_HomeFragment_to_AllProductsFragment,
                bundle
            )
        }

        _binding.rvProducts.layoutManager =
            GridLayoutManager(
                requireContext(),
                2
            )

        _productsAdapter = ProductsAdapter(ArrayList()).apply {

                inter = this@HomeFragment
            }


        _binding.rvProducts.adapter =
            _productsAdapter


        _binding.bottomNavigation.selectedItemId = R.id.nav_home

        _binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    true
                }

                R.id.nav_SavedForLater -> {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_SaveForLaterFragment
                    )
                    true
                }

                R.id.nav_ShopAll -> {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_AllProductsFragment
                    )
                    true
                }
                R.id.nav_Profile -> {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_ProfileFragment
                    )
                    true
                }
                else -> false
            }
        }

        _binding.txtShowAll.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_AllProductsFragment)
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
            R.id.action_HomeFragment_to_ProductDetailsFragment,
            bundle
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}