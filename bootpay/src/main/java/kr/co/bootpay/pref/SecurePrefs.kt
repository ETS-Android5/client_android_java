package kr.co.bootpay.pref

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import java.util.*

internal class SecurePrefs(private val preferences: SecurePreferences) : SharedPreferences by preferences {

    @SuppressLint("CommitPrefEdits")
    override fun edit(): SharedPreferences.Editor = preferences.edit()

    internal inner class SecureprefEditor(val editor: SharedPreferences.Editor) : SharedPreferences.Editor by editor {

        private val prefStringSet: LinkedList<StringSetPref.PrefMutableSet> by lazy { LinkedList<StringSetPref.PrefMutableSet>() }

        override fun apply() {
            editor.apply()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                prefStringSet.forEach { it.syncTransaction() }
                prefStringSet.clear()
            }
        }

        override fun commit(): Boolean {
            val result = editor.commit()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                prefStringSet.forEach { it.syncTransaction() }
                prefStringSet.clear()
            }
            return result
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        internal fun putStringSet(key: String?, values: MutableSet<String>?, prefSet: StringSetPref.PrefMutableSet): SharedPreferences.Editor {
            editor.putStringSet(key, values)
            prefStringSet.add(prefSet)
            return this
        }
    }
}