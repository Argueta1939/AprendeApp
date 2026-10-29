package com.example.aprendeapp.view

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.R
import com.example.aprendeapp.model.Recurso

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursoFormDialog(
    inicial: Recurso?,
    onDismiss: () -> Unit,
    onGuardar: (Recurso) -> Unit
) {
    var titulo by remember { mutableStateOf(inicial?.titulo.orEmpty()) }
    var descripcion by remember { mutableStateOf(inicial?.descripcion.orEmpty()) }
    var tipo by remember { mutableStateOf(inicial?.tipo.orEmpty()) }
    var enlace by remember { mutableStateOf(inicial?.enlace.orEmpty()) }
    var imagen by remember { mutableStateOf(inicial?.imagen.orEmpty()) }
    var intentado by remember { mutableStateOf(false) }
    var menuTipo by remember { mutableStateOf(false) }

    val errTitulo = titulo.isBlank()
    val errDescripcion = descripcion.trim().length < 10
    val errTipo = tipo !in TIPOS_RECURSO
    val errEnlace = !Patterns.WEB_URL.matcher(enlace.trim()).matches()
    val errImagen = !Patterns.WEB_URL.matcher(imagen.trim()).matches()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (inicial == null) R.string.titulo_nuevo_recurso
                    else R.string.titulo_editar_recurso
                )
            )
        },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoForm(titulo, { titulo = it }, R.string.label_titulo, R.string.error_titulo, intentado && errTitulo)
                CampoForm(descripcion, { descripcion = it }, R.string.label_descripcion, R.string.error_descripcion, intentado && errDescripcion, singleLine = false)

                ExposedDropdownMenuBox(expanded = menuTipo, onExpandedChange = { menuTipo = it }) {
                    OutlinedTextField(
                        value = if (tipo.isEmpty()) "" else etiquetaTipo(tipo),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_tipo)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipo) },
                        isError = intentado && errTipo,
                        supportingText = { if (intentado && errTipo) Text(stringResource(R.string.error_tipo)) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = menuTipo, onDismissRequest = { menuTipo = false }) {
                        TIPOS_RECURSO.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(etiquetaTipo(t)) },
                                onClick = { tipo = t; menuTipo = false }
                            )
                        }
                    }
                }

                CampoForm(enlace, { enlace = it }, R.string.label_enlace, R.string.error_url, intentado && errEnlace)
                CampoForm(imagen, { imagen = it }, R.string.label_imagen, R.string.error_url, intentado && errImagen)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                intentado = true
                if (!(errTitulo || errDescripcion || errTipo || errEnlace || errImagen)) {
                    onGuardar(
                        (inicial ?: Recurso()).copy(
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            tipo = tipo,
                            enlace = enlace.trim(),
                            imagen = imagen.trim()
                        )
                    )
                }
            }) { Text(stringResource(R.string.btn_guardar)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancelar)) }
        }
    )
}

@Composable
private fun CampoForm(
    valor: String,
    onCambio: (String) -> Unit,
    @StringRes label: Int,
    @StringRes error: Int,
    mostrarError: Boolean,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(stringResource(label)) },
        singleLine = singleLine,
        isError = mostrarError,
        supportingText = { if (mostrarError) Text(stringResource(error)) },
        modifier = Modifier.fillMaxWidth()
    )
}