package com.example.betaac.user

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
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
import java.io.ByteArrayOutputStream
import java.io.IOException

class SetProfileActivity : AppCompatActivity() {

    private lateinit var mgetUserImage: CardView
    private lateinit var  mgetUserImgIV: ImageView
    private var getImage: Int = 123
    private var PICK_IMAGE = 1
    private var imagePath: Uri? = null
    private lateinit var mgetuserName: EditText
    private lateinit var msaveProfileBtn: Button
    private lateinit var mprogressbarSetProfile : ProgressBar

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var name: String
    private var imgUriAccessToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        firebaseFirestore = FirebaseFirestore.getInstance()

        mgetuserName = findViewById(R.id.getUserName)
        mgetUserImage = findViewById(R.id.getUserImg)
        mgetUserImgIV = findViewById(R.id.getUserImgIV)
        msaveProfileBtn = findViewById(R.id.saveBtn)
        mprogressbarSetProfile = findViewById(R.id.progressBarSetProfile)

        mgetUserImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        })

        msaveProfileBtn.setOnClickListener(View.OnClickListener {
            name = mgetuserName.text.toString()
            if(name.isEmpty()){
                Toast.makeText(applicationContext, "Name is empty.", Toast.LENGTH_SHORT).show()
            }
            else if(imagePath==null){
                Toast.makeText(applicationContext, "Image is Empty.", Toast.LENGTH_SHORT).show()
            }
            else{
                mprogressbarSetProfile.visibility = View.VISIBLE
                sendDataForNewUser()
                mprogressbarSetProfile.visibility = View.INVISIBLE
                startActivity(Intent(this@SetProfileActivity, ChatActivity::class.java))
                finish()
            }
        })
    }

    private fun sendDataForNewUser() {
        sendDataToRealtimeDatabase()
    }

    private fun sendDataToRealtimeDatabase() {
        name = mgetuserName.text.toString().trim()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(firebaseAuth.uid.toString())

        val muserData= UserData(name, firebaseAuth.uid.toString())
        databaseReference.setValue(muserData)
        Toast.makeText(applicationContext, "User profile added successfully.", Toast.LENGTH_SHORT).show()
        sendImgToStore()

    }

    private fun sendImgToStore() {
        val imgRef: StorageReference = storageReference.child("Images").child(firebaseAuth.uid.toString()).child("Profile pic")
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)

        }
        catch (e: IOException){
            e.printStackTrace()
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 25,byteArrayOutputStream)
        var data = byteArrayOf()
        data = byteArrayOutputStream.toByteArray()

        val uploadtask: UploadTask = imgRef.putBytes(data)

        uploadtask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>() { snapshot ->
            imgRef.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri>() {uri ->  
                imgUriAccessToken = uri.toString()
                Toast.makeText(applicationContext, "Uri got successfully.", Toast.LENGTH_SHORT).show()
                sendDataToCloudFirestore()
            }).addOnFailureListener(OnFailureListener() {e ->
                Toast.makeText(applicationContext, "Uri failed.", Toast.LENGTH_SHORT).show()
            }).addOnFailureListener(OnFailureListener() {e ->
                Toast.makeText(applicationContext, "Image not loaded.", Toast.LENGTH_SHORT).show()
            })
            Toast.makeText(applicationContext, "Image uploaded.", Toast.LENGTH_SHORT).show()
        }).addOnFailureListener(OnFailureListener() {e ->
            Toast.makeText(applicationContext, "Image not loaded.", Toast.LENGTH_SHORT).show()
        })
    }

    private fun sendDataToCloudFirestore() {
        val documentRef : DocumentReference = firebaseFirestore.collection("users").document(firebaseAuth.uid.toString())
        val UD = mutableMapOf<String, Any>()
        UD["name"] = name
        UD["image"] = imgUriAccessToken
        UD["uid"] = firebaseAuth.uid.toString()

        documentRef.set(UD).addOnCompleteListener(OnCompleteListener {
            Toast.makeText(applicationContext, "Data successfully given to Firestore.", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK)

            if (data != null) {
                imagePath = data.data!!
                mgetUserImgIV.setImageURI(imagePath)
            }

        super.onActivityResult(requestCode, resultCode, data)
    }
}