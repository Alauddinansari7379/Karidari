package com.example.tlismimoti.listing

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.amtech.mehfeel.category.model.ChildrenCategory
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.Helper.vibrateOnce
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.CartFragment
import com.example.tlismimoti.databinding.ActivityDetailPageBinding
import com.example.tlismimoti.home.adapter.MultipleSizeAdapter
import com.example.tlismimoti.home.adapter.ProductColorsAdapter
import com.example.tlismimoti.home.adapter.ProductsAdapter
import com.example.tlismimoti.home.model.DataX
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.listing.adapter.AdapterListing
import com.example.tlismimoti.listing.model.Data
import com.example.tlismimoti.listing.model.ModelProductDetial
import com.example.tlismimoti.listing.model.cartModel.ModelCart
import com.example.tlismimoti.mainActivity.MainActivity
import com.example.tlismimoti.mainActivity.MainActivity.Companion.listing
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.example.tlismimoti.wishlist.model.ModelWishListId
import com.example.tlismimoti.wishlist.model.ModelWishlist
import com.ncorti.slidetoact.SlideToActView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Exception
import kotlin.Throwable
import kotlin.plus
import kotlin.toString
import kotlin.with

class DetailPage : AppCompatActivity() {
    private var context = this@DetailPage
    private lateinit var binding: ActivityDetailPageBinding
    private lateinit var sessionManager: SessionManager
    private var mainData = ArrayList<Data>()
    var productId = ""
    private var itemCount = 1
    private var count = 0
    private var countwishadd = 0
    private var countCart = 0
    private var countDet = 0
    private var countWish = 0
    private var countWishRe = 0
    var value = ""
    var wishlistId = ""
    var productTitle = ""
    var price = ""
    var option = ""
    var variation = ""
    private lateinit var mSizeAdapter: MultipleSizeAdapter
    private lateinit var mColorsAdapter: ProductColorsAdapter


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)
        productId = intent.getStringExtra("id").toString()
        value = intent.getStringExtra("value").toString()
        Log.e("value", value)

        Log.e("productId", productId)

        apiCallProductDetail()
        apiCallProduct()
        apiCallGetWishlist()

        with(binding) {
            imgBack.setOnClickListener {
                onBackPressed()
            }
//            btnAddToCart.setOnClickListener {
//                startActivity(Intent(this@DetailPage, Login::class.java))
//            }
//            colBlack.setOnClickListener {
//                tvColourName.text = "Black"
//                colBlack.background = ContextCompat.getDrawable(context, R.drawable.ring_bg)
//                colRed.background = ContextCompat.getDrawable(context, R.drawable.red_dot)
//                colBlue.background = ContextCompat.getDrawable(context, R.drawable.green_dot)
//            }
//            colRed.setOnClickListener {
//                tvColourName.text = "Red"
//                colBlack.background = ContextCompat.getDrawable(context, R.drawable.black_dot)
//                colRed.background = ContextCompat.getDrawable(context, R.drawable.ring_bg)
//                colBlue.background = ContextCompat.getDrawable(context, R.drawable.green_dot)
//            }
//            colBlue.setOnClickListener {
//                tvColourName.text = "Green"
//                colBlack.background = ContextCompat.getDrawable(context, R.drawable.black_dot)
//                colRed.background = ContextCompat.getDrawable(context, R.drawable.red_dot)
//                colBlue.background = ContextCompat.getDrawable(context, R.drawable.ring_bg)
//            }
//
//            layoutSmall.setOnClickListener {
//                tvSamll.setTextColor(Color.parseColor("#99C354"))
//                tvMedium.setTextColor(Color.parseColor("#FF000000"))
//                tvLarge.setTextColor(Color.parseColor("#FF000000"))
//            }
//
//            layoutMedium.setOnClickListener {
//                tvMedium.setTextColor(Color.parseColor("#99C354"))
//                tvSamll.setTextColor(Color.parseColor("#FF000000"))
//                tvLarge.setTextColor(Color.parseColor("#FF000000"))
//            }
//            layoutLarge.setOnClickListener {
//                tvLarge.setTextColor(Color.parseColor("#99C354"))
//                tvSamll.setTextColor(Color.parseColor("#FF000000"))
//                tvMedium.setTextColor(Color.parseColor("#FF000000"))
//            }
            layoutPlush.setOnClickListener {
                vibrateOnce(context)
                itemCount++
                tvCount.text = "${itemCount}"
            }

            layoutDelete.setOnClickListener {
                if (itemCount != 1) {
                    if (itemCount != 0) {
                        itemCount--
                        vibrateOnce(context)
                        tvCount.text = "${itemCount}"
                    }
                }
            }
//            btnAddToCart.setOnClickListener {
//                apiCallAddToCart()
//            }
            /*btnCart.setOnClickListener {
                val cartFragment = CartFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, cartFragment) // Replace existing fragment/view
                    .addToBackStack(null) // Add to back stack for navigation
                    .commit()
            }*/
            btnCart.setOnClickListener {
                listing = true // Set the flag
                val intent = Intent(this@DetailPage, MainActivity::class.java)
                intent.putExtra("navigate_to", "fragment_cart")
                startActivity(intent)
            }

            binding.btnAddToCart.onSlideCompleteListener = object: SlideToActView.OnSlideCompleteListener{
                override fun onSlideComplete(view: SlideToActView) {
                    apiCallAddToCart()
                }
            }
            wishList.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()) {
                    apiCallAddToWishList()
                } else {
                    myToast(this@DetailPage, "Please Login first")
                }
            }
            wishListRed.setOnClickListener {
                apiCallRemoveToWishList()
            }


        }


    }

    private fun apiCallProductDetail() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getProductDetail(sessionManager.authToken, productId)
            .enqueue(object : Callback<ModelProductDetial> {
                override fun onResponse(
                    call: Call<ModelProductDetial>, response: Response<ModelProductDetial>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.info.categories
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            countDet=0
                            productTitle = response.body()!!.data.info.title
                            binding.tvTitle.text = response.body()!!.data.info.title
                            binding.tvDescription.text =
                                response.body()!!.data.content.excerpt.toString()


                            if (response.body()!!.data.info.price.special_price != null) {
                                binding.tvPrice.text =
                                    "${sessionManager.currency}" + response.body()!!.data.info.price.special_price.toString()
                                binding.priceUndrline2.text =
                                    "${sessionManager.currency}"+ response.body()!!.data.info.price.regular_price.toString()
                                price = response.body()!!.data.info.price.special_price.toString()
                                binding.priceUndrline2.paintFlags =
                                    binding.priceUndrline2.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                            } else {
                                binding.tvPrice.text =
                                    "${sessionManager.currency}" + response.body()!!.data.info.price.price.toString()

//                                binding.tvPrice.text = "$sessionManager.currency" + response.body()!!.data.variations
//                                for (i in response.body()!!.data.variations ){
//                                    i.NewTest5
//                                }
                                price = response.body()!!.data.info.price.regular_price.toString()

                            }
                             for (i in response.body()!!.data.info.medias) {
                                Log.e("image", "https" + i.name)

                                Picasso.get().load("${sessionManager.baseURL + i.name}")
                                    .networkPolicy(
                                        NetworkPolicy.NO_CACHE
                                    )
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .placeholder(R.drawable.placeholder_n).stableKey("id")
                                    .into(binding.image)
                            }
                            //somenath
                            try {
                                var list = ArrayList<ChildrenCategory>()
                                for (i in response.body()?.data?.info!!.options) {
                                    list = i.children_categories
                                }
                                if (list != null) {
                                    // Create an array of state names
                                    val items = arrayOfNulls<kotlin.String>(list.size)
                                    for (i in list.indices) {
                                        items[i] = list[i].name
                                    }
                                    if (list.isEmpty()) {
                                        binding.sizeSelectionRecycler.visibility = View.GONE
                                        binding.tvSizeTitle.visibility = View.GONE

                                    }
                                    mSizeAdapter = MultipleSizeAdapter(items)
                                    binding.sizeSelectionRecycler.adapter = mSizeAdapter

//                                    // Create an ArrayAdapter using the state names
//                                    val adapter: ArrayAdapter<kotlin.String?> = ArrayAdapter(
//                                        context, R.layout.simple_list_item_1, items
//                                    )

                                    // Set the adapter to the spinner
//                                    binding.spinnerVariant.adapter = adapter

                                    // Set the default selection
//                            val relationId = "Some default relation id" // Replace with your default relation id if available
//                            binding.spinnerState.setSelection(items.indexOf(relationId))

                                    // Hide the loader dialog
                                    AppProgressBar.hideLoaderDialog()

                                    // Set the item selected listener for the spinner
//                                    binding.spinnerVariant.onItemSelectedListener =
//                                        object : AdapterView.OnItemSelectedListener {
//                                            @SuppressLint("SuspiciousIndentation")
//                                            override fun onItemSelected(
//                                                adapterView: AdapterView<*>?,
//                                                view: View?,
//                                                position: Int,
//                                                id: Long
//                                            ) {
//                                                val rprice = list[position].rprice
//                                                option = list[position].id.toString()
//                                                if (rprice != null) {
//                                                    binding.tvPrice.text =  "${sessionManager.currency}"+ rprice
//                                                    price = rprice
//                                                }
//
//
//                                            }
//
//                                            override fun onNothingSelected(adapterView: AdapterView<*>?) {
//                                                // Handle the case when nothing is selected, if needed
//                                            }
//                                        }
                                } else {
                                    // Handle the case when list is null
                                    // myToast(context, "No data found")
                                    AppProgressBar.hideLoaderDialog()
                                }
                            } catch (e: Exception) {
                                myToast(context, "Something went wrong")
                                AppProgressBar.hideLoaderDialog()
                            }

                            try {
                                var list = response.body()?.data?.info!!.attributes
//                                for (i in response.body()?.data?.info!!.attributes) {
//                                    list = i.variation.name
//                                }
                                if (list != null) {
                                    // Create an array of state names
                                    val items = arrayOfNulls<kotlin.String>(list.size)
                                    for (i in list.indices) {
                                        items[i] = list[i].variation.name
                                    }
                                    if (list.isEmpty()) {
                                        binding.colorSelectionRecycler.visibility = View.GONE
                                        binding.tvColorTitle.visibility = View.GONE

                                    }
                                    mColorsAdapter = ProductColorsAdapter(items)
                                    binding.colorSelectionRecycler.adapter = mSizeAdapter

                                    // Create an ArrayAdapter using the state names
//                                    val adapter: ArrayAdapter<kotlin.String?> = ArrayAdapter(
//                                        context, R.layout.simple_list_item_1, items
//                                    )
//
//                                    // Set the adapter to the spinner
//                                    binding.spinnerVariantNew.adapter = adapter

                                    // Set the default selection
//                            val relationId = "Some default relation id" // Replace with your default relation id if available
//                            binding.spinnerState.setSelection(items.indexOf(relationId))

                                    // Hide the loader dialog
                                    AppProgressBar.hideLoaderDialog()

                                    // Set the item selected listener for the spinner
//                                    binding.spinnerVariantNew.onItemSelectedListener =
//                                        object : AdapterView.OnItemSelectedListener {
//                                            @SuppressLint("SuspiciousIndentation")
//                                            override fun onItemSelected(
//                                                adapterView: AdapterView<*>?,
//                                                view: View?,
//                                                position: Int,
//                                                id: Long
//                                            ) {
//                                                variation = list[position].variation.id.toString()
////                                                if (rprice != null) {
////                                                    binding.tvPrice.text = "$sessionManager.currency" + rprice
////                                                    price = rprice
////                                                }
//
//
//                                            }
//
//                                            override fun onNothingSelected(adapterView: AdapterView<*>?) {
//                                                // Handle the case when nothing is selected, if needed
//                                            }
//                                        }
                                } else {
                                    // Handle the case when list is null
                                    // myToast(context, "No data found")
                                    AppProgressBar.hideLoaderDialog()
                                }
                            } catch (e: Exception) {
                                myToast(context, "Something went wrong")
                                AppProgressBar.hideLoaderDialog()
                            }
//                            somenath
//                            binding!!.recyclerView.adapter = AdapterListing(context, response.body()!!.data.posts.data)
//                            binding!!.recyclerView.layoutManager =
//                                GridLayoutManager(context, 2)
//
//                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelProductDetial>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    AppProgressBar.hideLoaderDialog()
                    countDet++
                    if (countDet <= 5) {
                        Log.e("count", countDet.toString())
                        apiCallProductDetail()
                    } else {
                        countDet=0
                        myToast(this@DetailPage, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                }

            })
    }

    private fun apiCallAddToCart() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.addToCart(
            sessionManager.authToken,
            productId,
            itemCount.toString(),
            sessionManager.deviceId, sessionManager.randomKey, "cart", option, variation
        )
            .enqueue(object : Callback<ModelCart> {
                override fun onResponse(
                    call: Call<ModelCart>, response: Response<ModelCart>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.info.categories
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            myToast(context, "item Added in Cart")
                            AppProgressBar.hideLoaderDialog()
                            countCart=0
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelCart>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countCart++
                    if (countCart <= 5) {
                        Log.e("count", countCart.toString())
                        apiCallAddToCart()
                    } else {
                        countCart=0
                        myToast(this@DetailPage, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.posts.data.isEmpty()) {
                            binding!!.recyclerViewMen.adapter =
                                AdapterListing(context, response.body()!!.data.posts.data)

                            myToast(context, "No Product Found")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val men = java.util.ArrayList<DataX>()
                            val women = java.util.ArrayList<DataX>()
                            val kids = java.util.ArrayList<DataX>()
                            // mainData = response.body()!!.data.posts.data
                            for (i in response.body()!!.data.posts.data) {
                                for (i1 in i.categories) {
                                    if (i1.name.isNotEmpty()) {
                                        when (i1.name) {
                                            "MEN" -> men.add(i)
                                            "WOMEN" -> women.add(i)
                                            "KIDS" -> kids.add(i)
                                        }
                                    }

                                }
                            }
                            var mainList = java.util.ArrayList<DataX>()

                            mainList = when (value) {
                                "MEN" -> {
                                    men
                                }
                                "WOMEN" -> {
                                    women
                                }
                                "KIDS" -> {
                                    kids
                                }
                                else -> {
                                    response.body()!!.data.posts.data
                                }
                            }
                            count=0
                            binding!!.recyclerViewMen.adapter =
                                ProductsAdapter(context, mainList)
//                            binding!!.recyclerViewMen.layoutManager =
//                                GridLayoutManager(context, 2)
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                     count++
                    if (count <= 5) {
                        Log.e("count", count.toString())
                        apiCallProduct()
                    } else {
                        count=0
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallAddToWishList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.addToWishlist(
            sessionManager.authToken,
            sessionManager.userEmail,
            productId,
            sessionManager.deviceId,sessionManager.randomKey,"wishlist"
        )
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
//                            binding.wishListRed.visibility = View.VISIBLE
//                            binding.wishList.visibility = View.GONE
                            apiCallGetWishlist()
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countWish++
                    if (countWish <= 5) {
                        Log.e("count", countWish.toString())
                        apiCallAddToWishList()
                    } else {
                        myToast(this@DetailPage, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallRemoveToWishList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.removeWishlist(sessionManager.authToken, wishlistId,sessionManager.randomKey,"wishlist")
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            binding.wishListRed.visibility = View.GONE
                            binding.wishList.visibility = View.VISIBLE
                            apiCallGetWishlist()
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countWishRe++
                    if (countWishRe <= 6) {
                        Log.e("count", countWishRe.toString())
                        apiCallRemoveToWishList()
                    } else {
                        myToast(this@DetailPage, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetWishlist() {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWishlists(sessionManager.authToken!!, sessionManager.deviceId,sessionManager.randomKey,"wishlist")
            .enqueue(object : Callback<ModelWishlist> {
                override fun onResponse(
                    call: Call<ModelWishlist>, response: Response<ModelWishlist>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.items.isEmpty()) {
//                            binding!!.recyclerView.adapter = AdapterWishlist(context, response.body()!!.data.items)
//
//                            myToast(context, "No Product Found")
//                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {

                            val wishlistData = ArrayList<ModelWishListId>()
                            for (i in response.body()!!.data.items) {
                                wishlistData.add(ModelWishListId(i.id.toString(), i.term_id, i.term_title))
                            }

                            val wishlistTerm = wishlistData.find { it.termId == productId }

                            if (wishlistTerm != null) {
                                binding.wishListRed.visibility = View.VISIBLE
                                binding.wishList.visibility = View.GONE
                            } else {
                                binding.wishListRed.visibility = View.GONE
                                binding.wishList.visibility = View.VISIBLE
                            }

                            for (i in wishlistData){
                                    if (i.termTitle.contentEquals(productTitle)) {
                                        Log.e("i.termTitle", i.termTitle.toString())
                                        wishlistId = i.id.toString()
                                    }
                                }
                                countwishadd=0


                                // val wishlistId= wishlistData.find { it==productId }


                                Log.e("wishlistId", wishlistId.toString())
                                Log.e("productTitle", productTitle.toString())
                                Log.e("wishlistTerm", wishlistTerm.toString())
//                            if (wishlistTerm != "1") {
//                                binding.wishListRed.visibility = View.GONE
//                                binding.wishList.visibility = View.VISIBLE
//                            } else {
//                                binding.wishListRed.visibility = View.VISIBLE
//                                binding.wishList.visibility = View.GONE
//
//                            }
//                            binding!!.recyclerView.adapter =
//                                AdapterWishlist(context, response.body()!!.data.items)
//                            binding!!.recyclerView.layoutManager =
//                                GridLayoutManager(context, 2)
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

                override fun onFailure(call: Call<ModelWishlist>, t: Throwable) {
                    countwishadd++
                    if (countwishadd <= 5) {
                        Log.e("count", countwishadd.toString())
                        apiCallGetWishlist()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun refresh() {
        overridePendingTransition(0, 0)
        finish()
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}