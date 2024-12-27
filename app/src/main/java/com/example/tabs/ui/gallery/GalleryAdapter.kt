package com.example.tabs.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R

class GalleryAdapter(private var nameList: List<String>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    // ViewHolder 정의
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_page, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = nameList[position] // 이름 데이터 가져오기
        holder.textView.text = name // TextView에 이름 설정
    }

    override fun getItemCount(): Int = nameList.size

    // 데이터를 업데이트하는 함수
    fun updateData(newData: List<String>) {
        nameList = newData
        notifyDataSetChanged() // RecyclerView 갱신
    }
}
