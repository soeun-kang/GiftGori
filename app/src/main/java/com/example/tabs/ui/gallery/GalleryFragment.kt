package com.example.tabs.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView 초기화
        val recyclerView: RecyclerView = binding.galleryRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2열 그리드
        galleryAdapter = GalleryAdapter(emptyList()) // 초기에는 빈 데이터 리스트로 설정
        recyclerView.adapter = galleryAdapter

        // ViewModel 데이터 관찰
        galleryViewModel.images.observe(viewLifecycleOwner) { images ->
            galleryAdapter.updateData(images) // 데이터 업데이트
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
