package com.example.aprendeapp.utils

import java.security.MessageDigest

object HashUtils {
    fun sha256(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}