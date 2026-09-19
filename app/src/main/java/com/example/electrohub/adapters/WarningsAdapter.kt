package com.example.electrohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemWarningBinding
import com.example.electrohub.models.Warning

class WarningsAdapter(
    private var warnings: List<Warning>
) : RecyclerView.Adapter<WarningsViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WarningsViewHolder {

        val binding =
            ItemWarningBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return WarningsViewHolder(
            binding
        )
    }


    override fun onBindViewHolder(
        holder: WarningsViewHolder,
        position: Int
    ) {

        holder.bind(
            warnings[position]
        )
    }


    override fun getItemCount(): Int =
        warnings.size


    fun updateWarnings(
        newWarnings: List<Warning>
    ) {

        warnings = newWarnings

        notifyDataSetChanged()
    }
}