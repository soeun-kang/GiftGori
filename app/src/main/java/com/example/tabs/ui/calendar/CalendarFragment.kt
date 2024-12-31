package com.example.tabs.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabs.databinding.FragmentCalendarBinding
import com.example.tabs.utils.models.Assigned
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.tabs.R
import com.example.tabs.utils.models.NameOccasion
import com.example.tabs.utils.models.Occasion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CalendarViewModel
    private var unfoldAssignedList: List<Assigned> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewModel 가져옴
        viewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getNameOccasionList(date: Date): List<NameOccasion> {
        val nameOccasionList = mutableListOf<NameOccasion>()
        for (assigned in unfoldAssignedList) {
            for (occasion in assigned.occasions) {
                if (occasion.date == date) {
                    val nameOccasion = NameOccasion(assigned.name, occasion.occasion)
                    nameOccasionList.add(nameOccasion)
                }
            }
        }
        return nameOccasionList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView: CalendarView = binding.calendarView
        val recyclerView = binding.calendarRecyclerView // binding을 사용해서 가져옵니다.
        recyclerView.layoutManager = LinearLayoutManager(context) // LayoutManager 설정

        // 세부 일정 표시
        val nameOccasionList = mutableListOf<NameOccasion>()
        val calendar: Calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDateString = formatter.format(calendar.time)
        val eventDate = formatter.parse(formattedDateString) ?: Date()
        // unfoldAssignedList에서 eventDate를 date로 가지고 있는 occasion들을 name과 occasion으로 새로운 NameOccasion을 만들어 nameOccasionList에 추가
        nameOccasionList.addAll(getNameOccasionList(eventDate))
        recyclerView.adapter = CalendarAdapter(nameOccasionList)

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val nameOccasionList = mutableListOf<NameOccasion>()
                val calendar: Calendar = eventDay.calendar
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDateString = formatter.format(calendar.time)
                val eventDate = formatter.parse(formattedDateString) ?: Date()
                // unfoldAssignedList에서 eventDate를 date로 가지고 있는 occasion들을 name과 occasion으로 새로운 NameOccasion을 만들어 nameOccasionList에 추가
                nameOccasionList.addAll(getNameOccasionList(eventDate))
                recyclerView.adapter = CalendarAdapter(nameOccasionList)
            }
        })


        viewModel.assignedList.observe(viewLifecycleOwner) { assignedList ->
            // 값이 바뀌었을 때 작동
            // 기념일을 받아 캘린더에 Icon 표시
            val events: MutableList<EventDay> = mutableListOf()
            val assignList: MutableList<Assigned> = mutableListOf()
            for (assigned in assignedList) {
                for (occasion in assigned.occasions) {
                    val exOccur = extendOccasion(occasion)
                    for(exOc in exOccur){
                        assignList.add(Assigned(assigned.name, listOf(exOc)))
                    }
                }
            }
            unfoldAssignedList = assignList.filter { true }
            assignList.groupBy { it.occasions[0].date }.forEach { (date, assigns) ->
                val calendar = Calendar.getInstance()
                /*
                 * If occasion in occasions has a 'occasion' of "Birthday" only, icon_event_1_bDay.
                 * If occasion in occasions has a 'occasion' of "Parents' Day" only, icon_event_1_pDay.
                 * If occasion in occasions has a 'occasion' of "Wedding Anniversary" only, icon_event_1_wAnniversary.
                 * If occasion in occasions has a 'occasion' that haven't be clarified, icon_event_1.
                 * If occasion in occasions has 'occasion' of "Birthday" and "Parents' Day", icon_event_2_bP.
                 * If occasion in occasions has 'occasion' of "Birthday" and "Wedding Anniversary", icon_event_2_bW.
                 * If occasion in occasions has 'occasion' of "Parents' Day" and "Wedding Anniversary", icon_event_2_pW.
                 * If occasion in occasions has 'occasion' of "Birthday" and some occasion that haven't be clarified, icon_event_2_b.
                 * If occasion in occasions has 'occasion' of "Parents' Day" and some occasion that haven't be clarified, icon_event_2_p.
                 * If occasion in occasions has 'occasion' of "Wedding Anniversary" and some occasion that haven't be clarified, icon_event_2_w.
                 * If occasion in occasions has 'occasion' of "Birthday" and "Parents' Day" and "Wedding Anniversary", icon_event_3_bPW.
                 * If occasion in occasions has 'occasion' of "Birthday" and "Parents' Day" and some occasion that haven't be clarified, icon_event_3_bP.
                 * If occasion in occasions has 'occasion' of "Birthday" and "Wedding Anniversary" and some occasion that haven't be clarified, icon_event_3_bW.
                 * If occasion in occasions has 'occasion' of "Parents' Day" and "Wedding Anniversary" and some occasion that haven't be clarified, icon_event_3_pW.
                 */
                calendar.time = date
                val occasionsList = assigns.map { it.occasions[0].occasion }
                val icon = getOccasionIcon(occasionsList)
                events.add(EventDay(calendar, icon))
            }
            calendarView.setEvents(events)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAssigned()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // occasion을 받아서 매년있는 것이라면 현재~현재+10년치 occasion 반환
    private fun extendOccasion(occasion: Occasion): List<Occasion> {
        val res = mutableListOf<Occasion>()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        if(occasion.occasion == getString(R.string.birthday)
            || occasion.occasion == getString(R.string.parents_day)
            || occasion.occasion == getString(R.string.wedding_anniversary)) {
            // 년마다 돌아오는 기념일이면 당일부터 지금으로부터 10년후까지 매년 생일에 EventDay 추가
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            calendar.time = occasion.date
            val bDayYear = calendar.get(Calendar.YEAR)
            val bDayMonth = calendar.get(Calendar.MONTH)
            val bDayDay = calendar.get(Calendar.DAY_OF_MONTH)
            for (year in currentYear..currentYear + 10) {
                val rcalendar = Calendar.getInstance()
                rcalendar.set(Calendar.YEAR, year)
                rcalendar.set(Calendar.MONTH, bDayMonth)
                rcalendar.set(Calendar.DAY_OF_MONTH, bDayDay)
                val formattedDateString = formatter.format(rcalendar.time)
                val roccasion = Occasion(occasion.occasion, formatter.parse(formattedDateString) ?: Date())
                res.add(roccasion)
            }
        }
        else {
            res.add(occasion)
        }
        return res
    }

    // occasion들을 받아 해당하는 icon 반환
    private fun getOccasionIcon(occasions: List<String>): Int{
        val hasBirthday = occasions.contains(getString(R.string.birthday))
        val hasParentsDay = occasions.contains(getString(R.string.parents_day))
        val hasWeddingAnniversary = occasions.contains(getString(R.string.wedding_anniversary))
        val hasUnknown = occasions.any { it != getString(R.string.birthday) && it != getString(R.string.parents_day) && it != getString(R.string.wedding_anniversary) }

        return when {
            hasBirthday && !hasParentsDay && !hasWeddingAnniversary && !hasUnknown -> R.drawable.icon_event_1_bday
            !hasBirthday && hasParentsDay && !hasWeddingAnniversary && !hasUnknown -> R.drawable.icon_event_1_pday
            !hasBirthday && !hasParentsDay && hasWeddingAnniversary && !hasUnknown -> R.drawable.icon_event_1_wanniversary
            occasions.size == 1 && hasUnknown -> R.drawable.icon_event_1
            hasBirthday && hasParentsDay && !hasWeddingAnniversary -> R.drawable.icon_event_2_bp
            hasBirthday && hasWeddingAnniversary && !hasParentsDay -> R.drawable.icon_event_2_bw
            hasParentsDay && hasWeddingAnniversary && !hasBirthday -> R.drawable.icon_event_2_pw
            hasBirthday && hasUnknown -> R.drawable.icon_event_2_b
            hasParentsDay && hasUnknown -> R.drawable.icon_event_2_p
            hasWeddingAnniversary && hasUnknown -> R.drawable.icon_event_2_w
            hasBirthday && hasParentsDay && hasWeddingAnniversary -> R.drawable.icon_event_3_bpw
            hasBirthday && hasParentsDay && hasUnknown -> R.drawable.icon_event_3_bp
            hasBirthday && hasWeddingAnniversary && hasUnknown -> R.drawable.icon_event_3_bw
            hasParentsDay && hasWeddingAnniversary && hasUnknown -> R.drawable.icon_event_3_pw
            else -> R.drawable.icon_event_1
        }
    }
}