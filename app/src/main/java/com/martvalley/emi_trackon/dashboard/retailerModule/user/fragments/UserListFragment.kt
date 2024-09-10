package com.martvalley.emi_trackon.dashboard.retailerModule.user.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.retailerModule.user.UserData
import com.martvalley.emi_trackon.dashboard.retailerModule.user.adapter.UserListAdapter
import com.martvalley.emi_trackon.databinding.FragmentUserListBinding


class UserListFragment : Fragment() {
    private var _binding:FragmentUserListBinding?=null
    private val binding get() = _binding!!
    private lateinit var userListAdapter: UserListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       _binding = FragmentUserListBinding.inflate(inflater,container,false)

        userListAdapter = UserListAdapter(requireContext())

        binding.userListRecyler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userListAdapter
        }

        userListAdapter.diffUtilList.submitList(UserData.loadData())

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}