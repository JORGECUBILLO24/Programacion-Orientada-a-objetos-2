import javax.swing.JOptionPane

fun main() {
    val numeroSecreto = (1..10).random()

    var intentos = 0
    var adivinado = false

    while (!adivinado && intentos < 3) {
        val input = JOptionPane.showInputDialog(null, "Adivina el número entre 1 y 10:")
        val numeroIngresado = input?.toIntOrNull()

        if (numeroIngresado == null) {
            JOptionPane.showMessageDialog(null, "Por favor, ingresa un número válido.")
            continue
        }

        intentos++


        when {
            numeroIngresado < numeroSecreto -> JOptionPane.showMessageDialog(null, "Demasiado bajo. Intenta de nuevo.")
            numeroIngresado > numeroSecreto -> JOptionPane.showMessageDialog(
                null, "Demasiado alto el tope es 10. Intenta de nuevo."
            )

            else -> {
                adivinado = true
                JOptionPane.showMessageDialog(null, " Adivinaste el número en $intentos intentos.")
            }
        }


        if (!adivinado && intentos < 3) {
            JOptionPane.showMessageDialog(null, "Te quedan ${3 - intentos} intentos.")
        }
    }

    if (!adivinado) {
        JOptionPane.showMessageDialog(null, "Eliminado gastaste  tus 3 intentos. El número secreto era: $numeroSecreto")
    }

}