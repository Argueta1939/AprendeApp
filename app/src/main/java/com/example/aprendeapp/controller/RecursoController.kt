package com.example.aprendeapp.controller

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprendeapp.R
import com.example.aprendeapp.model.Recurso
import com.example.aprendeapp.network.RetrofitClient
import com.example.aprendeapp.utils.UserDataManager
import kotlinx.coroutines.launch

enum class Orden { TITULO_ASC, TITULO_DESC, TIPO }

class RecursoController(app: Application) : AndroidViewModel(app) {

    private val api = RetrofitClient.api
    private val datos = UserDataManager(app)
    private var usuarioId: String = ""

    var recursos by mutableStateOf<List<Recurso>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<Int?>(null)
    var busqueda by mutableStateOf("")
    var filtroTipo by mutableStateOf<String?>(null)
    var orden by mutableStateOf(Orden.TITULO_ASC)
    var favoritos by mutableStateOf<Set<String>>(emptySet())
        private set
    var calificaciones by mutableStateOf<Map<String, Int>>(emptyMap())
        private set

    // ---------- Listas derivadas ----------
    val recursosFiltrados: List<Recurso>
        get() {
            val q = busqueda.trim().lowercase()
            val lista = recursos.filter { r ->
                (filtroTipo == null || r.tipo == filtroTipo) &&
                        (q.isEmpty() ||
                                r.id == q ||
                                r.titulo.lowercase().contains(q) ||
                                r.tipo.lowercase().contains(q))
            }
            return when (orden) {
                Orden.TITULO_ASC -> lista.sortedBy { it.titulo.lowercase() }
                Orden.TITULO_DESC -> lista.sortedByDescending { it.titulo.lowercase() }
                Orden.TIPO -> lista.sortedWith(compareBy({ it.tipo }, { it.titulo.lowercase() }))
            }
        }

    val recursosFavoritos: List<Recurso>
        get() = recursos.filter { it.id in favoritos }

    // ---------- Sesión ----------
    fun iniciarSesion(userId: String) {
        usuarioId = userId
        favoritos = datos.obtenerFavoritos(userId)
        busqueda = ""
        filtroTipo = null
        cargarRecursos()
    }

    // ---------- Lectura ----------
    fun cargarRecursos() {
        viewModelScope.launch {
            cargando = true
            try {
                val resp = api.obtenerRecursos()
                if (resp.isSuccessful) {
                    recursos = resp.body().orEmpty()
                    calificaciones = recursos.mapNotNull { r ->
                        val id = r.id ?: return@mapNotNull null
                        val c = datos.obtenerCalificacion(usuarioId, id)
                        if (c > 0) id to c else null
                    }.toMap()
                } else if (resp.code() == 404) {
                    recursos = emptyList()
                } else {
                    mensaje = R.string.error_red
                }
            } catch (e: Exception) {
                mensaje = R.string.error_red
            }
            cargando = false
        }
    }

    // ---------- Favoritos ----------
    fun toggleFavorito(recurso: Recurso) {
        val id = recurso.id ?: return
        val agregar = id !in favoritos
        favoritos = if (agregar) favoritos + id else favoritos - id
        datos.guardarFavoritos(usuarioId, favoritos)
        mensaje = if (agregar) R.string.msg_favorito_agregado else R.string.msg_favorito_quitado
    }

    // ---------- Calificación (PUT al backend) ----------
    fun calificar(recurso: Recurso, estrellas: Int) {
        val id = recurso.id ?: return
        val previa = calificaciones[id] ?: 0
        val votos = recurso.totalVotos
        val nuevosVotos = if (previa == 0) votos + 1 else votos
        val nuevaSuma = recurso.calificacionPromedio * votos - previa + estrellas
        val promedio = Math.round(nuevaSuma / nuevosVotos * 10) / 10.0
        val actualizado = recurso.copy(calificacionPromedio = promedio, totalVotos = nuevosVotos)

        viewModelScope.launch {
            try {
                val resp = api.actualizarRecurso(id, actualizado)
                if (resp.isSuccessful) {
                    datos.guardarCalificacion(usuarioId, id, estrellas)
                    calificaciones = calificaciones + (id to estrellas)
                    recursos = recursos.map { if (it.id == id) actualizado else it }
                    mensaje = R.string.msg_calificacion_guardada
                } else {
                    mensaje = R.string.error_red
                }
            } catch (e: Exception) {
                mensaje = R.string.error_red
            }
        }
    }

    // ---------- CRUD (docente) ----------
    fun guardarRecurso(recurso: Recurso) {
        viewModelScope.launch {
            cargando = true
            try {
                val id = recurso.id
                val resp = if (id == null) api.crearRecurso(recurso)
                else api.actualizarRecurso(id, recurso)
                if (resp.isSuccessful) {
                    mensaje = if (id == null) R.string.msg_recurso_creado
                    else R.string.msg_recurso_actualizado
                    cargarRecursos()
                } else {
                    mensaje = R.string.error_red
                    cargando = false
                }
            } catch (e: Exception) {
                mensaje = R.string.error_red
                cargando = false
            }
        }
    }

    fun eliminarRecurso(id: String) {
        viewModelScope.launch {
            cargando = true
            try {
                val resp = api.eliminarRecurso(id)
                if (resp.isSuccessful) {
                    mensaje = R.string.msg_recurso_eliminado
                    cargarRecursos()
                } else {
                    mensaje = R.string.error_red
                    cargando = false
                }
            } catch (e: Exception) {
                mensaje = R.string.error_red
                cargando = false
            }
        }
    }
}