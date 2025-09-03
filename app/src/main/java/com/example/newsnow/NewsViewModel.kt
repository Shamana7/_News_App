package com.example.newsnow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NewsViewModel : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    init {
        fetchNewsWithHttpUrlConnection()
    }
    fun fetchNewsWithHttpUrlConnection(category: String = "General") {
        Thread {
            try {
                val url = URL("https://newsapi.org/v2/top-headlines?country=us&category=${category.lowercase()}&apiKey=${Constant.apiKey}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = stream.readText()
                    stream.close()

                    // ✅ Parse JSON into List<Article>
                    val gson = Gson()
                    val newsResponse = gson.fromJson(response, NewsResponse::class.java)
                    val articlesList = newsResponse.articles

                    // ✅ Post parsed list to LiveData
                    _articles.postValue(articlesList)
                } else {
                    Log.e("NewsAPI Error", "HTTP error code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("NewsAPI Exception", e.localizedMessage ?: "Unknown error", e)
            }
        }.start()
    }


    fun fetchEverythingWithQuery(query: String) {
        Thread {            //So the app doesn't freeze while fetching data from the internet.
            try {
                val url =
                    URL("https://newsapi.org/v2/everything?q=${query}&apiKey=${Constant.apiKey}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = stream.readText()
                    stream.close()

                    val gson = Gson()
                    val newsResponse = gson.fromJson(response, NewsResponse::class.java)
                    val articlesList = newsResponse.articles

                    _articles.postValue(articlesList)
                } else {
                    Log.e("NewsAPI Error", "HTTP error code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("NewsAPI Exception", e.localizedMessage ?: "Unknown error", e)
            }
        }.start()
    }
}


