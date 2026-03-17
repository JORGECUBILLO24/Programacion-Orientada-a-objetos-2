


class Estudiante (
    val nombre: String,
    val carnet: String,
    val carrera: String,
    val año : Int,

) {
    fun mostrardatos() {
        println("Nombre: $nombre")
        println("Carnet: $carnet")
        println("Carrera: $carrera")
        println("Año: $año")
    }


}
fun main(args: Array<String>) {
    val estudiante1 = Estudiante("Maria Gomez", "2023-12345", "Ingeniería en Sistemas", 2)
    println(estudiante1.mostrardatos())
    println("--------------------------------------------------")
    val estudiante2 = Estudiante("Carlos Ramirez", "2022-54321", "Derecho", 3)
    println(estudiante2.mostrardatos())
    println("--------------------------------------------------")

}