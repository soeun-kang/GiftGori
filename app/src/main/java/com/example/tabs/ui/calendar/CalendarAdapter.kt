package com.example.tabs.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.models.NameOccasion

class CalendarAdapter(private val nameOccasions: List<NameOccasion>, private val moveToGallery: (Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.eventTextView)
        val galleryButton: Button = view.findViewById(R.id.galleryButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        // 텍스트 설정
        val outText = nameOccasions[position].name + "에게 " + nameOccasions[position].occasion
        holder.nameTextView.text = outText

        // 이름 기반으로 contact index 찾아서 gallery fragment로 넘겨주기
        val name = nameOccasions[position].name
        val manageJson = ManageJson(holder.itemView.context)
        val jsonString = manageJson.readFileFromInternalStorage("contacts.json")
        val contactList = manageJson.parseJsonToDataList(jsonString)
        // find contact Id by name
        val contactIndex = contactList.indexOfFirst { it.name == name } ?: -1
        if (contactIndex != -1) {
            holder.galleryButton.setOnClickListener {
                moveToGallery(contactIndex)
            }
        }
    }

    override fun getItemCount() = nameOccasions.size
}