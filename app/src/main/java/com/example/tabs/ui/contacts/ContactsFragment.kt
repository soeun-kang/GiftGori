package com.example.tabs.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabs.databinding.FragmentContactsBinding
import com.example.tabs.utils.models.Contact

class ContactsFragment : Fragment(), OnItemClickListener {

    // xml파일 바인딩
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    // 변수를 나중에 초기화 가능
    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewModel 가져옴
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        viewModel.loadContacts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.manageJson.logging()

        // recyclerView 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // adapter 설정
        viewModel.contactList.observe(viewLifecycleOwner) { contacts ->
            adapter = ContactsAdapter(contacts, this)
            binding.recyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(contact: Contact) { // OnItemClickListener 인터페이스의 메서드 구현
        println("Clicked item: $contact")
    }
}