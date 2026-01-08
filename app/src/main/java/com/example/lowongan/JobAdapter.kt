package com.example.lowongan

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lowongan.databinding.ItemJobCardBinding
import com.google.android.material.chip.Chip

class JobAdapter(
    private val jobList: List<JobModel>,
    private val onItemClick: (JobModel) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(val binding: ItemJobCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvCompanyCity.text = "${job.perusahaan} • ${job.kota}"
            tvPosition.text = job.position

            val cleanDesc = job.deskripsi_lengkap.replace("\\n", " ").replace("\n", " ")
            tvDescription.text = cleanDesc

            val percentage = (job.score * 100).toInt()
            tvMatchScore.text = "$percentage% Match"

            chipGroupTools.removeAllViews()
            val toolsArray = job.tools.split(",").map { it.trim() }

            for (tool in toolsArray.take(4)) {
                if (tool.isNotEmpty()) {
                    val chip = Chip(context).apply {
                        text = tool
                        textSize = 11f
                        setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F1F8E9")))
                        setTextColor(Color.parseColor("#33691E"))
                        isCheckable = false
                        isClickable = false
                        chipMinHeight = 28f
                        textStartPadding = 4f
                        textEndPadding = 4f
                        isCloseIconVisible = false
                    }
                    chipGroupTools.addView(chip)
                }
            }

            root.setOnClickListener {
                onItemClick(job)
            }
        }
    }

    override fun getItemCount() = jobList.size
}