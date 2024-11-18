package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.adapter.ListAlgorithmAdapter
import com.dicoding.cryptographyalgorithm.data_class.CryptoData
import com.dicoding.cryptographyalgorithm.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), ListAlgorithmAdapter.OnItemClickCallback {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var rvAlgorithm: RecyclerView
    private val list = ArrayList<CryptoData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment_home.xml
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()
        setupData()
    }

    override fun onStart() {
        super.onStart()
        // Cek apakah user sudah login
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            // Jika tidak login, langsung arahkan ke LoginActivity
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()  // Menutup MainActivity
        }
    }

    private fun setupRecyclerView() {
        rvAlgorithm = binding.rvAlgorithm
        rvAlgorithm.layoutManager = LinearLayoutManager(requireContext())

        // Menghubungkan adapter dan data ke RecyclerView
        val adapter = ListAlgorithmAdapter(list)
        adapter.setOnItemClickCallback(this)  // Set callback
        rvAlgorithm.adapter = adapter
    }

    override fun onItemClicked(data: CryptoData) {
        Log.d("HomeFragment", "Data clicked: ${data.name}")

        val intent = when (data.name) {
            "Caesar Cipher"         -> Intent(requireActivity(), CaesarActivity::class.java)
            "Rail Fence Cipher"     -> Intent(requireActivity(), RailFenceActivity::class.java)
            "Stream Cipher (RC4)"   -> Intent(requireActivity(), RC4Activity::class.java)
            "Block Cipher (AES)"    -> Intent(requireActivity(), AESActivity::class.java)
            "Super Enkripsi"        -> Intent(requireActivity(), SuperEncryptionActivity::class.java)
            "Steganography"         -> Intent(requireActivity(), SteganographyActivity::class.java)
            "File Encryption"       -> Intent(requireActivity(), FileEncryptionActivity::class.java)
            "Steganography Test"    -> Intent(requireActivity(), SteganographyTestActivity::class.java)
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


