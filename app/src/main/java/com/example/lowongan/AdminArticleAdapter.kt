package com.example.lowongan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lowongan.databinding.ItemAdminArticleBinding

class AdminArticleAdapter(
    private var list: List<ArticleModel>,
    private val listener: (ArticleModel) -> Unit
) : RecyclerView.Adapter<AdminArticleAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAdminArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvSnippet.text = item.content // Nanti ini kita potong biar ga kepanjangan

        holder.itemView.setOnClickListener {
            listener(item) // Kirim data item saat diklik
        }
    }

    override fun getItemCount() = list.size

    // Fungsi untuk update data dari Activity
    fun updateData(newList: List<ArticleModel>) {
        list = newList
        notifyDataSetChanged()
    }
}