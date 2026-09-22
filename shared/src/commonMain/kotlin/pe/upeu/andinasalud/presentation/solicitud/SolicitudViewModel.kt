package pe.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.domain.model.Modalidad
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.ErroresSolicitud
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudCita
import pe.upeu.andinasalud.domain.usecase.SolicitudInvalidaException

data class SolicitudUiState(
    val cargando: Boolean = true, val especialidades: List<String> = emptyList(), val sedes: List<Sede> = emptyList(),
    val especialidad: String = "", val sede: String = "", val fecha: String = "", val hora: String = "", val motivo: String = "",
    val modalidad: Modalidad = Modalidad.Presencial,
    val errores: ErroresSolicitud = ErroresSolicitud(), val enviando: Boolean = false, val mensaje: String? = null, val errorCarga: String? = null
)
class SolicitudViewModel(private val repository: CitaRepository, private val solicitarCita: SolicitarCitaUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()
    init { cargarOpciones() }
    fun cargarOpciones() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(cargando = true, errorCarga = null)
        runCatching { repository.obtenerMedicos().map { it.especialidad }.distinct() to repository.obtenerSedes() }
            .onSuccess { _uiState.value = _uiState.value.copy(cargando = false, especialidades = it.first, sedes = it.second) }
            .onFailure { _uiState.value = _uiState.value.copy(cargando = false, errorCarga = "No se pudo cargar el formulario") }
    }
    fun actualizar(campo: String, valor: String) { _uiState.value = when (campo) {
        "especialidad" -> _uiState.value.copy(especialidad = valor); "sede" -> _uiState.value.copy(sede = valor)
        "fecha" -> _uiState.value.copy(fecha = valor); "hora" -> _uiState.value.copy(hora = valor); else -> _uiState.value.copy(motivo = valor)
    } }
    fun cambiarModalidad(modalidad: Modalidad) { _uiState.value = _uiState.value.copy(modalidad = modalidad) }
    fun enviar(alCompletar: () -> Unit) = viewModelScope.launch {
        val actual = _uiState.value
        _uiState.value = actual.copy(enviando = true, errores = ErroresSolicitud(), mensaje = null)
        runCatching { solicitarCita(SolicitudCita(actual.especialidad, actual.sede, actual.fecha, actual.hora, actual.motivo, actual.modalidad)) }
            .onSuccess {
                _uiState.value = _uiState.value.copy(enviando = false, mensaje = "Cita solicitada correctamente")
                delay(600L)
                alCompletar()
            }
            .onFailure { error -> _uiState.value = _uiState.value.copy(enviando = false, errores = (error as? SolicitudInvalidaException)?.errores ?: ErroresSolicitud(general = error.message ?: "No se pudo solicitar la cita")) }
    }
}
