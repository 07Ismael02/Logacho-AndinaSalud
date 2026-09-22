package pe.upeu.andinasalud.domain.repository

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede

interface CitaRepository {
    suspend fun obtenerCitas(): List<Cita>
    suspend fun obtenerCita(id: Long): Cita?
    suspend fun guardarCita(cita: Cita): Cita
    suspend fun actualizarCita(cita: Cita): Cita
    suspend fun obtenerPaciente(): Paciente
    suspend fun obtenerSedes(): List<Sede>
    suspend fun obtenerMedicos(): List<Medico>
}
