package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.UserService
import kotlinx.android.synthetic.main.sign_in.*
import kotlinx.android.synthetic.main.sign_in.TextPassword
import kotlinx.android.synthetic.main.sign_in.TextPhone
import kotlinx.android.synthetic.main.sign_in.TextView
import kotlinx.android.synthetic.main.sign_up.*

class SignInActivity : AppCompatActivity() {

    private var userService: UserService = UserService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        super.getSupportActionBar()?.hide()
    }

    fun signIn(view: View) {
        val telephone = TextPhone.text.toString()
        val password = TextPassword.text.toString()
        userService.signIn(
            telephone,
            password
        ) { code: Int, body: String ->
            runOnUiThread {
                when (code) {
                    400 -> {
                        TextView.text = "Пользователь уже авторизован"
                    }
                    1 -> {
                        TextView.text = body
                    }
                    200 -> {
                        val mapsActivity = Intent(this, MapsActivity::class.java)
                        startActivity(mapsActivity)
                    }
                }
            }
        }
    }

    fun goToSignUp(view: View) {
        val signUp = Intent(this, SignUpActivity::class.java)
        startActivity(signUp)
    }
}