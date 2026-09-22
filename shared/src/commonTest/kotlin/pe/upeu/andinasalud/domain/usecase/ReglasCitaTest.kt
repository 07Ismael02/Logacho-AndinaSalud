package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Modalidad
import pe.upeu.andinasalud.data.local.CitasSimuladas
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class ReglasCitaTest {
    @Test fun seedConservaSeisCitasConAmbasModalidades() {
        assertEquals(6, CitasSimuladas.citas.size)
        assertTrue(CitasSimuladas.citas.any { it.modalidad == Modalidad.Presencial })
        assertTrue(CitasSimuladas.citas.any { it.modalidad == Modalidad.Teleconsulta })
    }
    private val ahora = LocalDateTime(2026, 9, 22, 10, 0)

    @Test fun rn1_rechazaFechaPasadaYAceptaFutura() {
        assertFalse(ReglasCita.fechaEsFutura(LocalDateTime(2026, 9, 22, 9, 59), ahora))
        assertTrue(ReglasCita.fechaEsFutura(LocalDateTime(2026, 9, 22, 10, 1), ahora))
    }

    @Test fun rn2_impideSuperarTresCitasProgramadas() {
        val citas = (1L..3L).map { cita(it, LocalDate(2026, 10, it.toInt()), LocalTime(10, 0)) }
        assertTrue(ReglasCita.excedeMaximoProgramadas(citas, 1))
        assertFalse(ReglasCita.excedeMaximoProgramadas(citas.take(2), 1))
        assertEquals(3, ReglasCita.cantidadProgramadas(citas, 1))
        assertEquals(2, ReglasCita.cantidadProgramadas(citas.take(2), 1))
    }

    @Test fun rn3_soloPermiteCancelarProgramadaConMasDe24Horas() {
        assertFalse(ReglasCita.puedeCancelar(cita(1, LocalDate(2026, 9, 23), LocalTime(10, 0)), ahora))
        assertTrue(ReglasCita.puedeCancelar(cita(2, LocalDate(2026, 9, 23), LocalTime(11, 0)), ahora))
        assertFalse(ReglasCita.puedeCancelar(cita(3, LocalDate(2026, 9, 24), LocalTime(11, 0), EstadoCita.Atendida("Alta")), ahora))
    }

    @Test fun rn4_validaLongitudDelMotivo() {
        assertFalse(ReglasCita.motivoValido("Corto"))
        assertTrue(ReglasCita.motivoValido("Control de salud"))
        assertFalse(ReglasCita.motivoValido("x".repeat(201)))
    }

    @Test fun rn5_detectaDuplicadaSoloSiEstaProgramada() {
        val fechaHora = LocalDateTime(2026, 10, 5, 8, 30)
        val programada = cita(1, fechaHora.date, fechaHora.time)
        assertTrue(ReglasCita.existeDuplicada(listOf(programada), 1, fechaHora))
        assertFalse(ReglasCita.existeDuplicada(listOf(programada.copy(estado = EstadoCita.Cancelada("Cambio de horario", true))), 1, fechaHora))
    }

    private fun cita(id: Long, fecha: LocalDate, hora: LocalTime, estado: EstadoCita = EstadoCita.Programada(true)) =
        Cita(id, 1, "Medicina General", "Dra. Ana Quispe", "Ñaña", fecha, hora, "Control preventivo", estado, Modalidad.Presencial)
}
