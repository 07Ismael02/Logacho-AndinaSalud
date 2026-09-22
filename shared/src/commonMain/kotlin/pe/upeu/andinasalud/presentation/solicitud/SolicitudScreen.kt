package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.Cargando
import pe.upeu.andinasalud.presentation.components.EstadoError
import pe.upeu.andinasalud.domain.model.Modalidad
import pe.upeu.andinasalud.presentation.components.IconoModalidad

@Composable
fun SolicitudScreen(viewModel: SolicitudViewModel, puedeSolicitar: Boolean, alCompletar: () -> Unit) {
    val estado by viewModel.uiState.collectAsState()
    when {
        estado.cargando -> Cargando("Preparando formulario...")
        estado.errorCarga != null -> EstadoError(estado.errorCarga!!, viewModel::cargarOpciones)
        else -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Especialidad"); Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                estado.especialidades.forEach { FilterChip(estado.especialidad == it, { viewModel.actualizar("especialidad", it) }, { Text(it) }) }
            }; ErrorCampo(estado.errores.especialidad)
            Text("Sede"); Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                estado.sedes.forEach { FilterChip(estado.sede == it.nombre, { viewModel.actualizar("sede", it.nombre) }, { Text(it.nombre) }) }
            }; ErrorCampo(estado.errores.sede)
            Text("Modalidad"); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Modalidad.entries.forEach { modalidad ->
                    FilterChip(
                        selected = estado.modalidad == modalidad,
                        onClick = { viewModel.cambiarModalidad(modalidad) },
                        label = { Text(modalidad.etiqueta) },
                        leadingIcon = { IconoModalidad(modalidad) }
                    )
                }
            }
            Campo("Fecha (AAAA-MM-DD)", estado.fecha, estado.errores.fecha) { viewModel.actualizar("fecha", it) }
            Campo("Hora (HH:MM)", estado.hora, estado.errores.hora) { viewModel.actualizar("hora", it) }
            Campo("Motivo", estado.motivo, estado.errores.motivo) { viewModel.actualizar("motivo", it) }
            ErrorCampo(estado.errores.general)
            estado.mensaje?.let { Text(it) }
            if (estado.enviando) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (!puedeSolicitar) Text("Límite de 3 citas programadas alcanzado")
            Button({ viewModel.enviar(alCompletar) }, Modifier.fillMaxWidth(), enabled = !estado.enviando && puedeSolicitar) { Text("Solicitar cita") }
        }
    }
}

@Composable private fun Campo(etiqueta: String, valor: String, error: String?, cambio: (String) -> Unit) {
    OutlinedTextField(valor, cambio, Modifier.fillMaxWidth(), label = { Text(etiqueta) }, isError = error != null, singleLine = etiqueta != "Motivo")
    ErrorCampo(error)
}
@Composable private fun ErrorCampo(error: String?) { error?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) } }
