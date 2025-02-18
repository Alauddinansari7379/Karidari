package com.example.tlismimoti.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.databinding.ActivityLoginBinding
import com.example.tlismimoti.login.model.ModelLogin
import com.example.tlismimoti.mainActivity.MainActivity
import com.example.tlismimoti.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private val context = this@Login
    private lateinit var binding: ActivityLoginBinding
    lateinit var sessionManager: SessionManager
    var count=0
    private val PREF_NAME = "MyPrefs"
    private val PREF_USERNAME = "username"
    private val PREF_PASSWORD = "password"
    private val FCM_TOKEN = "fcmtoken"
    private var fcmTokenNew = ""
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        Log.e("sessionManager.fcmToken", sessionManager.fcmToken.toString())


        with(binding) {
//            imgBack.setOnClickListener {//Commented by somenath
//                onBackPressed()
//            }
//            passwordToggle.setOnClickListener {
//                passwordToggleOff.visibility = View.VISIBLE
//                passwordToggle.visibility = View.GONE
//                edtPassword.transformationMethod = PasswordTransformationMethod()
//            }
//            passwordToggleOff.setOnClickListener {
//                passwordToggleOff.visibility = View.GONE
//                passwordToggle.visibility = View.VISIBLE
//                edtPassword.transformationMethod = null
//                //binding.passwordEdt.transformationMethod =PasswordTransformationMethod(false)
//                //binding.passwordToggle.sw
//            }
            tvForgot.setOnClickListener {
               // startActivity(Intent(this@Login, SignUp::class.java))
            }
            btnSignup.setOnClickListener {
                startActivity(Intent(this@Login,SignUp::class.java))
            }
            btnLogIn.setOnClickListener {
                if (edtUserName.text!!.isEmpty()) {
                    edtUserName.error = "Enter Username"
                    edtUserName.requestFocus()
                    return@setOnClickListener
                }
                if (edtPassword.text!!.isEmpty()) {
                    edtPassword.error = "Enter Password"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }
                if(sessionManager.fcmToken!!.isNotEmpty()){
                    saveFCM(sessionManager.fcmToken.toString())
                }
                fcmTokenNew = sharedPreferences.getString(FCM_TOKEN, "").toString()

                Log.e("FCMNewSession",fcmTokenNew)
                 login()

            }
        }

    }

    private fun login() {

        AppProgressBar.showLoaderDialog(this@Login)
        ApiClient.apiService.loginUser(
            binding.edtUserName.text.toString().trim(),
            binding.edtPassword.text.toString().trim(),
            fcmTokenNew,"android"
        ).enqueue(object :
            Callback<ModelLogin> {
            @SuppressLint("LogNotTimber", "LongLogTag")
            override fun onResponse(
                call: Call<ModelLogin>,
                response: Response<ModelLogin>
            ) {
                try {
                    if (response.code() == 500) {
                        myToast(this@Login, "Server Error")
                        AppProgressBar.hideLoaderDialog()
                    } else if (response.code() == 404) {
                        myToast(this@Login, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }else if (response.body()!!.data.token!!.isNotEmpty()) {
                        myToast(context, "Login Successfully")
                        sessionManager.authTokenUser = "Bearer " + response.body()!!.data.token
                        sessionManager.userEmail = response.body()!!.data.email
                        sessionManager.userName = response.body()!!.data.name
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        finish()
                        startActivity(intent)
                         AppProgressBar.hideLoaderDialog()

                    } else {
                        myToast(context, "Login failed, please try again!")
                        AppProgressBar.hideLoaderDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    myToast(context, "Something went wrong")
                    AppProgressBar.hideLoaderDialog()

                }

            }

            override fun onFailure(call: Call<ModelLogin>, t: Throwable) {
                count++
                if (count <= 5) {
                    login()
                } else {
                    myToast(context, "Something went wrong")
                    AppProgressBar.hideLoaderDialog()

                }
                AppProgressBar.hideLoaderDialog()


            }

        })
    }
    private fun saveFCM(fcmToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString(FCM_TOKEN, fcmToken)
        editor.apply()
    }
}