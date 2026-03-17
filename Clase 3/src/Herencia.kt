

open class Persona(

    private var nombre: String = "",
    private var apellido: String = "",
) {
    fun saludar() {
        println("Hola, mi nombre es $nombre $apellido,Mucho gusto")
    }
}
class Empleado(nombre: String,apellido: String, var rol : String) : Persona(nombre, apellido)

fun main (args: Array<String>) {

    val empleado = Empleado("Jorge", "cubillo", "patron")
    println(empleado.saludar())
    println("El rol del empleado es: ${empleado.rol}")
}