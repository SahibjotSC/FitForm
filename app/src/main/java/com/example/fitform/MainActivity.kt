package com.example.fitform

import com.example.fitform.exercise.helper.DataObject;
import android.content.Context
import android.content.SharedPreferences
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
import com.google.gson.Gson
import java.util.Date

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawerLayout: DrawerLayout? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    lateinit var poseLandmarkerHelper: PoseLandmarkerHelper

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

        poseLandmarkerHelper = PoseLandmarkerHelper(context = this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    HomeFragment()
                ).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container1, CameraFragment(this))
            .commit()

        TextToSpeech.initialize(this)
    }

    fun onSquatsButtonClick(view: View) {
        CameraFragment.exerciseType = Type.Squats
        CameraFragment.squatTracker.count = 0
        CameraFragment.squatTracker.direction = false
        TextToSpeech.speak("Starting Squats")
    }

    fun onPushupsButtonClick(view: View) {
        CameraFragment.exerciseType = Type.Pushups
        CameraFragment.pushUpTracker.count = 0
        CameraFragment.pushUpTracker.direction = false
        TextToSpeech.speak("Starting Pushups")
    }

    fun onLungesButtonClick(view: View) {
        CameraFragment.exerciseType = Type.Lunges
        CameraFragment.lungeTracker.count = 0
        CameraFragment.lungeTracker.direction = false
        TextToSpeech.speak("Starting Lunges")
    }

    fun onJumpingJacksButtonClick(view: View) {
        CameraFragment.exerciseType = Type.JumpingJacks
        CameraFragment.lungeTracker.count = 0
        CameraFragment.lungeTracker.direction = false
        TextToSpeech.speak("Starting JumpingJacks")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container1,
                    CameraFragment(this)
                ).commit()

            R.id.nav_dashboard -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container1,
                    DashboardFragment()
                ).commit()

            R.id.nav_graph ->
            {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container1,
                        GraphFragment()
                    ).commit()
            }

            R.id.nav_calendar -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container1,
                    CalendarFragment()
                ).commit()

            R.id.nav_setting -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container1,
                    SettingFragment(this)
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

    companion object {
        @JvmStatic
        fun getDataObject(context: Context, key: String): DataObject {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
            val gson = Gson()
            val jsonString = sharedPreferences.getString(key, null)
            var loadedDataObject = if (jsonString != null) {
                gson.fromJson(jsonString, DataObject::class.java)
            } else {
                DataObject(mutableListOf(), 0f)
            }

            return loadedDataObject;
        }

        fun updateDataObject(context: Context, key: String) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
            val gson = Gson()
            var loadedDataObject = getDataObject(context, key)

            loadedDataObject.dateTimes.add(Date())

            val editor = sharedPreferences.edit()
            editor.putString(key, gson.toJson(loadedDataObject))
            editor.apply()
        }

        fun updateErrorDataObject(context: Context, key: String) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
            val gson = Gson()
            var loadedDataObject = getDataObject(context, key)

            loadedDataObject.incorrect++

            val editor = sharedPreferences.edit()
            editor.putString(key, gson.toJson(loadedDataObject))
            editor.apply()
        }
    }
}