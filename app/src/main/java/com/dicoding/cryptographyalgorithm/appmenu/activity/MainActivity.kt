package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.adapter.ListAlgorithmAdapter
import com.dicoding.cryptographyalgorithm.data_class.CryptoData
import com.dicoding.cryptographyalgorithm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ListAlgorithmAdapter.OnItemClickCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAlgorithm: RecyclerView
    private val list = ArrayList<CryptoData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupData()
    }

    private fun setupRecyclerView() {
        rvAlgorithm = binding.rvAlgorithm
        rvAlgorithm.layoutManager = LinearLayoutManager(this)
        val adapter = ListAlgorithmAdapter(list)
        adapter.setOnItemClickCallback(this)
        rvAlgorithm.adapter = adapter
    }

    override fun onItemClicked(data: CryptoData) {
        Log.d("MainActivity", "Data clicked: ${data.name}")

        val intent = when (data.name) {
            "Caesar Cipher" -> Intent(this, CaesarActivity::class.java)
            "Rail Fence Cipher" -> Intent(this, RailFenceActivity::class.java)
            "Stream Cipher (RC4)" -> Intent(this, RC4Activity::class.java)
            "Block Cipher (AES)" -> Intent(this, AESActivity::class.java)
            "Super Enkripsi" -> Intent(this, SuperEncryptionActivity::class.java)
            else -> null
        }
        intent?.let { startActivity(it) }
    }

    private fun setupData() {
        val names = resources.getStringArray(R.array.data_name)
        val descriptions = resources.getStringArray(R.array.data_description)
        val typedArray = resources.obtainTypedArray(R.array.data_photo)

        for (i in names.indices) {
            val photoId = typedArray.getResourceId(i, -1)
            list.add(CryptoData(names[i], descriptions[i], photoId))
        }
        typedArray.recycle() // Jangan lupa untuk membebaskan TypedArray
    }
}