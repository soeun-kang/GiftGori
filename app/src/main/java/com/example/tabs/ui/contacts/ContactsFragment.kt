package com.example.tabs.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabs.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: ContactsViewModel? = null
    private val viewModel get() = _viewModel!!
    private var _adapter: ContactsAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        _viewModel =
            ViewModelProvider(this).get(ContactsViewModel::class.java)
        viewModel.manageJson.logging()

        val contactAdapter = ContactsAdapter(viewModel.manageJson.dataList)
        binding.recyclerView.adapter = contactAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
        _adapter = null
    }
}