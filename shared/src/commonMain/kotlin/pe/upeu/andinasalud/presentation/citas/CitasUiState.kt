package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita

enum class FiltroCita { Programada, Atendida, Cancelada }
sealed interface FaseCitas {
    data object Cargando : FaseCitas
    data object Vacia : FaseCitas
    data class Contenido(val citas: List<Cita>) : FaseCitas
    data class Error(val mensaje: String) : FaseCitas
}
data class CitasUiState(
    val fase: FaseCitas = FaseCitas.Cargando,
    val filtro: FiltroCita = FiltroCita.Programada,
    val soloHoy: Boolean = false,
    val busqueda: String = ""
)
