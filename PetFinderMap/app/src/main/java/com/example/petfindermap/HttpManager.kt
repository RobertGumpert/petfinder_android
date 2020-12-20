package com.example.petfindermap

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


class HttpManager {
    private val client = OkHttpClient()
    private val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val url: Map<String, String> = mapOf(
        "au" to "http://192.168.1.65:5490",
        "ad" to "http://192.168.1.65:5492",
        "di" to "http://192.168.1.65:5493"
    )

    companion object {
        private var instance: HttpManager? = null
        fun getInstance(): HttpManager {
            if (instance == null) {
                instance = HttpManager()
            }
            return instance!!
        }
    }

    fun query(
        serviceName: String,
        route: String,
        postBody: String?,
        postHeaders: List<Pair<String, String>>,
        callback: (Int, String) -> Unit
    ) {
        var request = Request.Builder()
            .url(url[serviceName] + route)
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

    fun queryFormData(
        serviceName: String,
        route: String,
        postBody: String,
        fileName: String?,
        fileUri: String,
        postHeaders: List<Pair<String, String>>,
        callback: (Int, String) -> Unit
    ) {
        var request = Request.Builder()
            .url(url[serviceName] + route)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("json", postBody)
            .addFormDataPart("file", fileName,
                File(fileUri).asRequestBody("application/octet-stream".toMediaTypeOrNull()))
            .build()
        request = request.post(requestBody)
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