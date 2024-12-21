package com.example.tlismimoti .order.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tlismimoti.databinding.SingleRowOrderNewBinding
import com.example.tlismimoti.order.OrderDetails
import com.example.tlismimoti.order.model.modelOrderDet.OrderItemWithFile
import com.example.tlismimoti.sharedpreferences.SessionManager

class AdapterOrderDetail(
    val context: Context,
    var list: ArrayList<OrderItemWithFile>,
 ) : RecyclerView.Adapter<AdapterOrderDetail.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowOrderNewBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleRowOrderNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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
               // for (i in term.){
                    binding.tvProducts.text=   term.title
                    binding.tvPrice.text =  "${sessionManager.currency}"+amount.toString()

               // }
//                 if (list[position].url != null) {
//                    Picasso.get().load("http:"+list[position].url)
//                        .placeholder(R.drawable.placeholder_n)
//                        .error(R.drawable.error_placeholder)
//                        .into(binding.image)
//
//                }
                binding.mainLayout.setOnClickListener {
                    val intent = Intent(context as Activity, OrderDetails::class.java)
                        .putExtra("id", list[position].id.toString())
                    context.startActivity(intent)

                }

             }
        }
    }
//    interface Cart{
//        fun addToCart(toString: String)
//    }
}
