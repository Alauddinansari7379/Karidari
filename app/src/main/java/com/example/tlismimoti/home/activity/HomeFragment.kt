package com.example.tlismimoti.home.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.afdhal_fa.imageslider.`interface`.ItemClickListener
import com.afdhal_fa.imageslider.model.SlideUIModel
import com.amtech.mehfeel.home.model.modelTop.ModelNewArraivals
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.categories.modelCurrency.ModelCurrency
import com.example.tlismimoti.databinding.FragmentHomeBinding
import com.example.tlismimoti.home.adapter.AdapterProduct
import com.example.tlismimoti.home.adapter.AdapterTopTrending
import com.example.tlismimoti.home.model.DataX
import com.example.tlismimoti.home.model.ModeCatId
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.home.model.modelSlider.ModelSlider
import com.example.tlismimoti.listing.Listing
import com.example.tlismimoti.listing.model.Category
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager
    private var mainData = ArrayList<DataX>()
    private var allCategoryName = ArrayList<String>()
    var shimmerFrameLayout: ShimmerFrameLayout? = null
    var categoryWithId = ArrayList<ModeCatId>()
    var count = 0
    var countCat = 0
    var countDis = 0
    var countNewA = 0
    var counTop = 0
    var counCur = 0
    var countS = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        shimmerFrameLayout = view.findViewById(R.id.shimmer1)
        shimmerFrameLayout!!.startShimmer()
        sessionManager.baseURL = "https://tlismimoti.com/"



        apiCallProduct()
        apiCallSlider()
        apiCallGetAllCategory()
        apiCallNewArrivals()
        apiCallDiscount()
        apiCallTopTre()
        apiCallGetCurrency()


        with(binding) {
            tvViewAll1.setOnClickListener {
                if (categoryWithId.size>0) {
                    val intent = Intent(context as Activity, Listing::class.java)
                        .putExtra("catName", binding.tvCat1.text)
                        .putExtra("catId", categoryWithId[0].id)
                    (context as Activity).startActivity(intent)
                }
            }
            tvViewAll2.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat2.text)
                    .putExtra("catId", categoryWithId[1].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll3.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat3.text)
                    .putExtra("catId", categoryWithId[2].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll4.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat4.text)
                    .putExtra("catId", categoryWithId[3].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll5.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat5.text)
                    .putExtra("catId", categoryWithId[4].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll6.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat6.text)
                    .putExtra("catId", categoryWithId[5].id)
                (context as Activity).startActivity(intent)
            }

            tvViewAll7.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat7.text)
                    .putExtra("catId", categoryWithId[6].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll8.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat8.text)
                    .putExtra("catId", categoryWithId[7].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll9.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat9.text)
                    .putExtra("catId", categoryWithId[8].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll10.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat10.text)
                    .putExtra("catId", categoryWithId[9].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll11.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat11.text)
                    .putExtra("catId", categoryWithId[10].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll12.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat12.text)
                    .putExtra("catId", categoryWithId[11].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll13.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat13.text)
                    .putExtra("catId", categoryWithId[12].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll14.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat14.text)
                    .putExtra("catId", categoryWithId[13].id)
                (context as Activity).startActivity(intent)
            }
            tvViewAll15.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("catName", binding.tvCat15.text)
                    .putExtra("catId", categoryWithId[14].id)
                (context as Activity).startActivity(intent)
            }

//            tvViewAllCate.setOnClickListener {
//                val intent = Intent(context as Activity, CategoryActivity::class.java)
//                (context as Activity).startActivity(intent)
//            }
        }


    }

    private fun apiCallSlider() {
        // AppProgressBar.showLoaderDialog(requireContext())

        ApiClient.apiService.getSlider(sessionManager.authToken)
            .enqueue(object : Callback<ModelSlider> {
                @SuppressLint("LogNotTimber")
                override fun onResponse(
                    call: Call<ModelSlider>, response: Response<ModelSlider>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.posts.isEmpty()) {

                            myToast(requireActivity(), "No Slider Found")
                            //   AppProgressBar.hideLoaderDialog()

                        } else {
                            val imageList = ArrayList<SlideUIModel>()

                            for (i in response.body()!!.data.posts) {
                                //uploads/506/23/10/1697450898.png
                                imageList.add(
                                    SlideUIModel(sessionManager.baseURL + i.name, "")
                                )
//                                imageList.add(
//                                    SlideUIModel(baseURL + i.name[1], "")
//                                )
//                                imageList.add(
//                                    SlideUIModel(baseURL + i.name[2], "")
//                                )
//                                imageList.add(
//                                    SlideUIModel(baseURL + i.name[3], "")
//                                )


                            }

                            binding.imageSlide.setImageList(imageList)



                            binding.imageSlide.setItemClickListener(object : ItemClickListener {
                                override fun onItemClick(model: SlideUIModel, position: Int) {
                                    val link = ArrayList<String>()
                                    for (i in response.body()!!.data.posts) {
                                        link.add(i.slug)
                                    }

                                    when (position) {
                                        0 -> {
                                            "https://tlismimoti.com/"
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[0]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        1 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[1]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        2 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[2]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        3 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[3]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        4 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[4]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        5 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[5]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        6 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[6]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        7 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[7]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        8 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[8]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        else -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[0]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }
                                    }
                                }
                            })
                            binding.imageSlide.startSliding(2000) // with new period
                            // binding.imageSlide.stopSliding()
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelSlider>, t: Throwable) {
                    // myToast(requireActivity(),t.message.toString())
                    // myToast(requireActivity(), "Something went wrong")

                    countS++
                    if (countS <= 2) {
                        Log.e("count", countS.toString())
                        apiCallSlider()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    //    private fun openBrowser(context: Context, url: String) {
//        var url = url
//        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
//            url = HTTP + url
//        }
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        context.startActivity(Intent.createChooser(intent, "Choose browser")) // Choose browser is arbitrary :)
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNewCategories(productResponse: ModelProduct): List<Category> {
        val newCategories = mutableSetOf<Category>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        val thresholdDate = LocalDate.parse("2024-01-01")

        productResponse.data.posts.data.forEach { product ->
            product.categories.forEach { category ->
                val createdAtDate = LocalDate.parse(category.created_at, formatter)
                if (createdAtDate.isAfter(thresholdDate)) {
                    newCategories.add(category)
                }
            }
        }
        return newCategories.toList()
    }


    private fun apiCallProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()
                            binding.shimmer1.visibility = View.GONE
                            binding.shimmer2.visibility = View.GONE
                            binding.shimmer3.visibility = View.GONE
                            binding.shimmer4.visibility = View.GONE
                            binding.shimmer5.visibility = View.GONE
                            binding.shimmer6.visibility = View.GONE
                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()
                            binding.shimmer1.visibility = View.GONE
                            binding.shimmer2.visibility = View.GONE
                            binding.shimmer3.visibility = View.GONE
                            binding.shimmer4.visibility = View.GONE
                            binding.shimmer5.visibility = View.GONE
                            binding.shimmer6.visibility = View.GONE
                        } else if (response.body()!!.data.posts.data.isEmpty()) {
//
                            myToast(requireActivity(), "No Product Found")
                            binding.shimmer1.visibility = View.GONE
                            binding.shimmer2.visibility = View.GONE
                            binding.shimmer3.visibility = View.GONE
                            binding.shimmer4.visibility = View.GONE
                            binding.shimmer5.visibility = View.GONE
                            binding.shimmer6.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val men = ArrayList<DataX>()
                            val women = ArrayList<DataX>()
                            val kids = ArrayList<DataX>()
                            mainData = response.body()!!.data.posts.data

                            val newCategories = getNewCategories(response.body()!!)
                            newCategories.forEach {
                                println(it)
                                allCategoryName.add(it.name)
                            }
                            val uniqueItemsSet = allCategoryName.toSet()

                            // Convert the set back to a list if needed
                            val uniqueItemsList = uniqueItemsSet.toList()
                            Log.e("allCategoryName", allCategoryName.toString())
                            Log.e("uniqueItemsList", uniqueItemsList.toString())
                            val list = ArrayList<DataX>()

                            for (i in response.body()!!.data.posts.data) {
                                if (i.categories.isNullOrEmpty()) {
                                    if (!list.contains(i)) {
                                        list.add(i)
                                        //   binding.tvCat1.text = i1.name
                                    }
                                }
                                binding.layoutCat1.visibility = View.GONE
                                binding!!.recycler1.adapter =
                                    activity?.let {
                                        AdapterProduct(it, mainData)
                                    }
                                binding.shimmer1.visibility = View.GONE
                            }
                            count = 0

//                            binding.layoutCat1.visibility = View.VISIBLE
//                            binding.layoutCat2.visibility = View.VISIBLE
//                            binding.layoutCat3.visibility = View.VISIBLE
//                            binding.layoutCat4.visibility = View.VISIBLE
//                            binding.layoutCat5.visibility = View.VISIBLE
//                            binding.layoutCat6.visibility = View.VISIBLE


                            if (uniqueItemsList.size >= 1) {
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[0] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat1.text = i1.name
                                                    }

                                                }
                                            }
                                        }

                                    }
                                }
                                binding.layoutCat1.visibility = View.VISIBLE
                                binding.tvCat1.text = uniqueItemsList[0]
                                //list.distinct()

                                binding!!.recycler1.adapter =
                                    activity?.let {
                                        AdapterProduct(it, list)
                                    }
                                binding.shimmer1.visibility = View.GONE
                            }

                            if (uniqueItemsList.size >= 2) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[1] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat2.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                binding.layoutCat2.visibility = View.VISIBLE
                                binding!!.recyclerVie2.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer2.visibility = View.GONE
                            }
                            if (uniqueItemsList.size >= 3) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[2] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat3.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                binding.layoutCat3.visibility = View.VISIBLE
                                binding!!.recycler3.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer3.visibility = View.GONE
                            }

                            if (uniqueItemsList.size >= 4) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[3] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat4.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat4.visibility = View.VISIBLE
                                binding!!.recycler4.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer4.visibility = View.GONE
                            }

                            if (uniqueItemsList.size >= 5) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[4] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat5.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat5.visibility = View.VISIBLE
                                binding!!.recycler5.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer5.visibility = View.GONE
                            }
                            if (uniqueItemsList.size >= 6) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[5] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat6.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat6.visibility = View.VISIBLE
                                binding!!.recycler6.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer6.visibility = View.GONE
                            }
                            if (uniqueItemsList.size >= 7) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[6] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat7.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat7.visibility = View.VISIBLE
                                binding!!.recycler7.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 8) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[7] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat8.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat8.visibility = View.VISIBLE
                                binding!!.recycler8.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 9) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[8] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat9.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat9.visibility = View.VISIBLE
                                binding!!.recycler9.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 10) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[9] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat10.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat10.visibility = View.VISIBLE
                                binding!!.recycler10.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 11) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[10] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat11.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat11.visibility = View.VISIBLE
                                binding!!.recycler11.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 12) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[11] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat12.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat12.visibility = View.VISIBLE
                                binding!!.recycler12.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 13) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[12] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat13.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat13.visibility = View.VISIBLE
                                binding!!.recycler13.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 14) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[13] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat14.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat14.visibility = View.VISIBLE
                                binding!!.recycler14.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }
                            if (uniqueItemsList.size >= 15) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[14] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat15.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat15.visibility = View.VISIBLE
                                binding!!.recycler15.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                            }


                            //
//                            for (i in response.body()!!.data.posts.data) {
//                                for (i1 in i.categories) {
//                                    if (i1.name.isNotEmpty()) {
//                                        when (i1.name) {
//                                            "National Delivery Available" -> {
//                                                men.add(i)
//                                                binding.tvTitle1.text = i1.name.toString()
//                                                CategorieName = i1.name
//                                            }
//
//                                            "WOMEN" -> women.add(i)
//                                            "KIDS" -> kids.add(i)
//                                        }
//                                    }
//
//                                }
//                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Execption", e.toString())
                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 3) {
                        Log.e("count", count.toString())
                        apiCallProduct()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        count = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallGetAllCategory() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.posts.data.isEmpty()) {
//
                            myToast(requireActivity(), "No Category Found")

                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val men = java.util.ArrayList<DataX>()
                            val women = java.util.ArrayList<DataX>()
                            val kids = java.util.ArrayList<DataX>()
                            mainData = response.body()!!.data.posts.data

                            val newCategories = getNewCategories(response.body()!!)
                            newCategories.forEach { category ->
                                println(category)
                                // Check if the category already exists in the list
                                allCategoryName.add(category.name)
                                if (categoryWithId.none { it.id == category.id.toString() }) {
                                    categoryWithId.add(
                                        ModeCatId(
                                            category.name,
                                            category.id.toString()
                                        )
                                    )
                                }
                            }
                            val uniqueItemsSet = allCategoryName.toSet()
                            countCat = 0
                            // Convert the set back to a list if needed
                            val uniqueItemsList = uniqueItemsSet.toList()
                            Log.e("allCategoryName", allCategoryName.toString())
                            Log.e("uniqueItemsList", uniqueItemsList.toString())




                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countCat++
                    if (countCat <= 3) {
                        Log.e("count", countCat.toString())
                        apiCallGetAllCategory()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countCat = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallDiscount() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.newArrivals(sessionManager.authToken!!,"","1","")
            .enqueue(object : Callback<ModelNewArraivals> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelNewArraivals>, response: Response<ModelNewArraivals>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.get_offerable_products.isEmpty()) {
//
                            //  myToast(requireActivity(), "No Category Found")

                            AppProgressBar.hideLoaderDialog()

                        } else {
                            countDis=0
                            binding.layoutDiscount.visibility = View.VISIBLE
                            binding!!.recyclerDis.adapter =
                                activity?.let {
                                    AdapterTopTrending(
                                        it,
                                        response.body()!!.data.get_offerable_products,
                                    )
                                }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelNewArraivals>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countDis++
                    if (countDis <= 3) {
                        Log.e("count", countDis.toString())
                        apiCallDiscount()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countDis = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallNewArrivals() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.newArrivals(sessionManager.authToken!!,"1","","")
            .enqueue(object : Callback<ModelNewArraivals> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelNewArraivals>, response: Response<ModelNewArraivals>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.get_latest_products.size==0) {
//
                            //   myToast(requireActivity(), "No Category Found")

                            AppProgressBar.hideLoaderDialog()

                        } else {
                            countNewA=0
                            binding.layoutNewArr.visibility = View.VISIBLE
                            binding!!.recyclerNeewArr.adapter =
                                activity?.let {AdapterTopTrending(it, response.body()!!.data.get_latest_products) }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelNewArraivals>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countNewA++
                    if (countNewA <= 3) {
                        Log.e("count", countNewA.toString())
                        apiCallNewArrivals()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countNewA = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallTopTre() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.newArrivals(sessionManager.authToken!!,"","","1")
            .enqueue(object : Callback<ModelNewArraivals> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelNewArraivals>, response: Response<ModelNewArraivals>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.get_trending_products.size==0) {
//
                            //   myToast(requireActivity(), "No Category Found")

                            AppProgressBar.hideLoaderDialog()

                        } else {
                            countNewA=0
                            binding.layoutTopTre.visibility = View.VISIBLE
                            binding!!.recyclerTop.adapter =
                                activity?.let {AdapterTopTrending(it, response.body()!!.data.get_trending_products) }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelNewArraivals>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    counTop++
                    if (counTop <= 3) {
                        Log.e("count", counTop.toString())
                        apiCallTopTre()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        counTop = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallGetCurrency() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getCurrency(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelCurrency> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelCurrency>, response: Response<ModelCurrency>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        }   else {
                            sessionManager.currency=response.body()!!.data.currency_icon
                         }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelCurrency>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    counTop++
                    if (counCur <= 3) {
                        Log.e("count", counCur.toString())
                        apiCallGetCurrency()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        counCur = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

}