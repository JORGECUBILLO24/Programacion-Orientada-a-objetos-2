import javax.swing.JOptionPane

fun main() {

    val num1 = JOptionPane.showInputDialog(null, "Ingrese el primer número:")!!.toDoubleOrNull()
    val num2 = JOptionPane.showInputDialog(null, "Ingrese el segundo número:")!!.toDoubleOrNull()

    val calc = Calculadora()

    val suma = calc.sumar(num1!!.toInt(), num2!!.toInt())
    val resta = calc.restar(num1.toInt(), num2.toInt())
    val multiplicacion = calc.multiplicar(num1.toInt(), num2.toInt())
    val division = calc.dividir(num1.toInt(), num2.toInt())

    JOptionPane.showMessageDialog(null, "La suma es: $suma")
    JOptionPane.showMessageDialog(null, "La resta es: $resta")
    JOptionPane.showMessageDialog(null, "La multiplicación es: $multiplicacion")
    JOptionPane.showMessageDialog(null, "La división es: $division")

    if (num2 != 0.0) {
        val div = calc.dividir(num1.toInt(), num2.toInt())
        JOptionPane.showMessageDialog(null, "La división es: $div")
    } else {
        JOptionPane.showMessageDialog(null, "No se puede dividir por cero.")
    }

    var msn = ""

    for (num in num1.toInt()..num2.toInt()) {
        msn += num.toString()
    }
    JOptionPane.showMessageDialog(null, msn)

}