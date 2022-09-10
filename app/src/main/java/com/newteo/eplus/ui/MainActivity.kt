package com.newteo.eplus.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.newteo.eplus.R
import com.newteo.eplus.base.*
import com.newteo.eplus.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private val guaList = ArrayList<GuaListItem>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.categories1)
        }

        binding.navView.setCheckedItem(R.id.gua)
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.gua -> startActivity(Intent(this, MainActivity::class.java))
                R.id.bu -> startActivity(Intent(this, BuActivity::class.java))
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        binding.fab.setOnClickListener {
            "You just click fab button!".snake(it) {
                "You press sure".toast()
            }
        }

        initList()

        binding.guaRec.adapter = GuaRecyclerViewAdaptor(guaList)
        binding.guaRec.layoutManager = GridLayoutManager(this, 3)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
            R.id.search -> "You just click search".toast()
        }
        return true
    }

    private fun initList() {
        val guas = file2Json("guas.json")
        val guasData: List<GuaListItem> = json2List(guas)
        Echo.d("guas", guas.toString())

        for (i in guasData) {
            i.picId = getPic(i.id)
            guaList.add(i)
        }
    }

    private fun getPic(id: Int) = resources.getIdentifier("g$id", "drawable", packageName)

}