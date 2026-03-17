class Estudiante {
    var nombre = ""
    var edad = 0

    fun estudiar() {
        println("$nombre está estudiando.")
    }
}


class Asignatura (private var nombre: String, private val precio: Double) {

    fun getNombre(): String {
        return "la asignatura es $nombre"
    }

    fun getPrecio(): Double {
        return precio
    }
}

fun main() {
    val estudiante1 = Estudiante()
    estudiante1.nombre = "jorge"
    estudiante1.edad = 19
    estudiante1.estudiar()

    val asignatura1 = Asignatura("Matemáticas", 150.0)
    println(asignatura1.getNombre())
    println("El precio de la asignatura es: ${asignatura1.getPrecio()}")

    val asignatura2 = Asignatura("POO", 200.0)
    println(asignatura2.getNombre())
    println("El precio de la asignatura es: ${asignatura2.getPrecio()}")
}