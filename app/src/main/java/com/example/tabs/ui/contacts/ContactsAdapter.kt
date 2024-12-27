package com.example.tabs.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.utils.models.Contact
import com.example.tabs.databinding.ItemContactBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ContactsAdapter(private var contactList: List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    class ContactViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.personName.text = contact.name
            val bDayFormatter = SimpleDateFormat("MM-dd", Locale.getDefault())
            binding.personBirthday.text = bDayFormatter.format(contact.bDay)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int = contactList.size

    fun updateData(newContactList: List<Contact>) {
        contactList = newContactList
        notifyDataSetChanged()
    }
}