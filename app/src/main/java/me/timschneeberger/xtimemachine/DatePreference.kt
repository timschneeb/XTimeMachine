package me.timschneeberger.xtimemachine

import android.content.Context
import android.content.res.TypedArray
import androidx.preference.DialogPreference
import android.util.AttributeSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DatePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    var date: LocalDate = LocalDate.now()

    override fun getSummary(): CharSequence? {
        return localized()
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val value: String = if (restoreValue) {
            if (defaultValue == null) {
                getPersistedString(LocalDate.now().toString())
            } else {
                getPersistedString(defaultValue.toString())
            }
        } else {
            defaultValue!!.toString()
        }

        date = LocalDate.parse(value)
    }

    fun persistDateValue(value: LocalDate) {
        summary = localized()
        persistString(value.toString())
    }

    private fun localized() = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
}