package com.example.tabs.ui.contacts.edit

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.utils.models.Occasion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OccasionAdapter(private val items: List<Occasion>, private val editFragment: EditFragment) :
    RecyclerView.Adapter<OccasionAdapter.DropdownViewHolder>() {

    class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // edit text로
        val occasion: EditText = view.findViewById(R.id.occasion)
        val date: EditText = view.findViewById(R.id.date)
        var selectDate: Date? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropdownViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dropdown_occasion, parent, false)
        return DropdownViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: DropdownViewHolder, position: Int) {
        val item = items[position]
        holder.occasion.setText(item.occasion)
        // 기념일 날짜 폼
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
        holder.selectDate = item.date
        val sDay = sdf.format(holder.selectDate)
        holder.date.setText(sDay)
        holder.date.setOnClickListener{
            holder.selectDate = editFragment.showDatePickerDialog(holder.selectDate, holder.date)
            holder.date.setText(sdf.format(holder.selectDate))
        }
    }

    override fun getItemCount(): Int = items.size
}