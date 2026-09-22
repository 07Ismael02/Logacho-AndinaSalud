package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _citasActuales = MutableStateFlow(citas.toList())
    override val citasActuales: StateFlow<List<Cita>> = _citasActuales.asStateFlow()

    override suspend fun obtenerCitas(): List<Cita> { delay(800L); return mutex.withLock { citas.toList() } }
    override suspend fun obtenerCita(id: Long): Cita? { delay(800L); return mutex.withLock { citas.firstOrNull { it.id == id } } }
    override suspend fun guardarCita(cita: Cita): Cita { delay(250L); return mutex.withLock {
        cita.copy(id = (citas.maxOfOrNull { it.id } ?: 0L) + 1).also {
            citas += it
            _citasActuales.value = citas.toList()
        }
    } }
    override suspend fun actualizarCita(cita: Cita): Cita { delay(250L); return mutex.withLock {
        val indice = citas.indexOfFirst { it.id == cita.id }
        require(indice >= 0) { "La cita no existe" }
        citas[indice] = cita
        _citasActuales.value = citas.toList()
        cita
    } }
    override suspend fun obtenerPaciente(): Paciente { delay(800L); return CitasSimuladas.paciente }
    override suspend fun obtenerSedes(): List<Sede> { delay(800L); return CitasSimuladas.sedes }
    override suspend fun obtenerMedicos(): List<Medico> { delay(800L); return CitasSimuladas.medicos }
}
