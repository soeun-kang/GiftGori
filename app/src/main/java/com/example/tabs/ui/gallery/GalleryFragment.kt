package com.example.tabs.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabs.databinding.FragmentGalleryBinding
import com.google.android.material.tabs.TabLayoutMediator

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        // ViewPager2 어댑터 설정
        galleryAdapter = GalleryAdapter(emptyList())
        binding.viewPager.adapter = galleryAdapter

        // 데이터 관찰 및 View 업데이트
        galleryViewModel.personData.observe(viewLifecycleOwner) { personDetailsList ->
            if (personDetailsList.isNotEmpty()) {
                // ViewPager2 어댑터에 데이터 전달
                galleryAdapter.updateData(personDetailsList)

                // TabLayoutMediator로 탭과 ViewPager 연결
                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    tab.text = personDetailsList[position].name
                }.attach()
            }
        }
        /*
        galleryViewModel.giftData.observe(viewLifecycleOwner) { giftList ->
            val recyclerView = binding.recommendedGiftsRecyclerView
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            //recyclerView.adapter = RecommendedGiftsAdapter(requireContext(), giftList)

            // 수동으로 샘플 데이터 설정 test
            val testGifts = listOf(
                GiftItem(
                    name = "테스트 선물",
                    imagePath = "smart_speaker", // 테스트용 샘플 이미지
                    price = "$100.00",
                    recommendation = listOf("Test")
                )
            )
            recyclerView.adapter = RecommendedGiftsAdapter(requireContext(), testGifts)

        }

        // Load gift data in ViewModel
        galleryViewModel.loadGifts(requireContext())*/

        // ViewModel에서 데이터 로드
        galleryViewModel.loadPersonData(requireContext())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
