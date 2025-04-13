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
import java.util.Calendar
import java.util.Date
import com.google.mediapipe.tasks.vision.core.RunningMode

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
        if (CameraFragment.exerciseType == Type.Squats) updateDataObject(this, "Squats")
        else updateErrorDataObject(this, "Squats")

        CameraFragment.exerciseType = Type.Squats
        CameraFragment.squatTracker.count = 0
        CameraFragment.squatTracker.direction = false
        TextToSpeech.speak("Starting Squats")
    }

    fun onPushupsButtonClick(view: View) {
        if (CameraFragment.exerciseType == Type.Pushups) updateDataObject(this, "Pushups")
        else updateErrorDataObject(this, "Pushups")

        CameraFragment.exerciseType = Type.Pushups
        CameraFragment.pushUpTracker.count = 0
        CameraFragment.pushUpTracker.direction = false
        TextToSpeech.speak("Starting Pushups")
    }

    fun onLungesButtonClick(view: View) {
        if (CameraFragment.exerciseType == Type.Lunges) updateDataObject(this, "Lunges")
        else updateErrorDataObject(this, "Lunges")

        CameraFragment.exerciseType = Type.Lunges
        CameraFragment.lungeTracker.count = 0
        CameraFragment.lungeTracker.direction = false
        TextToSpeech.speak("Starting Lunges")
    }

    fun onJumpingJacksButtonClick(view: View) {
        if (CameraFragment.exerciseType == Type.JumpingJacks) updateDataObject(this, "JumpingJacks")
        else updateErrorDataObject(this, "JumpingJacks")

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

            R.id.nav_setting -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container1,
                    SettingFragment(this)
                ).commit()

            R.id.nav_logout -> {
                addRandomDatesToDataObject(this, "Squats")
                addRandomDatesToDataObject(this, "Pushups")
                addRandomDatesToDataObject(this, "Lunges")
                addRandomDatesToDataObject(this, "JumpingJacks")
                Toast.makeText(this, "Added Random Dates", Toast.LENGTH_SHORT).show()
            }
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

    fun addRandomDatesToDataObject(context: Context, key: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val dataObject = getDataObject(context, key)

        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds

        val randomCount = (10..20).random()
        repeat(randomCount) {
            val randomTime = (oneWeekAgo..currentTime).random()
            dataObject.dateTimes.add(Date(randomTime))
        }

        val randomCount2 = (2..8).random()
        repeat(randomCount2) {
            val randomTime = (oneWeekAgo..currentTime).random()
            dataObject.incorrectTimes.add(Date(randomTime))
        }

        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(dataObject))
        editor.apply()
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
                DataObject(mutableListOf(), mutableListOf())
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

            loadedDataObject.incorrectTimes.add(Date())

            val editor = sharedPreferences.edit()
            editor.putString(key, gson.toJson(loadedDataObject))
            editor.apply()
        }
    }
}