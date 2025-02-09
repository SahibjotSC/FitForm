package com.example.fitform

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fitform.databinding.ActivityMainBinding
import com.example.fitform.exercise.helper.Type
import com.example.fitform.fragment.CameraFragment
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawerLayout: DrawerLayout? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar) //Ignore red line errors
        setSupportActionBar(toolbar)

        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout?
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    HomeFragment()
                ).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container1, CameraFragment()) // Default fragment for secondary container
            .commit()
    }

    fun onSquatsButtonClick(view: View) {
        CameraFragment.exerciseType = Type.Squats
        CameraFragment.squatTracker.count = 0
        CameraFragment.squatTracker.direction = false
    }

    fun onPushupsButtonClick(view: View) {
        CameraFragment.exerciseType = Type.Pushups
        CameraFragment.pushUpTracker.count = 0
        CameraFragment.pushUpTracker.direction = false
    }

    fun onLungesButtonClick(view: View) {
        CameraFragment.exerciseType = Type.Lunges
        CameraFragment.lungeTracker.count = 0
        CameraFragment.lungeTracker.direction = false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    HomeFragment()
                ).commit()

            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    SettingsFragment()
                ).commit()

            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    AboutFragment()
                ).commit()

            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed()
            finish()
        }
    }
}