package com.example.tlismimoti.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.FragmentAccountBinding
import com.example.tlismimoti.login.Login
import com.example.tlismimoti.login.SignUp
import com.example.tlismimoti.order.Order
import com.example.tlismimoti.setting.Settings
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.example.tlismimoti.wishlist.HelpCenter
import com.example.tlismimoti.wishlist.Wishlist
import com.google.android.material.bottomsheet.BottomSheetDialog

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    @SuppressLint("MissingInflatedId")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val parentView: View? = layoutInflater.inflate(R.layout.login_dialog, null)
        if (parentView != null) {
            bottomSheetDialog.setContentView(parentView)
        }

        val imgClose = parentView?.findViewById<ImageView>(R.id.imgBackDil)
        val login = parentView?.findViewById<Button>(R.id.btnLoginDil)
        val signUp = parentView?.findViewById<Button>(R.id.btnSignUpDil)

        imgClose?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        login?.setOnClickListener {
            startActivity(Intent(activity, Login::class.java))
        }
        signUp?.setOnClickListener {
            startActivity(Intent(activity, SignUp::class.java))
        }


        with(binding) {

            if (sessionManager.userName!!.isNotEmpty()) {
                tvFullName.text = sessionManager.userName
                tvEmail.text = sessionManager.userEmail
            }
            layoutTransactionHistory.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()) {
                    try {
                        bottomSheetDialog.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else {
                    startActivity(Intent(requireContext(), Order::class.java))
                }
            }

            layoutSettings.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()) {
                    try {
                        bottomSheetDialog.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else {
                    startActivity(Intent(requireContext(), Settings::class.java))
                }
            }

            layoutFavoriteOutfits.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()) {
                    try {
                        bottomSheetDialog.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else {
                    startActivity(Intent(requireContext(), Wishlist::class.java))
                }
            }
        }
    }


}