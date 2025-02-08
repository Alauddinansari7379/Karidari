package com.example.tlismimoti.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.mainActivity.MainActivity
import com.example.tlismimoti.databinding.ActivitySplashScreenBinding
import com.example.tlismimoti.login.model.ModelLogin
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySplashScreenBinding
    private lateinit var sessionManager:SessionManager
    private var context=this@SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
         binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager= SessionManager(this@SplashScreen)
        if (sessionManager.authToken!!.isEmpty()){
            login()
        }
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
    private fun login() {

       // AppProgressBar.showLoaderDialog(this@SplashScreen)
        ApiClient.apiService.autologin(
            "karidari.shop",
         ).enqueue(object :
            Callback<ModelLogin> {
            @SuppressLint("LogNotTimber", "LongLogTag")
            override fun onResponse(
                call: Call<ModelLogin>,
                response: Response<ModelLogin>
            ) {
                try {
                    if (response.code() == 500) {
                        myToast(this@SplashScreen, "Server Error")
                        AppProgressBar.hideLoaderDialog()
                    } else if (response.code() == 404) {
                        myToast(this@SplashScreen, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    } else if (response.body()!!.success) {
                        sessionManager.isLogin = true
                    //    myToast(context, "Login Successfully")
                        sessionManager.authToken = "Bearer " + response.body()!!.data.token
                        sessionManager.email = response.body()!!.data.email
                        sessionManager.customerName = response.body()!!.data.name
                    } else {
                    //    myToast(context, "Wrong Username or Password")
                        AppProgressBar.hideLoaderDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    myToast(context, "Something went wrong")
                    AppProgressBar.hideLoaderDialog()

                }

            }

            override fun onFailure(call: Call<ModelLogin>, t: Throwable) {
                myToast(context, "Something went wrong")
                AppProgressBar.hideLoaderDialog()

            }

        })
    }

}