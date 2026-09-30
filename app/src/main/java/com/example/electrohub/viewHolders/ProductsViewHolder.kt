package com.example.electrohub.viewHolders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.electrohub.R
import com.example.electrohub.databinding.ItemProductBinding
import com.example.electrohub.models.Products

class ProductsViewHolder(
    val binding: ItemProductBinding
) : RecyclerView.ViewHolder(binding.root){

    @SuppressLint("SetTextI18n")
    fun bind(product: Products) {

        binding.productName.text = product.name
        binding.productCategory.text = product.category
        binding.productPrice.text = "$${product.price}"

        Glide.with(binding.root.context)
            .load(product.imageUrls.firstOrNull())
            .placeholder(R.drawable.loading_spinner)
            .error(R.drawable.loading_spinner)
            .into(binding.productImage)
    }

}