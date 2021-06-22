package com.rsschool.quiz

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.rsschool.quiz.data.CATEGORIES
import com.rsschool.quiz.data.QuizData
import com.rsschool.quiz.data.category
import com.rsschool.quiz.databinding.ActivityMainBinding
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), QuizFragmentListener, ResultFragmentListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val dropdown: AppCompatSpinner = binding.categoryDropdown
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, CATEGORIES)
        dropdown.adapter = adapter

        dropdown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                if (position != 0) {
                    category = CATEGORIES[position]
                    dropdown.visibility = View.INVISIBLE
                    if (savedInstanceState == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.hostFragment.id, QuizFragment())
                            .commit()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun replaceFragment(reset: Boolean, questionCount: Int?, currentAnswers: MutableMap<Int?, String>?) {
        supportFragmentManager.beginTransaction()
            .replace(binding.hostFragment.id, QuizFragment.newInstance(reset, questionCount, currentAnswers))
            .commit()
    }

    override fun openResultFragment(result: Int, answers: Map<Int?, String>) {
        supportFragmentManager.beginTransaction()
            .replace(binding.hostFragment.id, ResultFragment.newInstance(result, answers))
            .commit()
    }

    override fun close() {
        finish()
        exitProcess(0)
    }

    override fun reset(reset: Boolean) {
        replaceFragment(true)
    }

    override fun share(message: String) {
        val shareIntent = Intent()
        shareIntent.apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Quiz Result")
            putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }
    }
}