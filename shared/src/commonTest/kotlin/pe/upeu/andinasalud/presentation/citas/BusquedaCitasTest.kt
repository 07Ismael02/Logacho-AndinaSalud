package pe.upeu.andinasalud.presentation.citas

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BusquedaCitasTest {
    @Test fun hoySeCombinaConEstadoYBusqueda() {
        val hoy = LocalDate(2026, 9, 22)
        val citas = listOf(
            Cita(1, 1, "Nutrición", "Lic. Elena Soto", "Ñaña", hoy, LocalTime(8, 0), "Consulta de nutrición", EstadoCita.Atendida("Control")),
            Cita(2, 1, "Nutrición", "Lic. Elena Soto", "Ñaña", hoy, LocalTime(9, 0), "Consulta de nutrición", EstadoCita.Programada(true)),
            Cita(3, 1, "Nutrición", "Lic. Elena Soto", "Ñaña", LocalDate(2026, 9, 23), LocalTime(9, 0), "Consulta de nutrición", EstadoCita.Atendida("Control"))
        )
        assertEquals(listOf(1L), filtrarCitas(citas, FiltroCita.Atendida, true, "nutricion", hoy).map { it.id })
        assertEquals(listOf(2L), filtrarCitas(citas, FiltroCita.Programada, true, "NUTRICION", hoy).map { it.id })
    }
    @Test fun busquedaIgnoraTildesYMayusculas() {
        assertTrue("Nutrición".normalizada().contains("NUTRICION".normalizada()))
        assertTrue("Ps. Mónica Ruiz".normalizada().contains("monica".normalizada()))
    }
}
