package com.bnkt.f106024.staniterminator
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

/**
 * Network service for fetching a random motivational quote
 * from ZenQuotes API using a background thread.
 */
class ApiService {

    private val uiHandler = Handler(Looper.getMainLooper())

//  handling API results.
    interface QuoteCallback {
        fun onQuoteReceived(quote: String, author: String)
        fun onQuoteFailed(error: String)
    }

    /**
     * Fetches a random quote from the API.
     * Runs on a background thread and posts results to the main thread.
     */
    fun getRandomQuote(callback: QuoteCallback) {
        Thread {
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
            } catch (_: Exception) {
                uiHandler.post { callback.onQuoteFailed("Network error") }
            }
        }.start()
    }
}
