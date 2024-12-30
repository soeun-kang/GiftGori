package com.example.tabs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tabs.databinding.ActivityMainBinding
import com.example.tabs.utils.ManageJson
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_STORAGE_PERMISSION = 100

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

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_calendar, R.id.navigation_contacts, R.id.navigation_gallery
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Navigation Bar 클릭 동작 명시적으로 처리
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    navController.navigate(R.id.navigation_calendar)
                    true
                }
                R.id.navigation_contacts -> { // Contacts로 이동 시 항상 새로 생성
                    navController.popBackStack(R.id.navigation_contacts, true)
                    navController.navigate(R.id.navigation_contacts)
                    true
                }
                R.id.navigation_gallery -> {
                    navController.navigate(R.id.navigation_gallery)
                    true
                }
                else -> false
            }
        }
        // 외부 저장소 접근 권한 요청
        requestStoragePermission()
        writeToInternalStorage()
    }

    private fun writeToInternalStorage() {
        val manageJson = ManageJson(this)
        val jsonString = manageJson.getJsonDataFromAsset("contacts.json")
        if (jsonString != null) {
            manageJson.writeFileToInternalStorage("contacts.json", jsonString)
        }
        val jsonString2 = manageJson.getJsonDataFromAsset("occasion.json")
        if (jsonString2 != null) {
            manageJson.writeFileToInternalStorage("occasion.json", jsonString2)
        }
    }
    // 검색 기능
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // 검색 기능 실행
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun requestStoragePermission() {
        // Android 6.0 이상에서 권한 체크 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됨 - 외부 저장소 접근 가능
            }else {
                // 권한 거부됨 - 사용자에게 알림 또는 처리
            }
        }
    }
}
