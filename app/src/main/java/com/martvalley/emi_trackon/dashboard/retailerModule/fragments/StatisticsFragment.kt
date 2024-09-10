package com.martvalley.emi_trackon.dashboard.retailerModule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        //(activity as AppCompatActivity).supportActionBar?.hide()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}