package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Modalidad
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ReprogramarCitaUseCaseTest {
    private val ahora = { LocalDateTime(2026, 9, 22, 10, 0) }

    @Test fun reprogramacionValidaConservaDatosYRegistraCambio() = runTest {
        val repository = CitaRepositoryFake()
        val anterior = repository.obtenerCita(1)!!
        val actualizada = ReprogramarCitaUseCase(repository, ahora)(1, "2026-10-20", "11:00")
        assertEquals(LocalDate(2026, 10, 20), actualizada.fecha)
        assertEquals(LocalTime(11, 0), actualizada.hora)
        assertEquals(anterior.medico, actualizada.medico)
        assertEquals(anterior.sede, actualizada.sede)
        assertEquals(anterior.motivo, actualizada.motivo)
        assertEquals(Modalidad.Presencial, actualizada.modalidad)
        assertIs<EstadoCita.Programada>(actualizada.estado)
        assertEquals(anterior.fecha, actualizada.reprogramaciones.single().fechaAnterior)
        assertEquals(anterior.hora, actualizada.reprogramaciones.single().horaAnterior)
        assertEquals(actualizada, repository.obtenerCita(1))
    }

    @Test fun fechaPasadaEsRechazada() = runTest {
        val repository = CitaRepositoryFake()
        val error = assertFailsWith<ReprogramacionInvalidaException> {
            ReprogramarCitaUseCase(repository, ahora)(1, "2026-09-21", "10:00")
        }
        assertEquals("La nueva fecha y hora deben ser futuras", error.errores.fecha)
        assertEquals(LocalDate(2026, 9, 25), repository.obtenerCita(1)?.fecha)
    }

    @Test fun horarioOcupadoPorOtraProgramadaEsRechazado() = runTest {
        val repository = CitaRepositoryFake()
        val error = assertFailsWith<ReprogramacionInvalidaException> {
            ReprogramarCitaUseCase(repository, ahora)(1, "2026-10-02", "15:30")
        }
        assertEquals("Ya tienes una cita programada en esa fecha y hora", error.errores.general)
        assertEquals(LocalDate(2026, 9, 25), repository.obtenerCita(1)?.fecha)
    }
}
