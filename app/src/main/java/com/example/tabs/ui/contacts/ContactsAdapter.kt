package com.example.tabs.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.utils.models.Contact
import com.example.tabs.databinding.ItemContactBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ContactsAdapter(private var contactList: List<Contact>)
    : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    class ContactViewHolder(private val binding: ItemContactBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) { // 데이터를 바인딩하는 함수
            binding.personName.text = contact.name
            val bDayFormatter = SimpleDateFormat("MM-dd", Locale.getDefault())
            binding.personBirthday.text = "생일: "+bDayFormatter.format(contact.bDay)
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        // item_contact.xml 바인딩
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int = contactList.size

    // 데이터 업데이트
    fun updateData(newContactList: List<Contact>) {
        contactList = newContactList
        notifyDataSetChanged()
    }
}