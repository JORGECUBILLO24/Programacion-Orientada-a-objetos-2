

class cuentaBancaria(private val numero_de_cuenta : String, private var saldo: Double) {
    fun depositar(monto: Double) {
        if (monto > 0) {
            saldo += monto
            println("Depósito exitoso. Nuevo saldo: $saldo")
        } else {
            println("Monto de depósito inválido.")
        }
    }

    fun retirar(monto: Double) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto
            println("Retiro exitoso. Nuevo saldo: $saldo")
        } else {
            println("Monto de retiro inválido o saldo insuficiente.")
        }
    }

    fun ConsultarSaldo() {
        println("Número de cuenta: $numero_de_cuenta, Saldo: $saldo")
    }



}

