package pe.upeu.andinasalud.data.local

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Modalidad
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede
import kotlin.time.Clock

object CitasSimuladas {
    val paciente = Paciente(1, "Ismael Logacho", "76543210", "ismael.logacho@upeu.edu.pe", "987654321")
    val sedes = listOf(Sede(1, "Ñaña"), Sede(2, "Chosica"), Sede(3, "Chaclacayo"), Sede(4, "Santa Anita"))
    val medicos = listOf(
        Medico(1, "Dra. Ana Quispe", "Medicina General", listOf(sedes[0], sedes[1])),
        Medico(2, "Dr. Luis Rojas", "Medicina General", listOf(sedes[2], sedes[3])),
        Medico(3, "Dra. Carla Vega", "Odontología", listOf(sedes[0], sedes[3])),
        Medico(4, "Dr. Marco León", "Odontología", listOf(sedes[1], sedes[2])),
        Medico(5, "Dra. Rosa Huamán", "Pediatría", listOf(sedes[0], sedes[2])),
        Medico(6, "Dr. Diego Silva", "Pediatría", listOf(sedes[1], sedes[3])),
        Medico(7, "Lic. Elena Soto", "Nutrición", listOf(sedes[0], sedes[3])),
        Medico(8, "Lic. Pablo Díaz", "Nutrición", listOf(sedes[1], sedes[2])),
        Medico(9, "Ps. Mónica Ruiz", "Psicología", listOf(sedes[0], sedes[1])),
        Medico(10, "Ps. Javier Peña", "Psicología", listOf(sedes[2], sedes[3]))
    )
    val citas = listOf(
        Cita(1, 1, "Medicina General", "Dra. Ana Quispe", "Ñaña", LocalDate(2026, 9, 25), LocalTime(10, 0), "Control preventivo anual", EstadoCita.Programada(true), Modalidad.Presencial),
        Cita(2, 1, "Odontología", "Dr. Marco León", "Chaclacayo", LocalDate(2026, 10, 2), LocalTime(15, 30), "Dolor dental persistente", EstadoCita.Programada(true), Modalidad.Presencial),
        Cita(3, 1, "Nutrición", "Lic. Elena Soto", "Santa Anita", LocalDate(2026, 10, 12), LocalTime(9, 15), "Evaluación de plan alimenticio", EstadoCita.Programada(false), Modalidad.Teleconsulta),
        Cita(4, 1, "Psicología", "Ps. Mónica Ruiz", "Chosica", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date, LocalTime(8, 0), "Seguimiento de bienestar emocional", EstadoCita.Atendida("Mantener ejercicios de respiración y control en cuatro semanas."), Modalidad.Teleconsulta),
        Cita(5, 1, "Pediatría", "Dra. Rosa Huamán", "Ñaña", LocalDate(2026, 7, 3), LocalTime(8, 30), "Consulta de control pediátrico", EstadoCita.Atendida("Continuar suplemento indicado y control semestral."), Modalidad.Presencial),
        Cita(6, 1, "Medicina General", "Dr. Luis Rojas", "Santa Anita", LocalDate(2026, 9, 18), LocalTime(16, 0), "Revisión de resultados clínicos", EstadoCita.Cancelada("Conflicto con horario académico", true), Modalidad.Teleconsulta)
    )
}
