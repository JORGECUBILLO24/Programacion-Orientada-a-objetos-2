interface Calculadora {
    fun sumar(num1: Int, num2: Int): Int
    fun max(num1: Int, num2: Int): Int
    fun min(num1: Int, num2: Int): Int // Cambié "mix" por "min"
}
class Micalc: Calculadora {
    override fun sumar(num1: Int, num2: Int): Int {
        return num1 + num2
    }

    override fun max(num1: Int, num2: Int): Int {
        if
                (num1 > num2) return num1 else return num2
    }

    override fun min(num1: Int, num2: Int): Int {
         if (num1 < num2) return num1 else return num2
    }

}
fun main (args: Array<String>) {
    val calculadora = Micalc()
    println("Suma: ${calculadora.sumar(5, 3)}")
    println("Máximo: ${calculadora.max(5, 3)}")
    println("Mínimo: ${calculadora.min(5, 3)}")
}