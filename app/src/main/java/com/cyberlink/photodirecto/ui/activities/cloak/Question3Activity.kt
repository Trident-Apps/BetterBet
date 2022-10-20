package com.cyberlink.photodirecto.ui.activities.cloak

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.databinding.Q3ActivityBinding
import kotlinx.coroutines.launch

class Question3Activity : AppCompatActivity() {

    private var _binding: Q3ActivityBinding? = null
    private val binding get() = _binding!!
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = Q3ActivityBinding.inflate(layoutInflater)
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
            if (tv == binding.answerTv2) {
                score++
            }
            nextQuestion(score)
        }
    }

    private fun nextQuestion(num: Int) {
        with(Intent(this, Question4Activity::class.java)) {
            this.putExtra(this@Question3Activity.getString(R.string.score), num)
            startActivity(this)
            this@Question3Activity.finish()
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