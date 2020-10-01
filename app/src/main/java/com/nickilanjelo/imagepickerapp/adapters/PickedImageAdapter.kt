package com.nickilanjelo.imagepickerapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nickilanjelo.imagepickerapp.R
import kotlinx.android.synthetic.main.item_img.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.collections.ArrayList

class PickedImageAdapter(
    images: List<Uri> = emptyList(),
    private val listener: EmptyImageSetListener,
    private val deleteListener: OnItemDeleteListener
): RecyclerView.Adapter<PickedImageAdapter.PickedImageViewHolder>() {

    private val imageSet = images.toMutableSet()

    inner class PickedImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(uri: Uri) {
            itemView.apply {
                imgVw.setImageURI(uri)
                imgDelete.setOnClickListener {
                    deleteListener.onItemDelete(uri)
                }
            }
        }
    }

    fun addItems(newImages: List<Uri>) {
        imageSet.addAll(newImages)
        notifyDataSetChanged()
        listener.setScreenEmpty(imageSet.isEmpty())
    }

    fun clearItems() {
        imageSet.clear()
        notifyDataSetChanged()
    }

    fun removeItem(uri: Uri) {
        val position = imageSet.indexOf(uri)
        imageSet.remove(uri)
        notifyItemRemoved(position)
        listener.setScreenEmpty(imageSet.isEmpty())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImageViewHolder {
        return PickedImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_img, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PickedImageViewHolder, position: Int) {
        holder.bind(imageSet.elementAt(position))
    }

    override fun getItemCount() = imageSet.size

    interface EmptyImageSetListener {
        fun setScreenEmpty(isEmpty: Boolean)
    }

    interface OnItemDeleteListener {
        fun onItemDelete(uri: Uri)
    }
}