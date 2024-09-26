package com.emitrackon.emi_trackon.dashboard.settings.controls.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.dashboard.settings.controls.Control
import com.emitrackon.emi_trackon.dashboard.settings.controls.ControlsActivity
import com.emitrackon.emi_trackon.databinding.FragmentReminderBinding
import com.emitrackon.emi_trackon.utils.hide
import com.emitrackon.emi_trackon.utils.show
import com.emitrackon.emi_trackon.utils.showApiErrorToast
import com.emitrackon.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReminderFragment : Fragment() {
    private val binding by lazy { FragmentReminderBinding.inflate(layoutInflater) }
    private lateinit var adapter: ReminderAdapter
    val list = ArrayList<Control.Record>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReminderAdapter(list, requireContext()) { data, pos ->
            UpdateActivity() {
                withNetwork { callApi() }
            }.apply {
                user_id = data.id
            }.show(parentFragmentManager, "")
        }

        binding.rv.adapter = adapter

        withNetwork { callApi() }
    }

    private fun callApi() {
        binding.pb.show()
        val call =
            RetrofitInstance.apiService.getEmiTransactionListApi((requireActivity() as ControlsActivity).id.toString())
        call.enqueue(object : Callback<Control.EmiTransactionListResponse> {
            override fun onResponse(
                call: Call<Control.EmiTransactionListResponse>,
                response: Response<Control.EmiTransactionListResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it.record)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Control.EmiTransactionListResponse>, t: Throwable) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })

    }


}