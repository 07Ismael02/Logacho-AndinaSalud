package pe.upeu.andinasalud.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class KoinViewModelTest {
    @Test
    fun creaViewModelsDeCitasDetalleYSolicitud() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            initKoin()
            val koin = GlobalContext.get()
            assertNotNull(koin.get<CitasViewModel>())
            assertNotNull(koin.get<DetalleCitaViewModel>())
            assertNotNull(koin.get<SolicitudViewModel>())
        } finally {
            stopKoin()
            Dispatchers.resetMain()
        }
    }
}
