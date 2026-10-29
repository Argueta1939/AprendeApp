package com.example.aprendeapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aprendeapp.R
import com.example.aprendeapp.controller.AuthController
import com.example.aprendeapp.controller.RecursoController
import com.example.aprendeapp.model.Usuario

private data class Destino(val ruta: String, val titulo: Int, val icono: ImageVector)

@Composable
fun AppNavigation(auth: AuthController) {
    val usuario = auth.usuario
    if (usuario == null) AuthNavHost(auth) else MainScaffold(auth, usuario)
}

@Composable
private fun AuthNavHost(auth: AuthController) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "login") {
        composable("login") {
            LoginScreen(auth) { auth.limpiarError(); nav.navigate("register") }
        }
        composable("register") {
            RegisterScreen(auth) { auth.limpiarError(); nav.popBackStack() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    auth: AuthController,
    usuario: Usuario,
    controller: RecursoController = viewModel()
) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val esDocente = usuario.rol == Usuario.ROL_DOCENTE

    LaunchedEffect(usuario.id) { controller.iniciarSesion(usuario.id.orEmpty()) }

    // Toast como feedback visual de cualquier operación
    val textoMensaje = controller.mensaje?.let { stringResource(it) }
    LaunchedEffect(textoMensaje) {
        textoMensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            controller.mensaje = null
        }
    }

    val destinos = listOf(
        Destino("home", R.string.nav_inicio, Icons.Filled.Home),
        if (esDocente) Destino("admin", R.string.nav_admin, Icons.Filled.Settings)
        else Destino("favoritos", R.string.nav_favoritos, Icons.Filled.Favorite)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    Text(
                        stringResource(R.string.bienvenido_usuario, usuario.nombre),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 140.dp)
                    )
                    IconButton(onClick = { auth.cerrarSesion() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, stringResource(R.string.cd_logout))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val entrada by nav.currentBackStackEntryAsState()
                val actual = entrada?.destination?.route
                destinos.forEach { d ->
                    NavigationBarItem(
                        selected = actual == d.ruta,
                        onClick = {
                            nav.navigate(d.ruta) {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(d.icono, contentDescription = null) },
                        label = { Text(stringResource(d.titulo)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(controller, esEstudiante = !esDocente) }
            composable("favoritos") { FavoritosScreen(controller) }
            composable("admin") { AdminScreen(controller) }
        }
    }
}