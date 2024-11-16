package com.example.selectpic

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val context: Context,
    private val imagePaths: List<String>,
    private val onItemSelected: (Uri, Boolean) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private val selectedImagesMap = mutableMapOf<Uri, Int>()  // Lưu thứ tự của ảnh được chọn
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val selectionOrder: TextView = view.findViewById(R.id.selectionOrder)  // TextView để hiển thị thứ tự
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = imagePaths[position]
        val uri = Uri.parse(imagePath)

        Glide.with(context).load(imagePath).error(R.drawable.noimage).centerCrop().into(holder.imageView)

        if (selectedImagesMap.containsKey(uri)) {
            holder.selectionOrder.text = selectedImagesMap[uri].toString()
            holder.selectionOrder.visibility = View.VISIBLE
        } else {
            holder.selectionOrder.visibility = View.GONE
        }

        holder.imageView.setOnClickListener {
            val isSelected = !selectedImagesMap.containsKey(uri)
            onItemSelected(uri, isSelected)  // Xử lý chọn/bỏ chọn
        }
    }
    override fun getItemCount(): Int = imagePaths.size

    // Cập nhật thứ tự khi chọn lại ảnh
    fun updateSelection(selectedUris: List<Uri>) {
        selectedImagesMap.clear()
        selectedUris.forEachIndexed { index, uri ->
            selectedImagesMap[uri] = index + 1  // Đánh số thứ tự từ 1
        }
        notifyDataSetChanged()  // Cập nhật lại giao diện hiển thị
    }
    // Cập nhật thứ tự cho một ảnh đã chọn cụ thể
    fun updateSelectedOrder(uri: Uri, order: Int) {
        if (selectedImagesMap.containsKey(uri)) {
            selectedImagesMap[uri] = order
            notifyDataSetChanged()  // Cập nhật lại giao diện hiển thị
        }
    }
}
