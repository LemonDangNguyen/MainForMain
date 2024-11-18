package com.example.selectpic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class SelectedImagesAdapter(
    private val context: Context,
    private val selectedImages: List<ImageModel>
) : RecyclerView.Adapter<SelectedImagesAdapter.SelectedImageViewHolder>() {

    inner class SelectedImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.selected_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_image, parent, false)
        return SelectedImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val image = selectedImages[position]

        Glide.with(context).load(image.filePath).into(holder.imageView)
    }

    override fun getItemCount(): Int = selectedImages.size
}
