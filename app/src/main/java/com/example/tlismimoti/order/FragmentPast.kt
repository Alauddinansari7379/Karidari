package com.example.tlismimoti .order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.FragmentPastBinding
import com.example.tlismimoti.order.adapter.AdapterOrder
import com.example.tlismimoti.order.model.DataX
import com.example.tlismimoti.order.model.ModelOrder
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentPast : Fragment() {
    lateinit var binding: FragmentPastBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var orderData = 0
    var completed = ArrayList<DataX>()
    val pending = ArrayList<DataX>()
    var canceled = ArrayList<DataX>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_past, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPastBinding.bind(view)
        sessionManager = SessionManager(requireContext())
        if (sessionManager.userEmail!!.isNotEmpty()) {
            apiCallAllOrderCom()
            apiCallAllOrderCan()

        }
    }

    private fun apiCallAllOrderCom() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.orders(
            sessionManager.authToken!!,
            sessionManager.userEmail,
            "completed"
        )
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

                        }else if (response.body()!!.data.total_amount==0) {
                            binding.tvNoDtaFound.visibility = View.VISIBLE
                            //   binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
//                            for (i in response.body()!!.data.info) {
//                                when (i.status) {
//                                    "4" -> completed.add(i)
//                                    "3" -> canceled.add(i)
//                                }
//                            }
                            completed= response.body()!!.data.info as ArrayList<DataX>

                            Log.e("completed", completed.toString())
                            Log.e("canceled", canceled.toString())
                            binding.itemQtyDel.text = completed.size.toString() + " items"
                            binding.totalPrice.text = "${sessionManager.currency}" + response.body()!!.data.total_amount

                            binding!!.recyclerViewDel.adapter =
                                activity?.let {
                                    AdapterOrder(
                                        it, completed,
                                    )
                                }

                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelOrder>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        apiCallAllOrderCom()
                    } else {
                        if (canceled.isEmpty() && completed.isEmpty()){
                            binding.tvNoDtaFound.visibility=View.VISIBLE
                            binding.cardDel.visibility=View.GONE
                            binding.cardCan.visibility=View.GONE
                        }
                        if (canceled.isEmpty()){
                            binding.cardCan.visibility=View.GONE
                        }

                        if (completed.isEmpty()){
                            binding.cardDel.visibility=View.GONE
                        }
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallAllOrderCan() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.orders(
            sessionManager.authToken!!,
            sessionManager.userEmail,
            "canceled"
        )
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

                        }else if (response.body()!!.data.total_amount==0) {
                             binding.tvNoDtaFound.visibility = View.VISIBLE
                            //   binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        }else {
 //                             val canceled = ArrayList<Info>()
//                            // mainData = response.body()!!.data.posts.data
//                            for (i in response.body()!!.data.info) {
//                                when (i.status) {
//                                    "canceled" -> canceled.add(i)
//                                }
//                                // i.order_content.value.find {  }
//                            }

//                            Log.e("completed", completed.toString())
//                             Log.e("canceled", canceled.toString())

                            canceled= response.body()!!.data.info as ArrayList<DataX>
                            binding.itemQty.text = canceled.size.toString() + " items"
                            binding.totalPriceCan.text = "${sessionManager.currency}"  + response.body()!!.data.total_amount
                            binding!!.recyclerViewCancle.adapter =
                                activity?.let {
                                    AdapterOrder(
                                        it, canceled,
                                    )
                                }
                            if (canceled.isEmpty() && completed.isEmpty()){
                                binding.tvNoDtaFound.visibility=View.VISIBLE
                                binding.cardDel.visibility=View.GONE
                                binding.cardCan.visibility=View.GONE
                            }
                            if (canceled.isEmpty()){
                                 binding.cardCan.visibility=View.GONE
                            }

                            if (completed.isEmpty()){
                                 binding.cardDel.visibility=View.GONE
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
                        apiCallAllOrderCan()
                    } else {
                        count=0
                        if (canceled.isEmpty() && completed.isEmpty()){
                            binding.tvNoDtaFound.visibility=View.VISIBLE
                            binding.cardDel.visibility=View.GONE
                            binding.cardCan.visibility=View.GONE
                        }
                        if (canceled.isEmpty()){
                            binding.cardCan.visibility=View.GONE
                        }

                        if (completed.isEmpty()){
                            binding.cardDel.visibility=View.GONE
                        }
                       // myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


}