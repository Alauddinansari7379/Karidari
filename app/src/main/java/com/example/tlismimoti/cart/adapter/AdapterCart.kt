package com.example.tlismimoti.cart.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tlismimoti.Helper.vibrateOnce
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.model.Items
import com.example.tlismimoti.cart.model.modelgetCart.Item
import com.example.tlismimoti.databinding.SingleRowCartBinding
import com.example.tlismimoti.listing.DetailPage
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.squareup.picasso.Picasso

class AdapterCart(
    val context: Context,
    var list: ArrayList<Items>,
    val cart:Cart
) : RecyclerView.Adapter<AdapterCart.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowCartBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleRowCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager= SessionManager(context)
        with(holder){
            with(list[position]){
                binding.priceUndrline.paintFlags = binding.priceUndrline.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.price.text = "${sessionManager.currency}$price"
                binding.title.text = term_title
                binding.qty.text = qty
                for (i in attributes){
                    binding.tvVariation.text=  i.variation.name
                }
                for (i in options){
                    binding.tvOptions.text=  i.name
                }
                if (list[position].preview != null) {
                    Picasso.get().load("http:"+list[position].preview)
                        .placeholder(R.drawable.placeholder_n)
                        .error(R.drawable.error_placeholder)
                        .into(binding.image)
                }

//                for (i in list[position].medias){
//                    if (list[position].medias != null) {
//                        Picasso.get().load("https:" + i.url)
//                            .placeholder(R.drawable.placeholder_n)
//                            .error(R.drawable.error_placeholder)
//                            .into(holder.imgPro)
//
//                    }
//                }
                 binding.layoutDetail.setOnClickListener {
                    val intent = Intent(context as Activity, DetailPage::class.java)
                        .putExtra("id", term_id)
                    context.startActivity(intent)
                }
                binding.layoutPlush.setOnClickListener {
                    vibrateOnce(context)
                    var variationId=""
                    var optionId=""
                    for (i in attributes){
                        variationId = i.variation.id.toString()
                    }
                    for (i in options){
                        optionId =  i.id.toString()
                    }
                    cart.addToCart(term_id,variationId,optionId)
                }
                binding.layoutDelete.setOnClickListener {
                    vibrateOnce(context)
                    cart.removeToCart(id.toString())
                }

             }
        }
    }
    interface Cart{
        fun addToCart(toString: String, variationId: String, optionId: String)
        fun removeToCart(toString: String)
    }
}
