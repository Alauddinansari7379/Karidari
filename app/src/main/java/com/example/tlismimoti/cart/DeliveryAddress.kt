package com.example.tlismimoti.cart

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.Checkout.Companion.totalAmt
import com.example.tlismimoti.cart.model.ModelActiveGateway.ModelActiveGateWays
import com.example.tlismimoti.cart.model.ModelOrderCreate
import com.example.tlismimoti.cart.model.ModelPayment.ModelPayment
import com.example.tlismimoti.cart.model.ModelState.ModelState
import com.example.tlismimoti.databinding.ActivityDeliveryAddressBinding
import com.example.tlismimoti.mainActivity.MainActivity
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.example.tlismimoti.order.Order
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DeliveryAddress : AppCompatActivity(), PaymentResultListener {
    private var context = this@DeliveryAddress
    private lateinit var binding: ActivityDeliveryAddressBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var count1 = 0
    var count2 = 0
    var countS = 0
    var countA = 0
    private var utrNumber = ""
    private var paymentType = ""
    private var selectedState = ""
    private var selectedCity = ""
    private var dialog: Dialog? = null
    var imageuRL = ""
    private var keyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        utrNumber = intent.getStringExtra("UTR").toString()
        paymentType = intent.getStringExtra("paymentType").toString()


        when(sessionManager.currency){
            "INR","₹"->{
                with(binding){
                    cardState.visibility=View.GONE
                    cardCity.visibility=View.GONE
                }
            }else->{
            with(binding) {
                layoutSpinnerCity.visibility=View.GONE
                layoutSpinnerState.visibility=View.GONE

            }
            }
        }

            apiCallGetState()
        apiCallGetPaymentList()
        apiCallGetActiveGateways()
        with(binding) {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            tvTotalAmt.text = "Total Amount : ${sessionManager.currency}" + totalAmt
            tvViewOrderDetial.setOnClickListener {
                val intent = Intent(applicationContext, Order::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                finish()
                startActivity(intent)
            }
            btnPlaceOrder.setOnClickListener {
                if (btnPlaceOrder.text.toString() == "Awesome") {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    finish()
                    startActivity(intent)
                } else {
                    if (edtAddress.text!!.isEmpty()) {
                        edtAddress.error = "Enter Address"
                        edtAddress.requestFocus()
                        return@setOnClickListener
                    }
                    if (edtZipCode.text!!.isEmpty()) {
                        edtZipCode.error = "Enter ZipCode"
                        edtZipCode.requestFocus()
                        return@setOnClickListener
                    }
                    when(sessionManager.currency) {
                        "INR", "₹" -> {


                        }else->{
                        if (edtState.text!!.isEmpty()) {
                            edtState.error = "Enter State"
                            edtState.requestFocus()
                            return@setOnClickListener
                        }
                        if (edtCity.text!!.isEmpty()) {
                            edtCity.error = "Enter City"
                            edtCity.requestFocus()
                            return@setOnClickListener
                        }

                    }
                    }
                    if (binding.radioOnline.isChecked) {
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
                            apiCallMakeOrder()
//                        val intent = Intent(context as Activity, DeliveryAddress::class.java)
//                            .putExtra("UTR", edtUTR.text.toString())
//                            .putExtra("paymentType", "1")
//                        context.startActivity(intent)

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
                    } else if (binding.radioCOD.isChecked) {
                        apiCallMakeOrder()
                    } else {
                        Toast.makeText(context, "Please Select Payment Type", Toast.LENGTH_LONG)
                            .show()

                    }
                }
            }
        }

    }

    private fun startPaymentOnlineRazorPay() {
        val co = com.razorpay.Checkout()
        try {
            co.setKeyID(keyId)
            val options = JSONObject()
            options.put("name", resources.getString(R.string.app_name))
            options.put("description", "Payment Description")
            //You can omit the image option to fetch the image from the dashboard
            options.put(
                "image",
                "https://kd.sellacha.com/uploads/797/logo.png"
            )
            options.put("theme.color", "#6c54a1")
            options.put("currency", "INR")
            options.put("amount", totalAmt.toInt() * 100)//pass amount in currency subunits
//            options.put("amount", 1 * 100)//pass amount in currency subunits
            val prefill = JSONObject()
            prefill.put("email", sessionManager.userEmail)
            prefill.put("contact", sessionManager.userMobile)
            options.put("prefill", prefill)
            co.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Successful: ", Toast.LENGTH_LONG).show()
        apiCallMakeOrder()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Field ", Toast.LENGTH_LONG).show()


    }

    private fun apiCallMakeOrder() {
        if (binding.edtState.text!!.isNotEmpty()){
            selectedState=binding.edtState.text.toString()
            selectedCity=binding.edtCity.text.toString()
        }
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.makeOrder(
            sessionManager.authToken!!,
            "1",
            "1",
            "",
            "",
            paymentType,
            "2",
            sessionManager.userName,
            sessionManager.userEmail,
            "",
            "",
            binding.edtAddress.text.toString() + " " + selectedCity,
            binding.edtZipCode.text.toString(),
            selectedState,
            sessionManager.deviceId,
            Checkout.totalAmt,
            "00",
            "00",
            utrNumber,

            )
            .enqueue(object : Callback<ModelOrderCreate> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelOrderCreate>, response: Response<ModelOrderCreate>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()
                        } else if (!response.body()!!.data.contains("Order Created")) {
                            // myToast(context, response.body()!!.data)
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            //  myToast(context, response.body()!!.data)

                            binding.layoutAddress.visibility = View.GONE
                            binding.layoutCongratulation.visibility = View.VISIBLE
                            binding.btnPlaceOrder.text = "Awesome"
                             AppProgressBar.hideLoaderDialog()
                            val se= SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Order placed")
                                .setConfirmText("Ok")
                                .showCancelButton(true)
                                .setConfirmClickListener { sDialog ->
                                    sDialog.cancel()
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    finish()
                                    startActivity(intent)
                                }
                            se.setCancelable(false)
                            se.setCancelClickListener { sDialog ->
                                sDialog.cancel()
                            }
                                .show()
                            apiCallDestroyCart()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelOrderCreate>, t: Throwable) {
                    count++
                    if (count <= 7) {
                        apiCallMakeOrder()
                    } else {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetState() {
        AppProgressBar.showLoaderDialog(context)

        ApiClient.apiService.getState()
            .enqueue(object : Callback<ModelState> {
                override fun onResponse(call: Call<ModelState>, response: Response<ModelState>) {
                    try {
                        val list = response.body()?.data?.posts
                        if (list != null) {
                            // Create an array of state names
                            val items = arrayOfNulls<String>(list.size)
                            for (i in list.indices) {
                                items[i] = list[i].name
                            }

                            // Create an ArrayAdapter using the state names
                            val adapter: ArrayAdapter<String?> = ArrayAdapter(
                                context, R.layout.simple_list_item_1, items
                            )

                            // Set the adapter to the spinner
                            binding.spinnerState.adapter = adapter

                            // Set the default selection
//                            val relationId = "Some default relation id" // Replace with your default relation id if available
//                            binding.spinnerState.setSelection(items.indexOf(relationId))

                            // Hide the loader dialog
                            AppProgressBar.hideLoaderDialog()

                            // Set the item selected listener for the spinner
                            binding.spinnerState.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    @SuppressLint("SuspiciousIndentation")
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        selectedState = list[position].name.toString()
                                        val id = list[position].id
                                        apiCallGetCity(id.toString())


                                        // Handle the selected state id
                                        // Toast.makeText(context, "Selected State ID: $selectedStateId", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                        // Handle the case when nothing is selected, if needed
                                    }
                                }
                        } else {
                            // Handle the case when list is null
                            myToast(context, "No data found")
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelState>, t: Throwable) {
                    AppProgressBar.hideLoaderDialog()
                    count1++
                    if (count1 <= 7) {
                        Log.e("count", count1.toString())
                        apiCallGetState()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                    }
                }
            })
    }

    private fun apiCallGetCity(stateId: String) {

        ApiClient.apiService.getCity(stateId)
            .enqueue(object : Callback<ModelState> {
                override fun onResponse(call: Call<ModelState>, response: Response<ModelState>) {
                    try {
                        val list = response.body()?.data?.posts
                        if (list != null) {
                            // Create an array of state names
                            val items = arrayOfNulls<String>(list.size)
                            for (i in list.indices) {
                                items[i] = list[i].city
                            }

                            // Create an ArrayAdapter using the state names
                            val adapter: ArrayAdapter<String?> = ArrayAdapter(
                                context, R.layout.simple_list_item_1, items
                            )

                            // Set the adapter to the spinner
                            binding.spinnerCity.adapter = adapter

                            // Set the default selection
//                            val relationId = "Some default relation id" // Replace with your default relation id if available
//                            binding.spinnerState.setSelection(items.indexOf(relationId))

                            // Hide the loader dialog
                            AppProgressBar.hideLoaderDialog()

                            // Set the item selected listener for the spinner
                            binding.spinnerCity.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        selectedCity = list[position].city.toString()
                                        // Handle the selected state id
                                        // Toast.makeText(context, "Selected State ID: $selectedStateId", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                        // Handle the case when nothing is selected, if needed
                                    }
                                }
                        } else {
                            // Handle the case when list is null
                            myToast(context, "No data found")
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelState>, t: Throwable) {
                    AppProgressBar.hideLoaderDialog()
                    count2++
                    if (count2 <= 7) {
                        Log.e("count", count2.toString())
                        apiCallGetCity(stateId)
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                    }
                }
            })
    }

    private fun apiCallDestroyCart() {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.destroyCart(
            sessionManager.authToken,
            sessionManager.deviceId.toString()
        )
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
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
//                            myToast(context, "All Item Deleted")
//                            AppProgressBar.hideLoaderDialog()
//                            val intent = Intent(this@MainActivity, MainActivity::class.java)
//                            intent.flags =
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            finish()
//                            startActivity(intent)
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
                    count++
                    if (count <= 7) {
                        Log.e("count", count.toString())
                        apiCallDestroyCart()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
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

    private fun apiCallGetPaymentList() {
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
                            countS = 0
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
                    if (countS <= 7) {
                        Log.e("count", countS.toString())
                        apiCallGetPaymentList()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetActiveGateways() {
        // AppProgressBar.showLoaderDialog(requireContext())

        ApiClient.apiService.activeGateways(sessionManager.authToken)
            .enqueue(object : Callback<ModelActiveGateWays> {
                @SuppressLint("LogNotTimber")
                override fun onResponse(
                    call: Call<ModelActiveGateWays>, response: Response<ModelActiveGateWays>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.getways.isEmpty()) {
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            var data = ""
                            for (i in response.body()!!.data.getways) {
                                data = i.content

                                countA = 0
                                val jsonString = """
                             ${data}
                            """.trimIndent()

                                val jsonObject = JSONObject(jsonString)
                                val title = jsonObject.getString("title")
                                when (title) {
                                    "QR Payment" -> binding.radioOnline.visibility = View.VISIBLE
                                    "Razorpay" -> {
                                        binding.radioRazorPay.visibility = View.VISIBLE
                                        keyId = jsonObject.getString("key_id")

                                    }

                                    "instamojo" -> binding.radioInstamojo.visibility = View.VISIBLE
                                    "COD", "Cash On Delivery (COD)", "cod" -> binding.radioCOD.visibility =
                                        View.VISIBLE

                                    "paypal" -> binding.radioPaypal.visibility = View.VISIBLE
                                }


                                println("title: $title")

                            }
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelActiveGateWays>, t: Throwable) {
                    countA++
                    if (countA <= 7) {
                        Log.e("count", countA.toString())
                        apiCallGetActiveGateways()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(applicationContext, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        finish()
//        startActivity(intent)
//    }

}