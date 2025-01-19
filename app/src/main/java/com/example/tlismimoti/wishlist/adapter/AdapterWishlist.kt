package com.example.tlismimoti.wishlist.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tlismimoti.R
import com.example.tlismimoti.listing.DetailPage
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.example.tlismimoti.wishlist.model.Item
import com.squareup.picasso.Picasso


class AdapterWishlist(val context: Context, private val list: ArrayList<Item>) :
    RecyclerView.Adapter<AdapterWishlist.MyViewHolder>() {
    lateinit var sessionManager: SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_favoutfit, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.SrNo.text= "${position+1}"
        sessionManager=SessionManager(context)
//        holder.tvTitle.text = list[position].term_title
//        holder.tvPricePro.text = "${sessionManager.currency}" + list[position].price
        // holder.tvRatingPro.text = list[position].title
        if (list[position].preview != null) {
            Picasso.get().load("http:" + list[position].preview)
                .placeholder(R.drawable.placeholder_n)
                .error(R.drawable.error_placeholder)
                .into(holder.imgPro)

        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context as Activity, DetailPage::class.java)
                .putExtra("id", list[position].term_id.toString())
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return list.size

    }

    open class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPro: ImageView = itemView.findViewById(R.id.imageLis)
//        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
//        val tvPricePro: TextView = itemView.findViewById(R.id.priceLis)
//        val tvRatingPro: TextView = itemView.findViewById(R.id.tvRatingPro)
//        val tvDescPro: TextView = itemView.findViewById(R.id.tvDescPro)


    }
}