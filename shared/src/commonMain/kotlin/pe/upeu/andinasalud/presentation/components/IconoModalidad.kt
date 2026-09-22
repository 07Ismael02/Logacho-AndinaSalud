package pe.upeu.andinasalud.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import pe.upeu.andinasalud.domain.model.Modalidad

@Composable
fun IconoModalidad(modalidad: Modalidad) {
    val icono = when (modalidad) {
        Modalidad.Presencial -> Icons.Default.Place
        Modalidad.Teleconsulta -> Icons.Default.Videocam
    }
    Icon(icono, contentDescription = modalidad.etiqueta)
}
