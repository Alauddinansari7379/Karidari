package com.example.tlismimoti.categories.Adapter

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
import com.example.tlismimoti.categories.model.ModelCatWithId.modelCat.DataX
import com.example.tlismimoti.home.model.ModeCatId
import com.example.tlismimoti.listing.Listing
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.squareup.picasso.Picasso


class AdapterCategory(val context: Context,private val list: ArrayList<DataX>) :
    RecyclerView.Adapter<AdapterCategory.MyViewHolder>() {
lateinit var sessionManager: SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_row_catgory, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.SrNo.text= "${position+1}"
        sessionManager= SessionManager(context)
        try {


             holder.tvCatRe.text = list[position].name



           // holder.tvPricePro.text = "$sessionManager.currency" + list[position].price.price.toString()
            // holder.tvRatingPro.text = list[position].title
            if (list[position].preview != null) {
                Picasso.get().load(sessionManager.baseURL + list[position].preview.content)
                    .placeholder(R.drawable.placeholder_n)
                    .error(R.drawable.error_placeholder)
                    .into(holder.imgView)

        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context as Activity, Listing::class.java)
                .putExtra("catId", list[position].id.toString())
                .putExtra("catName", list[position].name.toString())
            (context as Activity).startActivity(intent)
            }
//            val intent = Intent(context as Activity, DetailPage::class.java)
//                .putExtra("id", list[position].id.toString())
//                .putExtra("value", category)
//            context.startActivity(intent)
//        }
    }catch (e:Exception){
        e.printStackTrace()
    }
        }


    override fun getItemCount(): Int {
        return list.size

    }

    open class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val tvCatRe: TextView = itemView.findViewById(R.id.tvCatRe)
         val imgView: ImageView = itemView.findViewById(R.id.imgView)



    }
}