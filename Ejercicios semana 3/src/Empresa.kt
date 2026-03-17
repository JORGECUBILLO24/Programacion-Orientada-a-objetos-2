
interface Empleado {
    val nombre: String
    val salario: Double
}

class programador(override val nombre: String, override val salario: Double, val lenguaje: String) : Empleado {
    fun mostrardatos() {
        println("Nombre: $nombre")
        println("Salario: $salario")
        println("Lenguaje de Programación: $lenguaje")
    }

}

fun main(args: Array<String>) {
    val programador1 = programador("Jorge Cubillo", 50000.0, "Kotlin")
    println(programador1.mostrardatos())
    println("--------------------------------------------------")
    val programador2 = programador("Gabriel Garcia", 60000.0, "Java")
    println(programador2.mostrardatos())
    println("--------------------------------------------------")
    val programador3 = programador("Isaac Aragon", 55000.0, "Python")
    println(programador3.mostrardatos())
    println("--------------------------------------------------")
}