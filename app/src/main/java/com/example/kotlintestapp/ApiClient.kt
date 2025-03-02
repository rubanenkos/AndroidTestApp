import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import okhttp3.*
import java.io.IOException

class ApiClient {
    private val client = OkHttpClient()

    fun fetchInventory(context: Context, callback: (String) -> Unit) {
        val url = "https://petstore.swagger.io/v2/store/inventory"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                callback("Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback("Error: ${response.code}")
                        return
                    }

                    val message = "HTTP Code: ${response.code}"
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }

                    val responseBody = response.body?.string() ?: "Empty response"
                    callback(responseBody)
                }
            }
        })
    }
}
