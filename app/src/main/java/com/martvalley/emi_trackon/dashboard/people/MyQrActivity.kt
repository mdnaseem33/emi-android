package com.martvalley.emi_trackon.dashboard.people

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.databinding.ActivityMyQrBinding
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.hide

class MyQrActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyQrBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.text.text = "My Qr Code"
        binding.toolbar.arrow.setOnClickListener { onBackPressed() }
        binding.toolbar.calender.hide()
        binding.toolbar.filter.hide()


        binding.qr.setImageBitmap(getQrCodeBitmap())

        binding.name.text = SharedPref(this).getValueString(Constants.NAME)

    }

    private fun getQrCodeBitmap(
    ): Bitmap {
        val qrCodeContent =
            "emi,${MainApplication.authData?.qr_id}"
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 1
        } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, 1024, 1024, hints)
        val size = 1024 //pixels
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}