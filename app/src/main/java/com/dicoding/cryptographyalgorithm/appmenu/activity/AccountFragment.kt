package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        displayUserData()

        binding.btnAccountLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun displayUserData() {
        val email = sharedPreferences.getString("current_email", "Unknown Email")
        val username = sharedPreferences.getString("${email}_username", "Unknown User")

        binding.tvUsername.text = username
        binding.tvEmail.text = email
    }

    private fun logoutUser() {
        sharedPreferences.edit().putBoolean("is_logged_in", false).putString("current_email", null).apply()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}