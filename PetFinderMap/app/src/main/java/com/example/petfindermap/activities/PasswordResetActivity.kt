package com.example.petfindermap.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.models.UserResetPasswordHttpModel
import com.example.petfindermap.services.UserService
import kotlinx.android.synthetic.main.change_password.*
import kotlinx.android.synthetic.main.change_password.textViewResultRestPassword

class PasswordResetActivity: AppCompatActivity() {

    private var userService: UserService = UserService.getInstance()
    private lateinit var buttonChangePassword: Button
    private var flagOnClose = false
    private var flagOpenMenu = false
    private lateinit var buttonMenu: Button
    private lateinit var viewMenu: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        super.getSupportActionBar()?.hide()
        buttonChangePassword = findViewById(R.id.buttonUpdatePassword)
        viewMenu = findViewById(R.id.menu_slide)
        viewMenu.visibility = View.INVISIBLE
        buttonMenu = findViewById(R.id.buttonMenu3)
    }

    fun menuSlider(view: View) {
        if (flagOpenMenu) {
            viewMenu.visibility = View.INVISIBLE
            buttonMenu.text = "Меню"
        } else {
            viewMenu.visibility = View.VISIBLE
            buttonMenu.text = "Закрыть"
        }
        flagOpenMenu = !flagOpenMenu
    }

    fun menuButton(view: View) {
        when (view.id) {
            R.id.textViewMenuMessages -> {
                val dialogs = Intent(this, DialogsActivity::class.java)
                startActivity(dialogs)
            }
            R.id.textViewMenuMyAds -> {
                val myAds = Intent(this, MyAdsActivity::class.java)
                startActivity(myAds)
            }
            R.id.textViewMenuPersonalArea -> {
                val personalArea = Intent(this, PersonalAreaActivity::class.java)
                startActivity(personalArea)
            }
        }
    }

    fun resetPassword(view: View) {
        if (!flagOnClose) {
            this.textViewResultRestPassword.text = "Дождитесь ответа..."
            buttonChangePassword.isEnabled = false
            userService.resetPassword(
                userResetPasswordModel = UserResetPasswordHttpModel(
                    reset_token = editTextResetToken.text.toString(),
                    password = editTextTextPassword.text.toString()
                )
            ) { code: Int ->
                runOnUiThread {
                    if (code == 200) {
                        this.textViewResultRestPassword.text = "Пароль успешно сброшен"
                    } else {
                        this.textViewResultRestPassword.text = "Ваш код просрочен."
                    }
                    flagOnClose = true
                    buttonChangePassword.text = "Назад"
                    buttonChangePassword.isEnabled = true
                }
            }
        } else {
            finish()
        }
    }
}