package com.martvalley.emi_trackon.dashboard.retailerModule.user.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.FragmentUserDetailHomeBinding


class UserDetailHomeFragment : Fragment() {
   private var _binding:FragmentUserDetailHomeBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserDetailHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}