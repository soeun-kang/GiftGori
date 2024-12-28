package com.example.tabs.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.databinding.FragmentContactsBinding
import com.example.tabs.ui.contacts.popup.PresentHistoryAdapter
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.models.Assigned
import com.example.tabs.utils.models.Contact
import com.example.tabs.utils.models.Occasion
import com.google.gson.Gson
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

    private var _assignedList: List<Assigned> = listOf()

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
        // assignedList 관찰
        viewModel.assignedList.observe(viewLifecycleOwner) { assignedList ->
            // 값이 바뀌었을 때 작동
            _assignedList = assignedList.map {
                assigned ->  assigned.copy(occasions = assigned.occasions.map { it.copy() })
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadContacts()
        viewModel.loadAssigned()
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
        val popupView = layoutInflater.inflate(R.layout.popup_contact_detail, null)

        val popupInitial = popupView.findViewById<TextView>(R.id.popupInitial)
        val popupName = popupView.findViewById<TextView>(R.id.popupName)
        val popupBirthday = popupView.findViewById<TextView>(R.id.popupBirthday)
        val popupPhoneNumber = popupView.findViewById<TextView>(R.id.popupPhoneNumber)
        val popupRecentContact = popupView.findViewById<TextView>(R.id.popupRecentContact)
        // 버튼 텍스트 설정
        val assignButton = popupView.findViewById<Button>(R.id.buttonAssign)
        val isAssigned: Boolean = (_assignedList.any { it.name == contact.name })
        // 이 사람은 이미 기념일을 챙김
        if(isAssigned) {
            assignButton.text = "Unassign"
        }
        else{
            assignButton.text = "Assign"
        }

        popupInitial.text = ""+contact.name[0]
        popupName.text = contact.name
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
            val isVisible = presentHistoryLayout.visibility == View.VISIBLE
            presentHistoryLayout.visibility = if (isVisible) View.GONE else View.VISIBLE
            if(!isVisible){
                // recyclerView 값 넣기
                val recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerViewPresentHistory)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = PresentHistoryAdapter(contact.presentHistory)

            }
        }


        assignButton.setOnClickListener {
            val gson = Gson()
            if(isAssigned){
                assignButton.text = "Unassign"
                // remove name from occasion.json
                _assignedList = _assignedList.filter { it.name != contact.name }
                val jsonString = gson.toJson(_assignedList)
                // write to occasion.json in asset
                val manageJson = ManageJson(requireContext())
                manageJson.writeFileToInternalStorage("occasion.json", jsonString)
                popupWindow?.dismiss()
                Toast.makeText(requireContext(), getString(R.string.occasion_dismiss_success), Toast.LENGTH_SHORT).show()
            }else{
                assignButton.text = "Assign"
                // add name to occasion.json
                var occasions: List<Occasion> = contact.occasions
                occasions = occasions + Occasion("Birthday", contact.bDay)
                println(occasions)
                val assigned = Assigned(contact.name, occasions)
                _assignedList = _assignedList + assigned
                println(_assignedList)
                val jsonString = gson.toJson(_assignedList)
                val manageJson = ManageJson(requireContext())
                manageJson.writeFileToInternalStorage("occasion.json", jsonString)
                popupWindow?.dismiss()
                Toast.makeText(requireContext(), getString(R.string.occasion_assign_success), Toast.LENGTH_SHORT).show()
            }
        }


    }
}