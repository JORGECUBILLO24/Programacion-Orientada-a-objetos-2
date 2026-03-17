interface vehiculo {
    fun mover()

}
class Camion : vehiculo {
    override fun mover() {
        println("El camión se está moviendo")
    }
}

class Motocicleta : vehiculo {
    override fun mover() {
        println("La motocicleta se está moviendo")
    }
}
fun main(args: Array<String>) {
    val camion1 = Camion()
    camion1.mover()
    println("--------------------------------------------------")
    val motocicleta1 = Motocicleta()
    motocicleta1.mover()
    println("--------------------------------------------------")
}