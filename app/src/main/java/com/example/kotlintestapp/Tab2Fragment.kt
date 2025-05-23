package com.example.kotlintestapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class Tab2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab2, container, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)

        try {
            val jsonString = context?.assets?.open("donor_info.json")?.bufferedReader().use { it?.readText() }
            val gson = Gson()
            val listType = object : TypeToken<DonorInfo>() {}.type
            val donorInfo: DonorInfo = gson.fromJson(jsonString, listType)

            donorInfo.content.forEach { item ->
                when (item.type) {
                    "paragraph" -> {
                        val textView = TextView(context)
                        textView.text = item.text
                        textView.textSize = 18f // Increase paragraph font
                        textView.setPadding(0, 8, 0, 8) // Space above and below
                        linearLayout.addView(textView)
                    }
                    "heading" -> {
                        val textView = TextView(context)
                        textView.text = item.text
                        textView.setPadding(0, 16, 0, 8) // More top padding for headings
                        when (item.level) {
                            2 -> textView.textSize = 20f
                            3 -> textView.textSize = 18f
                        }
                        textView.setTypeface(null, android.graphics.Typeface.BOLD) // Bold headings
                        linearLayout.addView(textView)
                    }
                    "list" -> {
                        item.items?.forEach { listItem ->
                            val textView = TextView(context)
                            textView.text = "• $listItem"
                            textView.textSize = 18f
                            textView.setPadding(16, 4, 0, 4) // Indent list items a bit
                            linearLayout.addView(textView)
                        }
                    }
                    "empty" -> {
                        val emptyView = View(context)
                        emptyView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            32
                        )
                        linearLayout.addView(emptyView)
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return view
    }

    data class DonorInfo(val title: String, val content: List<ContentItem>)
    data class ContentItem(val type: String, val text: String? = null, val level: Int? = null, val items: List<String>? = null)
}