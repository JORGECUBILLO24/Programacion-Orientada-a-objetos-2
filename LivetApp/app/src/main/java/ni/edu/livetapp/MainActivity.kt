package ni.edu.livetapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import ni.edu.livetapp.ui.theme.LivetAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingInterno ->
                    PantallaDashboardHabitos(modifier = Modifier.padding(bottom = paddingInterno.calculateBottomPadding()))
                }
            }
        }
    }
}

// ==========================================
// MODELOS DE DATOS
// ==========================================
data class Categoria(val nombre: String, val color: Color)

data class Habito(
    val id: Int,
    val nombre: String,
    val metaSegundosPorSesion: Int,
    var segundosTrabajados: Int = 0, // Seguimiento de tiempo real agregado para que compile la validación
    val fechaLimiteMillis: Long,
    val horaAlerta: String,
    val sesionesTotales: Int,
    var sesionesCompletadas: Int = 0,
    val categoria: Categoria
) {
    val porcentajeProgreso: Float
        get() = if (sesionesTotales > 0) (sesionesCompletadas.toFloat() / sesionesTotales.toFloat()).coerceIn(0f, 1f) else 0f

    val estaTerminado: Boolean
        get() = sesionesCompletadas >= sesionesTotales
}

val frasesMotivadoras = listOf(
    "El único mal entrenamiento es el que no ocurrió. 💪",
    "La disciplina es el puente entre tus metas y tus logros. 🌉",
    "No te detengas hasta que te sientas orgulloso. ✨",
    "Pequeños pasos todos los días llevan a grandes resultados. 📈",
    "Haz hoy lo que otros no quieren, haz mañana lo que otros no pueden. 🚀"
)

fun formatearTemporizador(segundosTotales: Int): String {
    val h = segundosTotales / 3600
    val m = (segundosTotales % 3600) / 60
    val s = segundosTotales % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
}

// ==========================================
// COMPONENTES PERSONALIZADOS (Rueda y Animación)
// ==========================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectorRueda(
    rango: IntRange,
    valorActual: Int,
    onValorCambiado: (Int) -> Unit,
    etiqueta: String,
    colorTextoP: Color,
    colorTextoS: Color,
    modifier: Modifier = Modifier
) {
    val elementos = rango.toList()
    val startIndex = elementos.indexOf(valorActual).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val alturaItem = 40.dp

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            if (index in elementos.indices) onValorCambiado(elementos[index])
        }
    }

    Box(modifier = modifier.height(alturaItem * 3), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(alturaItem)
                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = alturaItem)
        ) {
            items(elementos.size) { index ->
                val isSelected = index == listState.firstVisibleItemIndex
                Box(
                    modifier = Modifier.height(alturaItem).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = elementos[index].toString().padStart(2, '0'),
                            fontSize = if (isSelected) 20.sp else 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colorTextoP else colorTextoS.copy(alpha = 0.5f)
                        )
                        if (etiqueta.isNotEmpty() && isSelected) {
                            Text(
                                text = " $etiqueta",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorTextoP,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimacionCascadaLogro(onAnimacionTerminada: () -> Unit) {
    val animatable = remember { Animatable(0f) }
    val particulas = remember { List(40) { Particula() } }

    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        )
        onAnimacionTerminada()
    }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particulas.forEach { p ->
            val progreso = animatable.value
            val currentX = width / 2f + p.velocidadX * progreso * 1000f
            val currentY = height - (p.velocidadY * progreso * 1500f) + (500f * progreso * progreso)
            val alpha = (1f - progreso).coerceIn(0f, 1f)

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radio * (1f - progreso * 0.5f),
                center = Offset(currentX, currentY)
            )
        }
    }
}

class Particula {
    val velocidadX = Random.nextFloat() * 2f - 1f
    val velocidadY = Random.nextFloat() * 1.5f + 1f
    val radio = Random.nextFloat() * 15f + 10f
    val color = if (Random.nextBoolean()) Color(0xFFFFD700) else Color(0xFF00E676)
}

// ==========================================
// PANTALLA PRINCIPAL
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDashboardHabitos(modifier: Modifier = Modifier) {
    var esModoOscuro by remember { mutableStateOf(true) }
    var nombreUsuario by remember { mutableStateOf("Isaac") }
    var listaHabitos by remember { mutableStateOf(emptyList<Habito>()) }
    var categorias by remember {
        mutableStateOf(
            listOf(
                Categoria("Estudio", Color(0xFF00B0FF)),
                Categoria("Salud", Color(0xFF00E676)),
                Categoria("Trabajo", Color(0xFFFFD700))
            )
        )
    }

    var fraseActualIdx by remember { mutableStateOf(0) }

    // --- ESTADOS DE CONTROL ---
    var mostrarDialogoCreacion by remember { mutableStateOf(false) }
    var habitoParaEditar by remember { mutableStateOf<Habito?>(null) }
    var mostrarDialogoCategoria by remember { mutableStateOf(false) }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }
    var mostrarTareasCompletadas by remember { mutableStateOf(false) }
    var mostrarAnimacionLogro by remember { mutableStateOf(false) }

    var tareaAlertaActual by remember { mutableStateOf<Habito?>(null) }

    var habitoActivoId by remember { mutableStateOf<Int?>(null) }
    var cronometroCorriendo by remember { mutableStateOf(false) }
    var segundosRestantesSesion by remember { mutableStateOf(0) }

    var mostrarCelebracionAnticipada by remember { mutableStateOf(false) }

    // Lógica del cronómetro: SOLO AQUÍ se completa la tarea
    LaunchedEffect(cronometroCorriendo) {
        while (cronometroCorriendo && segundosRestantesSesion > 0) {
            delay(1000)
            segundosRestantesSesion--

            if (segundosRestantesSesion <= 0) {
                cronometroCorriendo = false
                val hUpdate = listaHabitos.find { it.id == habitoActivoId }
                if (hUpdate != null) {
                    val nuevasSesiones = hUpdate.sesionesTotales // Forzamos completado al terminar tiempo
                    listaHabitos = listaHabitos.map { if (it.id == hUpdate.id) it.copy(sesionesCompletadas = nuevasSesiones) else it }
                    mostrarAnimacionLogro = true
                }
                habitoActivoId = null
            }
        }
    }

    // Verificador de hora para alertas
    LaunchedEffect(listaHabitos) {
        while (true) {
            val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val tareaPendiente = listaHabitos.find { !it.estaTerminado && it.horaAlerta == horaActual }

            if (tareaPendiente != null && tareaAlertaActual?.id != tareaPendiente.id && habitoActivoId != tareaPendiente.id) {
                tareaAlertaActual = tareaPendiente
            }
            delay(30000)
        }
    }

    val colorFondo by animateColorAsState(if (esModoOscuro) Color(0xFF0A0A0A) else Color(0xFFF3F4F6))
    val colorSuperficie by animateColorAsState(if (esModoOscuro) Color(0xFF1E1E1E) else Color(0xFFFFFFFF))
    val textoP by animateColorAsState(if (esModoOscuro) Color.White else Color.Black)
    val textoS by animateColorAsState(if (esModoOscuro) Color(0xFFCCCCCC) else Color(0xFF666666))
    val acentoOro = Color(0xFFFFD700)
    val colorExito = Color(0xFF00E676)

    var uriPerfil by remember { mutableStateOf<Uri?>(null) }
    val selectorFotos = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) uriPerfil = uri }

    val tareasCompletadas = listaHabitos.filter { it.estaTerminado }
    val tareasPendientes = listaHabitos.filter { !it.estaTerminado }
    val progresoGeneral = if (listaHabitos.isNotEmpty()) tareasCompletadas.size.toFloat() / listaHabitos.size.toFloat() else 0f
    val colorProgreso = if (progresoGeneral == 1f && listaHabitos.isNotEmpty()) colorExito else acentoOro

    val paddingSuperiorCámara = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    Box(modifier = modifier.fillMaxSize().background(colorFondo)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = paddingSuperiorCámara + 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { esModoOscuro = !esModoOscuro }, modifier = Modifier.background(colorSuperficie, CircleShape)) {
                        Icon(if (esModoOscuro) Icons.Rounded.WbSunny else Icons.Rounded.ModeNight, null, tint = if (esModoOscuro) acentoOro else textoS)
                    }

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        var expandirMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { expandirMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menú", tint = textoP)
                        }
                        DropdownMenu(
                            expanded = expandirMenu,
                            onDismissRequest = { expandirMenu = false },
                            modifier = Modifier.background(colorSuperficie)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ver Historial (${tareasCompletadas.size})", color = textoP) },
                                onClick = {
                                    expandirMenu = false
                                    mostrarTareasCompletadas = true
                                }
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(colorSuperficie)
                            .border(3.dp, colorProgreso, CircleShape)
                            .clickable { selectorFotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uriPerfil != null) AsyncImage(model = uriPerfil, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                        else Icon(Icons.Default.Person, contentDescription = null, tint = colorProgreso, modifier = Modifier.size(50.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // NOMBRE EDITABLE EXPLÍCITO
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { mostrarDialogoNombre = true }.padding(4.dp)
                    ) {
                        Text("¡Bienvenido, $nombreUsuario! 👋", color = textoP, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Edit, contentDescription = "Editar nombre", tint = textoS, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { fraseActualIdx = (fraseActualIdx + 1) % frasesMotivadoras.size },
                    colors = CardDefaults.cardColors(containerColor = colorSuperficie),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = frasesMotivadoras[fraseActualIdx], color = textoS, fontSize = 15.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp).fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                val progresoAnimado by animateFloatAsState(targetValue = progresoGeneral)
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(if (esModoOscuro) Color(0xFF151515) else Color(0xFFE0E0E0)).padding(24.dp)) {
                    Column {
                        Text("Logro del día", color = if(esModoOscuro) Color.LightGray else Color.DarkGray, fontSize = 14.sp)
                        Text("${(progresoAnimado * 100).toInt()}%", color = if(esModoOscuro) Color.White else Color.Black, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(progress = { progresoAnimado }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = colorProgreso, trackColor = if(esModoOscuro) Color.White.copy(0.1f) else Color.Black.copy(0.1f))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("Resumen Semanal", color = textoS, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val diaActual = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    val indiceDiaSemana = if (diaActual == Calendar.SUNDAY) 6 else diaActual - 2
                    listOf("L", "M", "M", "J", "V", "S", "D").forEachIndexed { index, inicial ->
                        val esHoy = index == indiceDiaSemana
                        val colorBola = when {
                            esHoy && listaHabitos.isEmpty() -> colorSuperficie
                            esHoy && progresoGeneral == 1f -> colorExito
                            esHoy && progresoGeneral < 1f -> acentoOro
                            index < indiceDiaSemana -> Color.Gray.copy(alpha = 0.5f)
                            else -> colorSuperficie
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(inicial, color = textoS, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(colorBola).border(1.dp, if (colorBola == colorSuperficie) textoS.copy(0.2f) else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) {
                                if ((esHoy && progresoGeneral == 1f) || index < indiceDiaSemana) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Lista de hoy (Pendientes)", color = textoS, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    if (tareasPendientes.isEmpty()) Text(if(listaHabitos.isEmpty()) "Agrega una tarea" else "¡Todo listo! 🎉", color = if(listaHabitos.isEmpty()) textoS.copy(0.5f) else colorExito, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- TAREAS PENDIENTES (Sin botón de check directo) ---
            items(tareasPendientes) { habito ->
                val esElActivo = habitoActivoId == habito.id
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (esElActivo) acentoOro.copy(0.1f) else Color.Transparent).padding(vertical = 12.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {

                    // ÚNICA FORMA DE AVANZAR: Botón Play
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(if (esElActivo) acentoOro else colorSuperficie).border(1.dp, if (esElActivo) Color.Transparent else textoS.copy(0.5f), CircleShape).clickable {
                        if (esElActivo) { cronometroCorriendo = false; habitoActivoId = null }
                        else { habitoActivoId = habito.id; segundosRestantesSesion = if (segundosRestantesSesion > 0 && habitoActivoId == habito.id) segundosRestantesSesion else habito.metaSegundosPorSesion; cronometroCorriendo = true }
                    }, contentAlignment = Alignment.Center) {
                        if (esElActivo) Icon(Icons.Default.PlayArrow, contentDescription = "Pausar", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(habito.nombre, color = textoP, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        if (esElActivo) {
                            Text("Corriendo: ${formatearTemporizador(segundosRestantesSesion)}", color = acentoOro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val progresoTarea = 1f - (segundosRestantesSesion.toFloat() / habito.metaSegundosPorSesion.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(progress = { progresoTarea }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape), color = acentoOro, trackColor = colorSuperficie)
                        } else {
                            Text("${habito.horaAlerta} • ${habito.metaSegundosPorSesion / 60} Min / ${habito.categoria.nombre}", color = habito.categoria.color, fontSize = 12.sp)
                        }
                    }
                    Row {
                        IconButton(onClick = { habitoParaEditar = habito; mostrarDialogoCreacion = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = textoS, modifier = Modifier.size(18.dp)) }
                        IconButton(onClick = { listaHabitos = listaHabitos.filter { it.id != habito.id } }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) }
                    }
                }
                HorizontalDivider(color = colorSuperficie, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
            }
        }

        // Overlay de la Animación de Partículas
        if (mostrarAnimacionLogro) {
            AnimacionCascadaLogro(onAnimacionTerminada = { mostrarAnimacionLogro = false })
        }

        ExtendedFloatingActionButton(
            onClick = { habitoParaEditar = null; mostrarDialogoCreacion = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = acentoOro,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, null)
            Text(" Nueva Tarea", fontWeight = FontWeight.Bold)
        }
    }

    // ==========================================
    // DIÁLOGOS Y MENÚS
    // ==========================================

    if (tareaAlertaActual != null) {
        AlertDialog(
            onDismissRequest = { tareaAlertaActual = null },
            containerColor = colorSuperficie,
            title = { Text("¡Es Hora! ⏰", color = acentoOro, fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = { Text("Es el momento de empezar tu tarea:\n\n**${tareaAlertaActual!!.nombre}**", color = textoP, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                Button(onClick = {
                    val h = tareaAlertaActual!!
                    tareaAlertaActual = null
                    habitoActivoId = h.id
                    segundosRestantesSesion = h.metaSegundosPorSesion
                    cronometroCorriendo = true
                }, colors = ButtonDefaults.buttonColors(acentoOro), modifier = Modifier.fillMaxWidth()) {
                    Text("¡Empezar ahora!", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { tareaAlertaActual = null }, modifier = Modifier.fillMaxWidth()) { Text("Más tarde", color = textoS) } }
        )
    }

    // --- HISTORIAL DE HOY MEJORADO (DISEÑO DE TARJETAS Y PROGRESO 100%) ---
    if (mostrarTareasCompletadas) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(onDismissRequest = { mostrarTareasCompletadas = false }, sheetState = sheetState, containerColor = colorFondo) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                Text("Historial de Hoy", color = textoP, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(16.dp))

                if (tareasCompletadas.isEmpty()) {
                    Text("Aún no has completado tareas hoy. ¡Tú puedes!", color = textoS, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(tareasCompletadas) { habito ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorSuperficie)
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colorExito, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(habito.nombre, color = textoP, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                        // Botón para eliminar del historial
                                        IconButton(onClick = { listaHabitos = listaHabitos.filter { it.id != habito.id } }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("${habito.horaAlerta} • Completado al 100% • ${habito.categoria.nombre}", color = colorExito, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Barra de progreso llena indicando que se completó el 100%
                                    LinearProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                        color = colorExito,
                                        trackColor = colorFondo
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO CREACIÓN CON RUEDA TIPO SCROLL
    if (mostrarDialogoCreacion) {
        var nombreTemp by remember { mutableStateOf(habitoParaEditar?.nombre ?: "") }

        var horasSeleccionadas by remember { mutableStateOf(habitoParaEditar?.metaSegundosPorSesion?.div(3600) ?: 0) }
        var minutosSeleccionados by remember { mutableStateOf((habitoParaEditar?.metaSegundosPorSesion?.rem(3600))?.div(60) ?: 30) }
        var segundosSeleccionados by remember { mutableStateOf(habitoParaEditar?.metaSegundosPorSesion?.rem(60) ?: 0) }

        val horaActualParts = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()).split(":")
        var alertH by remember { mutableStateOf(if(habitoParaEditar != null) habitoParaEditar!!.horaAlerta.split(":")[0].toInt() else horaActualParts[0].toInt()) }
        var alertM by remember { mutableStateOf(if(habitoParaEditar != null) habitoParaEditar!!.horaAlerta.split(":")[1].toInt() else horaActualParts[1].toInt()) }

        var cantSesiones by remember { mutableStateOf(habitoParaEditar?.sesionesTotales?.toString() ?: "1") }
        var catSel by remember { mutableStateOf(habitoParaEditar?.categoria ?: categorias.firstOrNull()) }
        var mensajeError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoCreacion = false },
            containerColor = colorSuperficie,
            title = { Text(if(habitoParaEditar == null) "Nueva Tarea" else "Editar Tarea", color = textoP, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (mensajeError.isNotEmpty()) Text(mensajeError, color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))

                    OutlinedTextField(value = nombreTemp, onValueChange = { nombreTemp = it; mensajeError = "" }, label = { Text("Nombre de la tarea") }, modifier = Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(color = textoP))
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Hora de la Alerta", color = textoP, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        SelectorRueda(rango = 0..23, valorActual = alertH, onValorCambiado = { alertH = it }, etiqueta = "hr", colorTextoP = textoP, colorTextoS = textoS, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        SelectorRueda(rango = 0..59, valorActual = alertM, onValorCambiado = { alertM = it }, etiqueta = "min", colorTextoP = textoP, colorTextoS = textoS, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Duración de la tarea", color = textoP, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        SelectorRueda(rango = 0..23, valorActual = horasSeleccionadas, onValorCambiado = { horasSeleccionadas = it }, etiqueta = "h", colorTextoP = textoP, colorTextoS = textoS, modifier = Modifier.weight(1f))
                        SelectorRueda(rango = 0..59, valorActual = minutosSeleccionados, onValorCambiado = { minutosSeleccionados = it }, etiqueta = "m", colorTextoP = textoP, colorTextoS = textoS, modifier = Modifier.weight(1f))
                        SelectorRueda(rango = 0..59, valorActual = segundosSeleccionados, onValorCambiado = { segundosSeleccionados = it }, etiqueta = "s", colorTextoP = textoP, colorTextoS = textoS, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categorias) { cat -> Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(catSel == cat) cat.color else colorFondo).clickable { catSel = cat }.padding(10.dp)) { Text(cat.nombre, color = if(catSel == cat) Color.Black else textoS, fontWeight = FontWeight.Bold) } }
                        item { Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, acentoOro, RoundedCornerShape(8.dp)).clickable { mostrarDialogoCategoria = true }.padding(10.dp)) { Text("+ Nueva", color = acentoOro) } }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val segsTotales = (horasSeleccionadas * 3600) + (minutosSeleccionados * 60) + segundosSeleccionados
                    val sTotales = cantSesiones.toIntOrNull() ?: 1
                    val stringAlerta = "${alertH.toString().padStart(2, '0')}:${alertM.toString().padStart(2, '0')}"

                    if (nombreTemp.isBlank()) { mensajeError = "Ingresa un nombre."; return@Button }
                    if (segsTotales <= 0) { mensajeError = "La duración debe ser mayor a 0."; return@Button }

                    val fechaSeleccionada = System.currentTimeMillis()

                    if (catSel != null) {
                        if (habitoParaEditar == null) {
                            listaHabitos = listaHabitos + Habito(
                                id = listaHabitos.size + 1,
                                nombre = nombreTemp,
                                metaSegundosPorSesion = segsTotales,
                                segundosTrabajados = 0,
                                fechaLimiteMillis = fechaSeleccionada,
                                horaAlerta = stringAlerta,
                                sesionesTotales = sTotales,
                                sesionesCompletadas = 0,
                                categoria = catSel!!
                            )
                        } else {
                            listaHabitos = listaHabitos.map {
                                if(it.id == habitoParaEditar!!.id) {
                                    it.copy(
                                        nombre = nombreTemp,
                                        metaSegundosPorSesion = segsTotales,
                                        fechaLimiteMillis = fechaSeleccionada,
                                        horaAlerta = stringAlerta,
                                        sesionesTotales = sTotales,
                                        categoria = catSel!!
                                    )
                                } else it
                            }
                        }
                        mostrarDialogoCreacion = false
                    }
                }, colors = ButtonDefaults.buttonColors(acentoOro)) { Text("Guardar", color = Color.Black) }
            }
        )
    }

    if (mostrarDialogoCategoria) {
        var nCat by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { mostrarDialogoCategoria = false }, containerColor = colorSuperficie, text = { OutlinedTextField(value = nCat, onValueChange = { nCat = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(color = textoP)) }, confirmButton = { Button(onClick = { if(nCat.isNotBlank()) { categorias = categorias + Categoria(nCat, Color((0..0xFFFFFF).random() or 0xFF000000.toInt())); mostrarDialogoCategoria = false } }, colors = ButtonDefaults.buttonColors(acentoOro)) { Text("Crear", color = Color.Black) } })
    }
    if (mostrarDialogoNombre) {
        var tN by remember { mutableStateOf(nombreUsuario) }
        AlertDialog(onDismissRequest = { mostrarDialogoNombre = false }, containerColor = colorSuperficie, text = { OutlinedTextField(value = tN, onValueChange = { tN = it }, label = { Text("Tu nombre") }, modifier = Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(color = textoP)) }, confirmButton = { Button(onClick = { nombreUsuario = tN; mostrarDialogoNombre = false }, colors = ButtonDefaults.buttonColors(acentoOro)) { Text("Guardar", color = Color.Black) } })
    }
}