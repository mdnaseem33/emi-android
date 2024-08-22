package com.martvalley.emi_trackon.dashboard.settings.controls.device

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.DashboardActivity
import com.martvalley.emi_trackon.dashboard.settings.controls.Control
import com.martvalley.emi_trackon.dashboard.settings.controls.ControlsActivity
import com.martvalley.emi_trackon.dashboard.settings.controls.device.popup.LocationData
import com.martvalley.emi_trackon.dashboard.settings.controls.device.popup.LocationLisRecylerview
import com.martvalley.emi_trackon.databinding.FragmentDeviceBinding
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.convertISOTimeToDate
import com.martvalley.emi_trackon.utils.getCurrentDate
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.isGone
import com.martvalley.emi_trackon.utils.loadImage
import com.martvalley.emi_trackon.utils.logd
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.withNetwork
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeviceFragment : Fragment() {
    val binding by lazy { FragmentDeviceBinding.inflate(layoutInflater) }
    var selectedAction = Constants.BASIC
    private lateinit var popUpDetails: PopUpDetails
    lateinit var adapter: BasicAdapter
    private var locList = ArrayList<LocationData>()
    private lateinit var locationLisRecylerview: LocationLisRecylerview

    var list1 = ArrayList<Control.DeviceActionOld>()
    var list2 = ArrayList<Control.DeviceActionOld>()
    var list3 = ArrayList<Control.DeviceActionOld>()
    var gotTheRefreshedDate = false
    var swd: Control.DeviceActionOld= Control.DeviceActionOld(
        "swd", "Set wallpaper Disable", true,  "list1"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        popUpDetails = PopUpDetails(requireContext())
       // initListener();


        return binding.root
    }

    private fun initListener() {

      //   ("lock_task").setOnPreferenceClickListener(this);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationLisRecylerview = LocationLisRecylerview(requireContext(), listOf())


        callLocationApi()

        list2.add(Control.DeviceActionOld(sm = "", display = "Audio", value = false, "list2"))
        list2.add(Control.DeviceActionOld(sm = "", display = "Wallpaper", value = false, "list2"))
        list2.add(Control.DeviceActionOld(sm = "", display = "Whatsapp", value = false, "list2"))
        list2.add(Control.DeviceActionOld(sm = "", display = "WA Business", value = false, "list2"))
        list2.add(Control.DeviceActionOld(sm = "", display = "Facebook", value = false, "list2"))
        list2.add(Control.DeviceActionOld(sm = "", display = "Instagram", value = false, "list2"))
        list2.add(Control.DeviceActionOld(sm = "", display = "Youtube", value = false, "list2"))

        list3.add(Control.DeviceActionOld(sm = "", display = "Location", value = false, "list3"))
        list3.add(Control.DeviceActionOld(sm = "", display = "Call List", value = false, "list3"))
        list3.add(Control.DeviceActionOld(sm = "", display = "Mobile No.", value = false, "list3"))
        list3.add(Control.DeviceActionOld(sm = "", display = "Soft Reset", value = false, "list1"))
        list3.add(
            Control.DeviceActionOld(
                sm = "fr",
                display = "Factory Reset",
                value = false,
                "list1"
            )
        )
        list3.add(
            Control.DeviceActionOld(
                sm = "uftd",
                display = "File Transfer",
                value = false,
                "list1"
            )
        )
        list3.add(Control.DeviceActionOld(sm = "", display = "Debugging", value = true, "list3"))


        binding.getDetail.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    requireContext(), ViewUsersDetailActivity::class.java
                ).putExtra("id", (requireActivity() as ControlsActivity).id.toString())
            )
        }

        adapter = BasicAdapter(list1, requireContext()) { pos, data, action ->
            when (data) {
                "Wallpaper" -> {
                    withNetwork { callSetWallpaperApi() }
                }
////////////////



                "Audio" -> {
                    withNetwork { callSetAudioApi() }
                }

                "Call List" -> {
                    withNetwork { callRequestDeviceInfoApi("call") }
                    showCallListPopUp()
                }

                "Location" -> {
                    withNetwork { callRequestDeviceInfoApi("location") }
                    showLocationPopUp()
                }

//                "Lock" -> {
//                    withNetwork { callRequestDevi
//                    ceInfoApi("lock") }
//                }

                "facebook" -> {
                    withNetwork { callRequestDeviceInfoApi("facebook") }
                }

                "Mobile No." -> {
                    showPhonePopUp()
                }

                "list1" -> {

                    withNetwork { callSubmitActionApi(false) }
                }

                //////////////////////////



            }
        }
        binding.rv.adapter = adapter

        setAdapter(list1)

        binding.refresh.setOnClickListener {
            withNetwork { callGetUserApi() }
            // binding.userDetail.syncValueTv.text = getCurrentDate()

        }

        binding.userimg.setOnClickListener {
            ("I'm clicked").logd()
            binding.closeEnlargedImg.show()
            binding.photoView.show()
        }

        binding.closeEnlargedImg.setOnClickListener {
            it.hide()
            binding.photoView.hide()
        }


        withNetwork { callGetUserApi() }

        binding.surrenderbtn.setOnClickListener {
            //popUpDetails.show()

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to Surrender?")

            builder.setPositiveButton("Yes") { _, _ ->
                withNetwork { surrenderCustomer() }
            }

            builder.setNegativeButton("Cancel") { _, _ ->
                // Call onCancelled() when the user clicks "Cancel"

            }

            val dialog = builder.create()
            dialog.show()


        }

        binding.basicBtn.backgroundTintList =
            ColorStateList.valueOf(requireContext().getColor(R.color.blue))
        binding.basicBtn.setTextColor(requireContext().getColor(R.color.white))

        binding.applicationBtn.backgroundTintList = null
        binding.applicationBtn.setTextColor(requireContext().getColor(R.color.blue))

        binding.advanceBtn.backgroundTintList = null
        binding.advanceBtn.setTextColor(requireContext().getColor(R.color.blue))

        binding.basicBtn.setOnClickListener {
            selectedAction = Constants.BASIC
            (requireActivity() as ControlsActivity).selectedList = "list1"
            Log.d("tab", (requireActivity() as ControlsActivity).selectedList)
            setAdapter(list1)

            binding.basicBtn.backgroundTintList =
                ColorStateList.valueOf(requireContext().getColor(R.color.blue))
            binding.basicBtn.setTextColor(requireContext().getColor(R.color.white))

            binding.applicationBtn.backgroundTintList = null
            binding.applicationBtn.setTextColor(requireContext().getColor(R.color.blue))

            binding.advanceBtn.backgroundTintList = null
            binding.advanceBtn.setTextColor(requireContext().getColor(R.color.blue))
            withNetwork { callGetUserApi() }
        }

        binding.applicationBtn.setOnClickListener {
            selectedAction = Constants.APPLICATION
            (requireActivity() as ControlsActivity).selectedList = "list2"
            Log.d("tab", (requireActivity() as ControlsActivity).selectedList)
            setAdapter(list2)
            binding.applicationBtn.backgroundTintList =
                ColorStateList.valueOf(requireContext().getColor(R.color.blue))
            binding.applicationBtn.setTextColor(requireContext().getColor(R.color.white))

            binding.basicBtn.backgroundTintList = null
            binding.basicBtn.setTextColor(requireContext().getColor(R.color.blue))

            binding.advanceBtn.backgroundTintList = null
            binding.advanceBtn.setTextColor(requireContext().getColor(R.color.blue))
            withNetwork { callGetUserApi()
            }
        }

        binding.advanceBtn.setOnClickListener {
            withNetwork { callGetUserApi() }
            selectedAction = Constants.ADVANCE
            (requireActivity() as ControlsActivity).selectedList = "list3"
            Log.d("tab", (requireActivity() as ControlsActivity).selectedList)
            setAdapter(list3)
            binding.advanceBtn.backgroundTintList =
                ColorStateList.valueOf(requireContext().getColor(R.color.blue))
            binding.advanceBtn.setTextColor(requireContext().getColor(R.color.white))

            binding.basicBtn.backgroundTintList = null
            binding.basicBtn.setTextColor(requireContext().getColor(R.color.blue))

            binding.applicationBtn.backgroundTintList = null
            binding.applicationBtn.setTextColor(requireContext().getColor(R.color.blue))

        }

    }

    private fun showLocationPopUp() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.location_listpopup, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.close_btn)
        val progress = dialogView.findViewById<TextView>(R.id.no_data)

        val recylerview = dialogView.findViewById<RecyclerView>(R.id.location_list_rv)

        if (locList.isEmpty()) {
            progress.show()
        } else {
            progress.isGone()
        }


        recylerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationLisRecylerview
        }


        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        val dialog = dialogBuilder.create()
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showCallListPopUp() {

        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.call_list_popup, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.close_btn)

        callApi(dialogView)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        val dialog = dialogBuilder.create()
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showPhonePopUp() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.phone_dialog_pop_up, null)
        val textView = dialogView.findViewById<TextView>(R.id.dialogTextView)
        textView.text = SharedPref(requireContext()).getValueString("UserPhoneNum")

        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.setOnDismissListener {
            withNetwork { callRequestDeviceInfoApi("mobile") }
        }
    }








    private fun callSubmitActionApi(wall: Boolean) {

        val jsonData = convertToJson(list1, list3)
        if(wall){
            swd.value= !swd.value;

        }

        val valueJson = JSONObject()
        valueJson.put("display", swd.display)
        valueJson.put("value", swd.value)
        jsonData.put(swd.sm, valueJson)
        val gson = Gson().fromJson(jsonData.toString(), Control.Data::class.java)
        Log.d("currentJson", jsonData.toString())
        binding.pb.show()
        val call = RetrofitInstance.apiService.submitActionApi(
            Control.ActionUpdateRequest(
                (requireActivity() as ControlsActivity).id.toString(), gson
            )
        )
        call.enqueue(object : Callback<Control.ActionUpdateResponse> {
            override fun onResponse(
                call: Call<Control.ActionUpdateResponse>,
                response: Response<Control.ActionUpdateResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            showToast(it.message)
                        }
                    }

                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(
                call: Call<Control.ActionUpdateResponse>, t: Throwable
            ) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })


    }

    private fun callSetWallpaperApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.sendWallpaperApi(
            DeviceBasics.SendRequest((requireActivity() as ControlsActivity).id.toString())
        )
        call.enqueue(object : Callback<DeviceBasics.DeviceInfoResponse> {
            override fun onResponse(
                call: Call<DeviceBasics.DeviceInfoResponse>,
                response: Response<DeviceBasics.DeviceInfoResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            showToast("Wallpaper Updated Succussfully")
                            response.logd("response")
                            /////////////
//                            if (response.message().contains("updated")){
//                                list2.add(Control.DeviceActionOld(sm = "", display = "Wallpaper", value = true, "list2"))
//                            } else {
//                                list2.add(Control.DeviceActionOld(sm = "", display = "Wallpaper", value = false, "list2"))
//                            }
//                            setAdapter(list2)
                            /////////////////
                            callSubmitActionApi(true)
                        }
                    }

                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(
                call: Call<DeviceBasics.DeviceInfoResponse>, t: Throwable
            ) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })
    }



    private fun callSetAudioApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.sendAudioApi(
            DeviceBasics.SendRequest((requireActivity() as ControlsActivity).id.toString())
        )
        call.enqueue(object : Callback<DeviceBasics.DeviceInfoResponse> {
            override fun onResponse(
                call: Call<DeviceBasics.DeviceInfoResponse>,
                response: Response<DeviceBasics.DeviceInfoResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            Toast.makeText(
                                context,
                                "Audio Playing in the User device",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(
                call: Call<DeviceBasics.DeviceInfoResponse>, t: Throwable
            ) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })
    }

    private fun callRequestDeviceInfoApi(type: String) {
        binding.pb.show()
        val call = RetrofitInstance.apiService.requestDeviceInfoApi(
            DeviceBasics.DeviceInfoRequest(
                (requireActivity() as ControlsActivity).id.toString(), type
            )
        )
        call.enqueue(object : Callback<DeviceBasics.DeviceInfoResponse> {
            override fun onResponse(
                call: Call<DeviceBasics.DeviceInfoResponse>,
                response: Response<DeviceBasics.DeviceInfoResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            showToast(it.message)
                        }
                    }

                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(
                call: Call<DeviceBasics.DeviceInfoResponse>, t: Throwable
            ) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })
    }

    private fun setAdapter(list: ArrayList<Control.DeviceActionOld>) {
        adapter.mList = list
        adapter.notifyDataSetChanged()
    }

    fun setUserData(data: Control.Customer) {
        var model: String? = ""
        if (data.device_detail != null) {
            model = getModelFromJson(data.device_detail)
        }

        SharedPref(requireContext()).save("UserPhoneNum", data.phone)

        binding.userDetail.apply {
            nameValueTv.text = data.name
            pNoValueTv.text = data.phone
            if (model != null) {
                modelValueTv.text = model
            }


            data.imei1?.let {
                imei1Tv.text = "IMEI 1 : " + it
            }

            data.imei2?.let {
                imei2Tv.text = "IMEI 2 : " + it
            }




            date.text = data.created_at.convertISOTimeToDate()
            if (gotTheRefreshedDate) {
                syncValueTv.text = getCurrentDate()
            } else {
                syncValueTv.text = data.last_sync?.split(" ")?.get(0)
                gotTheRefreshedDate = true
            }



            if (!data.image.isNullOrEmpty()) {
                binding.userimg.loadImage(Constants.BASEURL + "storage/" + data.image)
                binding.photoView.loadImage(Constants.BASEURL + "storage/" + data.image)
            } else {
                return
            }
        }



    }

    fun getModelFromJson(jsonString: String): String? {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.optString("Model")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun callGetUserApi() {
        binding.pb.show()
        val call =
            RetrofitInstance.apiService.getCustomerbyIdApi((requireActivity() as ControlsActivity).id.toString())
        call.enqueue(object : Callback<Control.GetCustomerResponse> {
            override fun onResponse(
                call: Call<Control.GetCustomerResponse>,
                response: Response<Control.GetCustomerResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            (requireActivity() as ControlsActivity).cust_data = it
                            setUserData(it.customer)
                            setList1Data(it.action)
                        }
                    }

                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Control.GetCustomerResponse>, t: Throwable) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }
        })
    }

    private fun surrenderCustomer() {
        binding.pb.show()
        val call =
            RetrofitInstance.apiService.surrenderCustomer((requireActivity() as ControlsActivity).id.toString())
        call.enqueue(object : Callback<Control.SurrenderResponse> {
            override fun onResponse(
                call: Call<Control.SurrenderResponse>,
                response: Response<Control.SurrenderResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        startActivity(Intent(requireContext(), DashboardActivity::class.java))
                        activity?.finish()

                    }

                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Control.SurrenderResponse>, t: Throwable) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }
        })
    }


    private fun setList1Data(action: Control.Action) {
        list1.clear()

        val json = JSONObject(Gson().toJson(action))
        val iter: Iterator<String> = json.keys()
        json.logd("actionData123")
        while (iter.hasNext()) {
            val key = iter.next()
            try {
                val value = json.get(key) as JSONObject
                if (key == "fr" || key == "uftd") {
                    list3.forEachIndexed { i, it ->
                        if (it.sm == key) {
                            list3[i].value = value.getBoolean("value")


                        }
                    }
                } else if( key == "swd" ) {
                    swd=
                        Control.DeviceActionOld(
                            key, value.getString("display"), value.getBoolean("value"), "list1"
                        )

                    list2[1].value= value.getBoolean("value")

                }  else  {
                    list1.add(
                        Control.DeviceActionOld(
                            key, value.getString("display"), value.getBoolean("value"), "list1"
                        )
                    )
                }

            } catch (e: JSONException) {
                // Something went wrong!
            }
        }

        adapter.notifyDataSetChanged()

        selectedAction = Constants.BASIC
        (activity as ControlsActivity).selectedList = "list1"

    }

    fun convertToJson(
        list: ArrayList<Control.DeviceActionOld>,
        list3: ArrayList<Control.DeviceActionOld>
    ): JSONObject {
        val jsonObject = JSONObject()
        list.forEach {
            val valueJson = JSONObject()
            valueJson.put("display", it.display)
            valueJson.put("value", it.value)
            jsonObject.put(it.sm, valueJson)
        }

        list3.forEach {

            if (it.sm == "fr" || it.sm == "uftd") {
                val valueJson = JSONObject()
                valueJson.put("display", it.display)

                valueJson.put("value", it.value)
                jsonObject.put(it.sm, valueJson)
            }

        }

        return jsonObject
    }

    private fun callApi(dialogView: View) {
        val progress = dialogView.findViewById<ProgressBar>(R.id.progress)
        val errorMessage = dialogView.findViewById<TextView>(R.id.title_txt)
        val callValue = dialogView.findViewById<TextView>(R.id.call_value)
        progress.show()
        val call = RetrofitInstance.apiService.getCustomerDeviceLastDataApi(
            (requireActivity() as ControlsActivity).id.toString()
        )
        call.enqueue(object : Callback<DeviceBasics.GetDeviceInfo> {
            override fun onResponse(
                call: Call<DeviceBasics.GetDeviceInfo>,
                response: Response<DeviceBasics.GetDeviceInfo>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        if (response.body()?.data == null) {
                            progress.show()
                            errorMessage.setText(R.string.no_data_found)
                        } else {
                            progress.hide()
                            response.body()?.let {
                                errorMessage.setText(R.string.data_found)
                                Log.d("ReceentCh", it.data.location.toString())

//                                    .let {
//                                    locList = it.replace("{", "").replace("}", "")
//                                }
//                                it.data.mobile?.let {
//                                    binding.mobileValue.text =
//                                        it.replace("null", "---").removeSuffix(",")
//                                }
                                it.data.call_list?.let {
                                    callValue.text = it
                                }


                            }
                        }
                    }

                    else -> {
                        progress.show()
                        // requireContext().showApiErrorToast()
                    }
                }
            }

            override fun onFailure(
                call: Call<DeviceBasics.GetDeviceInfo>, t: Throwable
            ) {

            }

        })
    }

    private fun callLocationApi() {
        val call = RetrofitInstance.apiService.getCustomerDeviceLastDataApi(
            (requireActivity() as ControlsActivity).id.toString()
        )
        call.enqueue(object : Callback<DeviceBasics.GetDeviceInfo> {
            override fun onResponse(
                call: Call<DeviceBasics.GetDeviceInfo>,
                response: Response<DeviceBasics.GetDeviceInfo>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {

                        if (response.body()?.data == null) {

                        } else {

                            response.body()?.let {
                                Log.d("ReceentCh", response.toString())
                                Log.d("ReceentCh", it.data.toString())
                                locList.clear()
                                for (i in it.data.location) {
                                    Log.d("ReceentCh", i.toString())
                                    try {
                                        val jsonObject = JSONObject(i.coordinates)
                                        val latitude = jsonObject.getDouble("lat")
                                        val longitude = jsonObject.getDouble("long")

                                        locList.addAll(
                                            listOf(
                                                LocationData(
                                                    latitude,
                                                    longitude
                                                )
                                            )
                                        )
                                        locationLisRecylerview =
                                            LocationLisRecylerview(requireContext(), locList)
                                        //locationLisRecylerview.mlist = locList

                                        // Do something with latitude and longitude values
                                        Log.d("ReceentCh", latitude.toString())
                                        Log.d("ReceentCh", longitude.toString())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                //Log.d("ReceentCh", it.data.location.coordinates)
//                                    .let {
//                                    locList = it.replace("{", "").replace("}", "")
//                                }
//                                it.data.mobile?.let {
//                                    binding.mobileValue.text =
//                                        it.replace("null", "---").removeSuffix(",")
//                                }
                                it.data.call_list?.let {

                                }


//                                it.data.updated_at?.let {
//                                    binding.time.text =
//                                        "Last updated at : ${it.convertISOTimeToDateTime()}"
//                                }

//                                if (it.data.location.toString().contains("lat")) {
//                                    binding.viewOnMap.show()
//                                } else binding.viewOnMap.hide()

                            }
                        }
                    }

                    else -> {
                        //progress.show()
                        // requireContext().showApiErrorToast()
                        Log.d("ReceentCh", response.message())
                        Log.d("ReceentCh", response.toString())
                    }
                }
            }

            override fun onFailure(
                call: Call<DeviceBasics.GetDeviceInfo>, t: Throwable
            ) {
                Log.d("ReceentCh", t.message.toString())
                Log.d("ReceentCh", t.toString())
            }

        })
    }
}



