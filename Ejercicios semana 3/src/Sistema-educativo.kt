open class Persona(val nombre: String, val edad: Int)

class Docente(nombre: String, edad: Int, val materia: String) : Persona(nombre, edad) {
    fun mostrarDatos() {
        println("Nombre: $nombre")
        println("Edad: $edad")
        println("Materia: $materia")
    }
}

class Estudi(nombre: String, edad: Int, val carrera: String) : Persona(nombre, edad) {
    fun mostrarDatos() {
        println("Nombre: $nombre")
        println("Edad: $edad")
        println("Carrera: $carrera")
    }
}

fun main() {
    val docente1 = Docente("Juan Pérez", 40, "Matemáticas")
    val estudiante1 = Estudi("María Gómez", 20, "Ingeniería")

    println("Datos del docente:")
    docente1.mostrarDatos()

    println("\nDatos del estudiante:")
    estudiante1.mostrarDatos()
}