package com.example.aprendeapp.utils

import android.content.Context

class UserDataManager(context: Context) {
    private val prefs = context.getSharedPreferences("datos_usuario", Context.MODE_PRIVATE)

    fun obtenerFavoritos(userId: String): Set<String> =
        prefs.getStringSet("fav_$userId", emptySet())?.toSet() ?: emptySet()

    fun guardarFavoritos(userId: String, ids: Set<String>) {
        prefs.edit().putStringSet("fav_$userId", ids).apply()
    }

    fun obtenerCalificacion(userId: String, recursoId: String): Int =
        prefs.getInt("rate_${userId}_$recursoId", 0)

    fun guardarCalificacion(userId: String, recursoId: String, estrellas: Int) {
        prefs.edit().putInt("rate_${userId}_$recursoId", estrellas).apply()
    }
}