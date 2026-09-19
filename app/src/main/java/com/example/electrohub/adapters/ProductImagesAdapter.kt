package com.example.electrohub.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemProductImageBinding
import com.example.electrohub.viewHolders.ProductImagesViewHolder

class ProductImagesAdapter(
    private val images: ArrayList<Uri>,
    private val showDelete: Boolean,
    private val onDeleteClick: (Uri) -> Unit
) : RecyclerView.Adapter<ProductImagesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductImagesViewHolder {

        val binding = ItemProductImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProductImagesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductImagesViewHolder,
        position: Int
    ) {

        val image = images[position]

        holder.bind(image)

        if (showDelete) {

            holder.binding.btnDeleteImage.visibility = View.VISIBLE

            holder.binding.btnDeleteImage.setOnClickListener {
                onDeleteClick(image)
            }

        } else {

            holder.binding.btnDeleteImage.visibility = View.GONE
            holder.binding.btnDeleteImage.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateImages(
        newImages: ArrayList<Uri>
    ) {

        images.clear()
        images.addAll(newImages)

        notifyDataSetChanged()
    }
}