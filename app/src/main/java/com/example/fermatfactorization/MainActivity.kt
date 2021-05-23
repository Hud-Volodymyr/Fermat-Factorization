package com.example.fermatfactorization

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.lang.NumberFormatException
import kotlin.math.ceil
import kotlin.math.sqrt

open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val numberView: EditText = findViewById(R.id.number)
        val textView: TextView = findViewById(R.id.result)
        numberView.addTextChangedListener {
            if (it.toString().isBlank()) {
                textView.text = null
                return@addTextChangedListener
            }
            try {
                fermatFactorization(it.toString().toLong(), textView)
            } catch (e: NumberFormatException) {
                Toast.makeText(applicationContext, "Number doesn't match long int format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fermatFactorization(number: Long, textView: TextView) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val res = factorize(number).joinToString(" * ")
                withContext(Dispatchers.Main) {
                    textView.text = "$number = $res"
                }
            } catch (e: IllegalStateException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun factorize(number: Long) : List<Long> {
        if (number <= 0) error("Cannot factorize non-positive numbers")
        val start = System.currentTimeMillis()
        var root = ceil(sqrt(number.toFloat())).toLong()
        if (root * root == number) return listOf(root, root)
        while (root != (number + 1) / 2) {
            if (System.currentTimeMillis() - start < 3000) error("Time limit exceeded")
            val r = root * root - number
            val perfectSqrt = isPerfectSquare(r)
            if (perfectSqrt != -1L) {
                return listOf(root + perfectSqrt, root - perfectSqrt)
            }
            ++root
        }
        error("Cannot factorize $number")
    }

    private fun isPerfectSquare(num: Long) : Long {
        val sqrt = sqrt(num.toFloat())
        if (ceil(sqrt) == sqrt) return sqrt.toLong()
        return -1
    }
}
