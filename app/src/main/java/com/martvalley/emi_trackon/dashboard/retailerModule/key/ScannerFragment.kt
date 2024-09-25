package com.martvalley.emi_trackon.dashboard.retailerModule.key

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.martvalley.emi_trackon.R

class ScannerFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private var listener: OnBarcodeScannedListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBarcodeScannedListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnBarcodeScannedListener")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                listener?.onBarcodeScanned(it.text)
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}