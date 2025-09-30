package com.bnkt.f106024.staniterminator

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
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
                val url = URL("https://api.quotable.io/random")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000

                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val json = JSONObject(response)
                val quote = json.getString("content")
                val author = json.getString("author")

                uiHandler.post { callback.onQuoteReceived(quote, author) }

            } catch (e: Exception) {
                tryBackupQuotes(callback)
            }
        }
    }

    private fun tryBackupQuotes(callback: QuoteCallback) {
        try {
            val url = URL("https://zenquotes.io/api/random")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000

            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val cleanResponse = response.replace("[", "").replace("]", "")
            val json = JSONObject(cleanResponse)
            val quote = json.getString("q")
            val author = json.getString("a")

            uiHandler.post { callback.onQuoteReceived(quote, author) }

        } catch (e: Exception) {
            val localQuotes = listOf(
                "You are stronger than you think!" to "Fitness App",
                "Every workout is progress!" to "Fitness App",
                "Don't give up, you've got this!" to "Fitness App",
                "Your body can do it!" to "Fitness App",
                "Success starts with discipline!" to "Fitness App"
            )
            val randomQuote = localQuotes.random()
            uiHandler.post { callback.onQuoteReceived(randomQuote.first, randomQuote.second) }
        }
    }
}
