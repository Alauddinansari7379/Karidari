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
import com.example.tlismimoti.order.model.ModelPendingOrder.Info
import com.example.tlismimoti.sharedpreferences.SessionManager

class AdapterOrderCompleted(
    val context: Context,
    var list: ArrayList<Info>?,
) : RecyclerView.Adapter<AdapterOrderCompleted.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowOrderNewBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleRowOrderNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return list?.size!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        with(holder) {
            with(list?.get(position)!! ) {
                // Set price with the currency
                if (order_item.isNotEmpty()) {
                    binding.tvPrice.text = "Price- "+sessionManager.currency+order_item[0].amount
                }

                // Set order number
               // binding.tvOrderNo.text = "Order No- $order_no"

                // Collect all titles from order_item and join them with a comma
                val productTitles = order_item.map { it.term.title }.joinToString(", ")
                binding.tvProducts.text = "Products- $productTitles"

                // Handle image loading if required
                // Uncomment this block if image URLs are provided and needed
                /*
                if (url != null) {
                    Picasso.get().load("http:$url")
                        .placeholder(R.drawable.placeholder_n)
                        .error(R.drawable.error_placeholder)
                        .into(binding.image)
                }
                */

                // Set click listener for the main layout
                binding.mainLayout.setOnClickListener {
                    val intent = Intent(context as Activity, OrderDetails::class.java)
                        .putExtra("id", id.toString())
                    context.startActivity(intent)
                }
            }
        }
    }

//    interface Cart{
//        fun addToCart(toString: String)
//    }
}
