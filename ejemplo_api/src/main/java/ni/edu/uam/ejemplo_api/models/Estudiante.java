package ni.edu.uam.ejemplo_api.models;

public class Estudiante {

    private String nombre;
    private String asignatura;
    private int corte1;
    private int corte2;
    private int corte3;


    public Estudiante() {}

    public Estudiante(String nombre, String asignatura, int corte1, int corte2, int corte3) {
        this.nombre = nombre;
        this.asignatura = asignatura;
        this.corte1 = corte1;
        this.corte2 = corte2;
        this.corte3 = corte3;
    }

    public String getNombre() { return nombre; }
    public String getAsignatura() { return asignatura; }
    public int getCorte1() { return corte1; }
    public int getCorte2() { return corte2; }
    public int getCorte3() { return corte3; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public void setCorte1(int corte1) { this.corte1 = corte1; }
    public void setCorte2(int corte2) { this.corte2 = corte2; }
    public void setCorte3(int corte3) { this.corte3 = corte3; }
}