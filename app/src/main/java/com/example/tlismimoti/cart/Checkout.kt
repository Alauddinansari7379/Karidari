package com.example.tlismimoti.cart

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.Helper.vibrateOnce
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.model.ModelPayment.ModelPayment
import com.example.tlismimoti.databinding.ActivityCheckoutBinding
import com.example.tlismimoti.listing.DetailPage
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import ng.max.slideview.SlideView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class Checkout : AppCompatActivity(){
    private lateinit var binding: ActivityCheckoutBinding
    private var context = this@Checkout
    lateinit var sessionManager: SessionManager
    var count = 0
    var countS = 0
    var imageuRL = ""
    var dialog: Dialog? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this@Checkout)
        apiCallGetQRCode()
        try {
            if (tax.isEmpty()) {
                tax = "0"
            }
            binding.totalAmt.text = "${sessionManager.currency}" +totalAmt
            binding.deliveryFee.text = "${sessionManager.currency}00"
            binding.tvGSTRate.text = "GST Rate $tax%"
            val gST = totalAmt.toInt() * (tax.toDouble() / 100)
            binding.tvgst.text = "${sessionManager.currency}${gST.toFloat()}"
            val subTotal = totalAmt.toInt() - gST
            binding.subTotal.text = sessionManager.currency+subTotal.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // apiCallCartProduct()
        with(binding) {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            val slideView = findViewById<View>(com.example.tlismimoti.R.id.slideView) as SlideView
            slideView.setOnSlideCompleteListener { // vibrate the device
                vibrateOnce(context)
//
//               binding.radioCash.setOnCheckedChangeListener { group, checkedId ->
//
//
//                   // Do something with the selected radio button text
//                   println("Selected option: $selectedText")
//               }
              /*  if (binding.radioOnline.isChecked) {

                    val view = layoutInflater.inflate(R.layout.dialog_pyment_qrcode, null)
                    dialog = Dialog(context)

                    val imgQr = view!!.findViewById<ImageView>(R.id.imgQRCode)
                    val btnSubmit = view!!.findViewById<Button>(R.id.btnSubmitDialog)
                    val edtUTR = view!!.findViewById<EditText>(R.id.edtUTR)
                    val btnCancel = view!!.findViewById<Button>(R.id.btnCancel)
                    val imgDownload = view!!.findViewById<ImageView>(R.id.imgDowanload)

                    dialog = Dialog(context)

                    if (imageuRL != null) {
                        Picasso.get().load("${sessionManager.baseURL}" + imageuRL)
                            .placeholder(R.drawable.placeholder_n)
                            .error(R.drawable.error_placeholder)
                            .into(imgQr)

                    }
                    if (view.parent != null) {
                        (view.parent as ViewGroup).removeView(view) // <- fix
                    }
                    dialog!!.setContentView(view)
                    dialog?.setCancelable(true)
                    dialog?.show()
                    btnSubmit.setOnClickListener {
                        if (edtUTR.text.isEmpty()) {
                            edtUTR.error = "Enter UTR Number"
                            edtUTR.requestFocus()
                            return@setOnClickListener
                        }
                        dialog?.dismiss()
                        val intent = Intent(context as Activity, DeliveryAddress::class.java)
                            .putExtra("UTR", edtUTR.text.toString())
                            .putExtra("paymentType", "1")
                        context.startActivity(intent)

                    }
                    imgDownload.setOnClickListener {
                        // Check for storage permissions
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                            )
                        } else {
                            downloadAndSaveImage()
                        }
                    }

                    btnCancel.setOnClickListener {
                        dialog?.dismiss()
                    }
                } else if (binding.radioRazorPay.isChecked) {
                    startPaymentOnlineRazorPay()
                }*/
                //else {
                    dialog?.dismiss()
                    val intent = Intent(context as Activity, DeliveryAddress::class.java)
                        .putExtra("paymentType", "2")
                    context.startActivity(intent)

               // }

            }


        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadAndSaveImage()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadAndSaveImage() {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url("${sessionManager.baseURL}" + imageuRL).build()
                val response = client.newCall(request).execute()
                val inputStream = response.body?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                bitmap?.let {
                    saveImageToGallery(this, it, "tilsmiQR.png")
                }

            } catch (e: IOException) {
                e.printStackTrace()
                // Toast.makeText(context, "Field to saved QRCode", Toast.LENGTH_SHORT).show()

            }
        }.start()
        Toast.makeText(context, "QRCode saved to gallery", Toast.LENGTH_SHORT).show()

    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        }

    }

    private fun apiCallGetQRCode() {
        // AppProgressBar.showLoaderDialog(requireContext())

        ApiClient.apiService.paymentList(sessionManager.authToken)
            .enqueue(object : Callback<ModelPayment> {
                @SuppressLint("LogNotTimber")
                override fun onResponse(
                    call: Call<ModelPayment>, response: Response<ModelPayment>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.posts.isEmpty()) {
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            var data = ""
                            for (i in response.body()!!.data.cod) {
                                data = i.active_getway.content
                            }
                            val jsonString = """
                             ${data}
                            """.trimIndent()

                            val jsonObject = JSONObject(jsonString)
                            imageuRL = jsonObject.getString("file_name")


                            println("File Name: $imageuRL")

                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelPayment>, t: Throwable) {
                    countS++
                    if (countS <= 2) {
                        Log.e("count", countS.toString())
                        apiCallGetQRCode()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }


    /*
        private fun apiCallCartProduct() {
            // AppProgressBar.showLoaderDialog(requireContext())
            ApiClient.apiService.getCart(sessionManager.authToken!!, sessionManager.deviceId)
                .enqueue(object : Callback<ModelAddtoCart> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<ModelAddtoCart>, response: Response<ModelAddtoCart>
                    ) {
                        try {
                            if (response.code() == 200) {
                                // mainData = response.body()!!.data.items!!
                                AppProgressBar.hideLoaderDialog()

                            }
                            if (response.code() == 500) {
                                myToast(context, "Server Error")

                            } else if (response.body()!!.data.items.equals("")) {

                                myToast(context, "No Item Found")
                                //  binding.shimmer.visibility = View.GONE
                                AppProgressBar.hideLoaderDialog()

                            } else {
    //                            var fAmt=0
    //                            for (i in response.body()!!.data.items) {
    //                                binding.subTotal.text = "$sessionManager.currency" + i.final_total
    //                                fAmt=i.final_total.toInt()
    //                                 // binding.qty.text= response.body()!!.data.items.size.toString()
    //                            }
                                val finalTotal = ArrayList<Int>()
                                // binding.shimmer.visibility = View.GONE
                                for (i in response.body()!!.data.items) {

                                    finalTotal.add(i.subtotal.toInt())
                                }
                                binding.totalAmt.text = "$sessionManager.currency" + finalTotal.sum()
                                binding.deliveryFee.text = "$sessionManager.currency200"
                                totalAmt = (finalTotal).toString()

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ModelAddtoCart>, t: Throwable) {
                        count++
                        if (count <= 3) {
                            apiCallCartProduct()
                        } else {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        }
                        AppProgressBar.hideLoaderDialog()


                    }

                })
        }
    */

    companion object {
        var totalAmt = ""
        var tax = ""
    }
}