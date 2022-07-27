package com.example.a10.dars.sodda.contactapp

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a10.dars.sodda.contactapp.databinding.ActivitySendMessageBinding
import com.example.a10.dars.sodda.contactdb.model.Contact
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class SendMessage : AppCompatActivity() {
    lateinit var binding: ActivitySendMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        val contact: Contact = bundle?.getSerializable("contact") as Contact

        binding.apply {
            back.setOnClickListener {
                finish()
            }
            nameNumberEt.setText("${contact.number}")
            nameNumberEt.isEnabled = false
            sendBtn.setOnClickListener {
                setMessagePermission(contact)
            }
        }
    }

    private fun setMessagePermission(contact: Contact) {
        Dexter.withContext(this).withPermission(Manifest.permission.SEND_SMS)
            .withListener(object : MultiplePermissionsListener, PermissionListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        val sms = binding.descriptions.text.trim().toString()
                        val phone = binding.nameNumberEt.text.trim().toString()
                        if (sms.isNotEmpty() && phone.isNotEmpty()) {
                            sendSMS(phone, sms)
                        } else {
                            Toast.makeText(this@SendMessage, "fill all fields", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(
                            this@SendMessage,
                            "Permission Denied! plz enable application permission from app settings!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val sms = binding.descriptions.text.trim().toString()
                    val phone = binding.nameNumberEt.text.trim().toString()
                    if (sms.isNotEmpty() && phone.isNotEmpty()) {
                        sendSMS(phone, sms)
                    } else {
                        Toast.makeText(this@SendMessage, "fill all fields", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Snackbar.make(
                        binding.root,
                        "Please permit the permission through Settings screen.",
                        Snackbar.LENGTH_LONG
                    ).setAction("Settings", object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }

                    })
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            Toast.makeText(
                applicationContext, "Message Sent",
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext, ex.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }
    }
}