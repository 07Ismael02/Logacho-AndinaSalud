package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresClaros = lightColorScheme(
    primary = AzulSalud,
    secondary = TurquesaSalud,
    primaryContainer = CelesteClaro,
    background = FondoClaro,
    surface = Color.White
)

private val ColoresOscuros = darkColorScheme(
    primary = AzulNoche,
    secondary = TurquesaNoche,
    background = FondoOscuro,
    surface = SuperficieOscura
)

@Composable
fun AndinaSaludTheme(oscuro: Boolean, contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) ColoresOscuros else ColoresClaros,
        typography = TipografiaAndinaSalud,
        content = contenido
    )
}
