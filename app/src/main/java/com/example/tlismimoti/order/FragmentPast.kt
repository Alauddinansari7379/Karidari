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
import com.example.tlismimoti.order.adapter.AdapterOrderCompleted
import com.example.tlismimoti.order.model.DataX
import com.example.tlismimoti.order.model.ModelPendingOrder.ModelOrderPending
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentPast : Fragment() {
    lateinit var binding: FragmentPastBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var count1 = 0
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

        }
    }

    private fun apiCallAllOrderCom() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.ordersCompleted(
            sessionManager.authToken!!,
            sessionManager.userEmail,
            "completed"
        )
            .enqueue(object : Callback<ModelOrderPending> {
                override fun onResponse(
                    call: Call<ModelOrderPending>, response: Response<ModelOrderPending>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
//                            for (i in response.body()!!.data.info) {
//                                when (i.status) {
//                                    "4" -> completed.add(i)
//                                    "3" -> canceled.add(i)
//                                }
//                            }

                            binding.itemQtyDel.text = response.body()!!.data.info.size.toString() + " items"
                            binding.totalPrice.text = "${sessionManager.currency}" + response.body()!!.data.total_amount

                            binding!!.recyclerViewDel.adapter =
                                activity?.let {
                                    AdapterOrderCompleted(
                                        it, response.body()!!.data.info,
                                    )
                                }
                            if (response.body()!!.data.info.size == 0) {
                                binding.cardDel.visibility = View.GONE
                            }
                            apiCallAllOrderCan()
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelOrderPending>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 5) {
                        Log.e("count", count.toString())
                        apiCallAllOrderCom()
                    } else {
                           //  binding.tvNoDtaFound.visibility=View.VISIBLE
                            binding.cardDel.visibility=View.GONE
                           // binding.cardCan.visibility=View.GONE
                        AppProgressBar.hideLoaderDialog()
                        Log.e("completed", t.toString())

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallAllOrderCan() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.ordersCompleted(
            sessionManager.authToken!!,
            sessionManager.userEmail,
            "canceled"
        )
            .enqueue(object : Callback<ModelOrderPending> {
                override fun onResponse(
                    call: Call<ModelOrderPending>, response: Response<ModelOrderPending>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        }else if (response.body()!!.data.total_amount==0) {
                           //  binding.tvNoDtaFound.visibility = View.VISIBLE
                            //   binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()
                            binding.cardCan.visibility = View.GONE

                        }else {
                            count1=0
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

                            binding.itemQty.text = response.body()!!.data.info.size.toString() + " items"
                            binding.totalPriceCan.text = "${sessionManager.currency}"  + response.body()!!.data.total_amount
                            binding!!.recyclerViewCancle.adapter =
                                activity?.let {
                                    AdapterOrderCompleted(
                                        it, response.body()!!.data.info,
                                    )
                                }
                            if (response.body()!!.data.info.size == 0) {
                              //  binding.tvNoDtaFound.visibility=View.VISIBLE
                                binding.cardCan.visibility = View.GONE
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
                        binding.cardCan.visibility=View.GONE

                    }
                }

                override fun onFailure(call: Call<ModelOrderPending>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count1++
                    if (count1 <= 5) {
                        Log.e("count", count1.toString())
                        apiCallAllOrderCan()
                    } else {
                        count1=0
                       // binding.tvNoDtaFound.visibility = View.VISIBLE
                             binding.cardCan.visibility=View.GONE
                        myToast(requireActivity(), t.message.toString())
                        //Log.e("completed", t.toString())

                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


}