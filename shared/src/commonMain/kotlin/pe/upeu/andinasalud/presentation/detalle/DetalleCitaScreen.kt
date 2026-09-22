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
import pe.upeu.andinasalud.presentation.components.IconoModalidad
import androidx.compose.foundation.layout.Row

@Composable
fun DetalleCitaScreen(id: Long, viewModel: DetalleCitaViewModel, alCancelar: () -> Unit) {
    val estado by viewModel.uiState.collectAsState()
    var dialogo by remember { mutableStateOf(false) }
    var dialogoReprogramar by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }
    var nuevaFecha by remember { mutableStateOf("") }
    var nuevaHora by remember { mutableStateOf("") }
    LaunchedEffect(id) { viewModel.cargar(id) }
    when (val actual = estado) {
        DetalleUiState.Cargando -> Cargando()
        is DetalleUiState.Error -> EstadoError(actual.mensaje) { viewModel.cargar(id) }
        is DetalleUiState.Contenido -> Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LaunchedEffect(actual.mensaje) {
                if (actual.mensaje == "Cita reprogramada correctamente") dialogoReprogramar = false
            }
            Text(actual.cita.especialidad, style = MaterialTheme.typography.headlineSmall)
            Text("Médico: ${actual.cita.medico}"); Text("Sede: ${actual.cita.sede}")
            Text("Fecha: ${actual.cita.fecha}"); Text("Hora: ${actual.cita.hora}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconoModalidad(actual.cita.modalidad)
                Text("Modalidad: ${actual.cita.modalidad.etiqueta}")
            }
            Text("Motivo: ${actual.cita.motivo}")
            Text("Estado: ${actual.cita.estado::class.simpleName}", color = MaterialTheme.colorScheme.primary)
            (actual.cita.estado as? EstadoCita.Atendida)?.let { Text("Indicaciones: ${it.indicaciones}") }
            (actual.cita.estado as? EstadoCita.Cancelada)?.let { Text("Motivo de cancelación: ${it.motivo}") }
            actual.cita.reprogramaciones.forEachIndexed { indice, cambio ->
                Text("Reprogramación ${indice + 1}: ${cambio.fechaAnterior} ${cambio.horaAnterior} → ${cambio.fechaNueva} ${cambio.horaNueva}")
            }
            actual.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            if (actual.puedeReprogramar) Button({
                nuevaFecha = actual.cita.fecha.toString()
                nuevaHora = actual.cita.hora.toString()
                dialogoReprogramar = true
            }, Modifier.fillMaxWidth()) { Text("Reprogramar cita") }
            if (actual.puedeCancelar) Button({ dialogo = true }, Modifier.fillMaxWidth()) { Text("Cancelar cita") }
        }
    }
    if (dialogo) AlertDialog(
        onDismissRequest = { dialogo = false }, title = { Text("Confirmar cancelación") },
        text = { OutlinedTextField(
            motivo, { motivo = it }, label = { Text("Motivo (10 a 200 caracteres)") },
            isError = motivo.isNotEmpty() && motivo.trim().length !in 10..200,
            supportingText = { if (motivo.isNotEmpty() && motivo.trim().length !in 10..200) Text("Ingresa entre 10 y 200 caracteres") }
        ) },
        confirmButton = { TextButton({ dialogo = false; viewModel.cancelar(id, motivo, alCancelar) }, enabled = motivo.trim().length in 10..200) { Text("Confirmar") } },
        dismissButton = { TextButton({ dialogo = false }) { Text("Volver") } }
    )
    if (dialogoReprogramar) {
        val errores = (estado as? DetalleUiState.Contenido)?.erroresReprogramacion
        AlertDialog(
            onDismissRequest = { dialogoReprogramar = false },
            title = { Text("Reprogramar cita") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(nuevaFecha, { nuevaFecha = it }, label = { Text("Nueva fecha (AAAA-MM-DD)") }, isError = errores?.fecha != null)
                errores?.fecha?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                OutlinedTextField(nuevaHora, { nuevaHora = it }, label = { Text("Nueva hora (HH:MM)") }, isError = errores?.hora != null)
                errores?.hora?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                errores?.general?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            } },
            confirmButton = { TextButton({ viewModel.reprogramar(id, nuevaFecha, nuevaHora) }) { Text("Confirmar") } },
            dismissButton = { TextButton({ dialogoReprogramar = false }) { Text("Volver") } }
        )
    }
}
