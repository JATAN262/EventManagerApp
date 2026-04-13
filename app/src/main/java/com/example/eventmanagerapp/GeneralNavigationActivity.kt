package com.example.eventmanagerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.FrameLayout

class GeneralNavigationActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_navigation)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentContainer = findViewById(R.id.fragment_container)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up navigation listeners
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            selectBottomNavItem(menuItem)
            true
        }
        // Default fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_bookings -> replaceFragment(BookingsFragment())
            R.id.nav_chat -> replaceFragment(ChatFragment())
            R.id.nav_profile -> replaceFragment(ProfileFragment())
            R.id.nav_settings -> replaceFragment(SettingsFragment())
            R.id.nav_help -> replaceFragment(HelpFragment())
        }
        drawerLayout.closeDrawers()
    }

    private fun selectBottomNavItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.bottom_nav_home -> replaceFragment(HomeFragment())
            R.id.bottom_nav_bookings -> replaceFragment(BookingsFragment())
            R.id.bottom_nav_chat -> replaceFragment(ChatFragment())
            R.id.bottom_nav_profile -> replaceFragment(ProfileFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
} 