package com.example.kotlintestapp

import Tab1Fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class TabsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Устанавливаем первую вкладку при запуске
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
