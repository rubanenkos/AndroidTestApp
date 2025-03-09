import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlintestapp.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class Tab1Fragment : Fragment() {

    private lateinit var textViewEmail: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewDetails: TextView
    private lateinit var baseUrl: String


    private val apiClient = ApiClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        baseUrl = getString(R.string.base_url)
        val view = inflater.inflate(R.layout.fragment_tab1, container, false)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewName = view.findViewById(R.id.textViewName)
        textViewDetails = view.findViewById(R.id.textViewDetails)

        val email = activity?.intent?.extras?.getString("email")

        if (email != null) {
//            textViewEmail.text = "Email: $email"
            Log.d("Tab1Fragment", "Received email: $email")
        } else {
            Log.e("Tab1Fragment", "Email is null")
        }

        fetchUserCoreRole(email)

        fetchUserData()
        fetchDonorData()

        return view
    }

    private fun fetchUserCoreRole(email: String?) {
        if (email == null) {
            Log.e("Tab1Fragment", "Email is null, cannot fetch user role.")
            Toast.makeText(requireContext(), "Email is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
        val url = "$baseUrl/user/email?email=$encodedEmail"
        apiClient.fetchData(requireContext(), url) { response, statusCode ->
            activity?.runOnUiThread {
                if (statusCode == 200) {
                    parseUserRoleResponse(response)
                } else {
                    Toast.makeText(requireContext(),
                        "Failed to fetch user role: $statusCode", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseUserRoleResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val roleId = jsonObject.optString("role_id")
            val userId = jsonObject.optString("user_id")

            if (roleId.isNotEmpty()) {
                Log.d("Tab1Fragment", "User role: $roleId")
                Toast.makeText(requireContext(), "User role: $roleId", Toast.LENGTH_SHORT).show()
            } else {
                Log.w("Tab1Fragment", "User role not found in response.")
                Toast.makeText(requireContext(), "User role not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Tab1Fragment", "Error parsing user role response: ${e.message}")
            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData() {
        val url = "$baseUrl/user/3"

        apiClient.fetchData(requireContext(), url) { response, statusCode ->
            activity?.runOnUiThread {
                if (statusCode == 200) {
                    parseUserResponse(response)
                } else {
                    Toast.makeText(requireContext(),
                        "Failed to fetch user data: $statusCode", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun fetchDonorData() {
        val url = "$baseUrl/donor/3"
        apiClient.fetchData(requireContext(), url) { response, statusCode ->
            activity?.runOnUiThread {
                if (statusCode == 200) {
                    parseDonorResponse(response)
                } else {
                    Toast.makeText(requireContext(),
                        "Failed to fetch donor data: $statusCode", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun parseUserResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val email = jsonObject.optString("email", "No email")
            val name = jsonObject.optString("name", "No name")

            activity?.runOnUiThread {
                textViewEmail.text = "Email: $email"
                textViewName.text = "Name: $name"
            }
        } catch (e: Exception) {
            activity?.runOnUiThread {
                textViewEmail.text = "Error parsing data"
                textViewName.text = ""
            }
        }
    }

    private fun parseDonorResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val sex = jsonObject.optString("sex", "No sex")
            val dateOfBirth = jsonObject.optString("date_of_birth", "No date of birth")
            val age = jsonObject.optString("age", "No age")
            val bloodGroup = jsonObject.optString("blood_group", "No blood group")
            val rhesusFactor = jsonObject.optString("rhesus_factor", "No rhesus factor")
            val contactNumber = jsonObject.optString("contact_number", "No contact number")

            val formattedDate = formatDate(dateOfBirth)

            val details = """
            Sex: $sex
            Date of Birth: $formattedDate
            Age: $age
            Blood Group: $bloodGroup  $rhesusFactor
            
            Contact Number: $contactNumber
        """.trimIndent()

            textViewDetails.text = details
        } catch (e: Exception) {
            textViewDetails.text = "Error parsing data"
        }
    }


    private fun formatDate(dateString: String): String {
        return try {
            Log.d("DateDebug", "Raw date: $dateString")
            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: return "Invalid date")
        } catch (e: Exception) {
            "Invalid date"
        }
    }

}
