package com.bnkt.f106024.staniterminator

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ApiService {

    private val backgroundWorker = Executors.newSingleThreadExecutor()
    private val uiHandler = Handler(Looper.getMainLooper())

    interface QuoteCallback {
        fun onQuoteReceived(quote: String, author: String)
        fun onQuoteFailed(error: String)
    }

    fun getRandomQuote(callback: QuoteCallback) {
        backgroundWorker.execute {
            try {
                val url = URL("https://zenquotes.io/api/random")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val jsonArray = JSONArray(response)
                val json = jsonArray.getJSONObject(0)
                val quote = json.getString("q")
                val author = json.getString("a")

                uiHandler.post { callback.onQuoteReceived(quote, author) }
            } catch (e: Exception) {
                uiHandler.post { callback.onQuoteFailed("Network error") }
            }
        }
    }
}
