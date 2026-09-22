package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Modalidad
import pe.upeu.andinasalud.domain.repository.CitaRepository

data class SolicitudCita(
    val especialidad: String,
    val sede: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val modalidad: Modalidad
)

data class ErroresSolicitud(
    val especialidad: String? = null,
    val sede: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val motivo: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean get() = listOf(especialidad, sede, fecha, hora, motivo, general).any { it != null }
}

class SolicitudInvalidaException(val errores: ErroresSolicitud) : IllegalArgumentException()

class SolicitarCitaUseCase(
    private val repository: CitaRepository,
    private val ahora: () -> LocalDateTime
) {
    suspend operator fun invoke(solicitud: SolicitudCita): Cita {
        val fecha = runCatching { LocalDate.parse(solicitud.fecha.trim()) }.getOrNull()
        val hora = runCatching { LocalTime.parse(solicitud.hora.trim()) }.getOrNull()
        var errores = ErroresSolicitud(
            especialidad = "Selecciona una especialidad".takeIf { solicitud.especialidad.isBlank() },
            sede = "Selecciona una sede".takeIf { solicitud.sede.isBlank() },
            fecha = "Usa el formato AAAA-MM-DD".takeIf { fecha == null },
            hora = "Usa el formato HH:MM".takeIf { hora == null },
            motivo = "El motivo debe tener entre 10 y 200 caracteres"
                .takeIf { !ReglasCita.motivoValido(solicitud.motivo) }
        )
        if (errores.hayErrores) throw SolicitudInvalidaException(errores)

        val paciente = repository.obtenerPaciente()
        val citas = repository.obtenerCitas()
        val fechaHora = LocalDateTime(fecha!!, hora!!)
        errores = when {
            !ReglasCita.fechaEsFutura(fechaHora, ahora()) -> errores.copy(fecha = "La cita debe ser posterior al momento actual")
            ReglasCita.excedeMaximoProgramadas(citas, paciente.id) -> errores.copy(general = "Solo puedes tener 3 citas programadas")
            ReglasCita.existeDuplicada(citas, paciente.id, fechaHora) -> errores.copy(general = "Ya tienes una cita programada en esa fecha y hora")
            else -> errores
        }
        val medico = repository.obtenerMedicos().firstOrNull {
            it.especialidad == solicitud.especialidad && it.sedes.any { sede -> sede.nombre == solicitud.sede }
        }
        if (medico == null) errores = errores.copy(sede = "No hay médicos disponibles para esta combinación")
        if (errores.hayErrores) throw SolicitudInvalidaException(errores)

        return repository.guardarCita(
            Cita(
                id = 0,
                pacienteId = paciente.id,
                especialidad = solicitud.especialidad,
                medico = medico!!.nombre,
                sede = solicitud.sede,
                fecha = fecha,
                hora = hora,
                motivo = solicitud.motivo.trim(),
                estado = EstadoCita.Programada(recordatorioActivo = true),
                modalidad = solicitud.modalidad
            )
        )
    }
}
