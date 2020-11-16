package com.example.petfindermap.services

import android.content.Context
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.User
import com.example.petfindermap.models.UserAuthTokens
import com.example.petfindermap.models.UserModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserService {

    var user: UserModel? = null
    var authTokens: UserAuthTokens? = null
    lateinit var appDatabase : AppDatabase
    lateinit var context: Context

    companion object {
        var instance: UserService? = null
        fun getInstance(context: Context): UserService? {
            if (instance == null){
                instance = UserService()
                instance!!.context = context
                instance!!.readConfigs()
            }
            return instance
        }
    }


    fun signUp(Telephone: String, Password: String, Email: String, Name: String) {
        if (authTokens != null) {
            throw java.lang.Exception("User is sign in.")
        }
        if (Telephone.isEmpty() || Password.isEmpty() || Email.isEmpty() || Name.isEmpty()) {
            throw java.lang.Exception("Non valid data.")
        }
        runBlocking {
            val writer = GlobalScope.launch {
                appDatabase.userDao().insert(
                    User(
                        userId = 0,
                        telephone = Telephone,
                        userName = Name,
                        email = Email,
                        avatarUrl = "",
                        accessToken = ""
                    )
                )
                user = UserModel(
                    UserID = 0,
                    Telephone = Telephone,
                    Name = Name,
                    Email = Email,
                    Password = Password
                )
            }
            writer.join()
        }
    }

    fun signIn(Telephone: String, Password: String) {
        if (authTokens != null) {
            throw java.lang.Exception("User is sign in.")
        }
        if (Telephone.isEmpty() || Password.isEmpty()) {
            throw java.lang.Exception("Non valid data.")
        }
        if (user?.Password != Password || user?.Telephone != Telephone) {
            throw java.lang.Exception("Non valid data.")
        }
        runBlocking {
            val writer = GlobalScope.launch {
                val list = appDatabase.userDao().getAll()
                if (list.isEmpty()) {
                    user = null
                    authTokens = null
                } else {
                    val userEntity = list[0]
                    user = UserModel(
                        UserID = userEntity.userId,
                        Telephone = userEntity.telephone,
                        Name = userEntity.userName,
                        Email = userEntity.email,
                        Password = ""
                    )
                    authTokens = UserAuthTokens(
                        Access = "access",
                        Refresh = "refresh"
                    )
                }
            }
            writer.join()
        }
    }

    fun isAuthorization(): UserAuthTokens {
        if (authTokens != null) {
            if (authTokens!!.Access == "access") {
                return authTokens!!
            } else {
                throw java.lang.Exception("Non valid access token. ")
            }
        } else {
            throw java.lang.Exception("User isn't sign in.")
        }
    }

    fun updateAccessToken() {
        if (authTokens != null) {
            runBlocking {
                val writer = GlobalScope.launch {
                    val list = appDatabase.userDao().getAll()
                    if (list.isEmpty()) {
                        user = null
                        authTokens = null
                    } else {
                        val userEntity = list[0]
                        userEntity.accessToken = "access"
                        appDatabase.userDao().update(userEntity)
                        authTokens = UserAuthTokens(
                            Access = "access",
                            Refresh = "refresh"
                        )
                    }
                }
                writer.join()
            }
        } else {
            throw java.lang.Exception("User isn't sign in.")
        }
    }


    fun readConfigs() {
        runBlocking {
            val reader = GlobalScope.launch {
                appDatabase = AppDatabase.getInstance(context)!!
                val list = appDatabase.userDao().getAll()
                if (list.isEmpty()) {
                    user = null
                    authTokens = null
                } else {
                    val userEntity = list[0]
                    user = UserModel(
                        UserID = userEntity.userId,
                        Telephone = userEntity.telephone,
                        Name = userEntity.userName,
                        Email = userEntity.email,
                        Password = ""
                    )
                    authTokens = if (userEntity.accessToken.equals("")) {
                        null
                    } else {
                        userEntity.accessToken?.let {
                            UserAuthTokens(
                                Access = it,
                                Refresh = ""
                            )
                        }
                    }
                }
            }
            reader.join()
        }
    }
}