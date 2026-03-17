import javax.swing.JOptionPane

data class EventoCalendario(val dia: String, val mes: String, val descripcion: String)

fun main() {
    val miAgenda = mutableListOf<EventoCalendario>()
    var respuesta: String


    do {

        val dia = JOptionPane.showInputDialog(null, "Ingrese el día del evento:") ?: ""
        val mes = JOptionPane.showInputDialog(null, "Ingrese el mes del evento:") ?: ""
        val descripcion = JOptionPane.showInputDialog(null, "Ingrese la descripción del evento:") ?: ""

        miAgenda.add(EventoCalendario(dia, mes, descripcion))


        respuesta = JOptionPane.showInputDialog(null, "¿Desea agregar otro evento? (s/n)") ?: "n"

    } while (respuesta.equals("s", ignoreCase = true))


    val verEventos = JOptionPane.showInputDialog(null, "¿Desea ver los eventos agregados? (s/n)") ?: "n"

    if (verEventos.equals("s", ignoreCase = true)) {
        val eventos = miAgenda.joinToString("\n") { "${it.dia} de ${it.mes}: ${it.descripcion}" }
        JOptionPane.showMessageDialog(null, "Eventos en la agenda:\n$eventos")
    }


    val eliminarEvento = JOptionPane.showInputDialog(null, "¿Desea eliminar un evento? (s/n)") ?: "n"

    if (eliminarEvento.equals("s", ignoreCase = true)) {
        val eventoAEliminar = JOptionPane.showInputDialog(null, "Ingrese la descripción del evento a eliminar:") ?: ""


        val eventoEncontrado = miAgenda.find { it.descripcion.equals(eventoAEliminar, ignoreCase = true) }

        if (eventoEncontrado != null) {
            miAgenda.remove(eventoEncontrado)
            JOptionPane.showMessageDialog(null, "Evento eliminado con éxito: ${eventoEncontrado.descripcion}")
        } else {
            JOptionPane.showMessageDialog(null, "Evento no encontrado: $eventoAEliminar")
        }
    }
} 