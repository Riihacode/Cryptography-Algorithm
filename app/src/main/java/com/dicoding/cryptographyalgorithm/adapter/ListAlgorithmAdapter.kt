package com.dicoding.cryptographyalgorithm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.data_class.CryptoData

class ListAlgorithmAdapter(private val listAlgorithm: ArrayList<CryptoData>) : RecyclerView.Adapter<ListAlgorithmAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_algorithm, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo) = listAlgorithm[position]
        holder.tvName.text = name
        holder.tvDescription.text = description
        holder.imgPhoto.setImageResource(photo)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listAlgorithm[position])
        }
    }

    override fun getItemCount(): Int = listAlgorithm.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: CryptoData)
    }
}
