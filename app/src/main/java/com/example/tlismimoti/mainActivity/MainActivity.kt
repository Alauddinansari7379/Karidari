package com.example.tlismimoti.mainActivity

//import com.google.android.play.core.appupdate.AppUpdateInfo
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.model.UpdateAvailability
//import com.google.android.play.core.tasks.Task
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.tlismimoti.Helper.AppProgressBar
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.R
import com.example.tlismimoti.cart.CartFragment
import com.example.tlismimoti.databinding.ActivityMainBinding
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.login.Login
import com.example.tlismimoti.login.SignUp
import com.example.tlismimoti.mainActivity.adapter.AutoSuggestAdapter
import com.example.tlismimoti.retrofit.ApiClient
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private var context = this@MainActivity
    private lateinit var binding: ActivityMainBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var countDes = 0
    var destinationFrom = ""
    private lateinit var bottomNav: BottomNavigationView
    private val NOTIFICATION_PERMISSION_CODE = 123

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(context)
        bottomNav = binding.bottomNavigationView
        var delete = false
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostFragment)
        val navController = navHostFragment!!.findNavController()
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bootom_nav_menu)
      //  binding.bottomNavigationView.setupWithNavController(navController)
        requestNotificationPermission()
        Log.e("authTokenUser", sessionManager.authTokenUser.toString())
        Log.e("userEmail", sessionManager.userEmail.toString())
        Log.e("userName", sessionManager.userName.toString())
        Log.e("sessionManager.fcmToken", sessionManager.fcmToken.toString())

        //   checkForUpdate(this)

        if (sessionManager.randomKey!!.isEmpty()) {
            sessionManager.randomKey = generateRandomString(10)
            val randomString = generateRandomString(10)
            println("Random String: $randomString")
        }


        val bottomSheetDialog = BottomSheetDialog(context)
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)
        val imgCloseNew = parentView.findViewById<ImageView>(R.id.imgBackDil)
        val login = parentView.findViewById<Button>(R.id.btnLoginDil)
        val signUp = parentView.findViewById<Button>(R.id.btnSignUpDil)

        imgCloseNew.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        login.setOnClickListener {
            startActivity(Intent(context, Login::class.java))
        }
        signUp.setOnClickListener {
            startActivity(Intent(context, SignUp::class.java))
        }
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.e("Notification", "onCreate: PERMISSION GRANTED")
                // sendNotification(this)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Snackbar.make(
                    findViewById<View>(android.R.id.content).rootView,
                    "Notification blocked",
                    Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    // Responds to click on the action
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            }

            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        apiCallProductList()
//        if (sessionManager.isLogin) {
//            startActivity(Intent(context, MainActivity::class.java))
//            finish()
//        }
        if (sessionManager.deviceId!!.isEmpty()) {
            val deviceID = getDeviceId(context)
            sessionManager.deviceId = deviceID
        }
        Log.e("DeviceId", sessionManager.deviceId.toString())
        // checkForUpdate(this)

        with(binding) {
            imgClose.setOnClickListener {
                layoutTitle.visibility = View.VISIBLE
                val rightSwipe: Animation =
                    AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_right)
                layoutTitle.startAnimation(rightSwipe)
                layoutSearch.visibility = View.GONE
                binding.SearchView.text.clear()
                SearchView.hideKeyboard()

            }

            imgSearch.setOnClickListener {
                layoutTitle.visibility = View.GONE
                layoutTitle.visibility = View.GONE
                layoutSearch.visibility = View.VISIBLE
                val leftSwipe: Animation =
                    AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_left)
                layoutSearch.startAnimation(leftSwipe)
                SearchView.requestFocus()
                SearchView.showKeyboard()


            }

            imgSearchNew.setOnClickListener {
                val intent = Intent(context as Activity, SearchActivity::class.java)
                    .putExtra("name", SearchView.text.toString().trim())
                context.startActivity(intent)
            }

            imDelete.setOnClickListener {
                if (CartFragment.CartItem != "0") {
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to Delete?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setConfirmClickListener { sDialog ->
                            sDialog.cancel()
                            apiCallDestroyCart()
                        }
                        .setCancelClickListener { sDialog ->
                            sDialog.cancel()
                        }
                        .show()

                }
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        destinationFrom = intent.getStringExtra("navigate_to").toString()
        if (destinationFrom == "fragment_cart") {
            navController.navigate(R.id.fragment_cart)
            bottomNavigationView.selectedItemId = R.id.fragment_cart
            // setFragment(CartFragment(), "CartFragment")
            destinationFrom=""
        }
// Assuming you have a BottomNavigationView with id bottomNavigationView

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_Home -> {
                    // Navigate to Home fragment or perform action
                    navController.navigate(R.id.fragment_Home)
                    true
                }

                R.id.fragment_categories -> {
                    // Navigate to Categories fragment or perform action
                    navController.navigate(R.id.fragment_categories)
                    true
                }

                R.id.fragment_account -> {
                    // Navigate to Account fragment or perform action
                    navController.navigate(R.id.fragment_account)
                    true
                }

                R.id.fragment_cart -> {
                    // Navigate to Cart fragment or perform action
                    navController.navigate(R.id.fragment_cart)
                    true
                }

                else -> false
            }
        }

/*
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_Home -> {
                    binding.imDelete.visibility = View.GONE
                    binding.SearchView.text.clear()
                    binding.imgSearch.visibility = View.VISIBLE
                    binding.layoutSearch.visibility = View.GONE
                    binding.layoutTitle.visibility = View.VISIBLE
                }

                R.id.fragment_categories -> {
                    binding.imDelete.visibility = View.GONE
                    binding.SearchView.text.clear()
                    binding.imgSearch.visibility = View.GONE
                    binding.layoutSearch.visibility = View.GONE
                    binding.layoutTitle.visibility = View.VISIBLE
                }

                R.id.fragment_account -> {
                    binding.imDelete.visibility = View.GONE
                    binding.SearchView.text.clear()
                    binding.layoutSearch.visibility = View.GONE
                    binding.imgSearch.visibility = View.GONE
                    binding.layoutTitle.visibility = View.VISIBLE

                    if (sessionManager.authTokenUser!!.isEmpty()) {
                        try {
                            bottomSheetDialog.show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                R.id.fragment_cart -> {
                    binding.imDelete.visibility = View.VISIBLE
                    binding.SearchView.text.clear()
                    binding.imgSearch.visibility = View.GONE
                    binding.layoutSearch.visibility = View.GONE
                    binding.layoutTitle.visibility = View.VISIBLE
                }
            }
        }
*/

    }
    private fun setFragment(fragment: Fragment, tag: String) {
        // Begin the fragment transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Replace the existing fragment with the new one
        transaction.replace(R.id.hostFragment, fragment, tag)

        // Optionally add the transaction to the backstack (if you want to go back to the previous fragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }


    private fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                //  sendNotification(this)
                // myToast(this@MainActivity,"Permission granted")
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        ) {
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
            NOTIFICATION_PERMISSION_CODE
        )
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale()
                        } else {
                            showSettingDialog()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "notification permission granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    var hasNotificationPermissionGranted = false
    private val isNotificationListenerEnabled: Boolean
        private get() {
            val pkgName = packageName
            val cn = ComponentName(pkgName, "$pkgName.NotificationListener")
            val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            return flat != null && flat.contains(cn.flattenToString())
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Checking the request code of our request
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(
                    this,
                    "Permission granted now you can read the storage",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //    private fun checkForUpdate(context: Context) {
//        val appUpdateManager = AppUpdateManagerFactory.create(context)
//        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
//
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            val currentVersionCode = context.packageManager
//                .getPackageInfo(context.packageName, 0).versionCode
//
//            Log.d("UpdateCheck", "Installed version: $currentVersionCode")
//            Log.d("UpdateCheck", "Play Store version: ${appUpdateInfo.availableVersionCode()}")
//
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
//                appUpdateInfo.clientVersionStalenessDays() ?: -1 >= 0) {
//                Log.d("UpdateCheck", "Update available")
//                showUpdateDialog(context)
//            } else {
//                Log.d("UpdateCheck", "No update available")
//            }
//        }
//    }
    private fun showUpdateDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Available")
        builder.setMessage("A new version of the app is available. Please update to the latest version.")
        builder.setPositiveButton("Update") { dialog, _ ->
            // Redirect to Play Store
            val appPackageName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (e: android.content.ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun getDeviceId(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    }


    private fun EditText.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    private fun apiCallDestroyCart() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.destroyCart(
            sessionManager.authToken,
            sessionManager.deviceId.toString()
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
                            myToast(context, "All Item Deleted")
                            AppProgressBar.hideLoaderDialog()
                            val intent = Intent(this@MainActivity, MainActivity::class.java)
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
                        apiCallDestroyCart()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallProductList() {
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {

                        if (ProductListSearch != null) {
                            ProductListSearch.clear()
                            //spinner code start
                            //  val items = arrayOfNulls<String>(testListNew.)
                            // mainData = response.body()!!.data.posts.data
                            for (i in response.body()!!.data.posts.data) {
                                if (i.title.isNotEmpty()) {
                                    ProductListSearch.add(i.title)
//                                         when (i1.name) {
//
//                                         }
                                }
//                                 for (i1 in i.categories) {
//
//
//
//                                 }
                            }
//                             for (i in testListNew.result!!.indices) {
//                                 items[i] = testListNew.result!![i].Test_Name
//                             }
//                        val adapter: ArrayAdapter<String?> =
//                            ArrayAdapter(this@ProfileSetting, android.R.layout.simple_list_item_1, items)
//                        binding.spinnerSpecialist.adapter = adapter
//                        progressDialog!!.dismiss()


                            val autoSuggestAdapter = AutoSuggestAdapter(
                                this@MainActivity,
                                android.R.layout.simple_list_item_1,
                                ProductListSearch.toMutableList()
                            )

                            binding.SearchView.setAdapter(autoSuggestAdapter)
                            binding.SearchView.threshold = 1


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        apiCallProductList()
                    } else {
                        myToast(this@MainActivity, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    companion object {
        val ProductListSearch = ArrayList<String>()
        var CategorieName = ""
        var listing = false

    }

}


