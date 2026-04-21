package ni.edu.tareasapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


val AzulPrimario = Color(0xFF2563EB)  
val AzulOscuro = Color(0xFF1E40AF)
val AzulClaro = Color(0xFFDBEAFE)
val FondoGrisClaro = Color(0xFFF8FAFC)
val SuperficieOscura = Color(0xFF0F172A)
val PaletaClara = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    background = FondoGrisClaro,
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F9)
)

val PaletaOscura = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color.Black,
    primaryContainer = AzulOscuro,
    onPrimaryContainer = AzulClaro,
    background = SuperficieOscura,
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155)
)
// ------------------------------------------------

data class Tarea(
    val id: String = UUID.randomUUID().toString(),
    val texto: String,
    val imagen: Uri?,
    val descripcion: String? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val colorScheme = if (isDarkTheme) PaletaOscura else PaletaClara

            MaterialTheme(
                colorScheme = colorScheme,
                // Aplicamos un ligero redondeo general si se desea
                shapes = MaterialTheme.shapes.copy(
                    small = RoundedCornerShape(8.dp),
                    medium = RoundedCornerShape(16.dp),
                    large = RoundedCornerShape(24.dp)
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TareasAppScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasAppScreen(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    // Estados del Formulario
    var idActual by remember { mutableStateOf<String?>(null) }
    var textoIngresado by remember { mutableStateOf("") }
    var descripcionIngresada by remember { mutableStateOf("") }
    var fechaInicioIngresada by remember { mutableStateOf("") }
    var fechaFinIngresada by remember { mutableStateOf("") }
    var imagenSeleccionadaUri by remember { mutableStateOf<Uri?>(null) }

    // Estados de la App
    val listaDeTareas = remember { mutableStateListOf<Tarea>() }
    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }
    var mostrandoFormulario by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados de los Pickers
    var mostrarDatePickerInicio by remember { mutableStateOf(false) }
    var mostrarDatePickerFin by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val selectorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imagenSeleccionadaUri = it
    }

    fun limpiarFormulario() {
        idActual = null
        textoIngresado = ""
        descripcionIngresada = ""
        fechaInicioIngresada = ""
        fechaFinIngresada = ""
        imagenSeleccionadaUri = null
        mostrandoFormulario = false
        tareaSeleccionada = null
    }

    if (mostrarDatePickerInicio || mostrarDatePickerFin) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerInicio = false; mostrarDatePickerFin = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    val fecha = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(Date(millis))
                    if (mostrarDatePickerInicio) fechaInicioIngresada = fecha else fechaFinIngresada = fecha
                    mostrarDatePickerInicio = false
                    mostrarDatePickerFin = false
                }) { Text("Aceptar", color = MaterialTheme.colorScheme.primary) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            mostrandoFormulario && idActual == null -> "Nueva Tarea"
                            mostrandoFormulario && idActual != null -> "Editar Tarea"
                            tareaSeleccionada != null -> "Detalle"
                            else -> "Mis Tareas"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (mostrandoFormulario || tareaSeleccionada != null) {
                        IconButton(onClick = { limpiarFormulario() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    } else {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = "Logo",
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (tareaSeleccionada != null && !mostrandoFormulario) {
                        IconButton(onClick = {
                            val t = tareaSeleccionada!!
                            idActual = t.id
                            textoIngresado = t.texto
                            descripcionIngresada = t.descripcion ?: ""
                            fechaInicioIngresada = t.fechaInicio ?: ""
                            fechaFinIngresada = t.fechaFin ?: ""
                            imagenSeleccionadaUri = t.imagen
                            mostrandoFormulario = true
                            tareaSeleccionada = null
                        }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }

                        IconButton(onClick = {
                            listaDeTareas.removeIf { it.id == tareaSeleccionada?.id }
                            scope.launch { snackbarHostState.showSnackbar("Tarea eliminada") }
                            limpiarFormulario()
                        }) { Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
                    }
                    IconButton(onClick = { onThemeChange(!isDarkTheme) }) {
                        Icon(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (!mostrandoFormulario && tareaSeleccionada == null) {
                FloatingActionButton(
                    onClick = {
                        limpiarFormulario()
                        mostrandoFormulario = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Tarea", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                mostrandoFormulario -> {
                    FormularioTareaModerno(
                        texto = textoIngresado, onTextoChange = { textoIngresado = it },
                        desc = descripcionIngresada, onDescChange = { descripcionIngresada = it },
                        fInicio = fechaInicioIngresada, onFInicioClick = { mostrarDatePickerInicio = true },
                        fFin = fechaFinIngresada, onFFinClick = { mostrarDatePickerFin = true },
                        uri = imagenSeleccionadaUri, onUriClick = { selectorGaleria.launch("image/*") },
                        btnTexto = if (idActual != null) "Actualizar" else "Crear Tarea",
                        onGuardar = {
                            if (textoIngresado.isNotBlank()) {
                                val nuevaTarea = Tarea(idActual ?: UUID.randomUUID().toString(), textoIngresado, imagenSeleccionadaUri, descripcionIngresada, fechaInicioIngresada, fechaFinIngresada)
                                if (idActual != null) {
                                    val index = listaDeTareas.indexOfFirst { it.id == idActual }
                                    if (index != -1) listaDeTareas[index] = nuevaTarea
                                } else {
                                    listaDeTareas.add(nuevaTarea)
                                }
                                scope.launch { snackbarHostState.showSnackbar("¡Guardado exitosamente!") }
                                limpiarFormulario()
                            }
                        }
                    )
                }
                tareaSeleccionada != null -> {
                    DetalleTareaViewModerno(tareaSeleccionada!!)
                }
                else -> {
                    if (listaDeTareas.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Outlined.CalendarToday, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No tienes tareas pendientes", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Toca el '+' para empezar", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(listaDeTareas) { tarea ->
                                ItemTareaModerno(tarea = tarea, onClick = { tareaSeleccionada = tarea })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTareaModerno(tarea: Tarea, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen o Icono redondeado
            if (tarea.imagen != null) {
                AsyncImage(
                    model = tarea.imagen,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.texto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!tarea.descripcion.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tarea.descripcion,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Mostrar fechas de forma minimalista
                if (!tarea.fechaInicio.isNullOrBlank() || !tarea.fechaFin.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${tarea.fechaInicio ?: "..."} - ${tarea.fechaFin ?: "..."}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FormularioTareaModerno(
    texto: String, onTextoChange: (String) -> Unit,
    desc: String, onDescChange: (String) -> Unit,
    fInicio: String, onFInicioClick: () -> Unit,
    fFin: String, onFFinClick: () -> Unit,
    uri: Uri?, onUriClick: () -> Unit,
    btnTexto: String, onGuardar: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        // Selector de Imagen Moderno
        item {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onUriClick() },
                contentAlignment = Alignment.Center
            ) {
                if (uri != null) {
                    AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Añadir foto", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Campos de texto
        item {
            OutlinedTextField(
                value = texto, onValueChange = onTextoChange,
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = desc, onValueChange = onDescChange,
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 4
            )
        }

        // Fechas alineadas
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fInicio, onValueChange = {}, label = { Text("Fecha Inicio") },
                    modifier = Modifier.weight(1f).clickable { onFInicioClick() }, enabled = false,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = { Icon(Icons.Outlined.CalendarToday, null) }
                )
                OutlinedTextField(
                    value = fFin, onValueChange = {}, label = { Text("Fecha Fin") },
                    modifier = Modifier.weight(1f).clickable { onFFinClick() }, enabled = false,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = { Icon(Icons.Outlined.CalendarToday, null) }
                )
            }
        }

        // Botón Guardar
        item {
            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(btnTexto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DetalleTareaViewModerno(tarea: Tarea) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Image
        if (tarea.imagen != null) {
            item {
                AsyncImage(
                    model = tarea.imagen,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = tarea.texto,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Fechas en formato de "Píldoras" (Badges)
                if (!tarea.fechaInicio.isNullOrBlank() || !tarea.fechaFin.isNullOrBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PildoraFecha("Inicio", tarea.fechaInicio)
                        PildoraFecha("Fin", tarea.fechaFin)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(24.dp))

                Text("Descripción", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (!tarea.descripcion.isNullOrBlank()) tarea.descripcion else "Esta tarea no tiene detalles adicionales.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PildoraFecha(titulo: String, fecha: String?) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CalendarToday, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(titulo.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Text(fecha ?: "Sin definir", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}