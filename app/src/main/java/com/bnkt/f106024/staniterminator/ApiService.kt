package com.bnkt.f106024.staniterminator

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

// This class gets motivational quotes from the internet
class ApiService {

    // These help us run internet code in background and update UI on main thread
    private val backgroundWorker = Executors.newSingleThreadExecutor()
    private val uiHandler = Handler(Looper.getMainLooper())

    // Interface to send results back to MainActivity
    interface QuoteCallback {
        fun onQuoteReceived(quote: String, author: String)
        fun onQuoteFailed(error: String)
    }

    fun getRandomQuote(callback: QuoteCallback) {
        // Run internet code in background so app doesn't freeze
        backgroundWorker.execute {
            try {
                // Try first quote website
                val url = URL("https://api.quotable.io/random")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000 // Wait max 5 seconds

                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                // Parse the JSON response
                val json = JSONObject(response)
                val quote = json.getString("content")
                val author = json.getString("author")

                // Send result back to UI thread
                uiHandler.post { callback.onQuoteReceived(quote, author) }

            } catch (e: Exception) {
                // If first website fails, try backup quotes
                tryBackupQuotes(callback)
            }
        }
    }

    private fun tryBackupQuotes(callback: QuoteCallback) {
        try {
            // Try second quote website
            val url = URL("https://zenquotes.io/api/random")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000

            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            // This API returns array format: [{"q":"quote","a":"author"}]
            val cleanResponse = response.replace("[", "").replace("]", "")
            val json = JSONObject(cleanResponse)
            val quote = json.getString("q")
            val author = json.getString("a")

            uiHandler.post { callback.onQuoteReceived(quote, author) }

        } catch (e: Exception) {
            // If internet fails completely, use built-in quotes
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
