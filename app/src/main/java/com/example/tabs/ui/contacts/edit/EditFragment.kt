package com.example.tabs.ui.contacts.edit

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.tabs.utils.models.Contact
import android.app.DatePickerDialog
import java.util.Date
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import kotlin.text.format
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.Locale

class EditFragment : Fragment() {

    private lateinit var newContact: Contact
    private var isOccasionDropdownVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 파일을 inflate하여 View 객체 생성
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 수정할 데이터들을 contact fragment에서 받아온다
        val contact = arguments?.getSerializable("contact", Contact::class.java) as? Contact


        // 정보 수정이 아니라 새로운 Contact 추가인 경우
        if(contact == null){

        }
        else{
            // 수정인 경우
            // 받아온 정보 풀어서 저장
            println("contact occasion in EF: ${contact.occasions}")
            contact.let {
                newContact = Contact(it.name, it.phoneNumber, it.bDay, it.gender, it.group, it.occasions, it.recentContact, it.presentHistory)
            }

            /* 해당하는 뷰 찾아서 기존 정보 표시 */
            val nameEditText = view.findViewById<EditText>(R.id.editName)
            nameEditText.setText(newContact.name)

            val phoneNumberEditText = view.findViewById<EditText>(R.id.editPhoneNumber)
            phoneNumberEditText.setText(newContact.phoneNumber)

            val birthdayEditText = view.findViewById<EditText>(R.id.editBirthday)
            // 생일
            val myFormat = "yyyy-MM-dd" // 원하는 날짜 형식
            val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
            birthdayEditText.setText(sdf.format(newContact.bDay))
            birthdayEditText.setOnClickListener{
                newContact.bDay = showDatePickerDialog(newContact.bDay, birthdayEditText)
                birthdayEditText.setText(sdf.format(newContact.bDay))
            }

            val genderEdit = view.findViewById<RadioGroup>(R.id.radioGroupGender)
            if(newContact.gender == "Male")
                genderEdit.check(R.id.radioMale)
            else
                genderEdit.check(R.id.radioFemale)

            val spinnerRelationship: Spinner = view.findViewById(R.id.spinnerRelationship)
            val relationships = resources.getStringArray(R.array.relationships)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, relationships)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRelationship.adapter = adapter
            val defaultRelationship = newContact.group
            val defaultPosition = relationships.indexOf(defaultRelationship)
            if (defaultPosition != -1) {
                spinnerRelationship.setSelection(defaultPosition)
            }

            // 기념일 설정
            val customDropdown = view.findViewById<View>(R.id.occasionDropdown)
            val occasionTitle = customDropdown.findViewById<TextView>(R.id.dropdownTitle)
            val occasionRecyclerView = customDropdown.findViewById<RecyclerView>(R.id.dropdownRecyclerView)
            println("newContact.occasions: ${newContact.occasions}")
            val occasionAdapter = OccasionAdapter(newContact.occasions, this)
            println(occasionAdapter)
            occasionRecyclerView.adapter = occasionAdapter
            occasionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            occasionTitle.text = "추가로 기념할 수 있는 날들이에요"
            occasionTitle.setOnClickListener {
                isOccasionDropdownVisible = toggleDropdown(occasionRecyclerView, isOccasionDropdownVisible)
            }

            // presentHistory 설정


        }


    }

    private fun toggleDropdown(recyclerView: RecyclerView, check: Boolean) : Boolean {
        if (check) {
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
        }
        println(recyclerView.visibility)
        return !check
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDatePickerDialog(selectedDate: Date?, editView: EditText) : Date {
        var sDate = selectedDate
        val calendar = Calendar.getInstance()
        if (selectedDate != null) {
            calendar.time = selectedDate!!
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val myFormat = "yyyy-MM-dd" // 원하는 날짜 형식
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                sDate = selectedCalendar.time
                editView.setText(sdf.format(sDate))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
        return sDate!!
    }


}