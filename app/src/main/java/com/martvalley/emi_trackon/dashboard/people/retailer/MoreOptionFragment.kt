package com.martvalley.emi_trackon.dashboard.people.retailer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.FragmentMoreOptionBinding
import com.martvalley.emi_trackon.databinding.FragmentRetailerListBinding
import com.martvalley.emi_trackon.utils.showToast

interface MoreOption {
    fun edit()
    fun view()
    //    fun control()
    fun delete()
    fun credit()
    fun debit()
}

class MoreOptionFragment : BottomSheetDialogFragment() {
    private val binding by lazy { FragmentMoreOptionBinding.inflate(layoutInflater) }
    var innterface: MoreOption? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.CustomBottomSheetDialogTheme
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editPostTextView.setOnClickListener {
            innterface?.edit()
        }
//        binding.deleteTextView.setOnClickListener {
//            innterface?.delete()
//        }
        binding.viewTextView.setOnClickListener {
            innterface?.view()
        }
        binding.creditTextView.setOnClickListener {
            innterface?.credit()
        }
        binding.debitTextView.setOnClickListener {
            innterface?.debit()
        }
    }
}

