class bus (val NumRuta: Double , val capacidad: Int , val Conductor : String) {
    fun Iniciarutas() {
        println("Número de Ruta: $NumRuta")
        println("Capacidad: $capacidad")
        println("Conductor: $Conductor")
    }
}
fun main(args: Array<String>) {
    val bus1 = bus(101.5, 50, "Juan Perez")
    println(bus1.Iniciarutas())
    println("--------------------------------------------------")
    val bus2 = bus(202.3, 40, "Maria Gomez")
    println(bus2.Iniciarutas())
    println("--------------------------------------------------")
    val bus3 = bus(303.7, 60, "Carlos Rodriguez")
    println(bus3.Iniciarutas())
    println("--------------------------------------------------")

}