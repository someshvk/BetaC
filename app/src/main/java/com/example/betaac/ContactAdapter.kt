package com.example.betaac

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(var context: Context, var contactArrayList: ArrayList<ContactModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.contact_view_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val model: ContactModel = contactArrayList[position]

        viewHolder.muserNameContact.text = model.getName()
    }

    override fun getItemCount(): Int {
        return contactArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var muserNameContact: TextView = itemView.findViewById(R.id.particularUsernameContact)
        lateinit var muserNumber: TextView
    }
}
