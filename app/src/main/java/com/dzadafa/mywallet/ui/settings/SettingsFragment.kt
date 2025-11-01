package com.dzadafa.mywallet.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dzadafa.mywallet.ThemeManager
import com.dzadafa.mywallet.databinding.FragmentSettingsBinding
import com.dzadafa.mywallet.R

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
