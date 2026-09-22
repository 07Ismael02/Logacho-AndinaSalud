package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.presentation.components.Cargando
import pe.upeu.andinasalud.presentation.components.EstadoError
import pe.upeu.andinasalud.presentation.components.EstadoVacio

@Composable
fun CitasScreen(viewModel: CitasViewModel, verDetalle: (Long) -> Unit) {
    val estado by viewModel.uiState.collectAsState()
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(estado.busqueda, viewModel::buscar, Modifier.fillMaxWidth().padding(vertical = 8.dp), label = { Text("Buscar especialidad o médico") }, singleLine = true)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FiltroCita.entries.forEach { filtro ->
                FilterChip(estado.filtro == filtro, { viewModel.cambiarFiltro(filtro) }, { Text(filtro.name) })
            }
        }
        when (val fase = estado.fase) {
            FaseCitas.Cargando -> Cargando("Cargando citas...")
            FaseCitas.Vacia -> EstadoVacio("Sin resultados", "No hay citas para el filtro y búsqueda seleccionados.")
            is FaseCitas.Error -> EstadoError(fase.mensaje, viewModel::cargar)
            is FaseCitas.Contenido -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                items(fase.citas, key = { it.id }) { TarjetaCita(it) { verDetalle(it.id) } }
            }
        }
    }
}

@Composable
private fun TarjetaCita(cita: Cita, onClick: () -> Unit) = Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(cita.especialidad, style = MaterialTheme.typography.titleMedium)
        Text(cita.medico)
        Text("${cita.fecha} · ${cita.hora} · ${cita.sede}")
        Text(cita.estado::class.simpleName ?: "Estado", color = MaterialTheme.colorScheme.primary)
    }
}
