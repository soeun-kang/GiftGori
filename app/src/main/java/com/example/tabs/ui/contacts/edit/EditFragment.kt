package com.example.tabs.ui.contacts.edit

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.tabs.utils.models.Contact
import java.util.Date
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import android.widget.DatePicker
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.Locale

class EditFragment : Fragment() {

    private lateinit var newContact: Contact
    private lateinit var occasionAdapter: OccasionAdapter
    private lateinit var presentAdapter: PresentAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contact = arguments?.getSerializable("contact", Contact::class.java)

        if (contact == null) {
            newContact = Contact("", "", Date(), "", "", mutableListOf(), 0, mutableListOf())
        } else {
            newContact = contact.copy(occasions = contact.occasions.toMutableList(), presentHistory = contact.presentHistory.toMutableList())
            setupContactInfo(view)
        }

        setupOccasionDropdown(view, contact)
        setupPresentDropdown(view, contact)
        setupApplyButton(view)
        setupCancelButton(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupContactInfo(view: View) {
        view.apply {
            findViewById<EditText>(R.id.editName).setText(newContact.name)
            findViewById<EditText>(R.id.editPhoneNumber).setText(newContact.phoneNumber)
            val birthdayEditText = findViewById<EditText>(R.id.editBirthday)
            birthdayEditText.setText(dateFormat.format(newContact.bDay))
            birthdayEditText.setOnClickListener {
                showDatePickerDialog(newContact.bDay, birthdayEditText) { date ->
                    newContact.bDay = date
                    birthdayEditText.setText(dateFormat.format(newContact.bDay))
                }
            }
            val genderEdit = findViewById<RadioGroup>(R.id.radioGroupGender)
            if (newContact.gender == getString(R.string.male)) genderEdit.check(R.id.radioMale)
            else genderEdit.check(R.id.radioFemale)

            val spinnerRelationship: Spinner = findViewById(R.id.spinnerRelationship)
            val relationships = resources.getStringArray(R.array.relationships)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, relationships)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRelationship.adapter = adapter
            val defaultPosition = relationships.indexOf(newContact.group)
            if (defaultPosition != -1) spinnerRelationship.setSelection(defaultPosition)
        }
    }

    private fun setupOccasionDropdown(view: View, contact: Contact?) {
        val customDropdown = view.findViewById<View>(R.id.occasionDropdown)
        val occasionTitle = customDropdown.findViewById<TextView>(R.id.dropdownTitle)
        val occasionRecyclerView = customDropdown.findViewById<RecyclerView>(R.id.dropdownRecyclerView)
        val addButton = customDropdown.findViewById<Button>(R.id.addButton)

        occasionAdapter = OccasionAdapter(newContact.occasions, this)
        occasionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = occasionAdapter
        }

        occasionTitle.text = getString(R.string.occasion_title)
        occasionTitle.setOnClickListener {
            toggleDropdown(occasionRecyclerView, addButton)
        }
        addButton.setOnClickListener {
            occasionAdapter.addItem()
        }
    }

    private fun setupPresentDropdown(view: View, contact: Contact?) {
        val presentDropdown = view.findViewById<View>(R.id.presentDropdown)
        val presentTitle = presentDropdown.findViewById<TextView>(R.id.dropdownTitle)
        val presentRecyclerView = presentDropdown.findViewById<RecyclerView>(R.id.dropdownRecyclerView)
        val addButton = presentDropdown.findViewById<Button>(R.id.addButton)

        presentAdapter = PresentAdapter(newContact.presentHistory, this)
        presentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = presentAdapter
        }

        presentTitle.text = getString(R.string.present_title)
        presentTitle.setOnClickListener {
            toggleDropdown(presentRecyclerView, addButton)
        }
        addButton.setOnClickListener {
            presentAdapter.addItem()
        }
    }

    private fun setupApplyButton(view: View) {
        view.findViewById<Button>(R.id.buttonApply).setOnClickListener {
            newContact.apply {
                name = view.findViewById<EditText>(R.id.editName).text.toString()
                phoneNumber = view.findViewById<EditText>(R.id.editPhoneNumber).text.toString()
                gender = if (view.findViewById<RadioGroup>(R.id.radioGroupGender).checkedRadioButtonId == R.id.radioMale) getString(R.string.male) else getString(R.string.female)
                group = view.findViewById<Spinner>(R.id.spinnerRelationship).selectedItem.toString()
                occasions = occasionAdapter.getItems()
                presentHistory = presentAdapter.getItems()
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set("editedContact", newContact)
            findNavController().popBackStack()
        }
    }

    private fun setupCancelButton(view: View) {
        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun toggleDropdown(recyclerView: RecyclerView, addButton: Button) {
        val isVisible = recyclerView.visibility == View.VISIBLE
        recyclerView.visibility = if (isVisible) View.GONE else View.VISIBLE
        addButton.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDatePickerDialog(selectedDate: Date?, editView: EditText, callback: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        if (selectedDate != null) {
            calendar.time = selectedDate
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                val sDate = selectedCalendar.time
                editView.setText(dateFormat.format(sDate))
                callback(sDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}