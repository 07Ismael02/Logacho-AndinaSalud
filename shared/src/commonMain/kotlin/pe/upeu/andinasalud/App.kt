package pe.upeu.andinasalud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.navigation.AppNavHost
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.citas.ResumenCitasViewModel

@Composable
fun App() {
    val perfilViewModel = koinViewModel<PerfilViewModel>()
    val perfil by perfilViewModel.uiState.collectAsState()
    val resumenViewModel = koinViewModel<ResumenCitasViewModel>()
    val resumen by resumenViewModel.uiState.collectAsState()
    AndinaSaludTheme(perfil.oscuro) { AppNavHost(perfilViewModel, resumen) }
}
