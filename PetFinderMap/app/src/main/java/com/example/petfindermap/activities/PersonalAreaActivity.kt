package com.example.petfindermap.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.HttpManager
import com.example.petfindermap.R
import com.example.petfindermap.models.UserUpdateHttpModel
import com.example.petfindermap.services.UserService
import kotlinx.android.synthetic.main.personal_area.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL

class PersonalAreaActivity : AppCompatActivity() {

    private var userService: UserService = UserService.getInstance()

    private var flagOpenMenu: Boolean = false
    private lateinit var viewMenu: View
    private lateinit var buttonMenu: Button
    private lateinit var buttonEditUser: Button
    private lateinit var buttonSignOut: Button
    private lateinit var buttonGetResetToken: Button
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonChangeAvatar: Button
    private lateinit var imageView: ImageView
    val FLAG_ON_SELECT_FILE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_area)
        editTextEmail.setText(userService.user?.email)
        editTextUserName.setText(userService.user?.name)
        editTextPhone.setText(userService.user?.telephone)
        super.getSupportActionBar()?.hide()

        viewMenu = findViewById(R.id.menu_slide)
        viewMenu.visibility = View.INVISIBLE

        buttonMenu = findViewById(R.id.buttonMenu2)
        buttonEditUser = findViewById(R.id.buttonEditUserData)
        buttonSignOut = findViewById(R.id.buttonSignOut)
        buttonGetResetToken = findViewById(R.id.buttonGetResetToken)
        buttonChangeAvatar = findViewById(R.id.buttonChangeAvatar)
        buttonChangePassword = findViewById(R.id.buttonChangePassword)

        imageView = findViewById(R.id.imageViewAvatar)

        runBlocking {
            val downloaderAvatarFile = GlobalScope.launch {
                val updateUrl = userService.user?.avatar_url?.replace(
                    oldValue = "127.0.0.1",
                    newValue = HttpManager.getInstance().IP_ADDR_DEVICE
                )
                val url = URL(updateUrl)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                imageView.setImageBitmap(bmp)
            }
            downloaderAvatarFile.join()
        }

        userService.checkAuthorizeAndUpdateToken()
        { code: Int ->
            if (code != 200) {
                val signIn = Intent(this, SignInActivity::class.java)
                startActivity(signIn)
            }
        }
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
            R.id.textViewMenuMap -> {
                val maps = Intent(this, MapsActivity::class.java)
                startActivity(maps)
            }
            R.id.textViewMenuMessages -> {
                val dialogs = Intent(this, DialogsActivity::class.java)
                startActivity(dialogs)
            }
            R.id.textViewMenuMyAds -> {
                val myAds = Intent(this, MyAdsActivity::class.java)
                startActivity(myAds)
            }
        }
    }

    fun editUserDataButton(view: View) {
        val updateUserData = UserUpdateHttpModel(
            user_id = 0,
            telephone = this.editTextPhone.text.toString(),
            name = this.editTextUserName.text.toString(),
            email = this.editTextEmail.text.toString()
        )
        userService.updateUser(
            updateUserData
        ) { code: Int, body: String ->
            runOnUiThread {
                if (code == 200) {
                    this.textViewResultRestPassword.text = "Ваши данные обновлены"
                } else {
                    this.textViewResultRestPassword.text = "Попробуйте еще раз."
                }
            }
        }
    }

    fun signOutButton(view: View) {
        userService.signOut()
        { code: Int ->
            runOnUiThread {
                if (code == 200) {
                    val signIn = Intent(this, SignInActivity::class.java)
                    startActivity(signIn)
                } else {
                    this.textViewResultRestPassword.text = "Попробуйте еще раз."
                }
            }
        }
    }

    fun getResetTokenButton(view: View) {
        userService.getResetPasswordToken { code: Int ->
            runOnUiThread {
                if (code == 200) {
                    this.textViewGetResetTokenInfo.text =
                        "Вам пришёл код на почту, можете сменить пароль."
                } else {
                    this.textViewGetResetTokenInfo.text = "Попробуйте еще раз."
                }
            }
        }
    }

    fun changePasswordButton(view: View) {
        val changePasswordActivity = Intent(this, PasswordResetActivity::class.java)
        startActivity(changePasswordActivity)
    }

    fun changeAvatarButton(view: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(photoPickerIntent, "Select Picture"),
            FLAG_ON_SELECT_FILE
        )
    }

    private fun getFilePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        when (requestCode) {
            FLAG_ON_SELECT_FILE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val imageUri: Uri = imageReturnedIntent!!.data as Uri
                        val inputStream: InputStream =
                            contentResolver.openInputStream(imageUri) as InputStream
                        val selectedImageBitmap: Bitmap =
                            BitmapFactory.decodeStream(inputStream) as Bitmap
                        imageView.setImageBitmap(selectedImageBitmap)
                        val filePath: String = getFilePath(imageUri) ?: return
                        userService.updateAvatar(
                            filePath = filePath
                        ) { code: Int ->
                            if (code != 200) {
                                runOnUiThread {
                                    val toMain = Intent(this, MapsActivity::class.java)
                                    startActivity(toMain)
                                }
                            }
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}