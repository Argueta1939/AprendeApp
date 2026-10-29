package com.example.aprendeapp.utils

import android.content.Context
import com.example.aprendeapp.model.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardar(u: Usuario) {
        prefs.edit()
            .putString("id", u.id)
            .putString("nombre", u.nombre)
            .putString("email", u.email)
            .putString("rol", u.rol)
            .apply()
    }

    fun obtener(): Usuario? {
        val id = prefs.getString("id", null) ?: return null
        return Usuario(
            id = id,
            nombre = prefs.getString("nombre", "").orEmpty(),
            email = prefs.getString("email", "").orEmpty(),
            rol = prefs.getString("rol", Usuario.ROL_ESTUDIANTE).orEmpty()
        )
    }

    fun cerrar() {
        prefs.edit().clear().apply()
    }
}