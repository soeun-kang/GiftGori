package com.example.tabs

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tabs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding   // 이걸로 UI 호출

    // 생성되면 실행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_main.xml을 가져와 여기서 바인딩
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)    // 화면 전체의 UI 설정: 파일의 최상위 뷰를 나타냄

        // activity_main.xml에 정의된 BottomNavigationView를 찾아서 navView 변수에 할당
        val navView: BottomNavigationView = binding.navView

        // activity_main.xml에 정의된 nav_host_fragment_activity_main 뷰를 찾아서
        // 해당 뷰와 연결된 NavController를 가져옵니다
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // navigation_home, navigation_contacts, navigation_notifications를
        // 최상위 목적지로 설정하는 AppBarConfiguration 객체를 생성
        // 최상위 목적지는 앱 바에 뒤로 가기 버튼이 표시되지 않는 목적지를 의미
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_contacts, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}