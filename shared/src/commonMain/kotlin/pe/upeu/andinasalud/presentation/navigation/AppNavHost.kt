package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.presentation.citas.CitasScreen
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaScreen
import pe.upeu.andinasalud.presentation.inicio.InicioScreen
import pe.upeu.andinasalud.presentation.perfil.PerfilScreen
import pe.upeu.andinasalud.presentation.solicitud.SolicitudScreen

private data class ItemNav(val ruta: String, val titulo: String, val icono: ImageVector)
private val principales = listOf(ItemNav(Destinos.INICIO, "Inicio", Icons.Default.Home), ItemNav(Destinos.CITAS, "Citas", Icons.Default.CalendarMonth), ItemNav(Destinos.PERFIL, "Perfil", Icons.Default.Person))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(oscuro: Boolean, cambiarTema: (Boolean) -> Unit) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route ?: Destinos.INICIO
    val esPrincipal = principales.any { it.ruta == ruta }
    val titulo = principales.firstOrNull { it.ruta == ruta }?.titulo ?: if (ruta == Destinos.SOLICITUD) "Solicitar cita" else "Detalle de cita"
    val repository: CitaRepository = koinInject()
    var citaSeleccionada by rememberSaveable { mutableStateOf(-1L) }
    Scaffold(
        topBar = { TopAppBar(title = { Text(titulo) }, navigationIcon = {
            if (!esPrincipal) IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
        }) },
        bottomBar = { if (esPrincipal) NavigationBar { principales.forEach { item ->
            NavigationBarItem(ruta == item.ruta, {
                nav.navigate(item.ruta) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true }
            }, { Icon(item.icono, item.titulo) }, label = { Text(item.titulo) })
        } } }
    ) { padding -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
      NavHost(nav, Destinos.INICIO, Modifier.fillMaxSize().widthIn(max = 900.dp)) {
        composable(Destinos.INICIO) { InicioScreen(repository, { nav.navigate(Destinos.CITAS) }, { nav.navigate(Destinos.SOLICITUD) }) }
        composable(Destinos.CITAS) { CitasScreen(koinViewModel()) { citaSeleccionada = it; nav.navigate(Destinos.DETALLE) } }
        composable(Destinos.PERFIL) { PerfilScreen(repository, oscuro, cambiarTema) }
        composable(Destinos.SOLICITUD) { SolicitudScreen(koinViewModel()) {
            nav.navigate(Destinos.CITAS) { popUpTo(Destinos.SOLICITUD) { inclusive = true }; launchSingleTop = true }
        } }
        composable(Destinos.DETALLE) {
            DetalleCitaScreen(citaSeleccionada, koinViewModel()) { nav.navigate(Destinos.CITAS) { popUpTo(Destinos.CITAS) { inclusive = true } } }
        }
      }
    } }
}
