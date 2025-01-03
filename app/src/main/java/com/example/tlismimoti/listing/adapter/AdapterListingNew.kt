package com.example.tlismimoti.listing.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amtech.mehfeel.category.model.ModelCatWithId.TakeProd
import com.example.tlismimoti.R
import com.example.tlismimoti.home.model.DataX
import com.example.tlismimoti.listing.DetailPage
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.squareup.picasso.Picasso


class AdapterListingNew(val context: Context, private val list: ArrayList<TakeProd>) :
    RecyclerView.Adapter<AdapterListingNew.MyViewHolder>() {
    lateinit var sessionManager: SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_row_listing, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.SrNo.text= "${position+1}"
        sessionManager=SessionManager(context)
        try {
            holder.tvTitle.text = list[position].title
            holder.tvPricePro.text = "${sessionManager.currency}"+ list[position].price.price.toString()
            // holder.tvRatingPro.text = list[position].title
//            if (list[position].preview != null) {
//                Picasso.get().load("https:" + list[position].preview.media.url)
//                    .placeholder(R.drawable.placeholder_n)
//                    .error(R.drawable.error_placeholder)
//                    .into(holder.imgPro)
//
//            }
            for (i in list[position].medias){
                if (list[position].medias != null) {
                    Picasso.get().load("http:" + i.url)
                        .placeholder(R.drawable.placeholder_n)
                        .error(R.drawable.error_placeholder)
                        .into(holder.imgPro)
                }
            }
            holder.itemView.setOnClickListener {
//                if (list[position].affiliate != null) {
//                    val intent =
//                        Intent(Intent.ACTION_VIEW, Uri.parse(list[position].affiliate.value))
//                    context.startActivity(Intent.createChooser(intent, "Choose browser"))
//                } else {
                    val intent = Intent(context as Activity, DetailPage::class.java)
                        .putExtra("id", list[position].id.toString())
                    context.startActivity(intent)
               // }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun getItemCount(): Int {
        return list.size

    }

    open class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPro: ImageView = itemView.findViewById(R.id.imageLis)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPricePro: TextView = itemView.findViewById(R.id.priceLis)
//        val tvRatingPro: TextView = itemView.findViewById(R.id.tvRatingPro)
//        val tvDescPro: TextView = itemView.findViewById(R.id.tvDescPro)


    }
}