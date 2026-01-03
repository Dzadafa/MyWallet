package com.dzadafa.mywallet.ui.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dzadafa.mywallet.ReminderScheduler
import com.dzadafa.mywallet.ThemeManager
import com.dzadafa.mywallet.databinding.FragmentSettingsBinding
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.ui.budget.ManageBudgetsActivity
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private var currentReminderHour: Int = 21
    private var currentReminderMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnManageBudgets.setOnClickListener {
            startActivity(Intent(requireContext(), ManageBudgetsActivity::class.java))
        }

        setupThemeRadioGroup()
        setupReminderSettings()
    }
    
    private fun setupThemeRadioGroup() {
        when (ThemeManager.getCurrentTheme(requireContext())) {
            ThemeManager.LIGHT_MODE -> binding.rbLight.isChecked = true
            ThemeManager.DARK_MODE -> binding.rbDark.isChecked = true
            else -> binding.rbSystem.isChecked = true
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val themeMode = when (checkedId) {
                R.id.rb_light -> ThemeManager.LIGHT_MODE
                R.id.rb_dark -> ThemeManager.DARK_MODE
                else -> ThemeManager.SYSTEM_DEFAULT
            }
            ThemeManager.saveTheme(requireContext(), themeMode)
        }
    }
    
    private fun setupReminderSettings() {
        binding.swReminders.isChecked = ReminderScheduler.getIsReminderEnabled(requireContext())
        val (hour, minute) = ReminderScheduler.getReminderTime(requireContext())
        currentReminderHour = hour
        currentReminderMinute = minute
        
        updateReminderTimeText()
        updateReminderTimePickerVisibility()

        binding.swReminders.setOnCheckedChangeListener { _, isChecked ->
            saveReminderSettings()
            updateReminderTimePickerVisibility()
        }
        
        binding.tvReminderTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                currentReminderHour = hourOfDay
                currentReminderMinute = minute
                updateReminderTimeText()
                saveReminderSettings()
            },
            currentReminderHour,
            currentReminderMinute,
            false
        )
        timePickerDialog.show()
    }
    
    private fun saveReminderSettings() {
        ReminderScheduler.saveReminderSetting(
            requireContext(),
            binding.swReminders.isChecked,
            currentReminderHour,
            currentReminderMinute
        )
    }

    private fun updateReminderTimeText() {
        binding.tvReminderTime.text = String.format(Locale.getDefault(), "%02d:%02d", currentReminderHour, currentReminderMinute)
    }
    
    private fun updateReminderTimePickerVisibility() {
        val visibility = if (binding.swReminders.isChecked) View.VISIBLE else View.GONE
        binding.tvReminderTimeLabel.visibility = visibility
        binding.tvReminderTime.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
