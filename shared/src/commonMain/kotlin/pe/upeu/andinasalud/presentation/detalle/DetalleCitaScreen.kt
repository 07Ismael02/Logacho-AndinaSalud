package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.components.Cargando
import pe.upeu.andinasalud.presentation.components.EstadoError

@Composable
fun DetalleCitaScreen(id: Long, viewModel: DetalleCitaViewModel, alCancelar: () -> Unit) {
    val estado by viewModel.uiState.collectAsState()
    var dialogo by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }
    LaunchedEffect(id) { viewModel.cargar(id) }
    when (val actual = estado) {
        DetalleUiState.Cargando -> Cargando()
        is DetalleUiState.Error -> EstadoError(actual.mensaje) { viewModel.cargar(id) }
        is DetalleUiState.Contenido -> Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(actual.cita.especialidad, style = MaterialTheme.typography.headlineSmall)
            Text("Médico: ${actual.cita.medico}"); Text("Sede: ${actual.cita.sede}")
            Text("Fecha: ${actual.cita.fecha}"); Text("Hora: ${actual.cita.hora}")
            Text("Motivo: ${actual.cita.motivo}")
            Text("Estado: ${actual.cita.estado::class.simpleName}", color = MaterialTheme.colorScheme.primary)
            (actual.cita.estado as? EstadoCita.Atendida)?.let { Text("Indicaciones: ${it.indicaciones}") }
            (actual.cita.estado as? EstadoCita.Cancelada)?.let { Text("Motivo de cancelación: ${it.motivo}") }
            if (actual.puedeCancelar) Button({ dialogo = true }, Modifier.fillMaxWidth()) { Text("Cancelar cita") }
        }
    }
    if (dialogo) AlertDialog(
        onDismissRequest = { dialogo = false }, title = { Text("Confirmar cancelación") },
        text = { OutlinedTextField(motivo, { motivo = it }, label = { Text("Motivo (10 a 200 caracteres)") }) },
        confirmButton = { TextButton({ dialogo = false; viewModel.cancelar(id, motivo, alCancelar) }) { Text("Confirmar") } },
        dismissButton = { TextButton({ dialogo = false }) { Text("Volver") } }
    )
}
