package com.cyberlink.photodirecto.ui.activities.cloak

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.databinding.Q5ActivityBinding
import kotlinx.coroutines.launch

class Question5Activity : AppCompatActivity() {

    private var _binding: Q5ActivityBinding? = null
    private val binding get() = _binding!!
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = Q5ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        score = intent.getIntExtra(this.getString(R.string.score), 0)
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
            if (tv == binding.answerTv4) {
                score++
            }
            nextQuestion(score)
        }
    }

    private fun nextQuestion(num: Int) {
        with(Intent(this, ResultActivity::class.java)) {
            this.putExtra(this@Question5Activity.getString(R.string.score), num)
            startActivity(this)
            this@Question5Activity.finish()
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