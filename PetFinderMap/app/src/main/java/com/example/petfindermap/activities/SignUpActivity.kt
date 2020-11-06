package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.UserService
import kotlinx.android.synthetic.main.sign_up.*

class SignUpActivity : AppCompatActivity() {

    private var userService: UserService = UserService.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        super.getSupportActionBar()?.hide()
        signUp()
    }

    private fun signUp() {
        ButtonSignUp.setOnClickListener {
            val email = TextEmail.text.toString()
            val telephone = TextPhone.text.toString()
            val name = TextName.text.toString()
            val password = TextPassword.text.toString()
            try {
                userService.signUp(
                    Telephone = telephone,
                    Name = name,
                    Email = email,
                    Password = password
                )
            } catch (ex: java.lang.Exception) {
                TextView.text = "Ошибка регистрации"
            }
            finish()
            val signIn = Intent(this, SignInActivity::class.java)
            startActivity(signIn)
        }

    }

}