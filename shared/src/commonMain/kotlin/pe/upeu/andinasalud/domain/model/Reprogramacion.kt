package pe.upeu.andinasalud.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Reprogramacion(
    val fechaAnterior: LocalDate,
    val horaAnterior: LocalTime,
    val fechaNueva: LocalDate,
    val horaNueva: LocalTime
)
