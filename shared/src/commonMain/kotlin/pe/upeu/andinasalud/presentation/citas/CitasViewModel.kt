package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.repository.CitaRepository
import kotlin.time.Clock

class CitasViewModel(
    private val obtenerCitas: ObtenerCitasUseCase,
    private val repository: CitaRepository,
    private val hoy: () -> LocalDate = { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
) : ViewModel() {
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()
    private var todas = emptyList<Cita>()
    init {
        cargar()
        viewModelScope.launch {
            repository.citasActuales.collect { citas ->
                if (_uiState.value.fase != FaseCitas.Cargando) {
                    todas = citas.sortedWith(compareBy<Cita> { it.fecha }.thenBy { it.hora })
                    aplicarFiltros()
                }
            }
        }
    }
    fun cargar() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(fase = FaseCitas.Cargando)
        runCatching { obtenerCitas() }.onSuccess { todas = it; aplicarFiltros() }
            .onFailure { _uiState.value = _uiState.value.copy(fase = FaseCitas.Error("No se pudieron cargar las citas")) }
    }
    fun cambiarFiltro(filtro: FiltroCita) { _uiState.value = _uiState.value.copy(filtro = filtro); aplicarFiltros() }
    fun cambiarHoy(activo: Boolean) { _uiState.value = _uiState.value.copy(soloHoy = activo); aplicarFiltros() }
    fun buscar(texto: String) { _uiState.value = _uiState.value.copy(busqueda = texto); aplicarFiltros() }
    private fun aplicarFiltros() {
        val estado = _uiState.value
        val filtradas = filtrarCitas(todas, estado.filtro, estado.soloHoy, estado.busqueda, hoy())
        _uiState.value = estado.copy(fase = if (filtradas.isEmpty()) FaseCitas.Vacia else FaseCitas.Contenido(filtradas))
    }
}

internal fun filtrarCitas(
    citas: List<Cita>, filtro: FiltroCita, soloHoy: Boolean, busqueda: String, hoy: LocalDate
): List<Cita> {
    val consulta = busqueda.normalizada()
    return citas.filter { cita ->
        val coincideEstado = when (filtro) {
            FiltroCita.Programada -> cita.estado is EstadoCita.Programada
            FiltroCita.Atendida -> cita.estado is EstadoCita.Atendida
            FiltroCita.Cancelada -> cita.estado is EstadoCita.Cancelada
        }
        coincideEstado && (!soloHoy || cita.fecha == hoy) &&
            (consulta.isBlank() || cita.especialidad.normalizada().contains(consulta) || cita.medico.normalizada().contains(consulta))
    }
}

internal fun String.normalizada(): String = lowercase().replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n")
