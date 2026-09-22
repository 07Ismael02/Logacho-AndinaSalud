package pe.upeu.andinasalud

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import pe.upeu.andinasalud.presentation.navigation.AppNavHost

@Composable
fun App() {
    var oscuro by rememberSaveable { mutableStateOf(false) }
    MaterialTheme { AppNavHost(oscuro, { oscuro = it }) }
}
