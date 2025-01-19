package com.example.tlismimoti.setting

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.ActivitySettingsBinding
import com.example.tlismimoti.mainActivity.MainActivity
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.setting.model.ModelSetting
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.example.tlismimoti.wishlist.Wishlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Settings : AppCompatActivity() {
    private var context = this@Settings
    private lateinit var binding: ActivitySettingsBinding
    lateinit var sessionManager: SessionManager
    var dialog: Dialog? = null
    var count = 0
    var countU = 0
    var countDes = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        if (sessionManager.authTokenUser!!.isNotEmpty()){
            apiCallGetSetting()
        }



        with(binding) {
            cardChanePass.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()){
                    changePassDialog()
                }
            }
            imgBack.setOnClickListener {
                onBackPressed()
            }

            layoutFavoriteOutfits.setOnClickListener {
                startActivity(Intent(context,Wishlist::class.java))
            }

            cardTerm.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://karidari.com/page/privacy/1982")
                }
                context.startActivity(intent)
            }
            btnSignOut.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()) {
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to Logout?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setConfirmClickListener { sDialog ->
                            sDialog.cancel()
                            sessionManager.userMobile = ""
                            sessionManager.authTokenUser = ""
                            sessionManager.userName = ""
                            sessionManager.userEmail = ""
                            sessionManager.deviceId = ""
                            myToast(context, "Successfully Logout")
                            startActivity(Intent(this@Settings, MainActivity::class.java))
                        }
                        .setCancelClickListener { sDialog ->
                            sDialog.cancel()
                        }
                        .show()


                }
            }
            cardDelete.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()) {
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to Delete?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setConfirmClickListener { sDialog ->
                            sDialog.cancel()
                            apiCallDeleteAccount()
                         }
                        .setCancelClickListener { sDialog ->
                            sDialog.cancel()
                        }
                        .show()


                }
            }

        }

    }

    private fun apiCallGetSetting() {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.userSettings(
            sessionManager.authToken,
            sessionManager.userEmail.toString()
        )
            .enqueue(object : Callback<ModelSetting> {
                override fun onResponse(
                    call: Call<ModelSetting>, response: Response<ModelSetting>
                ) {
                    try {

                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            sessionManager.userMobile = response.body()!!.data.check.mobile
                            sessionManager.userName = response.body()!!.data.check.name
                            sessionManager.userEmail = response.body()!!.data.check.email

                            binding.tvFullName.text = response.body()!!.data.check.name
                            binding.tvEmail.text = response.body()!!.data.check.email
                            binding.tvMobile.text = response.body()!!.data.check.mobile
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelSetting>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        apiCallGetSetting()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        myToast(context, "Something went wrong")

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallUpdateSetting(
        name: String,
        mobile: String,
        currentPass: String,
        password: String
    ) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.userSettingsUpdate(
            sessionManager.authToken,
            sessionManager.userEmail.toString(),
            name,
            mobile,
            currentPass,
            password,
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

                        } else {
                            myToast(context, response.body()!!.data)
                            apiCallGetSetting()
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    countU++
                    if (countU <= 2) {
                        Log.e("count", countU.toString())
                        apiCallGetSetting()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        myToast(context, "Something went wrong")

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun changePassDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_password_change, null)
        dialog = Dialog(context)

        val btnChange = view!!.findViewById<Button>(R.id.btnChangeDialogPass)
        val newPass = view!!.findViewById<EditText>(R.id.edtNewPasswordDialogPass)
        val fullName = view!!.findViewById<EditText>(R.id.edtFullNameDil)
        val mobile = view!!.findViewById<EditText>(R.id.edtMobileDil)
        val currentPass = view!!.findViewById<EditText>(R.id.edtCurrentPasswordDialogPass)
        dialog = Dialog(context)


        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view) // <- fix
        }
        dialog!!.setContentView(view)
        dialog?.setCancelable(true)

        dialog?.show()
        mobile.setText(sessionManager.userMobile)
        fullName.setText(sessionManager.userName)

        btnChange.setOnClickListener {
            if (currentPass.text!!.isEmpty()) {
                currentPass.error = "Enter Current Password"
                currentPass.requestFocus()
                return@setOnClickListener
            }
            if (newPass.text!!.isEmpty()) {
                newPass.error = "Enter New Password"
                newPass.requestFocus()
                return@setOnClickListener
            }
            val password = newPass.text.toString().trim()
            val currentPass = currentPass.text.toString().trim()
            val name = fullName.text.toString().trim()
            val mobile = mobile.text.toString().trim()

            apiCallUpdateSetting(name, mobile, currentPass, password)
            dialog?.dismiss()
        }
    }
    private fun apiCallDeleteAccount() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.deleteAccount(
            sessionManager.authToken,
            sessionManager.userEmail
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
                            myToast(context, "Successfully Account Deleted")
                            sessionManager.userMobile = ""
                            sessionManager.authTokenUser = ""
                            sessionManager.userName = ""
                            sessionManager.userEmail = ""
                            sessionManager.deviceId = ""
                            AppProgressBar.hideLoaderDialog()
                            val intent = Intent(this@Settings, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            finish()
                            startActivity(intent)
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
                    countDes++
                    if (countDes <= 2) {
                        Log.e("count", countDes.toString())
                        apiCallDeleteAccount()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

}