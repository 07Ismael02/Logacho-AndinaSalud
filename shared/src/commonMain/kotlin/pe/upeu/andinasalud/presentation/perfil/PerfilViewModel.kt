package pe.upeu.andinasalud.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository

sealed interface FasePerfil {
    data object Cargando : FasePerfil
    data class Contenido(val paciente: Paciente) : FasePerfil
    data class Error(val mensaje: String) : FasePerfil
}
data class PerfilUiState(val fase: FasePerfil = FasePerfil.Cargando, val oscuro: Boolean = false)

class PerfilViewModel(private val repository: CitaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()
    init { cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(fase = FasePerfil.Cargando)
        runCatching { repository.obtenerPaciente() }
            .onSuccess { _uiState.value = _uiState.value.copy(fase = FasePerfil.Contenido(it)) }
            .onFailure { _uiState.value = _uiState.value.copy(fase = FasePerfil.Error("No se pudo cargar el perfil")) }
    }
    fun cambiarTema(oscuro: Boolean) { _uiState.value = _uiState.value.copy(oscuro = oscuro) }
}
