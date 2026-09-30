package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electrohub.R
import com.example.electrohub.adapters.ProductsAdapter
import com.example.electrohub.databinding.FragmentMyProductsBinding
import com.example.electrohub.interfaces.IProductClickListener
import com.example.electrohub.models.Products
import com.example.electrohub.viewmodels.MyProductsViewModel

class MyProductsFragment :
    Fragment(),
    IProductClickListener {

    private lateinit var _binding: FragmentMyProductsBinding

    private val _viewModel: MyProductsViewModel by viewModels()

    private lateinit var _productsAdapter: ProductsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentMyProductsBinding.inflate(
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

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setViews()
        setObservers()

        _viewModel.loadMyProducts()
    }


    private fun setViews() {

        _binding.rvMyProducts.layoutManager =
            GridLayoutManager(
                requireContext(),
                2
            )


        _productsAdapter =
            ProductsAdapter(
                ArrayList()
            ).apply {

                inter = this@MyProductsFragment
            }


        _binding.rvMyProducts.adapter =
            _productsAdapter


        _binding.btnBack.setOnClickListener {

            findNavController()
                .navigateUp()
        }
    }


    private fun setObservers() {

        _viewModel
            .liveMyProducts()
            .observe(viewLifecycleOwner) { products ->

                _productsAdapter.updateProducts(
                    ArrayList(products)
                )
                if (products.isEmpty()) {

                    _binding.llEmpty.visibility =
                        View.VISIBLE

                    _binding.rvMyProducts.visibility =
                        View.GONE

                } else {

                    _binding.llEmpty.visibility =
                        View.GONE

                    _binding.rvMyProducts.visibility =
                        View.VISIBLE
                }
            }
    }


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


        findNavController().navigate(
            R.id.action_MyProductsFragment_to_ProductDetailsFragment,
            bundle
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}