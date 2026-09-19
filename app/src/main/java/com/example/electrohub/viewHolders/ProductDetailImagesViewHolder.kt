package com.example.electrohub.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.electrohub.databinding.ItemDetailsImageBinding


class ProductDetailImagesViewHolder(
    private val binding: ItemDetailsImageBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(imageUrl: String) {

        Glide.with(binding.root.context)
            .load(imageUrl)
            .into(binding.imgVProduct)

    }

}