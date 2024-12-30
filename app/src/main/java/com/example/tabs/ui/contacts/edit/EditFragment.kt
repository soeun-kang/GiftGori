package com.example.tabs.ui.contacts.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.tabs.utils.models.Contact
import com.example.tabs.utils.models.Occasion
import com.example.tabs.utils.models.PresentHistory
import java.util.Date
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import com.example.tabs.R

class EditFragment : Fragment() {

    private lateinit var name: String
    private lateinit var phoneNumber: String
    private lateinit var bDay: Date
    private lateinit var gender: String
    private lateinit var group: String
    private lateinit var occasions: List<Occasion>
    private var recentContact: Int = -1
    private lateinit var presentHistory: List<PresentHistory>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 파일을 inflate하여 View 객체 생성
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("view created")

        val contact = arguments?.getSerializable("contact") as? Contact
        contact?.let {
            name = it.name
            phoneNumber = it.phoneNumber
            bDay = it.bDay
            gender = it.gender
            group = it.group
            occasions = it.occasions
            recentContact = it.recentContact
            presentHistory = it.presentHistory
        }

        if(contact == null){

        }
        else{
            val nameEditText = view.findViewById<EditText>(R.id.editName)
            nameEditText.setText(name)

            val phoneNumberEditText = view.findViewById<EditText>(R.id.editPhoneNumber)
            phoneNumberEditText.setText(phoneNumber)

            val birthdayEditText = view.findViewById<EditText>(R.id.editBirthday)
            birthdayEditText.setText(bDay.toString())

            val genderEdit = view.findViewById<RadioGroup>(R.id.radioGroupGender)
            if(gender == "Male")
                genderEdit.check(R.id.radioMale)
            else
                genderEdit.check(R.id.radioFemale)

            val spinnerRelationship: Spinner = view.findViewById(R.id.spinnerRelationship)
            val relationships = resources.getStringArray(R.array.relationships)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, relationships)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRelationship.adapter = adapter

            val defaultRelationship = group
            val defaultPosition = relationships.indexOf(defaultRelationship)
            if (defaultPosition != -1) {
                spinnerRelationship.setSelection(defaultPosition)
            }

            // occasions 설정

            // presentHistory 설정

        }



    }
}