package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.ReglasCita

data class ResumenCitasUiState(
    val cargando: Boolean = true,
    val programadas: Int = 0,
    val puedeSolicitar: Boolean = false
)

class ResumenCitasViewModel(private val repository: CitaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ResumenCitasUiState())
    val uiState: StateFlow<ResumenCitasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val pacienteId = repository.obtenerPaciente().id
            repository.citasActuales.collect { citas ->
                _uiState.value = ResumenCitasUiState(
                    cargando = false,
                    programadas = ReglasCita.cantidadProgramadas(citas, pacienteId),
                    puedeSolicitar = !ReglasCita.excedeMaximoProgramadas(citas, pacienteId)
                )
            }
        }
    }
}
