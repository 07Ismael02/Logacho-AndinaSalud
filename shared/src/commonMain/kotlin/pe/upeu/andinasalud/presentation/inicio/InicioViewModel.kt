package pe.upeu.andinasalud.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository

sealed interface InicioUiState {
    data object Cargando : InicioUiState
    data class Contenido(val paciente: Paciente, val proximaCita: Cita?) : InicioUiState
    data class Error(val mensaje: String) : InicioUiState
}

class InicioViewModel(private val repository: CitaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Cargando)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()
    init { cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = InicioUiState.Cargando
        runCatching {
            val paciente = repository.obtenerPaciente()
            val proxima = repository.obtenerCitas().firstOrNull { it.estado is EstadoCita.Programada }
            InicioUiState.Contenido(paciente, proxima)
        }.onSuccess { _uiState.value = it }
            .onFailure { _uiState.value = InicioUiState.Error("No se pudo cargar el inicio") }
    }
}
