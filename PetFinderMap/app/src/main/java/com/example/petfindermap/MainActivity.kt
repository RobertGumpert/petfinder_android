package com.example.petfindermap

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.activities.MapsActivity
import com.example.petfindermap.activities.SignInActivity
import com.example.petfindermap.activities.SignUpActivity
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.User
import com.example.petfindermap.services.AdService
import com.example.petfindermap.services.DialogsService
import com.example.petfindermap.services.UserService

class MainActivity : AppCompatActivity() {

    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)

        val appDatabase : AppDatabase = AppDatabase.getInstance(this)
        userService = UserService.getInstance()

        userService.userDataLoad() {
            if (userService.user == null) {
                val signIn = Intent(this, SignInActivity::class.java)
                startActivity(signIn)
            }
            else {
                val mapsActivity = Intent(this, MapsActivity::class.java)
                startActivity(mapsActivity)
            }
        }
    }
}