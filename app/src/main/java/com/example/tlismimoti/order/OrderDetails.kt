package com.example.tlismimoti .order

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.databinding.ActivityOrderDetBinding
import com.example.tlismimoti.order.adapter.AdapterOrderDetail
import com.example.tlismimoti.order.model.modelOrderDet.ModelOrderDetail
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetails : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    val context=this@OrderDetails
    var count=0
    var orderId=""
    private lateinit var binding:ActivityOrderDetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderDetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager= SessionManager(context)
        orderId=intent.getStringExtra("id").toString()
        apiCallGetAllOrderDet()
        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }

    }

    private fun apiCallGetAllOrderDet() {
         AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.orderDetail(sessionManager.authToken!!,sessionManager.userEmail,orderId)
            .enqueue(object : Callback<ModelOrderDetail> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelOrderDetail>, response: Response<ModelOrderDetail>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()


                        } else {
                            count=0
                             val responseData = response.body()?.data?.info

                            if (responseData != null) {
                                when (responseData.status) {
                                    "pending" -> {
                                        binding.tvOrderStatues.text = "In Progress"
                                        binding.tvOrderStatuesRec.text = "In Progress"
                                    }
                                    "cancel" -> {
                                        binding.tvOrderStatues.text = "Cancel"
                                        binding.tvOrderStatuesRec.text = "Cancel"
                                    }
                                    "completed" -> {
                                        binding.tvOrderStatues.text = "Completed"
                                        binding.tvOrderStatuesRec.text = "Completed"
                                    }
                                }
                                binding.itemQty.text=response.body()!!.data.info.order_item_with_file.size.toString()+" items"

                                binding.totalPrice.text= "${sessionManager.currency}"+response.body()!!.data.order_content.sub_total
                                binding.subTotal.text= "${sessionManager.currency}"+response.body()!!.data.order_content.sub_total
                                binding.discount.text= "${sessionManager.currency}"+response.body()!!.data.order_content.coupon_discount
                                binding.totalAmt.text= "${sessionManager.currency}"+response.body()!!.data.order_content.sub_totalfdf

                                binding!!.recyclerViewMen.adapter =
                                        AdapterOrderDetail(context, response.body()!!.data.info.order_item_with_file)

                                binding.tvDate.text = "Order Date-"+ response.body()!!.data?.info!!.created_at.substringBefore("T")
                                binding.tvOrderNo.text = "Order No-"+ response.body()!!.data?.info!!.order_no
                                binding.tvName.text = response.body()!!.data.order_content.name
                                binding.tvAddress.text = response.body()!!.data.order_content.address+" "+response.body()!!.data.order_content.zip_code
                            }

                            AppProgressBar.hideLoaderDialog()


                         //   mainData = response.body()!!.data.data
                         //   val gridLayoutManager = GridLayoutManager(activity, 2) // 2 columns
                          //  binding.recyclerView.layoutManager = gridLayoutManager

                         //   binding.recyclerView.adapter = activity?.let { AdapterCategory(it, mainData) }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelOrderDetail>, t: Throwable) {
                    //myToast(context, "Something went wrong")
                    count++
                    if (count <= 7) {
                        Log.e("count", count.toString())
                        apiCallGetAllOrderDet()
                    } else {
                        count=0
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

}