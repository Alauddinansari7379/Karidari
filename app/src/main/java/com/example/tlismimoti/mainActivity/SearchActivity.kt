package com.example.tlismimoti.mainActivity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.ActivitySearchBinding
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.listing.adapter.AdapterListing
import com.example.tlismimoti.mainActivity.adapter.AutoSuggestAdapter
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    val context = this@SearchActivity
    var count=0
    val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager = SessionManager(context)
        val name=intent.getStringExtra("name")

        //apiCallProductList()
        apiCallSearchProduct(name!!)

        val autoSuggestAdapter = AutoSuggestAdapter(
            this@SearchActivity,
            android.R.layout.simple_list_item_1, MainActivity.ProductListSearch.toMutableList()
        )

        binding.SearchView.setAdapter(autoSuggestAdapter)
        binding.SearchView.threshold = 1

        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }
            imgClose.setOnClickListener {
                layoutTitle.visibility = View.VISIBLE
                appCompatTextView2.visibility = View.VISIBLE
                val rightSwipe: Animation =
                    AnimationUtils.loadAnimation(this@SearchActivity, R.anim.anim_right)
                layoutTitle.startAnimation(rightSwipe)
                layoutSearch.visibility = View.GONE
                binding.SearchView.text.clear()
                SearchView.hideKeyboard()

            }

            imgSearch.setOnClickListener {
                layoutTitle.visibility = View.GONE
                appCompatTextView2.visibility = View.GONE
                layoutSearch.visibility = View.VISIBLE
                val leftSwipe: Animation =
                    AnimationUtils.loadAnimation(this@SearchActivity, R.anim.anim_left)
                layoutSearch.startAnimation(leftSwipe)
                SearchView.requestFocus()
                SearchView.showKeyboard()

            }

            imgSearchNew.setOnClickListener {
                 AppProgressBar.showLoaderDialog(context)
                apiCallSearchProduct(binding.SearchView.text.toString().trim())
            }
        }
    }

    private fun apiCallSearchProduct(name:String) {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.searchProduct(sessionManager.authToken!!,name,"title")
            .enqueue(object : Callback<ModelProduct> {
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.posts.data
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.posts.data.isEmpty()) {
                            binding!!.recyclerView.adapter =
                                AdapterListing(context, response.body()!!.data.posts.data)

                            myToast(context, "No Product Found")
                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
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

                            binding!!.recyclerView.adapter =
                                AdapterListing(context, response.body()!!.data.posts.data)
                            binding!!.recyclerView.layoutManager =
                                GridLayoutManager(context, 2)
                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        binding.shimmerListing.visibility = View.GONE
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 10) {
                        Log.e("count", count.toString())
                        apiCallSearchProduct(name)
                    } else {
                        myToast(this@SearchActivity, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        binding.shimmerListing.visibility = View.GONE

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
     private fun EditText.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

}