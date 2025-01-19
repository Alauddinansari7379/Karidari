package com.example.tlismimoti.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.ItemCategoryBinding
import com.example.tlismimoti.home.model.OutfitCategory
import com.example.tlismimoti.categories.model.ModelCatWithId.modelCat.DataX
import com.example.tlismimoti.listing.Listing
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.squareup.picasso.Picasso


class CategoryAdapter(val list: List<OutfitCategory>, val context: Context): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var mListener: OnCategoryClickListener? = null
    lateinit var sessionManager: SessionManager

    interface OnCategoryClickListener{
        fun onCategoryClick(position: Int)
    }

    public fun setOnCategoryClickListener(listener: OnCategoryClickListener){
        this.mListener = listener
    }

    inner class ViewHolder(val binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root){

        fun bindItem(category: OutfitCategory){
            binding.catName.text = category.catName
           // binding.catImage.setImageResource(category.catImage)
            if (category.catImage != null) {
                Picasso.get().load(sessionManager.baseURL + category.catImage)
                    .placeholder(R.drawable.placeholder_n)
                    .error(R.drawable.error_placeholder)
                    .into(binding.catImage)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager=SessionManager(context)
        val category = list.get(position)

        holder.bindItem(category)

        holder.itemView.setOnClickListener {
            val intent = Intent(context as Activity, Listing::class.java)
                .putExtra("catId", list[position].id.toString())
                .putExtra("catName", list[position].catName.toString())
            (context as Activity).startActivity(intent)
        }

        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}