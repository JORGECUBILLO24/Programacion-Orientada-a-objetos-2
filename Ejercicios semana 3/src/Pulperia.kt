class producto (val nombre: String, val precio: Double,val cantidad: Int){
    fun mostrardatos() {
        println("Nombre: $nombre")
        println("Precio: $precio")
        println("Cantidad: $cantidad")
    }
}
fun main(args: Array<String>) {
    val producto1 = producto("Coca-Cola", 6.0, 10)
    println(producto1.mostrardatos())
    println("--------------------------------------------------")
    val producto2 = producto("Arroz", 1.4, 8)
    println(producto2.mostrardatos())
    println("--------------------------------------------------")
    val producto3 = producto("Frijoles", 1.3, 5)
    println(producto3.mostrardatos())
    println("--------------------------------------------------")
    val producto4 = producto("Huevos", 1.2, 12)
    println(producto4.mostrardatos())
    println("--------------------------------------------------")
    val producto5 = producto("Agua", 1.0, 20)
    println(producto5.mostrardatos())
    println("--------------------------------------------------")

}
