package com.example.tabs.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.tabs.databinding.FragmentGalleryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        galleryViewModel.loadNames(requireContext())

        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        // Adapter setup
        galleryAdapter = GalleryAdapter(emptyList()) // Initialize with empty list
        viewPager.adapter = galleryAdapter

        // Observe names and update tabs and adapter
        galleryViewModel.names.observe(viewLifecycleOwner) { names ->
            if (names.isNotEmpty()) {
                Log.d("GalleryFragment", "Names loaded successfully: $names")
                galleryAdapter.updateData(names)

                // Setup TabLayoutMediator
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = names[position]
                }.attach()
            } else {
                Log.d("GalleryFragment", "Names list is empty")
            }
        }

        galleryViewModel.personDetails.observe(viewLifecycleOwner) { details ->
            binding.personDetailsTextView.text = details
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}