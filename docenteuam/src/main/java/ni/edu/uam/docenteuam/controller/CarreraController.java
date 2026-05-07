package ni.edu.uam.docenteuam.controller;

import ni.edu.uam.docenteuam.models.Carrera;
import ni.edu.uam.docenteuam.Service.CarreraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraService service;


    public CarreraController(CarreraService service) {
        this.service = service;
    }

    @GetMapping
    public List<Carrera> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Carrera findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Carrera save(@RequestBody Carrera carrera) {
        return service.save(carrera);
    }

    @PutMapping("/{id}")
    public Carrera update(@PathVariable Long id, @RequestBody Carrera carrera) {
        carrera.setId(id);
        return service.save(carrera);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}