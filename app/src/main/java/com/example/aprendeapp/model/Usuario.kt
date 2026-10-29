package com.example.aprendeapp.model

data class Usuario(
    val id: String? = null,
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val rol: String = ROL_ESTUDIANTE
) {
    companion object {
        const val ROL_ESTUDIANTE = "estudiante"
        const val ROL_DOCENTE = "docente"
    }
}