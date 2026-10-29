package com.example.aprendeapp.controller

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprendeapp.R
import com.example.aprendeapp.model.Usuario
import com.example.aprendeapp.network.RetrofitClient
import com.example.aprendeapp.utils.HashUtils
import com.example.aprendeapp.utils.PasswordValidator
import com.example.aprendeapp.utils.SessionManager
import kotlinx.coroutines.launch

class AuthController(app: Application) : AndroidViewModel(app) {

    private val api = RetrofitClient.api
    private val session = SessionManager(app)

    var usuario by mutableStateOf<Usuario?>(session.obtener())
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<Int?>(null)
        private set

    fun limpiarError() {
        error = null
    }

    fun login(email: String, password: String) {
        val correo = email.trim()
        if (correo.isBlank() || password.isBlank()) {
            error = R.string.error_campos_vacios
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                val resp = api.buscarUsuarioPorEmail(correo)
                // MockAPI responde 404 cuando el filtro no encuentra nada
                if (!resp.isSuccessful && resp.code() != 404) {
                    error = R.string.error_red
                } else {
                    val encontrado = resp.body().orEmpty()
                        .firstOrNull { it.email.equals(correo, ignoreCase = true) }
                    if (encontrado != null && encontrado.password == HashUtils.sha256(password)) {
                        session.guardar(encontrado)
                        usuario = encontrado
                    } else {
                        error = R.string.error_credenciales
                    }
                }
            } catch (e: Exception) {
                error = R.string.error_red
            }
            cargando = false
        }
    }

    fun registrar(
        nombre: String,
        email: String,
        password: String,
        confirmacion: String,
        rol: String
    ) {
        val correo = email.trim()
        when {
            nombre.isBlank() || correo.isBlank() || password.isBlank() -> {
                error = R.string.error_campos_vacios; return
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                error = R.string.error_email_invalido; return
            }
            !PasswordValidator.validar(password).esValida -> {
                error = R.string.error_password_invalida; return
            }
            password != confirmacion -> {
                error = R.string.error_password_no_coincide; return
            }
        }
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                val existente = api.buscarUsuarioPorEmail(correo)
                val yaExiste = existente.isSuccessful &&
                        existente.body().orEmpty().any { it.email.equals(correo, ignoreCase = true) }
                if (yaExiste) {
                    error = R.string.error_email_existente
                } else if (!existente.isSuccessful && existente.code() != 404) {
                    error = R.string.error_red
                } else {
                    val nuevo = Usuario(
                        nombre = nombre.trim(),
                        email = correo,
                        password = HashUtils.sha256(password),
                        rol = rol
                    )
                    val resp = api.registrarUsuario(nuevo)
                    val creado = resp.body()
                    if (resp.isSuccessful && creado != null) {
                        session.guardar(creado)
                        usuario = creado
                    } else {
                        error = R.string.error_red
                    }
                }
            } catch (e: Exception) {
                error = R.string.error_red
            }
            cargando = false
        }
    }

    fun cerrarSesion() {
        session.cerrar()
        usuario = null
    }
}