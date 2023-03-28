package me.timschneeberger.xtimemachine

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneId

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    @Suppress("DEPRECATION")
    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is DatePreference -> {
                MaterialDatePicker.Builder.datePicker().run {
                    setSelection(preference.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    build()
                }.apply {
                    addOnPositiveButtonClickListener {
                        preference.date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (preference.callChangeListener(preference.date)) {
                            preference.persistDateValue(preference.date)
                        }
                    }
                }.run {
                    setTargetFragment(this@SettingsFragment, 0)
                    show(this@SettingsFragment.parentFragmentManager, null)
                }
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

}