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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.presentation.components.Cargando
import pe.upeu.andinasalud.presentation.components.EstadoError

@Composable
fun InicioScreen(repository: CitaRepository, irCitas: () -> Unit, solicitar: () -> Unit) {
    var datos by remember { mutableStateOf<Result<Pair<Paciente, Cita?>>?>(null) }
    fun cargar() { datos = null }
    LaunchedEffect(datos) {
        if (datos == null) datos = runCatching {
            repository.obtenerPaciente() to repository.obtenerCitas().firstOrNull { it.estado is pe.upeu.andinasalud.domain.model.EstadoCita.Programada }
        }
    }
    when {
        datos == null -> Cargando()
        datos!!.isFailure -> EstadoError("No se pudo cargar el inicio", ::cargar)
        else -> {
            val (paciente, proxima) = datos!!.getOrThrow()
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
                    Button(solicitar, Modifier.weight(1f)) { Text("Solicitar cita") }
                }
            }
        }
    }
}
