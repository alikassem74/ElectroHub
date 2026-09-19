package com.example.electrohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemProductBinding
import com.example.electrohub.interfaces.IProductClickListener
import com.example.electrohub.models.Products
import com.example.electrohub.viewHolders.ProductsViewHolder

class ProductsAdapter(private val products: ArrayList<Products>) : RecyclerView.Adapter<ProductsViewHolder>() {
    var inter: IProductClickListener? = null

    fun updateProducts(newProducts: ArrayList<Products>){

        products.clear()

        products.addAll(newProducts)

        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductsViewHolder {

        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {

        holder.bind(products[position])

        holder.itemView.setOnClickListener {
            inter?.onProductClick(products[position])
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}