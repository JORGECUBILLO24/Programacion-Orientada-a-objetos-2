import javax.swing.JOptionPane

data class Tarea(val descripcion: String, val fecha: String, val monto: Double, val tipo: String)

fun main() {
    val gestorTareas = mutableListOf<Tarea>()
    var respuesta: String

    // 1. Llenamos la lista primero
    do {
        val descripcion = JOptionPane.showInputDialog(null, "Ingrese la descripción de la tarea:") ?: ""
        val fecha = JOptionPane.showInputDialog(null, "Ingrese la fecha de la tarea (dd/mm/yyyy):") ?: ""
        val monto = JOptionPane.showInputDialog(null, "Ingrese el monto asociado a la tarea:")?.toDoubleOrNull() ?: 0.0
        val tipo = JOptionPane.showInputDialog(null, "Ingrese el tipo de tarea (ingreso/gasto):") ?: ""

        gestorTareas.add(Tarea(descripcion, fecha, monto, tipo))

        respuesta = JOptionPane.showInputDialog(null, "¿Desea agregar otra tarea? (s/n)") ?: "n"

    } while (respuesta.equals("s", ignoreCase = true))


    val saldototal = gestorTareas.sumOf { if (it.tipo.equals("ingreso", ignoreCase = true)) it.monto else -it.monto }


    val verTareas = JOptionPane.showInputDialog(null, "¿Desea ver las tareas agregadas? (s/n)") ?: "n"

    if (verTareas.equals("s", ignoreCase = true)) {
        val tareas = gestorTareas.joinToString("\n") { "${it.descripcion} - ${it.fecha} - ${it.monto} - ${it.tipo}" }
        // Imprimimos las tareas y el saldo total al final
        JOptionPane.showMessageDialog(
            null,
            "Tareas en el gestor:\n$tareas\n\n------------------\nSaldo Total: $$saldototal"
        )
    } else {
        
        JOptionPane.showMessageDialog(null, "Saldo Total: $$saldototal")
    }
}