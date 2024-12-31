package com.example.tabs.ui.contacts.edit

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.utils.models.PresentHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PresentAdapter(private val items: List<PresentHistory>, private val editFragment: EditFragment) :
    RecyclerView.Adapter<PresentAdapter.DropdownViewHolder>() {
    private val resItems: MutableList<PresentHistory> = items.toMutableList()

    class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gift: EditText = view.findViewById(R.id.gift)
        val date: EditText = view.findViewById(R.id.date)
        var selectDate: Date? = null
        val price: EditText = view.findViewById(R.id.price)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropdownViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dropdown_present, parent, false)
        return DropdownViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: DropdownViewHolder, position: Int) {
        val item = resItems[position]
        holder.gift.setText(item.gift)
        // 날짜 폼
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
        holder.selectDate = item.date
        val sDay = sdf.format(holder.selectDate)
        holder.date.setText(sDay)
        holder.date.setOnClickListener{
            editFragment.showDatePickerDialog(holder.selectDate, holder.date) { date ->
                holder.selectDate = date
                holder.date.setText(sdf.format(holder.selectDate))
            }
        }
        holder.price.setText(item.price.toString())
        // 삭제 버튼 클릭 시
        holder.deleteButton.setOnClickListener {
            removeItem(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = resItems.size

    fun getItems(): MutableList<PresentHistory> {
        return resItems
    }

    fun addItem() {
        val newItem = PresentHistory(Date(), "", 0)
        resItems.add(newItem)
        notifyItemInserted(resItems.size - 1)
    }

    fun removeItem(position: Int) {
        resItems.removeAt(position)
        notifyItemRemoved(position)
    }
}