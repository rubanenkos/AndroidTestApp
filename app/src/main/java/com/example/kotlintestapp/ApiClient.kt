import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import okhttp3.*
import java.io.IOException

class ApiClient {
    private val client = OkHttpClient()

    fun fetchData(context: Context, url: String, callback: (String, Int) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                callback("Request failed: ${e.message}", 0)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = response.body?.string() ?: "Empty response"
                    val statusCode = response.code
                    callback(responseBody, statusCode)
                }
            }
        })
    }

    fun putData(context: Context, url: String, requestBody: RequestBody? = null, callback: (String, Int) -> Unit) {
        val finalRequestBody = requestBody ?: RequestBody.create(null, ByteArray(0))

        val request = Request.Builder()
            .url(url)
            .put(finalRequestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                callback("Request failed: ${e.message}", 0)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = response.body?.string() ?: "Empty response"
                    val statusCode = response.code
                    callback(responseBody, statusCode)
                }
            }
        })
    }


    fun postData(context: Context, url: String, requestBody: RequestBody, callback: (String, Int) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                callback("Request failed: ${e.message}", 0)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = response.body?.string() ?: "Empty response"
                    val statusCode = response.code
                    callback(responseBody, statusCode)
                }
            }
        })
    }
}
