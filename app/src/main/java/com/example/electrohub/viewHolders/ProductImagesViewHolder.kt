package com.example.electrohub.viewHolders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.electrohub.databinding.ItemProductImageBinding
class ProductImagesViewHolder( val binding: ItemProductImageBinding ) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: Any) {
        Glide.with(binding.imgVProduct.context)
            .load(image)
            .into(binding.imgVProduct)
    }
}