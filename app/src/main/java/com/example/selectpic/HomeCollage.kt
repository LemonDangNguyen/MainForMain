package com.example.selectpic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.selectpic.databinding.ActivityHomeCollageBinding

class HomeCollage : BaseActivity() {
   private val binding by lazy { ActivityHomeCollageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(binding.root)  
    }
}