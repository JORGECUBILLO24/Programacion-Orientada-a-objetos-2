package ni.edu.uam.docenteuam.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private long id;
    @Column (name = "nombre_carrera", nullable  = false,length=100)
    private String nombre;
    @Column(name = "Descripcion_carrera",length=100)

    private String descripcion;
}
