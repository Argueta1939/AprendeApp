package com.example.aprendeapp.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.R
import com.example.aprendeapp.controller.Orden
import com.example.aprendeapp.controller.RecursoController
import com.example.aprendeapp.model.Recurso

@Composable
private fun etiquetaOrden(o: Orden): String = when (o) {
    Orden.TITULO_ASC -> stringResource(R.string.orden_titulo_asc)
    Orden.TITULO_DESC -> stringResource(R.string.orden_titulo_desc)
    Orden.TIPO -> stringResource(R.string.orden_tipo)
}

@Composable
fun HomeScreen(controller: RecursoController, esEstudiante: Boolean) {
    var menuOrden by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = controller.busqueda,
                onValueChange = { controller.busqueda = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.hint_buscar)) },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (controller.busqueda.isNotEmpty()) {
                        IconButton(onClick = { controller.busqueda = "" }) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.cd_limpiar))
                        }
                    }
                }
            )
            Box {
                IconButton(onClick = { menuOrden = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, stringResource(R.string.cd_ordenar))
                }
                DropdownMenu(expanded = menuOrden, onDismissRequest = { menuOrden = false }) {
                    Orden.entries.forEach { o ->
                        DropdownMenuItem(
                            text = { Text(etiquetaOrden(o)) },
                            onClick = { controller.orden = o; menuOrden = false }
                        )
                    }
                }
            }
            IconButton(onClick = { controller.cargarRecursos() }) {
                Icon(Icons.Filled.Refresh, stringResource(R.string.cd_refrescar))
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = controller.filtroTipo == null,
                    onClick = { controller.filtroTipo = null },
                    label = { Text(stringResource(R.string.filtro_todos)) }
                )
            }
            items(TIPOS_RECURSO) { tipo ->
                FilterChip(
                    selected = controller.filtroTipo == tipo,
                    onClick = {
                        controller.filtroTipo = if (controller.filtroTipo == tipo) null else tipo
                    },
                    label = { Text(etiquetaTipo(tipo)) }
                )
            }
        }

        ListaRecursos(controller, esEstudiante, controller.recursosFiltrados, R.string.sin_resultados)
    }
}

@Composable
fun ListaRecursos(
    controller: RecursoController,
    esEstudiante: Boolean,
    lista: List<Recurso>,
    @StringRes vacio: Int
) {
    Box(Modifier.fillMaxSize()) {
        if (controller.cargando && controller.recursos.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else if (lista.isEmpty()) {
            Text(
                stringResource(vacio),
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista, key = { it.id ?: it.titulo }) { r ->
                    RecursoCard(
                        recurso = r,
                        esFavorito = r.id in controller.favoritos,
                        miCalificacion = r.id?.let { controller.calificaciones[it] } ?: 0,
                        esEstudiante = esEstudiante,
                        onFavorito = { controller.toggleFavorito(r) },
                        onCalificar = { controller.calificar(r, it) }
                    )
                }
            }
        }
        if (controller.cargando && controller.recursos.isNotEmpty()) {
            LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}