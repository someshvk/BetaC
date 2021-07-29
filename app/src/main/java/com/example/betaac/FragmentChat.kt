package com.example.betaac

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betaac.chat.SpecificChatActivity
import com.example.betaac.chat.FirebaseModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso


class FragmentChat : Fragment() {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chatAdapter: FirestoreRecyclerAdapter<FirebaseModel?, NoteViewHolder?>

    lateinit var mimageviewofuser: ImageView
    private lateinit var mrecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.chat_fragment, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        mrecyclerView = v.findViewById(R.id.recyclerViewChat)

        val query: Query = firebaseFirestore.collection("users").whereNotEqualTo("uid", firebaseAuth.uid)
        val allusername: FirestoreRecyclerOptions<FirebaseModel?> =
            FirestoreRecyclerOptions.Builder<FirebaseModel>()
                .setQuery(query, FirebaseModel::class.java).build()

        chatAdapter =
            object : FirestoreRecyclerAdapter<FirebaseModel?, NoteViewHolder?>(allusername) {
                override fun onBindViewHolder(
                    holder: NoteViewHolder,
                    position: Int,
                    model: FirebaseModel
                ) {
                    holder.particularusername.text = model.getName()
                    val uri: String = model.getImg()
                    if(uri.isNotEmpty()){
                        Picasso.get().load(uri).into(mimageviewofuser)
                    }

                    holder.itemView.setOnClickListener(View.OnClickListener {
                        val intent = Intent(activity?.applicationContext, SpecificChatActivity::class.java)
                        intent.putExtra("name", model.getName())
                        intent.putExtra("receiveruid", model.getUid())
                        intent.putExtra("imageuri", model.getImg())
                        startActivity(intent)
                    })
                }

               override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentChat.NoteViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_view_layout, parent, false)
                    return NoteViewHolder(view)
                }

            }
        mrecyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mrecyclerView.layoutManager = linearLayoutManager
        mrecyclerView.adapter = chatAdapter

        return v
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val particularusername: TextView = itemView.findViewById(R.id.perticulaarUsername)

        init {
            mimageviewofuser = itemView.findViewById(R.id.imgViewOfUser)
        }
    }

    override fun onStart() {
        super.onStart()
        chatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatAdapter.stopListening()
    }
}

