package com.martvalley.emi_trackon.dashboard.retailerModule.key

import SearchableSpinnerAdapter
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.home.Dashboard
import com.martvalley.emi_trackon.dashboard.home.retailer.CreateUserActivity.Companion.bitmap
import com.martvalley.emi_trackon.dashboard.home.retailer.DialogFragment
import com.martvalley.emi_trackon.dashboard.people.user.UserQrActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.key.adapter.SearchableFinanceAdapter
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.Bank
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.Brand
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.CreateCustomerData
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.RequestSmartKey
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.SmartKey
import com.martvalley.emi_trackon.databinding.ActivitySmartKeyBinding
import com.martvalley.emi_trackon.utils.FileUtils
import com.martvalley.emi_trackon.utils.getBase64String
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.logd
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.toBase64StringBitmap
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class SmartKey : AppCompatActivity() {
    private val binding by lazy { ActivitySmartKeyBinding.inflate(layoutInflater) }
    private val TAG = "SmartKey"
    private var step = 0
    private var createCustomerData : CreateCustomerData? = null
    private var dialog: AlertDialog? = null
    // Track selected item
    var selectedItem: Brand? = null
    var selectedBank: Bank? = null
    var selectedFrequency: Int = 0
    val installmentType = arrayOf("Select Installment frequency", "Daily", "Weekly","Monthly")
    private lateinit var searchableSpinnerAdapter: SearchableSpinnerAdapter
    private lateinit var searchableFinanceAdapter: SearchableFinanceAdapter
    private val PICK_IMAGE_FROM_GALLERY = 1
    private val TAKE_PICTURE = 2
    private lateinit var currentPhotoPath: String
    private var customerImage: String? = null
    private var customer_id_front: String?= null
    private var customer_id_back: String?= null
    private var reference_id_front: String? = null
    private var reference_id_back: String? = null
    private var selected_image_id: String? = null
    private var signatureImage: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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

        binding.selectBank.setOnTouchListener{ v, _ ->
            showSearchableBankDialog()
            true
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
            selected_image_id = "photoText"
            showChooseImageSourceDialog()
        }
        binding.idFrontText.setOnClickListener {
            selected_image_id = "idFrontText"
            showChooseImageSourceDialog()
        }
        binding.idbackText.setOnClickListener {
            selected_image_id = "idbackText"
            showChooseImageSourceDialog()
        }
        binding.refFrontText.setOnClickListener {
            selected_image_id = "refFrontText"
            showChooseImageSourceDialog()
        }
        binding.refIdBackText.setOnClickListener {
            selected_image_id = "refIdBackText"
            showChooseImageSourceDialog()
        }

        binding.signText.setOnClickListener {
            showNextStep(0)
        }

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

    private fun showChooseImageSourceDialog() {
        // Open gallery to choose image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
        return;
        val dialogView = layoutInflater.inflate(R.layout.dialog_choose_image_source, null)
        val dialogBuilder = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Choose Image Source")

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        dialogView.findViewById<Button>(R.id.btn_gallery).setOnClickListener {
            // Open gallery to choose image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_camera).setOnClickListener {
            // Open camera to take picture
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.martvalley.emi_trackon.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, TAKE_PICTURE)
                    }
                }
            }
            alertDialog.dismiss()
        }
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
        when(selected_image_id){
            "photoText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        customerImage = ("data:image/png;base64,$media")
                        binding.photoChecked.setImageResource(R.drawable.checked_green)
                        binding.photoText.text = "Update"
                    }
                }
            }
            "idFrontText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        customer_id_front = ("data:image/png;base64,$media")
                        binding.idChecked.setImageResource(R.drawable.checked_green)
                        binding.idFrontText.text = "Update"
                    }
                }
            }
            "idbackText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        customer_id_back = ("data:image/png;base64,$media")
                        binding.idBackChecked.setImageResource(R.drawable.checked_green)
                        binding.idbackText.text = "Update"
                    }
                }
            }
            "refFrontText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        reference_id_front = ("data:image/png;base64,$media")
                        binding.referChecked.setImageResource(R.drawable.checked_green)
                        binding.refFrontText.text = "Update"
                    }
                }
            }
            "refIdBackText" -> {
                selectedImageUri?.let { uri ->
                    compressImageFromUriAndGetBase64(uri){ media->
                        reference_id_back = ("data:image/png;base64,$media")
                        binding.referBackChecked.setImageResource(R.drawable.checked_green)
                        binding.refIdBackText.text = "Update"
                    }
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_FROM_GALLERY -> {
                    // Handle image picked from gallery
                    val selectedImageUri = data?.data
                    // Do something with the selected image URI
                    processImages(selectedImageUri)
                }
                TAKE_PICTURE -> {
                    // Handle picture taken from camera.get("data")
                    val file = File(currentPhotoPath)
                    val selectedImageUri = Uri.fromFile(file)

                    // Do something with the selected image URI
                    processImages(selectedImageUri)

                }
                10 -> {
                    if (bitmap != null) {
                    }
                }
            }
        }
        selected_image_id = null
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

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath

        }
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
            if (position !=0){
                // Set the selected item when an item is clicked
                selectedItem = listView.getItemAtPosition(position) as? Brand
                binding.selectBrand.setSelection(position)
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
            if (position != 0){
                // Set the selected item when an item is clicked
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
        val call = RetrofitInstance.apiService.getCustomerCreateData()
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
                    brandList.add(0, Brand("",0, "", "Select Brand", ""))

                    searchableSpinnerAdapter = SearchableSpinnerAdapter(this@SmartKey, brandList)
                    binding.selectBrand.adapter = searchableSpinnerAdapter
                    binding.selectBrand.setSelection(0)

                }

                if(createCustomerData!!.banks != null){
                    var bankList = createCustomerData!!.banks as ArrayList<Bank>
                    bankList.add(0, Bank("", 0,"", "Select Bank", ""))
                    searchableFinanceAdapter = SearchableFinanceAdapter(this@SmartKey, bankList)
                    binding.selectBank.adapter = searchableFinanceAdapter
                    binding.selectBank.setSelection(0)

                }
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

        if(binding.radioColletEmi.isChecked){
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

            if(customerImage == null){
                Toast.makeText(this, "Please upload customer image", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(customer_id_front == null){
                Toast.makeText(this, "Please upload customer ID front", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            if(customer_id_back == null){
                Toast.makeText(this, "Please upload customer ID back", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

        }
        val request = RequestSmartKey(
            selectedBank!!.id,
            selectedItem!!.id,
            customer_id_back,
            customer_id_front,
            binding.downPaymentEditText.text.toString(),
            customerImage,
            binding.imeiEditText.text.toString(),
            binding.loanAmounttEditText.text.toString(),
            binding.datepicker.text.toString(),
            binding.loanNumEditText.text.toString(),
            binding.mobileEditText.text.toString(),
            binding.modelNoEditText.text.toString(),
            binding.custNameEditText.text.toString(),
            (selectedFrequency-1),
            payment_type,
            binding.priceEditText.text.toString().toInt(),
            reference_id_back,
            reference_id_front,
            binding.referenceNameEditText.text.toString(),
            binding.referenceMobileEditText.text.toString(),
            getSignature(),
            binding.numberOfInstallments.text.toString().toInt(),
            binding.applianceType.text.toString(),
            binding.serialNumberEdit.text.toString()
        )

        if(intent.getStringExtra("title") == "Smart Key"){
            withNetwork { createSuperKey(request) }
        }else if(intent.getStringExtra("title") == "Home Appliance"){
            withNetwork { createHomeKey(request) }
        }else{
            withNetwork { createSmartKey(request) }
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

}