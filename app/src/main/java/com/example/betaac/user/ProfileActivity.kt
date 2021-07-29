package com.example.betaac.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.betaac.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var mviewUsername: EditText
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private lateinit var mviewUserImgIV : ImageView
    private var imgUriAccessToken: String = ""
    private lateinit var mchangeBtn: Button
    private lateinit var mtoolbarViewProfile: androidx.appcompat.widget.Toolbar
    private lateinit var mbackBtn: ImageButton
    private var muserdata: UserData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mviewUserImgIV = findViewById(R.id.viewUserImgIV)
        mviewUsername = findViewById(R.id.viewUserName)
        mchangeBtn = findViewById(R.id.changeBtn)
        firebaseFirestore = FirebaseFirestore.getInstance()
        mtoolbarViewProfile = findViewById(R.id.toolbarProfile)
        mbackBtn = findViewById(R.id.backBtn)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        setSupportActionBar(mtoolbarViewProfile)

        mbackBtn.setOnClickListener(View.OnClickListener {
            finish()
        })

        storageReference = firebaseStorage.reference
        storageReference.child("Images").child(firebaseAuth.uid.toString()).child("Profile pic").downloadUrl.addOnSuccessListener(
            OnSuccessListener {uri->
                imgUriAccessToken = uri.toString()
                Picasso.get().load(uri).into(mviewUserImgIV)
            })

        databaseReference= firebaseDatabase.getReference(firebaseAuth.uid.toString())
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                muserdata = snapshot.getValue(
                    UserData::class.java
                )
                mviewUsername.setText(muserdata?.getUsername())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to fetch.", Toast.LENGTH_SHORT).show()
            }
        })

        mchangeBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfileActivity, UpdateProfileActivity::class.java)
            intent.putExtra("nameofuser", mviewUsername.text.toString())
            startActivity(intent)
        })

    }
}