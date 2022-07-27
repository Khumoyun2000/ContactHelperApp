package com.example.a10.dars.sodda.contactapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.a10.dars.sodda.contactapp.R
import com.example.a10.dars.sodda.contactapp.databinding.ItemRvBinding
import com.example.a10.dars.sodda.contactdb.model.Contact

class MyRecyclerViewAdapter(
    context: Context,
    val contactList: ArrayList<Contact>,
    val click: Click
) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.Vh>() {
    inner class Vh(val contactItemBinding: ItemRvBinding) :
        RecyclerView.ViewHolder(contactItemBinding.root) {
        @SuppressLint("ResourceAsColor")
        fun onBind(contact: Contact) {
            contactItemBinding.nameTv.text = contact.name
            contactItemBinding.phoneTv.text = contact.number
            contactItemBinding.btnCall.apply {
                useCompatPadding = true

                setPadding(25)
                setOnClickListener { click.callClick(contact, position) }
            }
            contactItemBinding.callLayout.setBackgroundColor(R.color.pink)
            contactItemBinding.messageLayout.setBackgroundColor(R.color.yellow)
            contactItemBinding.btnMessage.apply {
                setOnClickListener {
                    click.messageClick(contact, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        val contact = contactList[position]
        holder.onBind(contact)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    interface Click {
        fun callClick(contact: Contact, position: Int)
        fun messageClick(contact: Contact, position: Int)
    }
}