package com.cyberlink.photodirecto.ui.activities.cloack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.databinding.ResultActivityBinding

class ResultActivity : AppCompatActivity() {
    private var _binding: ResultActivityBinding? = null
    private val binding get() = _binding!!
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ResultActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        score = intent.getIntExtra(this@ResultActivity.getString(R.string.score), 0)
        showResult(score)
    }

    @SuppressLint("SetTextI18n")
    private fun showResult(int: Int) {
        binding.resultTv1.text = "Your score is: $int"

        when (int) {
            10 -> {
                binding.resultTv2.text = "Your knowledge of sport is amazing!"
            }
            1 -> {
                binding.resultTv2.text =
                    "Nice try, but you need to learn much more about sport to get higher score!"
            }
            else -> {
                binding.resultTv2.text = "Don't give up, you've done well, but try to learn more!"
            }
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