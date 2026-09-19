package com.example.electrohub.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electrohub.R
import com.example.electrohub.adapters.ProductsAdapter
import com.example.electrohub.databinding.FragmentHomeBinding
import com.example.electrohub.interfaces.IProductClickListener
import com.example.electrohub.models.Products
import com.example.electrohub.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), IProductClickListener {

    private lateinit var _binding: FragmentHomeBinding

    private val _viewModel: HomeViewModel by viewModels()

    private lateinit var _productsAdapter: ProductsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return _binding.root
    }


    override fun onStart() {
        super.onStart()
        setViews()
        setupSearch()
        setObservers()
        _viewModel.loadProducts()
    }


    // ============================================================
    // OBSERVE STATEFLOW
    // ============================================================

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                _viewModel.products.collect { products ->

                    Log.d(
                        "HomeFragment",
                        "Products received: ${products.size}"
                    )

                    _productsAdapter.updateProducts(
                        ArrayList(products)
                    )
                }
            }
        }
    }


    // ============================================================
    // SEARCH
    // ============================================================

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


    // ============================================================
    // VIEWS
    // ============================================================

    private fun setViews() {

        _binding.imgProfile.setOnClickListener {

            findNavController().navigate(
                R.id.action_HomeFragment_to_ProfileFragment
            )
        }


        _binding.rvProducts.layoutManager =
            GridLayoutManager(
                requireContext(),
                2
            )


        _binding.btnAddProduct.setOnClickListener {

            findNavController().navigate(
                R.id.action_HomeFragment_to_AddProductsFragment
            )
        }


        _binding.imgSavedForLater.setOnClickListener {

            findNavController().navigate(
                R.id.action_HomeFragment_to_SaveForLaterFragment
            )
        }


        _binding.btnBack.setOnClickListener {

            requireActivity().finish()
        }


        _productsAdapter =
            ProductsAdapter(ArrayList()).apply {

                inter = this@HomeFragment
            }


        _binding.rvProducts.adapter =
            _productsAdapter
    }


    // ============================================================
    // PRODUCT CLICK
    // ============================================================

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