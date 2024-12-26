package com.example.tabs.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R

class GalleryAdapter(private var imageList: List<Int>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    // ViewHolder 클래스 정의
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view) // ImageView 바인딩
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return ViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageResource = imageList[position] // 현재 아이템의 데이터 가져오기
        holder.imageView.setImageResource(imageResource) // 이미지 설정
    }

    // 데이터 리스트의 크기 반환
    override fun getItemCount(): Int = imageList.size

    // 데이터를 업데이트하는 함수
    fun updateData(newData: List<Int>) {
        imageList = newData
        notifyDataSetChanged() // RecyclerView 갱신
    }
}
