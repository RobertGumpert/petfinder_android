package com.example.petfindermap.services

import com.example.petfindermap.HttpManager
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.User
import com.example.petfindermap.models.*
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserService {
    var appDatabase : AppDatabase = AppDatabase.getInstance()
    var httpManager : HttpManager = HttpManager.getInstance()
    var gson : Gson = Gson()

    var user: UserModel? = null

    companion object {
        private var instance: UserService? = null
        fun getInstance(): UserService {
            if (instance == null){
                instance = UserService()
            }
            return instance!!
        }
    }

    fun signUp(telephone: String, name: String, email: String, password: String, callback: (Int, String)-> Unit) {
        if (user != null) {
            callback(1, "Пользователь уже авторизован")
            return
        }
        if (telephone.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
            callback(1, "Заполните все поля")
            return
        }
        val userSignUpData = UserSignUpHttpModel(telephone, name, email, password)
        val postBody = gson.toJson(userSignUpData)
        httpManager.query("au","/api/user/register", postBody, listOf()) { code: Int, body: String ->
            callback(code, body)
        }
    }

    fun signIn(telephone: String, password: String, callback: (Int, String)-> Unit) {
        if (user != null) {
            callback(1, "Пользователь уже авторизован")
            return
        }
        if (telephone.isEmpty() || password.isEmpty()) {
            callback(1, "Заполните все поля")
            return
        }
        val userSignInData = UserSignInHttpModel(telephone,  password)
        val postBody = gson.toJson(userSignInData)
        httpManager.query("au","/api/user/authorized", postBody, listOf()) { code: Int, body: String ->
            if (code == 200) {
                val info = gson.fromJson(body, UserSignInAnsHttpModel::class.java)
                info.user.access_token = info.token
                createOrUpdateUserData(info.user) {
                    callback(code, body)
                }
            }
            else {
                callback(code, body)
            }
        }
    }

    fun createOrUpdateUserData(info: UserModel, callback: ()-> Unit) {
        user = UserModel(
            user_id = info.user_id,
            telephone = info.telephone,
            email = info.email,
            name = info.name,
            avatar_url = info.avatar_url,
            access_token = info.access_token
        )
        runBlocking {
            val writer = GlobalScope.launch {
                val user = appDatabase.userDao().getById(info.user_id.toLong())
                if (user == null) {
                    val users = appDatabase.userDao().getAll()
                    if (users.isNotEmpty()) {
                        users.forEach {
                            appDatabase.userDao().delete(it)
                        }
                    }
                    appDatabase.userDao().insert(
                        User(
                            userId = info.user_id,
                            telephone = info.telephone,
                            userName = info.name,
                            email = info.email,
                            avatarUrl = info.avatar_url,
                            accessToken = info.access_token
                        )
                    )
                }
                else {
                    appDatabase.userDao().update(
                        User(
                            userId = info.user_id,
                            telephone = info.telephone,
                            userName = info.name,
                            email = info.email,
                            avatarUrl = info.avatar_url,
                            accessToken = info.access_token
                        )
                    )
                }
            }
            writer.join()
            callback()
        }
    }

    fun userDataLoad(callback: ()-> Unit) {
        runBlocking {
            val writer = GlobalScope.launch {
                val users = appDatabase.userDao().getAll()
                if (users.isNotEmpty()) {
                    user = UserModel(
                        user_id = users[0].userId,
                        telephone = users[0].telephone,
                        email = users[0].email,
                        name = users[0].userName,
                        avatar_url = users[0].avatarUrl,
                        access_token = users[0].accessToken
                    )
                    httpManager.query(
                        "au",
                        "/api/user/access",
                        null,
                        listOf(Pair("Authorization", "Bearer " + user!!.access_token))
                    ) { code: Int, body: String ->
                        if (code == 200) {
                            val info = gson.fromJson(body, UserModel::class.java)
                            info.access_token = user!!.access_token
                            createOrUpdateUserData(info) {
                                callback()
                            }
                        }
                        else {
                            val info = gson.fromJson(body, ErrorHttpModel::class.java)
                            if(info.error == "Non valid access token. ") {
                                refreshAccessToken() {
                                    callback()
                                }
                            }
                            else {
                                user = null
                                callback()
                            }
                        }
                    }
                }
                else {
                    callback()
                }
            }
            writer.join()
        }
    }

    fun refreshAccessToken(callback: ()-> Unit) {
        httpManager.query("au","/api/user/access/update", null, listOf(Pair("Authorization", "Bearer " + user!!.access_token))) { code: Int, body: String ->
            if (code == 200) {
                val info = gson.fromJson(body, UserSignInAnsHttpModel::class.java)
                info.user.access_token = info.token
                createOrUpdateUserData(info.user) {
                    callback()
                }
            }
            else{
                user = null
                callback()
            }
        }
    }
}