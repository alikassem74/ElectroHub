package com.example.electrohub.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.R
import com.example.electrohub.databinding.ItemWarningBinding
import com.example.electrohub.models.Warning
import java.text.SimpleDateFormat
import java.util.Locale

class WarningsViewHolder(
    private val binding: ItemWarningBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    fun bind(
        warning: Warning
    ) {

        // =====================================================
        // TITLE
        // =====================================================

        binding.txtVWarningTitle.setText(
            R.string.warning
        )


        // =====================================================
        // MESSAGE
        // =====================================================

        binding.txtVWarningMessage.text =
            warning.message


        // =====================================================
        // DATE
        // =====================================================

        binding.txtVWarningDate.text =
            warning.createdAt
                ?.toDate()
                ?.let { date ->

                    SimpleDateFormat(
                        "MMM dd, yyyy • HH:mm",
                        Locale.getDefault()
                    ).format(date)

                }
                ?: ""
    }
}