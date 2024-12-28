package com.example.tabs.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.example.tabs.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView: CalendarView = binding.calendarView
        // CalendarView 설정
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // 날짜 선택 시 이벤트 처리 (나중에 구현)
            println("Selected date: $year-${month + 1}-$dayOfMonth")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}