package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import kotlin.time.Duration.Companion.hours

object ReglasCita {
    fun fechaEsFutura(fechaHora: LocalDateTime, ahora: LocalDateTime): Boolean = fechaHora > ahora

    fun excedeMaximoProgramadas(citas: List<Cita>, pacienteId: Long): Boolean =
        citas.count { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada } >= 3

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
        fechaHora: LocalDateTime
    ): Boolean = citas.any {
        it.pacienteId == pacienteId &&
            it.estado is EstadoCita.Programada &&
            it.fecha == fechaHora.date &&
            it.hora == fechaHora.time
    }
}
