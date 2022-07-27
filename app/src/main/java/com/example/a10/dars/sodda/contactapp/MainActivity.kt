package com.example.a10.dars.sodda.contactapp

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a10.dars.sodda.contactapp.adapters.MyRecyclerViewAdapter
import com.example.a10.dars.sodda.contactapp.databinding.ActivityMainBinding
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


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val requestCode = 0
    lateinit var listOfContacts: ArrayList<Contact>
    val TAG = "Go"
    lateinit var myRecyclerViewAdapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listOfContacts = ArrayList()


    }

    private fun setCallPermission(contact: Contact, position: Int) {
        Dexter.withContext(this).withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : MultiplePermissionsListener, PermissionListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:" + contact.number)
                        startActivity(callIntent)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
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
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + contact.number)
                    startActivity(callIntent)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    binding.progress.visibility = View.VISIBLE
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



    override fun onStart() {
        super.onStart()
        setContactPermission()
    }

    override fun onResume() {
        super.onResume()
        if (listOfContacts.isNotEmpty()) {
            myRecyclerViewAdapter.notifyItemInserted(listOfContacts.size)
            myRecyclerViewAdapter.notifyItemChanged(listOfContacts.size)

        }
        Log.d(TAG, "onResume: ")
    }

    private fun setContactPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : MultiplePermissionsListener, PermissionListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()) {
                        binding.progress.visibility = View.INVISIBLE
                        listOfContacts = getData()
                        myRecyclerViewAdapter =
                            MyRecyclerViewAdapter(
                                this@MainActivity,
                                listOfContacts,
                                object : MyRecyclerViewAdapter.Click {
                                    override fun callClick(contact: Contact, position: Int) {
                                        setCallPermission(contact, position)
                                    }

                                    override fun messageClick(contact: Contact, position: Int) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("contact", contact)
                                        val intent =
                                            Intent(this@MainActivity, SendMessage::class.java)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }

                                })
                        binding.rv.adapter = myRecyclerViewAdapter
                        myRecyclerViewAdapter.notifyItemInserted(listOfContacts.size)
                        myRecyclerViewAdapter.notifyItemChanged(listOfContacts.size)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
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
                    binding.progress.visibility = View.INVISIBLE
                    listOfContacts = getData()
                    myRecyclerViewAdapter = MyRecyclerViewAdapter(
                        this@MainActivity,
                        listOfContacts,
                        object : MyRecyclerViewAdapter.Click {
                            override fun callClick(contact: Contact, position: Int) {
                                setCallPermission(contact, position)
                            }

                            override fun messageClick(contact: Contact, position: Int) {
                                val bundle = Bundle()
                                bundle.putSerializable("contact", contact)
                                val intent = Intent(this@MainActivity, SendMessage::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                        })
                    binding.rv.adapter = myRecyclerViewAdapter
                    myRecyclerViewAdapter.notifyItemInserted(listOfContacts.size)
                    myRecyclerViewAdapter.notifyItemChanged(listOfContacts.size)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    binding.progress.visibility = View.VISIBLE
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


    private fun displayNeverAskAgainDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(
            """
            We need to send SMS for performing necessary task. Please permit the permission through Settings screen.
            
            Select Permissions -> Enable permission
            """.trimIndent()
        )
        builder.setCancelable(false)
        builder.setPositiveButton("Permit Manually",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            })
        builder.setNegativeButton("Cancel") { p0, p1 ->

        }
        builder.show()
    }

    //
    fun getData(): ArrayList<Contact> {
        val list = ArrayList<Contact>()
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val names: Cursor = contentResolver.query(uri, projection, null, null, null)!!
        val indexName: Int =
            names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val indexNumber: Int =
            names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        names.moveToFirst()
        do {
            val name: String = names.getString(indexName)
            Log.e("Name new:", name)
            val number: String = names.getString(indexNumber)
            Log.e("Number new:", "::$number")
            val contact = Contact(name, number)
            list.add(contact)
        } while (names.moveToNext())
        return list
    }


}
