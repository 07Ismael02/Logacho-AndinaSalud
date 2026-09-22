package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(private val obtenerCitas: ObtenerCitasUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()
    private var todas = emptyList<Cita>()
    init { cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(fase = FaseCitas.Cargando)
        runCatching { obtenerCitas() }.onSuccess { todas = it; aplicarFiltros() }
            .onFailure { _uiState.value = _uiState.value.copy(fase = FaseCitas.Error("No se pudieron cargar las citas")) }
    }
    fun cambiarFiltro(filtro: FiltroCita) { _uiState.value = _uiState.value.copy(filtro = filtro); aplicarFiltros() }
    fun buscar(texto: String) { _uiState.value = _uiState.value.copy(busqueda = texto); aplicarFiltros() }
    private fun aplicarFiltros() {
        val estado = _uiState.value
        val consulta = estado.busqueda.normalizada()
        val filtradas = todas.filter { cita ->
            val coincide = when (estado.filtro) {
                FiltroCita.Programada -> cita.estado is EstadoCita.Programada
                FiltroCita.Atendida -> cita.estado is EstadoCita.Atendida
                FiltroCita.Cancelada -> cita.estado is EstadoCita.Cancelada
            }
            coincide && (consulta.isBlank() || cita.especialidad.normalizada().contains(consulta) || cita.medico.normalizada().contains(consulta))
        }
        _uiState.value = estado.copy(fase = if (filtradas.isEmpty()) FaseCitas.Vacia else FaseCitas.Contenido(filtradas))
    }
}

internal fun String.normalizada(): String = lowercase().replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n")
