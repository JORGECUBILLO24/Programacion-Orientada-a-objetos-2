
class Libro(val titulo: String, val autor: String, val añoPublicacion: Int) {


    fun mostrarInformacion() {
        println(" Título: $titulo")
        println(" Autor: $autor")
        println(" Año de publicación: $añoPublicacion")
        println("--------------------------------------------------")
    }
}

fun main() {

    val libro1 = Libro("Clean Code (Código Limpio)", "Robert C. Martin", 2008)
    val libro2 = Libro("The Pragmatic Programmer", "Andrew Hunt y David Thomas", 1999)
    val libro3 = Libro("Kotlin in Action", "Dmitry Jemerov y Svetlana Isakova", 2017)
    val libro4 = Libro("Cracking the Coding Interview", "Gayle Laakmann McDowell", 2015)


    println("=== CATÁLOGO DE LA BIBLIOTECA UNIVERSITARIA ===")
    libro1.mostrarInformacion()
    libro2.mostrarInformacion()
    libro3.mostrarInformacion()
    libro4.mostrarInformacion()
}