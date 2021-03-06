package com.maltepeuniversiyesi.stok.helpers

import android.content.Context
import com.maltepeuniversiyesi.stok.models.LoginForm
import com.google.gson.Gson

/**
 * Created by olyanren on 24.01.2018.
 * SharedPreferencesUtil
 */
object SharedPreferencesUtil {
    fun writeLoginForm(context: Context, loginForm: LoginForm?) {
        write(context, "loginForm", loginForm)
    }

    fun write(context: Context, name: String?, `object`: Any?) {
        write(context, name, Gson().toJson(`object`))
    }

    fun write(context: Context, name: String?, value: String?) {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        preferences.edit().putString(name, value).apply()
    }


    fun readLoginForm(context: Context): LoginForm {
        return read(context, "loginForm", LoginForm::class.java)
    }

    fun <T> read(context: Context, name: String?, clazz: Class<T>?): T {
        return Gson().fromJson(read(context, name), clazz)
    }

    fun read(context: Context, name: String?): String? {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return preferences.getString(name, "")
    }

}