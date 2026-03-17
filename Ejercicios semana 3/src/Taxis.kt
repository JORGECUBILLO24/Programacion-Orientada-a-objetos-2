class Taxis (
    val placa : String,
    val Conductor : String,
    val modelo : String,
) {

    fun iniciarServicio() {
        println("Placa: $placa")
        println("Conductor: $Conductor")
        println("Modelo: $modelo")
    }

}

fun main(args: Array<String>) {
    val taxi1 = Taxis("CH1234", "Jorge Cubillo", "Toyota Corolla")
    taxi1.iniciarServicio()
    println("--------------------------------------------------")

    val taxi2 = Taxis("MT5678", "Son Goku", "Honda Civic")
    taxi2.iniciarServicio()
    println("--------------------------------------------------")

    val taxi3 = Taxis("M9012", "Luis Alberto", "Ford Focus")
    taxi3.iniciarServicio()
    println("--------------------------------------------------")
}