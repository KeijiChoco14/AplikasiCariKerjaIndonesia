package com.example.lowongan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lowongan.databinding.ItemArticleUserBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserArticleAdapter(
    private var list: List<ArticleModel>,
    private val listener: (ArticleModel) -> Unit
) : RecyclerView.Adapter<UserArticleAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemArticleUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvSnippet.text = item.content // TextView akan otomatis potong (ellipsize) sesuai XML

        // Format Tanggal (Dari Long ke String Readable)
        if (item.date > 0) {
            val date = Date(item.date)
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            holder.binding.tvDate.text = format.format(date)
        } else {
            holder.binding.tvDate.text = "-"
        }

        // Load Gambar pakai Glide
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery) // Gambar loading sementara
                .error(android.R.drawable.ic_menu_report_image) // Gambar jika error/link mati
                .into(holder.binding.imgArticle)
        } else {
            // Jika tidak ada URL, set gambar default
            holder.binding.imgArticle.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<ArticleModel>) {
        list = newList
        notifyDataSetChanged()
    }
}