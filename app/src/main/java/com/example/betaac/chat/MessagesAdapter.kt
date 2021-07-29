package com.example.betaac.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betaac.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class MessagesAdapter(var context: Context, var messagesArrayList: ArrayList<Messages>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var ITEM_SEND= 1
    private var ITEM_RECEIVE= 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ITEM_SEND){
            val view : View = LayoutInflater.from(context).inflate(R.layout.sender_chat_layout, parent, false)
            return SenderViewHolder(view)
        }
        else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.receiver_chat_layout, parent, false)
            return ReceiverViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messagesArrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages: Messages = messagesArrayList[position]
        if (holder.javaClass === SenderViewHolder::class.java) {
            val viewHolder = holder as SenderViewHolder
            if(messages.getMessage() == "photo") {
                viewHolder.mimage.visibility = View.VISIBLE;
                viewHolder.mTVmessage.visibility = View.GONE;
                Picasso.get().load(messages.getImageUrl()).into(viewHolder.mimage)
            }
            viewHolder.mTVmessage.text = messages.getMessage()
            viewHolder.mtimeOfMsg.text = messages.getCurrentTime()
        }
        else{
            val viewHolder = holder as ReceiverViewHolder
            if(messages.getMessage() == "photo") {
                viewHolder.mimage.visibility = View.VISIBLE;
                viewHolder.mTVmessage.visibility = View.GONE;
                Picasso.get().load(messages.getImageUrl()).into(viewHolder.mimage)
            }
            viewHolder.mTVmessage.text = messages.getMessage()
            viewHolder.mtimeOfMsg.text = messages.getCurrentTime()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages: Messages = messagesArrayList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid  == messages.getSenderId()){
            return ITEM_SEND
        }
        else{
            return ITEM_RECEIVE
        }
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTVmessage : TextView = itemView.findViewById(R.id.senderMsg)
        var mimage: ImageView = itemView.findViewById(R.id.image)
        var mtimeOfMsg : TextView = itemView.findViewById(R.id.msgTime)

    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTVmessage : TextView = itemView.findViewById(R.id.receiverMsg)
        var mimage: ImageView = itemView.findViewById(R.id.image)
        var mtimeOfMsg : TextView = itemView.findViewById(R.id.msgTime)

    }

}