package com.newteo.eplus.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.newteo.eplus.base.BaseActivity
import com.newteo.eplus.base.Echo
import com.newteo.eplus.base.snake
import com.newteo.eplus.databinding.ActivityGuaDetailsBinding

class GuaDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityGuaDetailsBinding
    private var guaDetails: GuaListItem? = null

    companion object {
        fun go(context: Context, data: GuaListItem, ) {
            context.startActivity(Intent(context, GuaDetailsActivity::class.java).run {
                putExtra("guaDetails", data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<GuaListItem>("guaDetails")?.let {
            Echo.d("guaa", it.toString())
            title = "${it.hint}"
            binding.guaPic.setImageResource(it.picId)
            binding.guaDes.text = "卦辞: \n${it.des}"
            // binding.yaoList.text = "爻辞: \n${it.yaoList}"
            binding.details.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, it.yaoList + it.details)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.fab.setOnClickListener {
            "去看六十四卦吗？".snake(it) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}