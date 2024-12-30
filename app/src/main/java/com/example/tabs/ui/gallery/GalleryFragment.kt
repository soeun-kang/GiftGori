package com.example.tabs.ui.gallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.tabs.R
import com.example.tabs.databinding.FragmentGalleryBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.util.Log

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var galleryAdapter: GalleryAdapter

    // Debugging 태그
    private val TAG = "GalleryFragmentDebug"

    // Handler for debounce
    private val searchHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: 시작됨")
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupMenuProvider()
        Log.d(TAG, "setupMenuProvider 호출 완료")

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        // ViewPager2 어댑터 설정
        galleryAdapter = GalleryAdapter(emptyList())
        binding.viewPager.adapter = galleryAdapter
        Log.d(TAG, "ViewPager2 어댑터 설정 완료")

        // 데이터 관찰 및 View 업데이트
        galleryViewModel.personData.observe(viewLifecycleOwner) { personDetailsList ->
            Log.d(TAG, "personData 변경 감지: ${personDetailsList?.size ?: 0}개의 데이터")
            if (personDetailsList.isNotEmpty()) {
                galleryAdapter.updateData(personDetailsList)

                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    tab.text = personDetailsList[position].name
                }.attach()

                val contactIndex = arguments?.getInt("contactIndex", 0) ?: 0
                binding.viewPager.setCurrentItem(contactIndex, false)
                Log.d(TAG, "TabLayout 및 ViewPager 설정 완료")
            } else {
                Log.w(TAG, "personDetailsList가 비어 있음")
            }
        }
        galleryViewModel.loadPersonData(requireContext())
        Log.d(TAG, "loadPersonData 호출 완료")

        return root
    }

    override fun onResume() {
        super.onResume()
        val contactIndex = arguments?.getInt("contactIndex", 0) ?: 0
        binding.viewPager.setCurrentItem(contactIndex, false)
        Log.d(TAG, "onResume 호출됨: contactIndex=$contactIndex")
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                Log.d(TAG, "onCreateMenu 호출") //여기에서 오래 걸리는 것 같음! 이전에는 viewpager가 안뜸ㅎ
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

                searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.d(TAG, "onQueryTextSubmit: $query")
                        query?.let { performSearch(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.d(TAG, "onQueryTextChange: $newText")
                        newText?.let { updateSearchResults(it) }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        Log.d(TAG, "검색 메뉴 선택됨")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun performSearch(query: String) {
        try {
            Log.d(TAG, "performSearch 시작: $query")
            val personDetailsList = galleryViewModel.personData.value
            if (personDetailsList.isNullOrEmpty()) {
                Log.w(TAG, "performSearch: personDetailsList가 비어 있음")
                binding.tabLayout.removeAllTabs()
                galleryAdapter.updateData(emptyList())
                return
            }

            val filteredList = if (query.isBlank()) {
                personDetailsList // 검색어가 공란이면 전체 데이터를 사용
            } else {
                personDetailsList.filter { personDetails ->
                    personDetails.name.contains(query, ignoreCase = true)
                }
            }

            if (filteredList.isEmpty()) {
                Log.w(TAG, "performSearch: 검색 결과 없음")
                binding.tabLayout.removeAllTabs()
                galleryAdapter.updateData(emptyList())
                return
            }

            galleryAdapter.updateData(filteredList)

            binding.tabLayout.removeAllTabs()
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                if (position < filteredList.size) {
                    tab.text = filteredList[position].name
                } else {
                    Log.w(TAG, "TabLayoutMediator: 잘못된 position 접근 $position, 리스트 크기: ${filteredList.size}")
                }
            }.attach()

            binding.viewPager.post {
                binding.viewPager.adapter = galleryAdapter
            }

            if (filteredList.size < binding.viewPager.adapter?.itemCount ?: 0) {
                Log.w(TAG, "performSearch: 이전 데이터 크기와 불일치, 초기화 진행")
                binding.viewPager.adapter = null // ViewPager 초기화
            }

            Log.d(TAG, "performSearch 완료: ${filteredList.size}개의 결과")
        } catch (e: Exception) {
            Log.e(TAG, "performSearch 중 예외 발생", e)
        }
    }

    private fun updateSearchResults(newText: String) {
        Log.d(TAG, "updateSearchResults 호출: $newText")
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed({
            performSearch(newText)
        }, 300) // 300ms 디바운스 추가
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView 호출")
        super.onDestroyView()
        _binding = null
    }
}

