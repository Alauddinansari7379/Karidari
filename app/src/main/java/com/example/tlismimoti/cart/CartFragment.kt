package com.example.tlismimoti.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.adapter.AdapterCart
import com.example.tlismimoti.cart.model.ModelAddtoCart
import com.example.tlismimoti.cart.model.modelRemove.ModelRemoveCart
import com.example.tlismimoti.cart.model.modelgetCart.ModelGetCart
import com.example.tlismimoti.databinding.FragmentCartBinding
import com.example.tlismimoti.listing.model.cartModel.ModelCart
import com.example.tlismimoti.login.Login
import com.example.tlismimoti.login.SignUp
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment(), AdapterCart.Cart {
    private lateinit var binding: FragmentCartBinding
    private lateinit var sessionManager: SessionManager
    var shimmerFrameLayout: ShimmerFrameLayout? = null
    val finalTotal=ArrayList<Int>()

    // private var mainData = ArrayList<Items>()
    var count = 0
    var countGet = 0
    var countRe = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)
        shimmerFrameLayout = view.findViewById(R.id.shimmer)
        shimmerFrameLayout!!.startShimmer()
        sessionManager = SessionManager(requireContext())

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)
//        if (parentView.parent != null) {
//            (parentView.parent as ViewGroup).removeView(parentView) // <- fix
//        }
        apiCallCartProduct()
        // MainActivity
        with(binding) {
            // priceUndrline.paintFlags = priceUndrline.paintFlags or STRIKE_THRU_TEXT_FLAG
            btnGoToCheckout.setOnClickListener {
                 if (sessionManager.authTokenUser!!.isEmpty()){
                     try {
                         bottomSheetDialog.show()
                     }catch (e:Exception){
                         e.printStackTrace()
                     }
                }else if(Checkout.totalAmt.isEmpty()){
                    myToast(requireActivity(),"Cart Empty")
                }else{
                     val intent = Intent(context as Activity, Checkout::class.java)
                         .putExtra("finalTotal", finalTotal)
                     requireActivity().startActivity(intent)

                 }
            }
            val imgClose = parentView.findViewById<ImageView>(R.id.imgBackDil)
            val login = parentView.findViewById<Button>(R.id.btnLoginDil)
            val signUp = parentView.findViewById<Button>(R.id.btnSignUpDil)

            imgClose.setOnClickListener {

                bottomSheetDialog.dismiss()
            }
            login.setOnClickListener {
                startActivity(Intent(activity, Login::class.java))
            }
            signUp.setOnClickListener {
                startActivity(Intent(activity, SignUp::class.java))
            }

            //priceUndrline.paintFlags = priceUndrline.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }

//        val dialog = BottomSheetDialog(requireContext())
//        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)
//
//        bottomSheet.buttonSubmit.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.setContentView(bottomSheet)
//        dialog.show()

    }
//     override fun onDestroy() {
//        super.onDestroy()
//        if (bottomSheetDialog != null && pDialog.isShowing()) {
//            pDialog.dismiss()
//        }
//    }
    private fun apiCallCartProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getCart(sessionManager.authToken!!, sessionManager.deviceId,sessionManager.randomKey,"cart")
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
                            myToast(requireActivity(), "Server Error")
                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.body()!!.data.items.size == 0) {
                            binding!!.cartRecycler.adapter =
                                activity?.let {
                                    AdapterCart(
                                        it,
                                        response.body()!!.data.items,
                                        this@CartFragment
                                    )
                                }
                            myToast(requireActivity(), "No Item Found")
                            binding.shimmer.visibility = View.GONE
                            CartItem="0"
                            binding.totalPrice.text=""
                            binding.totalItems.text="0 items"
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            countGet=0
                            binding!!.cartRecycler.adapter =
                                activity?.let {
                                    AdapterCart(
                                        it,
                                        response.body()!!.data.items!!,
                                        this@CartFragment
                                    )
                                }
                            CartItem=""
                            finalTotal.clear()
                             binding.shimmer.visibility = View.GONE
                            for (i in response.body()!!.data.items) {

                                finalTotal.add(i.qty.toInt()*i.price.toInt())
                                if (i.tax!=null){
                                    Checkout.tax=i.tax.value
                                }
                            }
                            Checkout.totalAmt= finalTotal.sum().toString()

                            binding.totalPrice.text = "${sessionManager.currency}"  + finalTotal.sum()
                            binding.totalItems.text = response.body()!!.data.items.size.toString() + " Items added"

                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(requireActivity(), "Something went wrong")
                        binding.shimmer.visibility = View.GONE

                    }
                }

                override fun onFailure(call: Call<ModelGetCart>, t: Throwable) {
                    countGet++
                    if (countGet <= 7) {
                        apiCallCartProduct()
                    } else {
                        countGet=0
                        myToast(requireActivity(), "Try Again")
                        AppProgressBar.hideLoaderDialog()
                        binding.shimmer.visibility = View.GONE

                    }
                    AppProgressBar.hideLoaderDialog()
                    binding.shimmer.visibility = View.GONE


                }

            })
    }

    override fun addToCart(productId: String, variationId: String, optionId: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.addToCart(
            sessionManager.authToken,
            productId,
            "1",
            sessionManager.deviceId.toString(),sessionManager.randomKey,"cart",optionId,variationId
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
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            myToast(requireActivity(), "item Added in Cart")
                            apiCallCartProduct()
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(requireActivity(), "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelCart>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        addToCart(productId, variationId, optionId)
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    override fun removeToCart(id: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.removeToCart(
            sessionManager.authToken,
            id,
            sessionManager.deviceId.toString(),sessionManager.randomKey,"cart"
        )
            .enqueue(object : Callback<ModelRemoveCart> {
                override fun onResponse(
                    call: Call<ModelRemoveCart>, response: Response<ModelRemoveCart>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.info.categories
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            myToast(requireActivity(), "item Removed from Cart")
                            apiCallCartProduct()
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(requireActivity(), "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelRemoveCart>, t: Throwable) {
                     countRe++
                    if (countRe <= 2) {
                        Log.e("count", countRe.toString())
                        removeToCart(id)
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })

     }
companion object{
    var CartItem=""
 }

}