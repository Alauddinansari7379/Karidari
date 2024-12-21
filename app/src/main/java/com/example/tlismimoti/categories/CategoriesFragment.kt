package com.example.tlismimoti.categories

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.categories.Adapter.AdapterCategory
import com.example.tlismimoti.categories.model.ModelCatWithId.modelCat.ModelGetCar
import com.example.tlismimoti.databinding.FragmentCategoriesBinding
import com.example.tlismimoti.home.model.DataX
import com.example.tlismimoti.home.model.ModeCatId
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.listing.Listing
import com.example.tlismimoti.listing.model.Category
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList


class CategoriesFragment : Fragment() {
    private lateinit var binding:FragmentCategoriesBinding
    lateinit var sessionManager:SessionManager
    var count=0
    private var mainData = ArrayList<com.example.tlismimoti.categories.model.ModelCatWithId.modelCat.DataX>()
    private var allCategoryName = ArrayList<String>()
    var categoryWithId=ArrayList<ModeCatId>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentCategoriesBinding.bind(view)
        sessionManager=SessionManager(requireActivity())
        apiCallGetAllCategory()
    }
/*
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
                            count=0
                            mainData = response.body()!!.data.posts.data

                            val newCategories = getNewCategories(response.body()!!)
                            newCategories.forEach { category ->
                                println(category)
                                // Check if the category already exists in the list
                                allCategoryName.add(category.name)
                                if (categoryWithId.none {it.id == category.id.toString() }) {
                                    categoryWithId.add(ModeCatId(category.name, category.id.toString()))
                                }
                            }
                            val uniqueItemsSet = allCategoryName.toSet()
                             // Convert the set back to a list if needed
                            val uniqueItemsList = uniqueItemsSet.toList()
                            Log.e("allCategoryName", allCategoryName.toString())
                            Log.e("uniqueItemsList", uniqueItemsList.toString())

                            binding.recyclerView.adapter =
                                activity?.let { AdapterCategory(it, categoryWithId,) }



                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 7) {
                        Log.e("count", count.toString())
                        apiCallGetAllCategory()
                    } else {
                        count=0
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
*/
    private fun apiCallGetAllCategory() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getCategory(sessionManager.authToken!!,"category")
            .enqueue(object : Callback<ModelGetCar> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelGetCar>, response: Response<ModelGetCar>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.data.isEmpty()) {
//
                            myToast(requireActivity(), "No Category Found")

                            AppProgressBar.hideLoaderDialog()

                        } else {
                            count=0
                            mainData = response.body()!!.data.data
                            val gridLayoutManager = GridLayoutManager(activity, 2) // 2 columns
                            binding.recyclerView.layoutManager = gridLayoutManager

                            binding.recyclerView.adapter = activity?.let { AdapterCategory(it, mainData) }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelGetCar>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 7) {
                        Log.e("count", count.toString())
                        apiCallGetAllCategory()
                    } else {
                        count=0
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

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

}