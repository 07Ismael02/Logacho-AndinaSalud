package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase

sealed interface DetalleUiState { data object Cargando : DetalleUiState; data class Contenido(val cita: Cita, val puedeCancelar: Boolean) : DetalleUiState; data class Error(val mensaje: String) : DetalleUiState }
class DetalleCitaViewModel(private val repository: CitaRepository, private val cancelarCita: CancelarCitaUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()
    fun cargar(id: Long) = viewModelScope.launch {
        _uiState.value = DetalleUiState.Cargando
        runCatching { requireNotNull(repository.obtenerCita(id)) }
            .onSuccess { _uiState.value = DetalleUiState.Contenido(it, cancelarCita.puedeCancelar(it)) }
            .onFailure { _uiState.value = DetalleUiState.Error("No se pudo cargar la cita") }
    }
    fun cancelar(id: Long, motivo: String, alCompletar: () -> Unit) = viewModelScope.launch {
        runCatching { cancelarCita(id, motivo) }.onSuccess { alCompletar() }
            .onFailure { _uiState.value = DetalleUiState.Error(it.message ?: "No se pudo cancelar la cita") }
    }
}
