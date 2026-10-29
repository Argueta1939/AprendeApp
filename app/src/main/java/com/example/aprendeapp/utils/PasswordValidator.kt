package com.example.aprendeapp.utils

/**
 * Resultado de validar una contraseña: cada regla se evalúa por separado
 * para poder mostrarle al usuario exactamente qué le falta.
 */
data class PasswordValidationResult(
    val longitudValida: Boolean,
    val tieneMayuscula: Boolean,
    val tieneMinuscula: Boolean,
    val tieneNumero: Boolean,
    val tieneEspecial: Boolean
) {
    val esValida: Boolean
        get() = longitudValida && tieneMayuscula && tieneMinuscula &&
                tieneNumero && tieneEspecial
}

object PasswordValidator {

    const val LONGITUD_MINIMA = 12
    private const val CARACTERES_ESPECIALES = "!@#$%^&*"

    fun validar(password: String): PasswordValidationResult {
        return PasswordValidationResult(
            longitudValida = password.length >= LONGITUD_MINIMA,
            tieneMayuscula = password.any { it.isUpperCase() },
            tieneMinuscula = password.any { it.isLowerCase() },
            tieneNumero = password.any { it.isDigit() },
            tieneEspecial = password.any { it in CARACTERES_ESPECIALES }
        )
    }
}