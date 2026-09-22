package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import kotlin.time.Duration.Companion.hours

object ReglasCita {
    enum class ErrorHorario { PASADO, OCUPADO }

    fun fechaEsFutura(fechaHora: LocalDateTime, ahora: LocalDateTime): Boolean = fechaHora > ahora

    fun cantidadProgramadas(citas: List<Cita>, pacienteId: Long): Int =
        citas.count { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada }

    fun excedeMaximoProgramadas(citas: List<Cita>, pacienteId: Long): Boolean =
        cantidadProgramadas(citas, pacienteId) >= 3

    fun puedeCancelar(cita: Cita, ahora: LocalDateTime): Boolean {
        if (cita.estado !is EstadoCita.Programada) return false
        val zona = TimeZone.currentSystemDefault()
        val fechaCita = LocalDateTime(cita.fecha, cita.hora).toInstant(zona)
        return fechaCita - ahora.toInstant(zona) > 24.hours
    }

    fun motivoValido(motivo: String): Boolean = motivo.trim().length in 10..200

    fun existeDuplicada(
        citas: List<Cita>,
        pacienteId: Long,
        fechaHora: LocalDateTime,
        excluirCitaId: Long? = null
    ): Boolean = citas.any {
        it.pacienteId == pacienteId &&
            it.id != excluirCitaId &&
            it.estado is EstadoCita.Programada &&
            it.fecha == fechaHora.date &&
            it.hora == fechaHora.time
    }

    fun validarHorario(
        citas: List<Cita>, pacienteId: Long, fechaHora: LocalDateTime,
        ahora: LocalDateTime, excluirCitaId: Long? = null
    ): ErrorHorario? = when {
        !fechaEsFutura(fechaHora, ahora) -> ErrorHorario.PASADO
        existeDuplicada(citas, pacienteId, fechaHora, excluirCitaId) -> ErrorHorario.OCUPADO
        else -> null
    }
}
