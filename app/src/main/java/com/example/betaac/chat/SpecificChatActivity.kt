package com.example.betaac.chat

import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betaac.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*


class SpecificChatActivity : AppCompatActivity() {

    private lateinit var mgetMessage: EditText
    private lateinit var msendMsgBtn: ImageButton
    private lateinit var msendMsgCardview: CardView
    private lateinit var mtoolbarSpecificChat : Toolbar
    private lateinit var mimgviewSpecificChatIV: ImageView
    private lateinit var muserName : TextView
    private lateinit var mbackBtnSpecificChat: ImageButton
    private lateinit var mmsgRecyclerView: RecyclerView
    private lateinit var mimageBtn: ImageButton
    private lateinit var mstatus: TextView

    private lateinit var enteredMessage : String
    private lateinit var mreceiverName: String
    private lateinit var mreceiverId: String
    private lateinit var msenderId: String
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var currentTime: String

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var calendar: Calendar
    private lateinit var simpleDateFormat: SimpleDateFormat

    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var arrayList: ArrayList<Messages>
    var dialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_chat)

        mgetMessage = findViewById(R.id.getMessage)
        msendMsgCardview = findViewById(R.id.cardviewSpecificChat)
        msendMsgBtn = findViewById(R.id.sendMsgBtn)
        mtoolbarSpecificChat = findViewById(R.id.toolbarSpecificChat)
        muserName = findViewById(R.id.userNameSpecificChat)
        mimgviewSpecificChatIV = findViewById(R.id.imgviewSpecificChatIV)
        mbackBtnSpecificChat = findViewById(R.id.backBtnSpecificChat)
        mimageBtn = findViewById(R.id.imageBtn)
        mstatus = findViewById(R.id.status)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading Image")
        dialog!!.setCanceledOnTouchOutside(false)

        mmsgRecyclerView = findViewById(R.id.recyclerviewSpecificChat)
        arrayList = arrayListOf()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        mmsgRecyclerView.layoutManager = linearLayoutManager

        messagesAdapter =
            MessagesAdapter(this, arrayList)
        mmsgRecyclerView.adapter = messagesAdapter

        setSupportActionBar(mtoolbarSpecificChat)
        mtoolbarSpecificChat.setOnClickListener {
            Toast.makeText(applicationContext, "Toolbar is Clicked", Toast.LENGTH_SHORT).show()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        storageReference = firebaseStorage.reference
        calendar = Calendar.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            simpleDateFormat = SimpleDateFormat("h:mm a", Locale.US)
        }

        msenderId = firebaseAuth.uid.toString()
        mreceiverId = intent.getStringExtra("receiveruid").toString()
        mreceiverName = intent.getStringExtra("name").toString()

        senderRoom = msenderId + mreceiverId
        receiverRoom = mreceiverId + msenderId

        firebaseDatabase.reference.child("presence").child(mreceiverId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status!!.isNotEmpty()) {
                            if (status == "Offline") {
                                mstatus.visibility = View.GONE
                            } else {
                                mstatus.text = status
                                mstatus.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        val databaseReference = firebaseDatabase.reference.child("chats").child(senderRoom).child("messages")
        messagesAdapter =
            MessagesAdapter(this, arrayList)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                for (snapshot1 in snapshot.children) {
                    val messages = snapshot1.getValue(
                        Messages::class.java
                    )
                    if (messages != null) {
                        arrayList.add(messages)
                    }
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        mbackBtnSpecificChat.setOnClickListener {
            finish()
        }

        muserName.text = mreceiverName

        val uri = intent.getStringExtra("imageuri")
            if (uri?.isEmpty()!!) {
                Toast.makeText(applicationContext, "null is received.", Toast.LENGTH_SHORT).show()
            }
            else{
                Picasso.get().load(uri).into(mimgviewSpecificChatIV)
            }

        msendMsgBtn.setOnClickListener {
            val messageTxt: String = mgetMessage.text.toString()

            val date = Date()
            val message = Messages(messageTxt, msenderId, date.time.toString())

            val randomKey: String? = firebaseDatabase.reference.push().key

            val lastMsgObj: HashMap<String, Any> = HashMap()
            lastMsgObj["lastMsg"] = message.getMessage()
            lastMsgObj["lastMsgTime"] = date.time

            firebaseDatabase.reference.child("chats").child(senderRoom).updateChildren(lastMsgObj)
            firebaseDatabase.reference.child("chats").child(receiverRoom).updateChildren(lastMsgObj)

            firebaseDatabase.reference.child("chats")
                .child(senderRoom)
                .child("messages")
                .child(randomKey.toString())
                .setValue(message).addOnSuccessListener(OnSuccessListener<Void?> {
                    firebaseDatabase.reference.child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(randomKey.toString())
                        .setValue(message).addOnSuccessListener(OnSuccessListener<Void?> { })
                })
            mgetMessage.text = null
        }

        mimageBtn.setOnClickListener{
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 25)
        }

        val handler = Handler()
        mgetMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                firebaseDatabase.reference.child("presence").child(msenderId).setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }

            var userStoppedTyping =
                Runnable {
                    firebaseDatabase.reference.child("presence").child(msenderId).setValue("Online")
                }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int,  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 25) {
            if (data?.data != null) {
                val selectedImage: Uri = data.data!!
                val calendar = Calendar.getInstance()
                val reference: StorageReference = storageReference.child("chats")
                    .child(calendar.timeInMillis.toString() + "")
                dialog!!.show()
                reference.putFile(selectedImage).addOnCompleteListener { task ->
                    dialog!!.dismiss()
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            val filePath = uri.toString()
                            val messageTxt: String = mgetMessage.text.toString()
                            val date = Date()
                            val message = Messages(messageTxt, msenderId, date.time.toString())
                            message.setMessage("photo")
                            message.setImageUrl(filePath)
                            mgetMessage.text = null
                            val randomKey: String? = firebaseDatabase.reference.push().key
                            val lastMsgObj: HashMap<String, Any> = HashMap()
                            lastMsgObj["lastMsg"] = message.getMessage()
                            lastMsgObj["lastMsgTime"] = date.time
                            firebaseDatabase.reference.child("chats").child(senderRoom)
                                .updateChildren(lastMsgObj)
                            firebaseDatabase.reference.child("chats").child(receiverRoom)
                                .updateChildren(lastMsgObj)
                            firebaseDatabase.reference.child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .child(randomKey.toString())
                                .setValue(message)
                                .addOnSuccessListener(OnSuccessListener<Void?> {
                                    firebaseDatabase.reference.child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(randomKey.toString())
                                        .setValue(message).addOnSuccessListener(
                                            OnSuccessListener<Void?> { })
                                })

                            //Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId: String = FirebaseAuth.getInstance().uid.toString()
        firebaseDatabase.reference.child("presence").child(currentId).setValue("Online")
    }

    override fun onStart() {
        super.onStart()
        messagesAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter.notifyDataSetChanged()
    }
}