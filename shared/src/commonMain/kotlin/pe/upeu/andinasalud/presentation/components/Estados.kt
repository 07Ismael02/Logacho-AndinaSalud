package pe.upeu.andinasalud.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Cargando(mensaje: String = "Cargando información...") = Column(
    Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
) { CircularProgressIndicator(); Text(mensaje, Modifier.padding(top = 12.dp)) }

@Composable
fun EstadoVacio(titulo: String, detalle: String) = Column(
    Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally
) { Text(titulo, style = MaterialTheme.typography.titleLarge); Text(detalle, Modifier.padding(top = 8.dp)) }

@Composable
fun EstadoError(mensaje: String, reintentar: () -> Unit) = Column(
    Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally
) { Text(mensaje, color = MaterialTheme.colorScheme.error); Button(reintentar, Modifier.padding(top = 12.dp)) { Text("Reintentar") } }
