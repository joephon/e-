package com.newteo.eplus.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.newteo.eplus.base.BaseActivity
import com.newteo.eplus.databinding.ActivityBuBinding
import com.newteo.eplus.databinding.ActivityMainBinding

class BuActivity : BaseActivity() {
    private lateinit var binding: ActivityBuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}