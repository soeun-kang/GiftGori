package com.example.tabs.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabs.R
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
        // 검색 기능
        setupMenuProvider()

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        // ViewPager2 어댑터 설정
        galleryAdapter = GalleryAdapter(emptyList())
        binding.viewPager.adapter = galleryAdapter

        // 데이터 관찰 및 View 업데이트
        galleryViewModel.personData.observe(viewLifecycleOwner) { personDetailsList ->
            if (personDetailsList.isNotEmpty()) {
                galleryAdapter.updateData(personDetailsList)

                // 탭과 ViewPager 연결
                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    tab.text = personDetailsList[position].name
                }.attach()

                // Set initial tab if contactIndex is provided
                val contactIndex = arguments?.getInt("contactIndex", 0) ?: 0
                binding.viewPager.setCurrentItem(contactIndex, false)
            }
        }
        galleryViewModel.loadPersonData(requireContext())

        return root
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

                // Set up search query listener
                searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { performSearch(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { updateSearchResults(it) }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle menu item clicks here if needed
                return when (menuItem.itemId) {
                    R.id.action_search -> true
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun performSearch(query: String) {
        galleryViewModel.personData.value?.let { personDetailsList ->
            val filteredList = personDetailsList.filter { personDetails ->
                personDetails.name.contains(query, ignoreCase = true)
            }

            if (filteredList.isEmpty()) {
                binding.tabLayout.removeAllTabs()
                galleryAdapter.updateData(emptyList())
                return
            }

            binding.tabLayout.clearOnTabSelectedListeners()
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = filteredList[position].name
            }.attach()
            galleryAdapter.updateData(filteredList)
        }
    }

    private fun updateSearchResults(newText: String) {
        performSearch(newText)
    }

    override fun onResume() {
        super.onResume()
        val contactIndex = arguments?.getInt("contactIndex", 0) ?: 0
        binding.viewPager.setCurrentItem(contactIndex, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
