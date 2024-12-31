package com.example.tabs.ui.contacts

import android.app.AlertDialog
import android.content.Context
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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
import androidx.lifecycle.MutableLiveData
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

        // EditFragment에서 수정된 데이터를 받는 로직
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Contact>("editedContact")?.observe(viewLifecycleOwner) { editedContact ->
            // 수정된 데이터를 받아서 처리하는 로직
            val currentList = viewModel.contactList.value?.toMutableList() ?: mutableListOf()
            val index = currentList.indexOfFirst { it.name == editedContact.name } // 이름으로 찾기에 이름을 변경하면 변경사항이 저장안됨 ㅋㅋ
            if (index != -1) {
                currentList[index] = editedContact
                viewModel.updateContactList(currentList)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadContacts()
        viewModel.loadAssigned()
        binding.recyclerView.scrollToPosition(0)
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
            assignButton.text = getString(R.string.unassign)
        }
        else{
            assignButton.text = getString(R.string.assign)
        }

        popupInitial.text = "" + contact.name[0]
        popupName.text = contact.name
        val bDayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        popupBirthday.text = bDayFormatter.format(contact.bDay)
        popupPhoneNumber.text = getString(R.string.tel_phonenumber) + " " + contact.phoneNumber
        popupRecentContact.text = getString(R.string.recentcontact) + " " + contact.recentContact.toString()

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

        // 수정버튼 클릭 시
        val editButton = popupView.findViewById<ImageButton>(R.id.editButton)
        editButton.setOnClickListener {
            // 현재 contact bundle로 전송
            val bundle = Bundle()
            bundle.putSerializable("contact", contact)
            findNavController().navigate(R.id.contact_to_edit, bundle)
            popupWindow?.dismiss()
        }

        // 삭제버튼 클릭 시
        val deleteButton = popupView.findViewById<ImageButton>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(requireContext(), contact)
            popupWindow?.dismiss()
        }

        // 팝업 중 선물 목록 클릭 시
        val presentHistoryButton = popupView.findViewById<Button>(R.id.buttonPresentHistory)
        val presentHistoryLayout = popupView.findViewById<LinearLayout>(R.id.presentHistoryLayout)
        presentHistoryButton.setOnClickListener {
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

        // 일정 등록 버튼
        assignButton.setOnClickListener {
            val gson = Gson()
            if(isAssigned){
                assignButton.text = getString(R.string.unassign)
                // remove name from occasion.json
                _assignedList = _assignedList.filter { it.name != contact.name }
                val jsonString = gson.toJson(_assignedList)
                // write to occasion.json in asset
                val manageJson = ManageJson(requireContext())
                manageJson.writeFileToInternalStorage("occasion.json", jsonString)
                popupWindow?.dismiss()
                Toast.makeText(requireContext(), getString(R.string.occasion_dismiss_success), Toast.LENGTH_SHORT).show()
            }else{
                assignButton.text = getString(R.string.assign)
                // add name to occasion.json
                var occasions: List<Occasion> = contact.occasions
                occasions = occasions + Occasion(getString(R.string.birthday), contact.bDay)
                val assigned = Assigned(contact.name, occasions)
                _assignedList = _assignedList + assigned
                val jsonString = gson.toJson(_assignedList)
                val manageJson = ManageJson(requireContext())
                manageJson.writeFileToInternalStorage("occasion.json", jsonString)
                popupWindow?.dismiss()
                Toast.makeText(requireContext(), getString(R.string.occasion_assign_success), Toast.LENGTH_SHORT).show()
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

    private fun showDeleteConfirmationDialog(context: Context, contact: Contact){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("삭제 확인")
        builder.setMessage("정말 삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            // "예"를 눌렀을 때 실행할 동작
            val currentList = viewModel.contactList.value?.toMutableList() ?: mutableListOf()
            val index = currentList.indexOfFirst { it.name == contact.name } // 이름으로 찾기에 이름을 변경하면 변경사항이 저장안됨 ㅋㅋ
            if (index != -1) {
                currentList.removeAt(index)
                viewModel.updateContactList(currentList)
            }
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            // "아니오"를 눌렀을 때 다이얼로그 닫기
            dialog.dismiss()
        }
        builder.create().show()
    }
}