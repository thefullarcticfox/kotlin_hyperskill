package com.thefullarcticfox.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private var billVal = ""
    private var tipPercent = 0.0
    companion object {
        const val EXIT_DELAY = 2000
        var back_pressed = 0L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextInputEditText>(R.id.bill_input).addTextChangedListener(object : TextWatcher {
            var old = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                old = s?.toString() ?: ""
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val new = s?.toString() ?: ""
                billVal = new
                updateTextView()
            }
        })

        findViewById<Slider>(R.id.slider).addOnChangeListener { _, value, _ ->
            tipPercent = value.toDouble()
            updateTextView()
        }
    }

    fun updateTextView() {
        val tmp = if (this.billVal.isEmpty()) ""; else {
            val tip = billVal.toDouble() * tipPercent / 100.0
            "Tip amount: ${"%.2f".format(tip)}"
        }
        findViewById<TextView>(R.id.text_view).text = tmp
    }

    override fun onBackPressed() {
        if (back_pressed + EXIT_DELAY > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
            back_pressed = System.currentTimeMillis()
        }
    }
}
