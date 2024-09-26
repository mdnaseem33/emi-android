package com.emitrackon.emi_trackon.dashboard.retailerModule.key.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.databinding.FragmentUdharBinding


class UdharFragment : Fragment() {
    private var _binding:FragmentUdharBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUdharBinding.inflate(inflater,container,false)

        binding.backTextView.setOnClickListener {
            activity?.finish()
        }
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_udharFragment_to_udaar2Fragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}