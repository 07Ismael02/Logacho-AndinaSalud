package pe.upeu.andinasalud.di

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.citas.ResumenCitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel
import kotlin.time.Clock

private val dataModule = module { single<CitaRepository> { CitaRepositoryFake() } }
private val domainModule = module {
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get()) { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) } }
    factory { CancelarCitaUseCase(get()) { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) } }
    factory { ReprogramarCitaUseCase(get()) { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) } }
}
private val presentationModule = module {
    viewModelOf(::InicioViewModel); viewModel { CitasViewModel(get(), get()) }; viewModelOf(::DetalleCitaViewModel)
    viewModelOf(::SolicitudViewModel); viewModelOf(::PerfilViewModel)
    viewModelOf(::ResumenCitasViewModel)
}
internal expect val platformModule: Module
fun initKoin() {
    startKoin { modules(dataModule, domainModule, presentationModule, platformModule) }
}
