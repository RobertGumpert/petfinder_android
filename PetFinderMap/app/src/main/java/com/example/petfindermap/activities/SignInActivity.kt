package com.example.petfindermap.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.UserService
import kotlinx.android.synthetic.main.sign_in.*

class SignInActivity : AppCompatActivity() {

    private var userService: UserService = UserService.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        super.getSupportActionBar()?.hide()
        signIn()
    }

    private fun signIn() {
        ButtonSignUp.setOnClickListener {
            val telephone = TextPhone.text.toString()
            val password = TextPassword.text.toString()
            try {
                userService.signIn(
                    Telephone = telephone,
                    Password = password
                )
            } catch (ex: java.lang.Exception) {
                TextView.text = "Ошибка авторизации"
            }
            finish()
        }
    }
}