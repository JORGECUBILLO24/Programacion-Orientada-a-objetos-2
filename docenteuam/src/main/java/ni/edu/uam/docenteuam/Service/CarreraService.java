package ni.edu.uam.docenteuam.Service;


import ni.edu.uam.docenteuam.models.Carrera;
import ni.edu.uam.docenteuam.repository.CarreraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository repo;

    public CarreraService(CarreraRepository repo) {
        this.repo = repo;
    }

    public List<Carrera> findAll(){
        return repo.findAll();
    }

    public Carrera findById(Long id){
        return repo.findById(id).orElse(null);
    }

    public Carrera save(Carrera carrera){
        return repo.save(carrera);
    }

    public void delete(Long id){
        repo.deleteById(id);
    }
}