package com.example.tlismimoti.listing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.amtech.mehfeel.category.model.ModelCatWithId.ModelCatWithId
import com.amtech.mehfeel.category.model.ModelCatWithId.TakeProd
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.Checkout
import com.example.tlismimoti.cart.model.modelgetCart.ModelGetCart
import com.example.tlismimoti.databinding.ActivityListingBinding
import com.example.tlismimoti.home.model.DataX
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.listing.adapter.AdapterListing
import com.example.tlismimoti.listing.adapter.AdapterListingNew
import com.example.tlismimoti.mainActivity.MainActivity
import com.example.tlismimoti.mainActivity.MainActivity.Companion.listing
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Listing : AppCompatActivity() {
    private lateinit var binding:ActivityListingBinding
     private lateinit var sessionManager:SessionManager
     private var context=this@Listing
    private var mainData = ArrayList<TakeProd>()
    var shimmerFrameLayout: ShimmerFrameLayout? = null
    var count=0
    var countNew=0
    var catId=""
    var catName=""
    val finalTotal=ArrayList<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shimmerFrameLayout = findViewById(R.id.shimmer_listing)
        shimmerFrameLayout!!.startShimmer()
        sessionManager= SessionManager(context)
         catId=intent.getStringExtra("catId").toString()
        catName=intent.getStringExtra("catName").toString()
        binding.appCompatTextView2.text=catName
//        when(value){
//            "MEN"->{
//                binding.appCompatTextView2.text="Men"
//            }
//            "WOMEN"->{
//                binding.appCompatTextView2.text="Women"
//            }
//            "KIDS"->{
//                binding.appCompatTextView2.text ="Kids"
//            }else->{
//            binding.appCompatTextView2.text ="Special Deal"
//            }
//        }
        apiCallProductWithId()
        apiCallCartProduct()

        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }
            // Assuming you have a NavController

            // Declare a variable to track navigation source

             btnFloating.setOnClickListener {
                listing = true // Set the flag
                val intent = Intent(this@Listing, MainActivity::class.java)
                intent.putExtra("navigate_to", "fragment_cart")
                startActivity(intent)
            }


            edtSearch.addTextChangedListener { str ->
                setRecyclerViewAdapter(mainData.filter {
                    it.title != null && it.title.toString()!!.contains(str.toString(), ignoreCase = true)
                } as ArrayList<TakeProd>)
            }

       }

    }
    private fun setRecyclerViewAdapter(data: ArrayList<TakeProd>) {
        binding.recyclerView.apply {
            adapter = AdapterListingNew(context, data)
            AppProgressBar.hideLoaderDialog()

        }
    }
     private fun apiCallProductWithId() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getProductByCategory(sessionManager.authToken!!,catId)
            .enqueue(object : Callback<ModelCatWithId> {
                override fun onResponse(
                    call: Call<ModelCatWithId>, response: Response<ModelCatWithId>
                ) {
                    try {
                        if (response.code() == 200) {
                            for (i in response.body()!!.data){
                                mainData = i.take_prod

                            }
                            AppProgressBar.hideLoaderDialog()

                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.isEmpty()) {
                            binding!!.recyclerView.adapter =
                                AdapterListingNew(context, mainData)

                            myToast(context, "No Product Found")
                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            binding!!.recyclerView.adapter = AdapterListingNew(context, mainData)
                            binding!!.recyclerView.layoutManager = GridLayoutManager(context, 2)

                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()
//                            val men = java.util.ArrayList<DataX>()
//                            val women = java.util.ArrayList<DataX>()
//                            val kids = java.util.ArrayList<DataX>()
//                            // mainData = response.body()!!.data.posts.data
//                            for (i in response.body()!!.data.posts.data) {
//                                for (i1 in i.categories) {
//                                    if (i1.name.isNotEmpty()) {
//                                        when (i1.name) {
//                                            "MEN" -> men.add(i)
//                                            "WOMEN" -> women.add(i)
//                                            "KIDS" -> kids.add(i)
//                                        }
//                                    }
//
//                                }
//                            }
//                            var mainList = java.util.ArrayList<DataX>()
//
//                            mainList = when(value){
//                                "MEN"->{
//                                    men
//                                }
//                                "WOMEN"->{
//                                    women
//                                }
//                                "KIDS"->{
//                                    kids
//                                }else->{
//                                    response.body()!!.data.posts.data
//
//                                }
//                            }
//
//                            binding!!.recyclerView.adapter = AdapterListing(context, mainList)
//                            binding!!.recyclerView.layoutManager = GridLayoutManager(context, 2)
//
//                            binding.shimmerListing.visibility = View.GONE
//                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelCatWithId>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 7) {
                        Log.e("count", count.toString())
                        apiCallProductWithId()
                    } else {
                        count=0
                        myToast(this@Listing, "Try again")
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallCartProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getCart(sessionManager.authToken!!,sessionManager.deviceId,sessionManager.randomKey,"cart")
            .enqueue(object : Callback<ModelGetCart> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelGetCart>, response: Response<ModelGetCart>
                ) {
                    try {
                        if (response.code() == 200) {
                            // mainData = response.body()!!.data.items!!
                            AppProgressBar.hideLoaderDialog()

                        }
                        if (response.code() == 500) {
                            myToast(this@Listing, "Server Error")

                        } else if (response.body()!!.data.items.equals("")) {

                            myToast(this@Listing, "No Item Found")
                            //  binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {

                            for (i in response.body()!!.data.items){
                                binding.btnFloating.text=  "${sessionManager.currency}"+i.final_total
                                binding.qty.text= response.body()!!.data.items.size.toString()

                            }
                            finalTotal.clear()
                            // binding.shimmer.visibility = View.GONE
                            for (i in response.body()!!.data.items) {
                                finalTotal.add(i.qty.toInt()*i.price.toInt())
                            }

                            binding.btnFloating.text =  "${sessionManager.currency}" + finalTotal.sum()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelGetCart>, t: Throwable) {
                    countNew++
                    if (countNew<=7){
                        apiCallCartProduct()
                    }else{
                        myToast(this@Listing, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

}