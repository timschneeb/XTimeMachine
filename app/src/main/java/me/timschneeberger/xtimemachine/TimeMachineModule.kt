package me.timschneeberger.xtimemachine

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.util.Date
import java.util.Locale


class TimeMachineModule : IXposedHookLoadPackage {

    private val prefs by lazy {
        XSharedPreferences(
            "me.timschneeberger.xtimemachine",
            "me.timschneeberger.xtimemachine_preferences"
        ).also {
            it.makeWorldReadable()
        }
    }

    private fun combine(date: Date, time: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = time
        val hour = cal[Calendar.HOUR_OF_DAY]
        val min = cal[Calendar.MINUTE]
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = min
        return cal.time
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        XposedBridge.log("Loaded: ${lpparam.packageName}")

        try {
            prefs.reload()

            XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass("java.lang.System", lpparam.classLoader),
                "currentTimeMillis",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val newTimestamp = combine(
                            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(
                                prefs.getString("fakeDate", "2022-01-01")
                            ),
                            Calendar.getInstance().time
                        )

                        XposedBridge.log("System.currentTimeMillis intercepted ($newTimestamp)")
                        param.result = newTimestamp.time
                    }
                })

        } catch (e: Exception) {
            XposedBridge.log(e)
        }
    }
}