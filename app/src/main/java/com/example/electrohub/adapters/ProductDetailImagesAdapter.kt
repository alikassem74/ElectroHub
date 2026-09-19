package com.example.electrohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemDetailsImageBinding
import com.example.electrohub.viewHolders.ProductDetailImagesViewHolder

class ProductDetailImagesAdapter(private val images: ArrayList<String>
) : RecyclerView.Adapter<ProductDetailImagesViewHolder>() {


    fun updateImages(newImages: ArrayList<String>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailImagesViewHolder {
        val binding =
            ItemDetailsImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ProductDetailImagesViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: ProductDetailImagesViewHolder,
        position: Int
    ) {
        holder.bind(
            images[position]
        )
    }
    override fun getItemCount(): Int {
        return images.size
    }
}