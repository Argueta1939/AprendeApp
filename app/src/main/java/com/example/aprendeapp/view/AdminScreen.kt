package com.example.aprendeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aprendeapp.R
import com.example.aprendeapp.controller.RecursoController
import com.example.aprendeapp.model.Recurso

@Composable
fun AdminScreen(controller: RecursoController) {
    var enEdicion by remember { mutableStateOf<Recurso?>(null) }
    var mostrarForm by remember { mutableStateOf(false) }
    var aEliminar by remember { mutableStateOf<Recurso?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text(
                stringResource(R.string.admin_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            if (controller.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
            LazyColumn(
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(controller.recursos, key = { it.id ?: it.titulo }) { r ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = r.imagen,
                                contentDescription = r.titulo,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.small)
                            )
                            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text(r.titulo, style = MaterialTheme.typography.titleSmall)
                                Text(etiquetaTipo(r.tipo), style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { enEdicion = r; mostrarForm = true }) {
                                Icon(Icons.Filled.Edit, stringResource(R.string.cd_editar))
                            }
                            IconButton(onClick = { aEliminar = r }) {
                                Icon(
                                    Icons.Filled.Delete, stringResource(R.string.cd_eliminar),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { enEdicion = null; mostrarForm = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, stringResource(R.string.cd_agregar))
        }
    }

    if (mostrarForm) {
        RecursoFormDialog(
            inicial = enEdicion,
            onDismiss = { mostrarForm = false },
            onGuardar = { controller.guardarRecurso(it); mostrarForm = false }
        )
    }

    aEliminar?.let { r ->
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            title = { Text(stringResource(R.string.confirm_eliminar_titulo)) },
            text = { Text(stringResource(R.string.confirm_eliminar_msg, r.titulo)) },
            confirmButton = {
                TextButton(onClick = {
                    controller.eliminarRecurso(r.id.orEmpty())
                    aEliminar = null
                }) {
                    Text(stringResource(R.string.btn_eliminar), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { aEliminar = null }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        )
    }
}