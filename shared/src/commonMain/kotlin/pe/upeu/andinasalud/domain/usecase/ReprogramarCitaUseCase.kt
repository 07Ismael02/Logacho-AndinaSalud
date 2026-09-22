package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Reprogramacion
import pe.upeu.andinasalud.domain.repository.CitaRepository

data class ErroresReprogramacion(
    val fecha: String? = null,
    val hora: String? = null,
    val general: String? = null
)

class ReprogramacionInvalidaException(val errores: ErroresReprogramacion) : IllegalArgumentException()

class ReprogramarCitaUseCase(
    private val repository: CitaRepository,
    private val ahora: () -> LocalDateTime
) {
    fun puedeReprogramar(cita: Cita): Boolean = cita.estado is EstadoCita.Programada

    suspend operator fun invoke(id: Long, fechaTexto: String, horaTexto: String): Cita {
        val fecha = runCatching { LocalDate.parse(fechaTexto.trim()) }.getOrNull()
        val hora = runCatching { LocalTime.parse(horaTexto.trim()) }.getOrNull()
        val erroresFormato = ErroresReprogramacion(
            fecha = "Usa el formato AAAA-MM-DD".takeIf { fecha == null },
            hora = "Usa el formato HH:MM".takeIf { hora == null }
        )
        if (fecha == null || hora == null) throw ReprogramacionInvalidaException(erroresFormato)

        val cita = requireNotNull(repository.obtenerCita(id)) { "La cita no existe" }
        require(puedeReprogramar(cita)) { "Solo una cita programada puede reprogramarse" }
        val citas = repository.obtenerCitas()
        when (ReglasCita.validarHorario(citas, cita.pacienteId, LocalDateTime(fecha, hora), ahora(), id)) {
            ReglasCita.ErrorHorario.PASADO -> throw ReprogramacionInvalidaException(
                ErroresReprogramacion(fecha = "La nueva fecha y hora deben ser futuras")
            )
            ReglasCita.ErrorHorario.OCUPADO -> throw ReprogramacionInvalidaException(
                ErroresReprogramacion(general = "Ya tienes una cita programada en esa fecha y hora")
            )
            null -> Unit
        }
        if (cita.fecha == fecha && cita.hora == hora) throw ReprogramacionInvalidaException(
            ErroresReprogramacion(general = "Elige una fecha u hora diferente")
        )
        return repository.actualizarCita(cita.copy(
            fecha = fecha,
            hora = hora,
            reprogramaciones = cita.reprogramaciones + Reprogramacion(cita.fecha, cita.hora, fecha, hora)
        ))
    }
}
