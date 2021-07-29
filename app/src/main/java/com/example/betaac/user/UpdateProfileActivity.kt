package com.example.betaac.user

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.example.betaac.R
import com.example.betaac.chat.ChatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var mgetNewUsername: EditText
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private lateinit var mgetNewUserImgIV : ImageView
    private var imgUriAccessToken: String = ""
    private lateinit var mupdateBtn: Button
    private lateinit var mtoolbarUpdateProfile: androidx.appcompat.widget.Toolbar
    private lateinit var mbackBtnUpdateProfile: ImageButton
    private lateinit var progressbarUpdateProfile: ProgressBar
    private var imagepath: Uri? = null
    private var PICK_IMAGE: Int = 1
    private lateinit var newName: String
    private var muserdata: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        mgetNewUserImgIV = findViewById(R.id.getNewUserImgIV)
        mgetNewUsername = findViewById(R.id.getNewUserName)
        mupdateBtn = findViewById(R.id.updateBtn)
        firebaseFirestore = FirebaseFirestore.getInstance()
        mtoolbarUpdateProfile = findViewById(R.id.toolbarUpdateProfile)
        mbackBtnUpdateProfile = findViewById(R.id.backBtnUpdateProfile)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        progressbarUpdateProfile = findViewById(R.id.progressBarUpdateProfile)

        setSupportActionBar(mtoolbarUpdateProfile)

        mbackBtnUpdateProfile.setOnClickListener(View.OnClickListener {
            finish()
        })

        mgetNewUsername.setText(intent.getStringExtra("nameofuser"))

        databaseReference = firebaseDatabase.getReference(firebaseAuth.uid.toString())

        mupdateBtn.setOnClickListener(View.OnClickListener {
            newName = mgetNewUsername.text.toString()
            if(newName.isEmpty()){
                Toast.makeText(applicationContext, "Name is Empty.", Toast.LENGTH_SHORT).show()
            }
            else if(imagepath != null){
                progressbarUpdateProfile.visibility = View.VISIBLE
                muserdata = UserData(newName, firebaseAuth.uid.toString())
                databaseReference.setValue(muserdata)

                updateImageToStorage()

                Toast.makeText(applicationContext, "Updated.", Toast.LENGTH_SHORT).show()
                progressbarUpdateProfile.visibility = View.INVISIBLE
                startActivity(Intent(this@UpdateProfileActivity, ChatActivity::class.java))
                finish()
            }
            else{
                progressbarUpdateProfile.visibility = View.VISIBLE
                muserdata = UserData(newName, firebaseAuth.uid.toString())
                databaseReference.setValue(muserdata)

                updateNameOnCloudFirestore()

                Toast.makeText(applicationContext, "Updated.", Toast.LENGTH_SHORT).show()
                progressbarUpdateProfile.visibility = View.INVISIBLE
            }
        })

        mgetNewUserImgIV.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        })

        storageReference = firebaseStorage.reference
        storageReference.child("Images").child(firebaseAuth.uid.toString()).child("Profile pic").downloadUrl.addOnSuccessListener(
            OnSuccessListener {uri->
                imgUriAccessToken = uri.toString()
                Picasso.get().load(uri).into(mgetNewUserImgIV)
            })
    }

    private fun updateNameOnCloudFirestore() {
        val documentRef : DocumentReference = firebaseFirestore.collection("users").document(firebaseAuth.uid.toString())
        val UD = mutableMapOf<String, Any>()
        UD["name"] = newName
        UD["image"] = imgUriAccessToken
        UD["uid"] = firebaseAuth.uid.toString()

        documentRef.set(UD).addOnCompleteListener(OnCompleteListener {
            Toast.makeText(applicationContext, "Profile updated Successfully.", Toast.LENGTH_SHORT).show()
        })

    }

    private fun updateImageToStorage() {
        val imgRef: StorageReference = storageReference.child("Images").child(firebaseAuth.uid.toString()).child("Profile pic")
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagepath)
        }
        catch (e: IOException){
            e.printStackTrace()
        }

        val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 25,byteArrayOutputStream)
        var data = byteArrayOf()
        data = byteArrayOutputStream.toByteArray()

        val uploadtask: UploadTask = imgRef.putBytes(data)

        uploadtask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>() { snapshot ->
            imgRef.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri>() {uri ->
                imgUriAccessToken = uri.toString()
                Toast.makeText(applicationContext, "Uri got successfully.", Toast.LENGTH_SHORT).show()
                updateNameOnCloudFirestore()
            }).addOnFailureListener(OnFailureListener() {e ->
                Toast.makeText(applicationContext, "Uri failed.", Toast.LENGTH_SHORT).show()
            }).addOnFailureListener(OnFailureListener() {e ->
                Toast.makeText(applicationContext, "Image not loaded.", Toast.LENGTH_SHORT).show()
            })
            Toast.makeText(applicationContext, "Image updated.", Toast.LENGTH_SHORT).show()
        }).addOnFailureListener(OnFailureListener() {e ->
            Toast.makeText(applicationContext, "Image not updated.", Toast.LENGTH_SHORT).show()
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK)

            if (data != null) {
                imagepath = data.data!!
                mgetNewUserImgIV.setImageURI(imagepath)
            }

        super.onActivityResult(requestCode, resultCode, data)
    }
}