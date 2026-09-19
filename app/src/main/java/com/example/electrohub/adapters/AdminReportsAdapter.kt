package com.example.electrohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemAdminReportBinding
import com.example.electrohub.models.Reports

class AdminReportsAdapter(
    private var reports: List<Reports>,
    private val onViewReportClick: (Reports) -> Unit
) : RecyclerView.Adapter<AdminReportsViewHolder>() {


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminReportsViewHolder {

        val binding =
            ItemAdminReportBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return AdminReportsViewHolder(
            binding,
            onViewReportClick
        )
    }


    // =========================================================
    // BIND
    // =========================================================

    override fun onBindViewHolder(
        holder: AdminReportsViewHolder,
        position: Int
    ) {

        holder.bind(
            reports[position]
        )
    }


    // =========================================================
    // COUNT
    // =========================================================

    override fun getItemCount(): Int =
        reports.size


    // =========================================================
    // UPDATE REPORTS
    // =========================================================

    fun updateReports(
        newReports: List<Reports>
    ) {

        reports =
            newReports

        notifyDataSetChanged()
    }
}