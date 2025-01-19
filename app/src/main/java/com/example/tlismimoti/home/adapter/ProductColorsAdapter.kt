package com.example.tlismimoti.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tlismimoti.databinding.ItemProductcolorsBinding

class ProductColorsAdapter(val list: Array<String?>): RecyclerView.Adapter<ProductColorsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductcolorsBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(color: Int){
            binding.imgColor.setImageResource(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductcolorsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = list.get(position)

        if (color != null) {
            holder.bindItem(color.toInt())
        }

        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}