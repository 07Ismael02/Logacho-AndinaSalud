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
import pe.upeu.andinasalud.domain.usecase.ErroresReprogramacion
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramacionInvalidaException

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Contenido(
        val cita: Cita,
        val puedeCancelar: Boolean,
        val puedeReprogramar: Boolean,
        val erroresReprogramacion: ErroresReprogramacion = ErroresReprogramacion(),
        val mensaje: String? = null
    ) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}
class DetalleCitaViewModel(
    private val repository: CitaRepository,
    private val cancelarCita: CancelarCitaUseCase,
    private val reprogramarCita: ReprogramarCitaUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()
    fun cargar(id: Long) = viewModelScope.launch {
        _uiState.value = DetalleUiState.Cargando
        runCatching { requireNotNull(repository.obtenerCita(id)) }
            .onSuccess { _uiState.value = contenido(it) }
            .onFailure { _uiState.value = DetalleUiState.Error("No se pudo cargar la cita") }
    }
    fun cancelar(id: Long, motivo: String, alCompletar: () -> Unit) = viewModelScope.launch {
        runCatching { cancelarCita(id, motivo) }.onSuccess { alCompletar() }
            .onFailure { _uiState.value = DetalleUiState.Error(it.message ?: "No se pudo cancelar la cita") }
    }

    fun reprogramar(id: Long, fecha: String, hora: String) = viewModelScope.launch {
        val anterior = _uiState.value as? DetalleUiState.Contenido ?: return@launch
        runCatching { reprogramarCita(id, fecha, hora) }
            .onSuccess { _uiState.value = contenido(it).copy(mensaje = "Cita reprogramada correctamente") }
            .onFailure { error ->
                val errores = (error as? ReprogramacionInvalidaException)?.errores
                    ?: ErroresReprogramacion(general = error.message ?: "No se pudo reprogramar")
                _uiState.value = anterior.copy(erroresReprogramacion = errores, mensaje = null)
            }
    }

    private fun contenido(cita: Cita) = DetalleUiState.Contenido(
        cita = cita,
        puedeCancelar = cancelarCita.puedeCancelar(cita),
        puedeReprogramar = reprogramarCita.puedeReprogramar(cita)
    )
}
