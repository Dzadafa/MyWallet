package com.dzadafa.mywallet

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var filterMenuItem: MenuItem? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        askNotificationPermission()
    }
    
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        filterMenuItem = menu.findItem(R.id.action_filter)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            R.id.action_settings -> NavigationUI.onNavDestinationSelected(item, navController)
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showFilterDialog() {
        val filterOptions = listOf(
            FilterManager.FilterType.THIS_MONTH,
            FilterManager.FilterType.LAST_3_MONTHS,
            FilterManager.FilterType.THIS_YEAR,
            FilterManager.FilterType.ALL_TIME,
        )

        val filterNames = listOf(
            getString(R.string.filter_current_month),
            getString(R.string.last_3_months),
            getString(R.string.this_year),
            getString(R.string.all_time),
            getString(R.string.select_month)
        )
        
        val (currentType, currentYear, currentMonth) = FilterManager.getFilterState(this)

        AlertDialog.Builder(this)
            .setTitle(R.string.filter_by)
            .setItems(filterNames.toTypedArray()) { dialog, which ->
                when (which) {
                    0 -> setFilterAndRefresh(FilterManager.FilterType.THIS_MONTH)
                    1 -> setFilterAndRefresh(FilterManager.FilterType.LAST_3_MONTHS)
                    2 -> setFilterAndRefresh(FilterManager.FilterType.THIS_YEAR)
                    3 -> setFilterAndRefresh(FilterManager.FilterType.ALL_TIME)
                    4 -> showMonthPickerDialog(currentYear, currentMonth)
                }
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showMonthPickerDialog(initialYear: Int, initialMonth: Int) {
        val cal = Calendar.getInstance()
        
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, _ ->
            setFilterAndRefresh(FilterManager.FilterType.THIS_MONTH, year, month)
        }
        
        val monthPicker = DatePickerDialog(
            this,
            dateSetListener,
            initialYear,
            initialMonth,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        monthPicker.datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android"))?.visibility = View.GONE
        monthPicker.show()
    }
    
    private fun setFilterAndRefresh(type: FilterManager.FilterType, year: Int = Calendar.getInstance().get(Calendar.YEAR), month: Int = Calendar.getInstance().get(Calendar.MONTH)) {
        FilterManager.saveFilterState(this, type, year, month)
        navController.currentDestination?.let {
            navController.popBackStack(it.id, true)
            navController.navigate(it.id)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
