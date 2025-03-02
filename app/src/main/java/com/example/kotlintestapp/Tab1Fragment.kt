import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kotlintestapp.R

class Tab1Fragment : Fragment() {
    private lateinit var textView: TextView
    private val apiClient = ApiClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab1, container, false)
        textView = view.findViewById(R.id.textViewTab1)

        // При активации таба выполняем GET-запрос
        fetchData()

        return view
    }

    private fun fetchData() {
//        val url = "http://192.168.1.106:5000/user/3"
        val url = "http://10.0.2.2:5000/user/3"
//        val url = "http://127.0.0.1:5000/user/3"
        apiClient.fetchData(requireContext(), url) { response ->
            activity?.runOnUiThread {
                textView.text = response  // Отобразим данные в TextView
            }
        }
    }
}
