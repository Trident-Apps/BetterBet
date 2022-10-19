package com.cyberlink.photodirecto.ui.activities.cloack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cyberlink.photodirecto.databinding.CloakActivityBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CloakActivity : AppCompatActivity() {
    private var _binding: CloakActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CloakActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cloakBtn.setOnClickListener {
            lifecycleScope.launch {
                startGame()
                delay(1000L)
            }
        }
    }

    private fun startGame() {
        with(Intent(this, Question1Activity::class.java)) {
            startActivity(this)
            this@CloakActivity.finish()
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