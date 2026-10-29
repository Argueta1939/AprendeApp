package com.example.aprendeapp.view

import androidx.compose.runtime.Composable
import com.example.aprendeapp.R
import com.example.aprendeapp.controller.RecursoController

@Composable
fun FavoritosScreen(controller: RecursoController) {
    ListaRecursos(controller, true, controller.recursosFavoritos, R.string.sin_favoritos)
}