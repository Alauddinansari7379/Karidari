package com.example.tlismimoti .order

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.tlismimoti.R
import com.example.tlismimoti.databinding.ActivityOrderBinding
import com.example.tlismimoti.order.adapter.ViewPagerAdapter
import com.example.tlismimoti.sharedpreferences.SessionManager
import com.google.android.material.tabs.TabLayout

class Order : AppCompatActivity() {
    private var context = this@Order
    val binding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }
    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout  // creating object of TabLayout
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        with(binding){
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }

        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val tabs = findViewById<View>(R.id.tabs) as TabLayout
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                when (tab.position) {
                    0 -> tabs.setSelectedTabIndicatorColor(Color.parseColor("#FF000000"))
                    1 -> tabs.setSelectedTabIndicatorColor(Color.parseColor("#FF000000"))
                 }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        adapter.addFragment(FragmentActive(), "Active")
        adapter.addFragment(FragmentPast(), "Past")
        pager.adapter = adapter
        tab.setupWithViewPager(pager)
    }
}