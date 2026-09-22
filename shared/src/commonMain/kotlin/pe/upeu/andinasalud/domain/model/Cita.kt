package pe.upeu.andinasalud.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Cita(
    val id: Long,
    val pacienteId: Long,
    val especialidad: String,
    val medico: String,
    val sede: String,
    val fecha: LocalDate,
    val hora: LocalTime,
    val motivo: String,
    val estado: EstadoCita
)
