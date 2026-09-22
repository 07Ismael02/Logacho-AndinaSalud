package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CancelarCitaUseCase(
    private val repository: CitaRepository,
    private val ahora: () -> LocalDateTime
) {
    fun puedeCancelar(cita: Cita): Boolean = ReglasCita.puedeCancelar(cita, ahora())

    suspend operator fun invoke(id: Long, motivo: String): Cita {
        val cita = requireNotNull(repository.obtenerCita(id)) { "La cita no existe" }
        require(puedeCancelar(cita)) { "La cita no puede cancelarse porque faltan 24 horas o menos" }
        require(ReglasCita.motivoValido(motivo)) { "El motivo debe tener entre 10 y 200 caracteres" }
        return repository.actualizarCita(
            cita.copy(estado = EstadoCita.Cancelada(motivo.trim(), canceladaPorPaciente = true))
        )
    }
}
