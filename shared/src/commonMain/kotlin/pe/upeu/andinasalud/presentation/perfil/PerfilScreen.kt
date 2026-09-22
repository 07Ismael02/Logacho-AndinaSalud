package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.Cargando
import pe.upeu.andinasalud.presentation.components.EstadoError

@Composable
fun PerfilScreen(viewModel: PerfilViewModel) {
    val estado by viewModel.uiState.collectAsState()
    when (val fase = estado.fase) {
        FasePerfil.Cargando -> Cargando()
        is FasePerfil.Error -> EstadoError(fase.mensaje, viewModel::cargar)
        is FasePerfil.Contenido -> Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val valor = fase.paciente
                Text(valor.nombre, style = MaterialTheme.typography.titleLarge)
                Text("Documento: ${valor.documento}"); Text("Correo: ${valor.correo}"); Text("Teléfono: ${valor.telefono}")
            } }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Modo oscuro"); Switch(estado.oscuro, onCheckedChange = viewModel::cambiarTema)
            }
        }
    }
}
