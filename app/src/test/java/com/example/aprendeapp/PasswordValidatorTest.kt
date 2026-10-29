package com.example.aprendeapp

import com.example.aprendeapp.utils.PasswordValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun contrasenaValida_cumpleTodasLasReglas() {
        assertTrue(PasswordValidator.validar("Segura#Clave123").esValida)
    }

    @Test
    fun contrasenaCorta_noEsValida() {
        assertFalse(PasswordValidator.validar("Ab1#").esValida)
    }

    @Test
    fun sinEspecial_noEsValida() {
        assertFalse(PasswordValidator.validar("SinEspecial1234").esValida)
    }

    @Test
    fun sinMayuscula_noEsValida() {
        assertFalse(PasswordValidator.validar("sin#mayuscula123").esValida)
    }
}