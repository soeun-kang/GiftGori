package com.example.tabs.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tabs.R
import com.example.tabs.databinding.FragmentContactsBinding
import com.example.tabs.ui.contacts.popup.PresentHistoryAdapter
import com.example.tabs.ui.gallery.GalleryFragment
import com.example.tabs.utils.models.Contact
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
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

        popupInitial.text = "" + contact.name[0]
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

        // 팝업 중 선물 목록 클릭 시
        val presentHistoryButton = popupView.findViewById<Button>(R.id.buttonPresentHistory)
        val presentHistoryLayout = popupView.findViewById<LinearLayout>(R.id.presentHistoryLayout)
        presentHistoryButton.setOnClickListener {
            println("Clicked presentHistoryLayout")
            val isVisible = presentHistoryLayout.visibility == View.VISIBLE
            presentHistoryLayout.visibility = if (isVisible) View.GONE else View.VISIBLE
            if (!isVisible) {
                // recyclerView 값 넣기
                val recyclerView =
                    popupView.findViewById<RecyclerView>(R.id.recyclerViewPresentHistory)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = PresentHistoryAdapter(contact.presentHistory)

            }
        }

        // 추천 선물 탭으로 이동
        val buttonRecommendation = popupView.findViewById<Button>(R.id.buttonRecommendation)
        buttonRecommendation.setOnClickListener {
            popupWindow?.dismiss()

            // Find the index of the clicked contact
            val contactIndex =
                viewModel.contactList.value?.indexOfFirst { it.name == contact.name } ?: -1
            if (contactIndex != -1) {
                Log.d(
                    "ContactsFragment",
                    "ContactIndex: $contactIndex for Contact: ${contact.name}"
                )

                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
                val bundle = Bundle()
                bundle.putInt("contactIndex", contactIndex)

                navController.navigate(R.id.navigation_gallery, bundle)
            } else {
                println("Contact not found in Contactlist")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.scrollToPosition(0)
    }

}