package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake : CitaRepository {
    private val mutex = Mutex()
    private val citas = CitasSimuladas.citas.toMutableList()

    override suspend fun obtenerCitas(): List<Cita> { delay(800L); return mutex.withLock { citas.toList() } }
    override suspend fun obtenerCita(id: Long): Cita? { delay(250L); return mutex.withLock { citas.firstOrNull { it.id == id } } }
    override suspend fun guardarCita(cita: Cita): Cita { delay(250L); return mutex.withLock {
        cita.copy(id = (citas.maxOfOrNull { it.id } ?: 0L) + 1).also { citas += it }
    } }
    override suspend fun actualizarCita(cita: Cita): Cita { delay(250L); return mutex.withLock {
        val indice = citas.indexOfFirst { it.id == cita.id }
        require(indice >= 0) { "La cita no existe" }
        citas[indice] = cita
        cita
    } }
    override suspend fun obtenerPaciente(): Paciente = CitasSimuladas.paciente
    override suspend fun obtenerSedes(): List<Sede> = CitasSimuladas.sedes
    override suspend fun obtenerMedicos(): List<Medico> = CitasSimuladas.medicos
}
