package com.example.electrohub.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemAdminReportBinding
import com.example.electrohub.models.Reports
import java.text.SimpleDateFormat
import java.util.Locale

class AdminReportsViewHolder(
    private val binding: ItemAdminReportBinding,
    private val onViewReportClick: (Reports) -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {

    fun bind(
        report: Reports
    ) {

        binding.txtVProductName.text =
            report.productName

        binding.txtVReason.text =
            report.reason

        binding.txtVStatus.text =
            report.status.uppercase()

        binding.txtVTimestamp.text =
            report.timestamp?.toDate()?.let {

                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(it)

            } ?: "Unknown date"


        binding.btnViewReport.setOnClickListener {

            onViewReportClick(
                report
            )
        }
    }
}