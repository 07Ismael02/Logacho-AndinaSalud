package pe.upeu.andinasalud.presentation.citas

import kotlin.test.Test
import kotlin.test.assertTrue

class BusquedaCitasTest {
    @Test fun busquedaIgnoraTildesYMayusculas() {
        assertTrue("Nutrición".normalizada().contains("NUTRICION".normalizada()))
        assertTrue("Ps. Mónica Ruiz".normalizada().contains("monica".normalizada()))
    }
}
