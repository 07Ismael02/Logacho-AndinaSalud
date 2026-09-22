package pe.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.Cargando
import pe.upeu.andinasalud.presentation.components.EstadoError

@Composable
fun InicioScreen(viewModel: InicioViewModel, puedeSolicitar: Boolean, irCitas: () -> Unit, solicitar: () -> Unit) {
    val estado by viewModel.uiState.collectAsState()
    when (val actual = estado) {
        InicioUiState.Cargando -> Cargando()
        is InicioUiState.Error -> EstadoError(actual.mensaje, viewModel::cargar)
        is InicioUiState.Contenido -> {
            val paciente = actual.paciente
            val proxima = actual.proximaCita
            Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Hola, ${paciente.nombre}", style = MaterialTheme.typography.headlineMedium)
                Text("Gestiona tu atención en AndinaSalud")
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Próxima cita", style = MaterialTheme.typography.titleMedium)
                    if (proxima == null) Text("No tienes citas programadas") else {
                        Text(proxima.especialidad, style = MaterialTheme.typography.titleLarge)
                        Text("${proxima.fecha} · ${proxima.hora}")
                        Text("${proxima.medico} · ${proxima.sede}")
                    }
                } }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(irCitas, Modifier.weight(1f)) { Text("Mis citas") }
                    Button(solicitar, Modifier.weight(1f), enabled = puedeSolicitar) { Text("Solicitar cita") }
                }
                if (!puedeSolicitar) Text("Límite de 3 citas programadas alcanzado", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
