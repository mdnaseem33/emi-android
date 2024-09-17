package com.martvalley.emi_trackon.dashboard.retailerModule.user.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.people.retailer.Retailer
import com.martvalley.emi_trackon.dashboard.people.user.MoreOption
import com.martvalley.emi_trackon.dashboard.people.user.MoreOptionFragment
import com.martvalley.emi_trackon.dashboard.people.user.User
import com.martvalley.emi_trackon.dashboard.people.user.UserAdapter
import com.martvalley.emi_trackon.dashboard.people.user.UserViewActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.user.UserData
import com.martvalley.emi_trackon.dashboard.retailerModule.user.adapter.UserListAdapter
import com.martvalley.emi_trackon.dashboard.settings.controls.ControlsActivity
import com.martvalley.emi_trackon.databinding.FragmentUserListBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.logd
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserListFragment : Fragment() {
    private var _binding:FragmentUserListBinding?=null
    private val binding get() = _binding!!
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var adapter: UserAdapter
    val list = ArrayList<User.Customer>()

    val vieww =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("statuschanged", false) == true) {
                    withNetwork { callApi() }
                }
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       _binding = FragmentUserListBinding.inflate(inflater,container,false)

//        userListAdapter = UserListAdapter(requireContext())
//
        binding.userListRecyler.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
//
//        userListAdapter.diffUtilList.submitList(UserData.loadData())
//        adapter = UserAdapter(list, this) { data, action, pos ->
//            when (action) {
////                "action" -> callChangeStatusApi(data.id.toString(), data.status.toString(), pos)
//                "more" -> {
//                    MoreOptionFragment().apply {
//                        innterface = object : MoreOption {
//                            override fun view() {
//                                vieww.launch(
//                                    Intent(
//                                        requireContext(),
//                                        UserViewActivity::class.java
//                                    ).putExtra("id", data.id)
//                                )
//                                dismiss()
//                            }
//
//                            override fun control() {
//                            }
//
//                            override fun delete() {
//                                withNetwork { callDeleteApi(data.id.toString(), pos) }
//                                dismiss()
//                            }
//
//                        }
//                    }.show(supportFragmentManager, "")
//                }
//                "control" -> {
//                    startActivity(
//                        Intent(
//                            this,
//                            ControlsActivity::class.java
//                        ).putExtra("id", data.id)
//                    )
//                }
//            }
//
//        }
        adapter = UserAdapter(list, requireContext()) { data, action, pos ->
            when (action) {
//                "action" -> callChangeStatusApi(data.id.toString(), data.status.toString(), pos)
                "more" -> {
                    MoreOptionFragment().apply {
                        innterface = object : MoreOption {
                            override fun view() {
                                vieww.launch(
                                    Intent(
                                        requireContext(),
                                        UserViewActivity::class.java
                                    ).putExtra("id", data.id)
                                )
                                dismiss()
                            }

                            override fun control() {
                            }

                            override fun delete() {
                                withNetwork { callDeleteApi(data.id.toString(), pos) }
                                dismiss()
                            }

                        }
                    }.show(requireActivity().supportFragmentManager, "")
                }
                "control" -> {
                    startActivity(
                        Intent(
                            requireContext(),
                            ControlsActivity::class.java
                        ).putExtra("id", data.id)
                    )
                }
            }

        }
        binding.userListRecyler.adapter = adapter

//        binding.clearSearch.setOnClickListener {
//            binding.searchEt.text.clear()
//            adapter.mList = list
//            adapter.notifyDataSetChanged()
//        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterList(p0.toString().lowercase())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        withNetwork { callApi() }

        return binding.root
    }

    private fun filterList(key: String) {
        val filter_list = ArrayList<User.Customer>()
        list.forEach {
            if (it.name.lowercase().contains(key)) {
                filter_list.add(it)
            }else if (it.id.toString().lowercase().contains(key)) {
                filter_list.add(it)
            }
        }
        adapter.mList = filter_list
        adapter.notifyDataSetChanged()
    }

    private fun callApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getCustomerListApi()
        call.enqueue(object : Callback<User.UserListResponse> {
            override fun onResponse(
                call: Call<User.UserListResponse>,
                response: Response<User.UserListResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it.customer)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        showToast("Oops! There is a problem connecting to the server. Please try again")
                    }
                }
            }

            override fun onFailure(
                call: Call<User.UserListResponse>, t: Throwable
            ) {
                binding.pb.hide()
                showToast("Oops! There is a problem connecting to the server. Please try again")
            }

        })

    }

    private fun callDeleteApi(id: String, pos: Int) {
        binding.pb.show()
        val request = Retailer.DeleteRequest(id)
        val call = RetrofitInstance.apiService.userDeleteApi(request)
        call.enqueue(object : Callback<Retailer.StatusChangeResponse> {
            override fun onResponse(
                call: Call<Retailer.StatusChangeResponse>,
                response: Response<Retailer.StatusChangeResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.removeAt(pos)
                            adapter.notifyItemRemoved(pos)
                        }
                    }
                    else -> {
                        showToast("Oops! There is a problem connecting to the server. Please try again")
                    }
                }
            }

            override fun onFailure(call: Call<Retailer.StatusChangeResponse>, t: Throwable) {
                binding.pb.hide()
                showToast("Oops! There is a problem connecting to the server. Please try again")
            }

        })

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}