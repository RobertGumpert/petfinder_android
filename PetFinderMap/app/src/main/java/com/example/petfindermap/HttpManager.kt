package com.example.petfindermap

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class HttpManager {
    private val client = OkHttpClient()
    private val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val url = "http://192.168.1.67:5490"

    companion object {
        private var instance: HttpManager? = null
        fun getInstance(): HttpManager {
            if (instance == null) {
                instance = HttpManager()
            }
            return instance!!
        }
    }

    fun query(route: String, postBody: String?, postHeaders: List<Pair<String, String>>, callback: (Int, String)-> Unit) {
        var request = Request.Builder()
            .url(url + route)
        if (postBody != null) {
            request = request.post(postBody.toRequestBody(JSON))
        }
        postHeaders.forEach {
            request.addHeader(it.first, it.second)
        }

        val requestB = request.build()
        client.newCall(requestB).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    callback(response.code, response.body!!.string())
                }
            }
        })
    }
}