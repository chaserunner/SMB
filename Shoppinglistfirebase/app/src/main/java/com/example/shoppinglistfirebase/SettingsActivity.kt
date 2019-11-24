package com.example.shoppinglistfirebase

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.item_view_holder.*

class SettingsActivity : AppCompatActivity() {

    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        darkModeCheckBox.isChecked = sharedPref.getBoolean(MainActivity.DARK_MODE, false)
        darkModeCheckBox.setOnCheckedChangeListener({ button, isChecked ->
            sharedPref.edit().putBoolean(MainActivity.DARK_MODE, isChecked).commit()
            updateColor()
        })

        bigfontCheckbox.isChecked = sharedPref.getBoolean(MainActivity.BIG_FONT, false)
        bigfontCheckbox.setOnCheckedChangeListener({ button, isChecked ->
            sharedPref.edit().putBoolean(MainActivity.BIG_FONT, isChecked).commit()
        })

    }

    override fun onStart() {
        super.onStart()
        updateColor()
    }

    fun updateColor() {
        if (sharedPref.getBoolean(MainActivity.DARK_MODE, false)) {
            setActivityBackgroundColor(Color.GRAY)
        } else {
            setActivityBackgroundColor(Color.WHITE)
        }
    }

    fun setActivityBackgroundColor(color: Int) {
        val view = this.window.decorView
        view.setBackgroundColor(color)
    }
}
