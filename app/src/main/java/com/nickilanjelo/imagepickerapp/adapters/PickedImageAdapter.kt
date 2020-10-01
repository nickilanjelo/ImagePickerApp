package com.nickilanjelo.imagepickerapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nickilanjelo.imagepickerapp.R
import kotlinx.android.synthetic.main.item_img.view.*

class PickedImageAdapter(
    private val images: ArrayList<Uri>
): RecyclerView.Adapter<PickedImageAdapter.PickedImageViewHolder>() {
    inner class PickedImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(uri: Uri) {
            itemView.apply {
                imgVw.setImageURI(uri)
            }
        }
    }

    fun addItems(newImages: List<Uri>) {
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun clearItems() {
        images.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImageViewHolder {
        return PickedImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_img, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PickedImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount() = images.size
}