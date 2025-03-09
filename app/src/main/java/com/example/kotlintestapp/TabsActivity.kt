package com.example.kotlintestapp

import Tab1Fragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class TabsActivity : AppCompatActivity() {
    var donorId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        loadFragment(Tab3Fragment())
        loadFragment(Tab1Fragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.tab1 -> Tab1Fragment()
                R.id.tab2 -> Tab2Fragment()
                R.id.tab3 -> Tab3Fragment()
                else -> Tab1Fragment()
            }
            loadFragment(selectedFragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val bundle = Bundle()
        if (fragment is Tab3Fragment && donorId != null) {
            bundle.putString("donorId", donorId)
            fragment.arguments = bundle
        }
        val tag = if (fragment is Tab3Fragment) "Tab3Fragment" else null
        Log.d("TabsActivity", "loadFragment called with: $fragment, tag: $tag")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }


}
