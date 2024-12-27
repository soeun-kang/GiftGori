package com.example.tabs.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tabs.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val contactsViewModel =
            ViewModelProvider(this).get(ContactsViewModel::class.java)
        contactsViewModel.manageJson.logging()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}