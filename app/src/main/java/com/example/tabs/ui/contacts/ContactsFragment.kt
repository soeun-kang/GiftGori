package com.example.tabs.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabs.R
import com.example.tabs.databinding.FragmentContactsBinding
import com.example.tabs.utils.models.Contact
import java.text.SimpleDateFormat
import java.util.Locale

class ContactsFragment : Fragment(), OnItemClickListener {

    // xml파일 바인딩
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    // 변수를 나중에 초기화 가능
    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: ContactsAdapter
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewModel 가져옴
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
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
        popupWindow?.dismiss()
        popupWindow = null
    }

    override fun onItemClick(contact: Contact) { // OnItemClickListener 인터페이스의 메서드 구현
        //println("Clicked item: $contact")
        showContactPopup(contact)
    }

    private fun showContactPopup(contact: Contact) {
        println("Clicked item: $contact")
        val popupView = layoutInflater.inflate(R.layout.popup_contact_detail, null)

        val popupInitial = popupView.findViewById<TextView>(R.id.popupInitial)
        val popupName = popupView.findViewById<TextView>(R.id.popupName)
        val popupBirthday = popupView.findViewById<TextView>(R.id.popupBirthday)
        val popupPhoneNumber = popupView.findViewById<TextView>(R.id.popupPhoneNumber)
        val popupRecentContact = popupView.findViewById<TextView>(R.id.popupRecentContact)

        popupInitial.text = ""+contact.name[0]
        popupName.text = contact.name
        println(popupName.text)
        val bDayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        popupBirthday.text = bDayFormatter.format(contact.bDay)
        popupPhoneNumber.text = "Tel: " + contact.phoneNumber
        popupRecentContact.text = "recentContact: " + contact.recentContact.toString()

        // popupWindow가 null인 경우에만 새로운 PopupWindow 객체를 생성
        if (popupWindow == null) {
            popupWindow = PopupWindow(
                popupView,
                810,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                isFocusable = true
                setBackgroundDrawable(ColorDrawable(android.graphics.Color.WHITE))
            }
        } else {
            popupWindow?.contentView = popupView
        }

        // 팝업 윈도우 표시
        popupWindow?.showAtLocation(
            binding.root,
            Gravity.CENTER,
            0,
            0
        )
    }
}