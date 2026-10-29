package com.example.aprendeapp.view

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.R
import com.example.aprendeapp.controller.AuthController
import com.example.aprendeapp.model.Usuario
import com.example.aprendeapp.utils.PasswordValidator

@Composable
fun RegisterScreen(auth: AuthController, onVolver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf(Usuario.ROL_ESTUDIANTE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.register_title), style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it },
            label = { Text(stringResource(R.string.label_nombre)) },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text(stringResource(R.string.label_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text(stringResource(R.string.label_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        PasswordChecklist(password)
        OutlinedTextField(
            value = confirmacion, onValueChange = { confirmacion = it },
            label = { Text(stringResource(R.string.label_confirm_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Text(stringResource(R.string.label_rol), style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                Usuario.ROL_ESTUDIANTE to R.string.rol_estudiante,
                Usuario.ROL_DOCENTE to R.string.rol_docente
            ).forEach { (valor, etiqueta) ->
                FilterChip(
                    selected = rol == valor,
                    onClick = { rol = valor },
                    label = { Text(stringResource(etiqueta)) }
                )
            }
        }

        auth.error?.let {
            Text(stringResource(it), color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { auth.registrar(nombre, email, password, confirmacion, rol) },
            enabled = !auth.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (auth.cargando) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.btn_register))
            }
        }
        TextButton(onClick = onVolver) { Text(stringResource(R.string.link_ir_login)) }
    }
}

@Composable
fun PasswordChecklist(password: String) {
    val r = PasswordValidator.validar(password)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        ReglaItem(R.string.password_rule_length, r.longitudValida)
        ReglaItem(R.string.password_rule_uppercase, r.tieneMayuscula)
        ReglaItem(R.string.password_rule_lowercase, r.tieneMinuscula)
        ReglaItem(R.string.password_rule_number, r.tieneNumero)
        ReglaItem(R.string.password_rule_special, r.tieneEspecial)
    }
}

@Composable
private fun ReglaItem(@StringRes texto: Int, cumplida: Boolean) {
    val color by animateColorAsState(
        if (cumplida) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
        label = "colorRegla"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (cumplida) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null, tint = color, modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(texto), color = color, style = MaterialTheme.typography.bodySmall)
    }
}