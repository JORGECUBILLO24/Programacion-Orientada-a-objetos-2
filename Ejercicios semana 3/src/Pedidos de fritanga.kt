data class Pedidos(
    val cliente : String,
    val Platillo : String,
    val precio : Int,
)
fun main(args: Array<String>) {
    val pedido1 = Pedidos("Juan Perez", "Chicharrón con yuca", 5)
    println("Cliente: ${pedido1.cliente}")
    println("Platillo: ${pedido1.Platillo}")
    println("Precio: ${pedido1.precio}")
    println("--------------------------------------------------")
    val pedido2 = Pedidos("Maria Gomez", "Carne asada con papas", 7)
    println("Cliente: ${pedido2.cliente}")
    println("Platillo: ${pedido2.Platillo}")
    println("Precio: ${pedido2.precio}")
    println("--------------------------------------------------")
    val pedido3 = Pedidos("Carlos Rodriguez", "Pollo frito con arroz", 6)
    println("Cliente: ${pedido3.cliente}")
    println("Platillo: ${pedido3.Platillo}")
    println("Precio: ${pedido3.precio}")
    println("--------------------------------------------------")
}