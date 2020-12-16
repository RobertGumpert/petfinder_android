package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.UserService
import kotlinx.android.synthetic.main.sign_up.*

class SignUpActivity : AppCompatActivity() {
    private var userService: UserService = UserService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        super.getSupportActionBar()?.hide()
    }

    fun signUp(view: View) {
        val email = TextEmail.text.toString()
        val telephone = TextPhone.text.toString()
        val name = TextName.text.toString()
        val password = TextPassword.text.toString()
        userService.signUp(
            telephone,
            name,
            email,
            password
        ) { code: Int, body: String ->
            runOnUiThread {
                when (code) {
                    400 -> {
                        TextView.text = "Пользователь с такими данными уже существует"
                    }
                    1 -> {
                        TextView.text = body
                    }
                    200 -> {
                        val signIn = Intent(this, SignInActivity::class.java)
                        startActivity(signIn)
                    }
                }
            }
        }
    }

    fun goToSignIn(view: View) {
        val signIn = Intent(this, SignInActivity::class.java)
        startActivity(signIn)
    }
}