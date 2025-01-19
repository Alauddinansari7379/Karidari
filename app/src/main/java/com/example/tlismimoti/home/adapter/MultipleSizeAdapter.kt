package com.example.tlismimoti.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.ItemSizelistBinding

class MultipleSizeAdapter(val list: Array<String?>): RecyclerView.Adapter<MultipleSizeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSizelistBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(model: String){
            binding.title.text = model
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSizelistBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list.get(position)

        if (model != null) {
            holder.bindItem(model)
        }

        if (position == 0){
            holder.binding.bgImage.setImageResource(
                R.drawable.bg_size_selected
            )
            holder.binding.title.setTextColor(Color.WHITE)
            holder.binding.demoView.visibility = View.VISIBLE
        }

        holder.binding.parent.setOnClickListener {
            if (holder.binding.demoView.visibility == View.VISIBLE){
                holder.binding.bgImage.setImageResource(
                    R.drawable.bg_size_unselected
                )
                holder.binding.title.setTextColor(Color.BLACK)
                holder.binding.demoView.visibility = View.GONE
            }else{
                holder.binding.bgImage.setImageResource(
                    R.drawable.bg_size_selected
                )
                holder.binding.title.setTextColor(Color.WHITE)
                holder.binding.demoView.visibility = View.VISIBLE
            }
        }

        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}