package com.example.tlismimoti.wishlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.databinding.ActivityWishlistBinding
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.example.tlismimoti.wishlist.adapter.AdapterWishlist
import com.example.tlismimoti.wishlist.model.ModelWishlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Wishlist : AppCompatActivity() {
     val binding by lazy {
         ActivityWishlistBinding.inflate(layoutInflater)
     }
    val context=this@Wishlist
    lateinit var sessionManager:SessionManager
    var count=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager= SessionManager(context)
        if (sessionManager.authTokenUser!!.isNotEmpty()){
            apiCallGetWishlist()
        }else{
            myToast(this@Wishlist,"User not logged in")
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

    }
    private fun apiCallGetWishlist() {
        // AppProgressBar.showLoaderDialog(context)a
        ApiClient.apiService.getWishlists(sessionManager.authToken!!,sessionManager.deviceId,sessionManager.randomKey,"wishlist")
            .enqueue(object : Callback<ModelWishlist> {
                override fun onResponse(
                    call: Call<ModelWishlist>, response: Response<ModelWishlist>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.items.isEmpty()) {
                            binding!!.recyclerView.adapter =
                                AdapterWishlist(context, response.body()!!.data.items)

                            myToast(context, "No Product Found")
                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            count=0
                            binding!!.recyclerView.adapter =
                                AdapterWishlist(context, response.body()!!.data.items)
                            binding!!.recyclerView.layoutManager =
                                GridLayoutManager(context, 2)

                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelWishlist>, t: Throwable) {
                      count++
                    if (count <= 7) {
                        Log.e("count", count.toString())
                        apiCallGetWishlist()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

}