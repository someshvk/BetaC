package com.example.betaac

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentContact : Fragment() {

    private lateinit var mrecyclerviewContact: RecyclerView
    private lateinit var arrayList: ArrayList<ContactModel>
    private lateinit var mcontactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.contact_fragment, container, false)

        mrecyclerviewContact = v.findViewById(R.id.recyclerViewContacts)
        arrayList = arrayListOf()

        checkPermission()

        return v
    }

    private fun checkPermission() {
        if(activity?.applicationContext?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_CONTACTS)
            } != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(activity?.applicationContext as Activity, arrayOf(android.Manifest.permission.READ_CONTACTS), 100)
        }
        else{
            getContactDetails()
        }
    }

    private fun getContactDetails() {
        val uri: Uri = ContactsContract.Contacts.CONTENT_URI
        val sort: String = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val cursor: Cursor? = activity?.contentResolver?.query(uri, null, null, null, sort)

        if (cursor != null) {
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id: String = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                    ))
                    val name: String? = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    ))

                    val uriPhone: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

                    val selection: String = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +  "=?"

                    val phoneCursor: Cursor? = activity?.contentResolver?.query(uriPhone, null, selection, arrayOf(id), null)

                    if (phoneCursor != null) {
                        if(phoneCursor.moveToNext()){
                            val number: String = phoneCursor.getString(phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            ))

                            val model = ContactModel(name.toString(), number)
                            arrayList.add(model)
                            phoneCursor.close()
                        }
                    }
                }
                cursor.close()
            }
        }
        mrecyclerviewContact.layoutManager = LinearLayoutManager(activity?.applicationContext)
        mcontactAdapter = activity?.applicationContext?.let { ContactAdapter(it, arrayList) }!!
        mrecyclerviewContact.adapter = mcontactAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactDetails()
        }
        else{
            Toast.makeText(activity?.applicationContext, "Permission Denied.", Toast.LENGTH_SHORT).show()
            checkPermission()
        }
    }
}