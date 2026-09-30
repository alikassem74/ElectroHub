package com.example.electrohub.fragments

import android.os.Bundle
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
import com.example.electrohub.databinding.FragmentSavedForLaterBinding
import com.example.electrohub.interfaces.IProductClickListener
import com.example.electrohub.models.Products
import com.example.electrohub.viewmodels.SavedViewModel
import kotlinx.coroutines.launch


class SavedForLaterFragment : Fragment(), IProductClickListener {

    private lateinit var _binding: FragmentSavedForLaterBinding

    private lateinit var adapter: ProductsAdapter

    private val _viewModel: SavedViewModel by viewModels()

    // CREATE VIEW
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSavedForLaterBinding.inflate(
                inflater,
                container,
                false
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

        setViews()
        setObservers()

        _viewModel.loadSavedProducts()
    }

    // VIEWS
    private fun setViews() {
        _binding.bottomNavigation.selectedItemId = R.id.nav_SavedForLater

        _binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_SaveForLaterFragment_to_HomeFragment)
                    true
                }

                R.id.nav_SavedForLater -> {
                    true
                }

                R.id.nav_ShopAll -> {
                    findNavController().navigate(
                        R.id.action_SaveForLaterFragment_to_AllProductsFragment
                    )
                    true
                }
                R.id.nav_Profile -> {
                    findNavController().navigate(
                        R.id.action_SaveForLaterFragment_to_ProfileFragment
                    )
                    true
                }
                else -> false
            }
        }

        _binding.rvSavedProducts.layoutManager =
            GridLayoutManager(
                requireContext(),
                2
            )


        adapter =
            ProductsAdapter(
                ArrayList()
            ).apply {

                inter =
                    this@SavedForLaterFragment
            }


        _binding.rvSavedProducts.adapter =
            adapter
    }

    // OBSERVERS
    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                _viewModel.savedProducts
                    .collect { products ->

                        adapter.updateProducts(
                            ArrayList(products)
                        )


                        if (products.isEmpty()) {

                            _binding.llEmpty.visibility =
                                View.VISIBLE

                            _binding.rvSavedProducts.visibility =
                                View.GONE

                        } else {

                            _binding.llEmpty.visibility =
                                View.GONE

                            _binding.rvSavedProducts.visibility =
                                View.VISIBLE
                        }
                    }
            }
        }
    }


    // PRODUCT CLICK
    override fun onProductClick(
        product: Products
    ) {

        val bundle =
            Bundle().apply {

                putString(
                    "productId",
                    product.productId
                )
            }


        Log.d(
            "SAVED_CLICK",
            "Clicked product ID: ${product.productId}"
        )


        findNavController().navigate(
            R.id.action_SaveForLaterFragment_to_ProductDetailsFragment,
            bundle
        )
    }



    // DESTROY VIEW
    override fun onDestroyView() {

        super.onDestroyView()
    }
}