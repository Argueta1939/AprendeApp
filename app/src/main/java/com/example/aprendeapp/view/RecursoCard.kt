package com.example.aprendeapp.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aprendeapp.R
import com.example.aprendeapp.model.Recurso

val TIPOS_RECURSO = listOf("libro", "video", "articulo")

@Composable
fun etiquetaTipo(tipo: String): String = when (tipo) {
    "libro" -> stringResource(R.string.tipo_libro)
    "video" -> stringResource(R.string.tipo_video)
    "articulo" -> stringResource(R.string.tipo_articulo)
    else -> tipo
}

private val Dorado = Color(0xFFFFB300)

@Composable
fun RecursoCard(
    recurso: Recurso,
    esFavorito: Boolean,
    miCalificacion: Int,
    esEstudiante: Boolean,
    onFavorito: () -> Unit,
    onCalificar: (Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = recurso.imagen,
                contentDescription = recurso.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        recurso.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (esEstudiante) {
                        IconButton(onClick = onFavorito) {
                            Icon(
                                if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(R.string.cd_favorito),
                                tint = if (esFavorito) Color(0xFFE53935)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(onClick = {}, label = { Text(etiquetaTipo(recurso.tipo)) })
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.label_id, recurso.id.orEmpty()),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    recurso.descripcion,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Dorado, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (recurso.totalVotos == 0) stringResource(R.string.sin_calificaciones)
                        else stringResource(
                            R.string.rating_promedio,
                            recurso.calificacionPromedio,
                            recurso.totalVotos
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    recurso.enlace,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { runCatching { uriHandler.openUri(recurso.enlace) } }
                )
                if (esEstudiante) {
                    Text(
                        stringResource(R.string.tu_calificacion),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row {
                        (1..5).forEach { i ->
                            IconButton(onClick = { onCalificar(i) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    if (i <= miCalificacion) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = null,
                                    tint = Dorado
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}