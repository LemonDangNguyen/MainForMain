package com.example.selectpic
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.selectpic.R

class SelectedImagesAdapter(private val selectedImages: List<Uri>) : RecyclerView.Adapter<SelectedImagesAdapter.SelectedImageViewHolder>() {

    inner class SelectedImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.selected_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_image, parent, false)
        return SelectedImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(selectedImages[position])
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = selectedImages.size
}
