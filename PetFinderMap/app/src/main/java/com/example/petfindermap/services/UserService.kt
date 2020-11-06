package com.example.petfindermap.services

import com.example.petfindermap.models.UserAuthTokens
import com.example.petfindermap.models.UserModel

class UserService {

    var user: UserModel? = null
    var authTokens: UserAuthTokens? = null

    companion object {

        var instance: UserService? = null
            get() {
                if (field == null) {
                    instance = UserService()
                    field!!.readConfigs()
                }
                return field
            }
    }


    fun signUp(Telephone: String, Password: String, Email: String, Name: String) {
        if (authTokens != null) {
            throw java.lang.Exception("User is sign in.")
        }
        if (Telephone.isEmpty() || Password.isEmpty() || Email.isEmpty() || Name.isEmpty()) {
            throw java.lang.Exception("Non valid data.")
        }
        user = UserModel(
            UserID = 0,
            Telephone = Telephone,
            Name = Name,
            Email = Email,
            Password = Password
        )
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
        authTokens = UserAuthTokens(
            Access = "access",
            Refresh = "refresh"
        )
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

    fun updateAccessToken(): UserAuthTokens {
        if (authTokens != null) {
            if (authTokens!!.Refresh == "refresh") {
                authTokens!!.Access = "access"
                return authTokens!!
            } else {
                throw java.lang.Exception("Non valid refresh token. ")
            }
        } else {
            throw java.lang.Exception("User isn't sign in.")
        }
    }


    fun readConfigs() {
        user = null
        authTokens = null
    }
}