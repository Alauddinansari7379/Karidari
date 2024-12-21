package com.example.tlismimoti .order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.FragmentActiveBinding
import com.example.tlismimoti.order.adapter.AdapterOrder
import com.example.tlismimoti.order.model.DataX
import com.example.tlismimoti.order.model.ModelOrder
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class FragmentActive : Fragment() {
    private lateinit var binding: FragmentActiveBinding
    lateinit var sessionManager: SessionManager
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentActiveBinding.bind(view)
        sessionManager = SessionManager(requireContext())
        binding.cardProgress.setOnClickListener {
           // startActivity(Intent(this,Order))
        }
        if (sessionManager.userEmail!!.isNotEmpty()){
            apiCallAllOrder()

        }
    }

    private fun apiCallAllOrder() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.orders(sessionManager.authToken!!, sessionManager.userEmail,"pending")
            .enqueue(object : Callback<ModelOrder> {
                override fun onResponse(
                    call: Call<ModelOrder>, response: Response<ModelOrder>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.info.data.isEmpty()) {
                                binding.cardProgress.visibility = View.GONE
                                binding.tvNoDtaFound.visibility = View.VISIBLE
                            //   binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val totalAmt=ArrayList<Int>()
                            val completed = ArrayList<DataX>()
                            val pending = ArrayList<DataX>()
                            val canceled = ArrayList<DataX>()
                            // mainData = response.body()!!.data.posts.data
                            for (i in response.body()!!.data.info.data) {
                                when (i.status) {
                                    "completed" -> completed.add(i)
                                    "pending" -> {
                                        for (i1 in i.order_item){
                                            totalAmt.add(i1.amount)

                                        }

                                        pending.add(i)
                                    }
                                    "canceled" -> canceled.add(i)
                                }
                               // i.order_content.value.find {  }
                            }

                            Log.e("completed", completed.toString())
                            Log.e("pending", pending.toString())
                            Log.e("canceled", canceled.toString())
                            binding.itemQty.text=pending.size.toString()+" items"
                            binding.totalPrice.text= "${sessionManager.currency}"+totalAmt.sum()
                            binding!!.recyclerViewMen.adapter =
                                activity?.let {
                                    AdapterOrder(
                                        it, pending,
                                    )
                                }
                            if (pending.isEmpty()){
                                binding.cardProgress.visibility = View.GONE
                                binding.tvNoDtaFound.visibility = View.VISIBLE
                            }
//                            binding.shimmer.visibility = View.GONE
//
//                            binding!!.recyclerViewWomen.adapter =
//                                activity?.let {
//                                    AdapterProduct(
//                                        it, women,
//                                    )
//                                }
//                            binding.shimmerWomen.visibility = View.GONE
//
//                            binding!!.recyclerViewKid.adapter =
//                                activity?.let {
//                                    AdapterProduct(
//                                        it, kids,
//                                    )
//                                }
//                            binding.shimmerKid.visibility = View.GONE
//
//                            binding!!.recyclerViewSpecial.adapter =
//                                activity?.let {
//                                    AdapterProduct(
//                                        it, response.body()!!.data.posts.data,
//                                    )
//                                }
//                            binding.shimmerSpecial.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelOrder>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 5) {
                        Log.e("count", count.toString())
                        apiCallAllOrder()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


}