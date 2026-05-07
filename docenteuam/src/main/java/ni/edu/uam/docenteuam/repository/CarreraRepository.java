package ni.edu.uam.docenteuam.repository;

import ni.edu.uam.docenteuam.models.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

}

