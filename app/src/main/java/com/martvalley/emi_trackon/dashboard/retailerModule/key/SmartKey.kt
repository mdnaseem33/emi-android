package com.martvalley.emi_trackon.dashboard.retailerModule.key

import SearchableSpinnerAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.home.Dashboard
import com.martvalley.emi_trackon.dashboard.people.user.UserQrActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.key.adapter.SearchableFinanceAdapter
import com.martvalley.emi_trackon.dashboard.retailerModule.key.adapter.SearchableModelAdapter
import com.martvalley.emi_trackon.dashboard.retailerModule.key.fragments.CameraFragment
import com.martvalley.emi_trackon.dashboard.retailerModule.key.fragments.onImageCaptureListener
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.Bank
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.Brand
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.CreateCustomerData
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.Model
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.RequestSmartKey
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.SmartKeyModel
import com.martvalley.emi_trackon.databinding.ActivitySmartKeyBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.logd
import com.martvalley.emi_trackon.utils.toBase64StringBitmap
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.Calendar

class SmartKey : AppCompatActivity(), OnBarcodeScannedListener, onImageCaptureListener {
    private val binding by lazy { ActivitySmartKeyBinding.inflate(layoutInflater) }
    private var step = 0
    private var createCustomerData : CreateCustomerData? = null
    private var dialog: AlertDialog? = null
    // Track selected item
    var selectedItem: Brand? = null
    var selectedModel: Model? = null
    var selectedBank: Bank? = null
    var selectedFrequency: Int = 0
    var selectedNumberOfInstallment: Int =0
    val installmentType = arrayOf("Select Installment frequency", "Daily", "Weekly","Monthly")
    val numberOfInstallment = arrayOf("Select Number of Installment", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    private lateinit var searchableSpinnerAdapter: SearchableSpinnerAdapter
    private lateinit var searchableFinanceAdapter: SearchableFinanceAdapter
    private lateinit var searchableModelAdapter: SearchableModelAdapter

    private var signatureImage: Boolean = false
    // Start MainFragment
    val fragmentManager: FragmentManager = supportFragmentManager
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uri ->
            try {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment != null) {
                    supportFragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            processImages(uri)
            viewModel.selected_image_id = null
        }
    }
    private lateinit var viewModel: SmartKeyModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SmartKeyModel::class.java)
        binding.splashKeyScreen.visibility = android.view.View.VISIBLE
        binding.mainView.visibility = android.view.View.GONE
        showNextStep(0);
        withNetwork { getCreateData() }
        binding.nextButton.setOnClickListener {
            showNextStep(step + 1)
        }
        binding.keyDes.text = intent.getStringExtra("sub_title")
        binding.keyName.text = intent.getStringExtra("title")
        binding.keyNameTitle.text = intent.getStringExtra("title")
        if(intent.getStringExtra("title") == "Home Appliance"){
            binding.applianceType.visibility = View.VISIBLE
            binding.serialNumberEdit.visibility = View.VISIBLE
        }else{
            binding.applianceType.visibility = View.GONE
            binding.serialNumberEdit.visibility = View.GONE
        }
        binding.skipButton.setOnClickListener {
            if(step > 0){
                showNextStep(step - 1)
            }
        }

        binding.scanButton.setOnClickListener{
            // Start MainFragment
            val fragmentManager: FragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ScannerFragment())
                .addToBackStack(null) // Optional: allows user to go back
                .commit()
        }

        binding.clearSign.setOnClickListener{
            binding.signaturePad.clear()
        }

        binding.Referencecheckbox.setOnCheckedChangeListener { comp, bool ->
            if (bool) {
                binding.referenceNameEditText.visibility = View.VISIBLE
                binding.referenceMobileEditText.visibility = View.VISIBLE
            } else {
                binding.referenceNameEditText.visibility = View.GONE
                binding.referenceMobileEditText.visibility = View.GONE
            }
        }

        binding.selectBrand.setOnTouchListener{ v, _ ->
            showSearchableDialog()
            true
        }

        binding.modelNoEditText.setOnTouchListener{ v, _ ->
            showSearchableModelDialog()
            true
        }

        binding.selectBank.setOnTouchListener{ v, _ ->
            showSearchableBankDialog()
            true
        }

        binding.backTextView.setOnClickListener {
            finish()
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioColletEmi) {
                binding.emiSection.visibility = View.VISIBLE
            } else {
                binding.emiSection.visibility = View.GONE
            }
        }

        setUpSpinners();
        binding.datepicker.setOnClickListener {
            openDatePicker()
        }

        binding.photoText.setOnClickListener {
            viewModel.selected_image_id = "photoText"
            showChooseImageSourceDialog()
        }
        binding.idFrontText.setOnClickListener {
            viewModel.selected_image_id = "idFrontText"
            showChooseImageSourceDialog()
        }
        binding.productText.setOnClickListener {
            viewModel.selected_image_id = "productImage"
            showChooseImageSourceDialog()
        }

        binding.idbackText.setOnClickListener {
            viewModel.selected_image_id = "idbackText"
            showChooseImageSourceDialog()
        }
        binding.refFrontText.setOnClickListener {
            viewModel.selected_image_id = "refFrontText"
            showChooseImageSourceDialog()
        }
        binding.refIdBackText.setOnClickListener {
            viewModel.selected_image_id = "refIdBackText"
            showChooseImageSourceDialog()
        }

        binding.signText.setOnClickListener {
            showNextStep(0)
        }
        binding.loanAmounttEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                calculateEmi()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                // Event triggered when the pad is touched
            }

            override fun onSigned() {
                binding.signText.text = "Updated"
                binding.signChecked.setImageResource(R.drawable.checked_green)
                signatureImage= true
            }

            override fun onClear() {
                binding.signText.text = "Add"
                binding.signChecked.setImageResource(R.drawable.checked_grey)
                signatureImage= false
            }
        })
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {

    }

    private fun showChooseImageSourceDialog() {

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CameraFragment())
            .addToBackStack(null) // Optional: allows user to go back
            .commit()
        pickImage.launch("image/*")

        return;
    }

    fun compressImageFromUriAndGetBase64( imageUri: Uri, callback: (String?) -> Unit) {
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .apply(RequestOptions().override(512, 384)) // Resize the image as needed
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Compress the Bitmap to ByteArray
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream) // Quality (0-100)
                    val byteArray = byteArrayOutputStream.toByteArray()

                    // Encode ByteArray to Base64
                    val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                    // Pass the Base64 string to the callback
                    callback(base64String)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Toast.makeText(this@SmartKey, "Image loading failed", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun processImages(selectedImageUri: Uri?){
        when(viewModel.selected_image_id){
            "photoText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        viewModel.customerImage = ("data:image/png;base64,$media")
                        binding.photoChecked.setImageResource(R.drawable.checked_green)
                        binding.photoText.text = "Update"
                    }
                }
            }
            "idFrontText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        viewModel.customer_id_front = ("data:image/png;base64,$media")
                        binding.idChecked.setImageResource(R.drawable.checked_green)
                        binding.idFrontText.text = "Update"
                    }
                }
            }
            "productImage" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        viewModel.product_photo = ("data:image/png;base64,$media")
                        binding.productChecked.setImageResource(R.drawable.checked_green)
                        binding.productText.text = "Update"
                    }
                }
            }
            "idbackText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        viewModel.customer_id_back = ("data:image/png;base64,$media")
                        binding.idBackChecked.setImageResource(R.drawable.checked_green)
                        binding.idbackText.text = "Update"
                    }
                }
            }
            "refFrontText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        viewModel.reference_id_front = ("data:image/png;base64,$media")
                        binding.referChecked.setImageResource(R.drawable.checked_green)
                        binding.refFrontText.text = "Update"
                    }
                }
            }
            "refIdBackText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        viewModel.reference_id_back = ("data:image/png;base64,$media")
                        binding.referBackChecked.setImageResource(R.drawable.checked_green)
                        binding.refIdBackText.text = "Update"
                    }
                }
            }

        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        val minYear = calendar.get(Calendar.YEAR)
        val minMonth = calendar.get(Calendar.MONTH)
        val minDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                Log.d("Selecteddata",selectedDate)
                binding.datepicker.text = selectedDate
            },
            minYear, minMonth, minDay
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun calculateEmi(){
        if(selectedNumberOfInstallment ==0){
            binding.totalEmi.text = "Total Loan Amount: ₹0.00"
            return;
        }

        if(binding.loanAmounttEditText.text.toString() == "" || binding.loanAmounttEditText.text.toString() == "0"){
            binding.totalEmi.text = "Total Loan Amount: ₹0.00"
            return
        }
        val loanAmount = binding.loanAmounttEditText.text.toString().toDouble() * selectedNumberOfInstallment
        binding.totalEmi.text = "Total Loan Amount: ₹${String.format("%.2f", loanAmount)}"
    }

    private fun setUpSpinners() {
        val adapter =
            ArrayAdapter(this,R.layout.spinner_layout, installmentType)

        binding.installmentFreqBtn.adapter = adapter

        binding.installmentFreqBtn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                binding.installmentFreqBtn.setSelection(postion);
                selectedFrequency = postion
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        val adapterInstall =
            ArrayAdapter(this,R.layout.select_number_layout, numberOfInstallment)
        binding.numberOfInstallmentsBtn.adapter = adapterInstall

        binding.numberOfInstallmentsBtn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                binding.numberOfInstallmentsBtn.setSelection(postion);
                selectedNumberOfInstallment = postion
                calculateEmi()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun showSearchableDialog() {
        // Check if a dialog is already showing
        if (dialog?.isShowing == true) {
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.spinner_dropdown, null)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)
        val searchEditText = dialogView.findViewById<EditText>(R.id.search_edit_text)

        listView.adapter = searchableSpinnerAdapter

        // Implement TextWatcher correctly
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the adapter
                searchableSpinnerAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val data = listView.getItemAtPosition(position) as? Brand
            if (data != null && data.id !=0){
                // Set the selected item when an item is clicked
                selectedItem = listView.getItemAtPosition(position) as? Brand

                binding.selectBrand.setSelection(position)
                val modelList = if(selectedItem != null && selectedItem!!.models == null){
                    arrayListOf<Model>()
                }else{
                     selectedItem!!.models as ArrayList<Model>
                }

                modelList.add(0, Model("","", 0, "", "Select Model", "", ""))

                searchableModelAdapter = SearchableModelAdapter(this@SmartKey, modelList)
                binding.modelNoEditText.adapter = searchableModelAdapter
                binding.modelNoEditText.setSelection(0)
                // Dismiss the dialog
                dialog?.dismiss()
                dialog = null
            }

        }
        dialog = MaterialAlertDialogBuilder(this@SmartKey)
            .setTitle("Select Brand")
            .setView(dialogView)
            .create()

        dialog?.show()
    }

    private fun showSearchableModelDialog() {
        // Check if a dialog is already showing
        if (dialog?.isShowing == true) {
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.spinner_dropdown, null)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)
        val searchEditText = dialogView.findViewById<EditText>(R.id.search_edit_text)

        listView.adapter = searchableModelAdapter

        // Implement TextWatcher correctly
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the adapter
                searchableModelAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val data = listView.getItemAtPosition(position) as? Model
            if (data != null && data.id !=0){
                // Set the selected item when an item is clicked
                selectedModel = listView.getItemAtPosition(position) as? Model
                binding.modelNoEditText.setSelection(position)
                // Dismiss the dialog
                dialog?.dismiss()
                dialog = null
            }

        }
        dialog = MaterialAlertDialogBuilder(this@SmartKey)
            .setTitle("Select Model")
            .setView(dialogView)
            .create()

        dialog?.show()
    }

    private fun showSearchableBankDialog() {
        // Check if a dialog is already showing
        if (dialog?.isShowing == true) {
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.spinner_dropdown, null)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)
        val searchEditText = dialogView.findViewById<EditText>(R.id.search_edit_text)

        listView.adapter = searchableFinanceAdapter

        // Implement TextWatcher correctly
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the adapter
                searchableFinanceAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->

                // Set the selected item when an item is clicked
                val data = listView.getItemAtPosition(position) as? Bank
                if ( data != null && data.id !=0){
                    selectedBank = listView.getItemAtPosition(position) as? Bank
                    binding.selectBank.setSelection(position)
                    // Dismiss the dialog
                    dialog?.dismiss()
                    dialog = null
                }

        }
        dialog = MaterialAlertDialogBuilder(this@SmartKey)
            .setTitle("Select Finance")
            .setView(dialogView)
            .create()

        dialog?.show()
    }

    private fun getCreateData(){
        var is_appliance = 'N'
        var is_mobile = 'Y'
        if(intent.getStringExtra("title") == "Home Appliance"){
            is_appliance = 'Y'
            is_mobile = 'N'
        }
        val call = RetrofitInstance.apiService.getCustomerCreateData(is_mobile, is_appliance, null)
        call.enqueue(object : Callback<CreateCustomerData> {
            override fun onResponse(
                p0: Call<CreateCustomerData>,
                p1: Response<CreateCustomerData>
            ) {
                createCustomerData = p1.body()
                Handler(Looper.getMainLooper()).postDelayed(1000) {
                    binding.splashKeyScreen.visibility = android.view.View.GONE
                    binding.mainView.visibility = android.view.View.VISIBLE
                }

                if(createCustomerData!!.brands != null){
                    var brandList = createCustomerData!!.brands as ArrayList<Brand>
                    brandList.add(0, Brand("",0, "", "Select Brand", "", null))

                    searchableSpinnerAdapter = SearchableSpinnerAdapter(this@SmartKey, brandList)
                    binding.selectBrand.adapter = searchableSpinnerAdapter
                    binding.selectBrand.setSelection(0)

                }

                if(createCustomerData!!.banks != null){
                    var bankList = createCustomerData!!.banks as ArrayList<Bank>
                    bankList.add(0, Bank("", 0,"", "Select Bank", ""))
                    searchableFinanceAdapter = SearchableFinanceAdapter(this@SmartKey, bankList)
                    searchableSpinnerAdapter.filter.filter("")
                    binding.selectBank.adapter = searchableFinanceAdapter
                    binding.selectBank.setSelection(0)

                }

                binding.loanNumEditText.setText(createCustomerData!!.loan_id)
            }

            override fun onFailure(p0: Call<CreateCustomerData>, p1: Throwable) {
                Toast.makeText(this@SmartKey, "Unable to fetch data", Toast.LENGTH_SHORT)
            }

        })
    }

    private fun getSignature(): String{
        try {
            return "data:image/png;base64," + binding.signaturePad.getSignatureBitmap().toBase64StringBitmap()
        }catch (e: Exception){
            e.printStackTrace()
        }

        return "";
    }
    private fun showNextStep(currentStep: Int) {
        if(currentStep < 4){
            step = currentStep
        }
        showLoading()
        when (currentStep) {
            0 -> {
                binding.keyStep1.visibility = android.view.View.VISIBLE
                binding.keyStep2.visibility = android.view.View.GONE
                binding.keyStep3.visibility = android.view.View.GONE
                binding.keyStep4.visibility = android.view.View.GONE
                binding.skipButton.visibility = android.view.View.INVISIBLE
                binding.nextButton.text = "Next"
                binding.cardView.visibility = android.view.View.VISIBLE
                hideLoading()
            }
            1 -> {
                binding.keyStep1.visibility = android.view.View.GONE
                binding.keyStep2.visibility = android.view.View.VISIBLE
                binding.keyStep3.visibility = android.view.View.GONE
                binding.keyStep4.visibility = android.view.View.GONE
                binding.skipButton.visibility = android.view.View.VISIBLE
                binding.nextButton.text = "Next"
                binding.cardView.visibility = android.view.View.VISIBLE
                hideLoading()
            }
            2 -> {
                binding.keyStep1.visibility = android.view.View.GONE
                binding.keyStep2.visibility = android.view.View.GONE
                binding.keyStep3.visibility = android.view.View.VISIBLE
                binding.keyStep4.visibility = android.view.View.GONE
                binding.nextButton.text = "Next"
                binding.cardView.visibility = android.view.View.VISIBLE
                hideLoading()
            }
            3 -> {
                binding.keyStep1.visibility = android.view.View.GONE
                binding.keyStep2.visibility = android.view.View.GONE
                binding.keyStep3.visibility = android.view.View.GONE
                binding.keyStep4.visibility = android.view.View.VISIBLE
                binding.nextButton.text = "Register"
                binding.cardView.visibility = android.view.View.GONE
                hideLoading()
            }
            4 -> {
                validate()
            }
        }
    }

    private fun showLoading(){
        binding.pb.visibility = android.view.View.VISIBLE
        binding.overlay.visibility = android.view.View.VISIBLE
    }
    private fun hideLoading(){
        binding.pb.hide()
        binding.overlay.hide()
    }

    private fun validate(){

        var payment_type = 0
        if(binding.custNameEditText.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }
        if(binding.imeiEditText.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter IMEI number", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }
        if(binding.mobileEditText.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }else if(!isValidMobileNumber(binding.mobileEditText.text.toString())){
            Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }

        if(selectedItem == null){
            Toast.makeText(this, "Please select a brand", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }

        if(selectedModel == null){
            Toast.makeText(this, "Please select a model no.", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }

        if(selectedBank == null){
            Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }
        if(!signatureImage){
            Toast.makeText(this, "Signature is Required", Toast.LENGTH_SHORT).show()
            showNextStep(0)
            hideLoading()
            return
        }

            payment_type=1
            if(binding.datepicker.text.toString().isEmpty()){
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(selectedFrequency < 1){
                Toast.makeText(this, "Please select installment frequency", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(selectedNumberOfInstallment < 1){
                Toast.makeText(this, "Please select Number of installments", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(viewModel.customerImage == null){
                Toast.makeText(this, "Please upload customer image", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(viewModel.customer_id_front == null){
                Toast.makeText(this, "Please upload customer ID front", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(viewModel.customer_id_back == null){
                Toast.makeText(this, "Please upload customer ID back", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }


        val request = RequestSmartKey(
            selectedBank!!.id,
            selectedItem!!.id,
            viewModel.customer_id_back,
            viewModel.customer_id_front,
            binding.downPaymentEditText.text.toString(),
            viewModel.customerImage,
            binding.imeiEditText.text.toString(),
            binding.loanAmounttEditText.text.toString(),
            binding.datepicker.text.toString(),
            binding.loanNumEditText.text.toString(),
            binding.mobileEditText.text.toString(),
            selectedModel!!.name,
            binding.custNameEditText.text.toString(),
            (selectedFrequency-1),
            payment_type,
            binding.priceEditText.text.toString().toInt(),
            viewModel.reference_id_back,
            viewModel.reference_id_front,
            binding.referenceNameEditText.text.toString(),
            binding.referenceMobileEditText.text.toString(),
            getSignature(),
            selectedNumberOfInstallment,
            binding.applianceType.text.toString(),
            binding.serialNumberEdit.text.toString(),
            viewModel.product_photo
        )

        if(intent.getStringExtra("title") == "Smart Key"){
            withNetwork { createSmartKey(request) }
        }else if(intent.getStringExtra("title") == "Home Appliance"){
            withNetwork { createHomeKey(request) }
        }else{
            withNetwork { createSuperKey(request) }
        }
    }

    fun isValidMobileNumber(number: String): Boolean {
        val regex = "^[0-9]{10}$"
        return number.matches(regex.toRegex())
    }

    private fun createSmartKey(request: RequestSmartKey) {
        request.logd()
        val call = RetrofitInstance.apiService.registerSmartKey(request)
        call.enqueue(object : Callback<Dashboard.CreateUserResponse> {
            override fun onResponse(
                call: Call<Dashboard.CreateUserResponse>,
                response: Response<Dashboard.CreateUserResponse>
            ) {
                binding.pb.hide()
                binding.overlay.hide()
                Log.d("response",response.body().toString())
                when (response.code()) {
                    200 -> {

                        startActivity(
                            Intent(this@SmartKey, UserQrActivity::class.java).putExtra(
                                "id",
                                response.body()!!.customer_id.toString()
                            ).putExtra(
                                "type",
                                1
                            ))
                        finish()
                    }
                    else -> {
                        Toast.makeText(this@SmartKey, "Something went wrong", Toast.LENGTH_SHORT).show()
                        binding.pb.hide()
                        binding.overlay.hide()
                    }
                }
            }

            override fun onFailure(call: Call<Dashboard.CreateUserResponse>, t: Throwable) {
                Toast.makeText(this@SmartKey, "Something went wrong", Toast.LENGTH_SHORT).show()
                binding.pb.hide()
                binding.overlay.hide()
            }

        })

    }

    private fun createHomeKey(request: RequestSmartKey) {
        request.logd()
        val call = RetrofitInstance.apiService.registerHomeKey(request)
        call.enqueue(object : Callback<Dashboard.CreateUserResponse> {
            override fun onResponse(
                call: Call<Dashboard.CreateUserResponse>,
                response: Response<Dashboard.CreateUserResponse>
            ) {
                binding.pb.hide()
                binding.overlay.hide()
                Log.d("response",response.body().toString())
                when (response.code()) {
                    200 -> {

                        startActivity(
                            Intent(this@SmartKey, UserQrActivity::class.java).putExtra(
                                "id",
                                response.body()!!.customer_id.toString()
                            ).putExtra("local_image", true).putExtra(
                                "type",
                                3
                            ))
                        finish()
                    }
                    else -> {
                        Toast.makeText(this@SmartKey, "Something went wrong", Toast.LENGTH_SHORT).show()
                        binding.pb.hide()
                        binding.overlay.hide()
                    }
                }
            }

            override fun onFailure(call: Call<Dashboard.CreateUserResponse>, t: Throwable) {
                Toast.makeText(this@SmartKey, "Something went wrong", Toast.LENGTH_SHORT).show()
                binding.pb.hide()
                binding.overlay.hide()
            }

        })

    }
    private fun createSuperKey(request: RequestSmartKey) {
        request.logd()

        val call = RetrofitInstance.apiService.registerSuperKey(request)
        call.enqueue(object : Callback<Dashboard.CreateUserResponse> {
            override fun onResponse(
                call: Call<Dashboard.CreateUserResponse>,
                response: Response<Dashboard.CreateUserResponse>
            ) {
                binding.pb.hide()
                binding.overlay.hide()
                Log.d("response",response.body().toString())
                when (response.code()) {
                    200 -> {

                        startActivity(
                            Intent(this@SmartKey, UserQrActivity::class.java).putExtra(
                                "id",
                                response.body()!!.customer_id.toString()
                            ).putExtra(
                                "type",
                                2
                            ))
                        finish()
                    }
                    else -> {
                        Toast.makeText(this@SmartKey, "Something went wrong", Toast.LENGTH_SHORT).show()
                        binding.pb.hide()
                        binding.overlay.hide()
                    }
                }
            }

            override fun onFailure(call: Call<Dashboard.CreateUserResponse>, t: Throwable) {
                Toast.makeText(this@SmartKey, "Something went wrong", Toast.LENGTH_SHORT).show()
                binding.pb.hide()
                binding.overlay.hide()
            }

        })

    }

    override fun onBarcodeScanned(barcode: String) {
        binding.imeiEditText.setText(barcode)
    }

    override fun onImageCapture(uri: Uri) {
        processImages(uri)
        viewModel.selected_image_id = null
    }

}