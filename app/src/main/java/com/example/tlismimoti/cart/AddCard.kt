package com.example.tlismimoti.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tlismimoti.databinding.ActivityAddCardBinding

class AddCard : AppCompatActivity() {
    private lateinit var binding:ActivityAddCardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }
        }

    }
}