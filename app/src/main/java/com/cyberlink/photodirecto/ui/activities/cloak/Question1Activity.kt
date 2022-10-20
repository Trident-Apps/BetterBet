package com.cyberlink.photodirecto.ui.activities.cloak

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.databinding.Q1ActivityBinding
import kotlinx.coroutines.launch

class Question1Activity : AppCompatActivity() {
    private var _binding: Q1ActivityBinding? = null
    private val binding get() = _binding!!
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = Q1ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listOf(
            binding.answerTv1,
            binding.answerTv2,
            binding.answerTv3,
            binding.answerTv4
        ).forEach { tv ->
            tv.setOnClickListener {
                checkAnswer(it as TextView)
            }
        }
    }

    private fun checkAnswer(tv: TextView) {
        lifecycleScope.launch {
            if (tv == binding.answerTv1) {
                score++
            }
            nextQuestion(score)
        }
    }

    private fun nextQuestion(num: Int) {
        with(Intent(this, Question2Activity::class.java)) {
            this.putExtra(this@Question1Activity.getString(R.string.score), num)
            startActivity(this)
            this@Question1Activity.finish()
        }
    }

    override fun onBackPressed() {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}