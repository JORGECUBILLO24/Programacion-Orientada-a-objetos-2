package ni.edu.uam.ejemplo_api.controllers;

import ni.edu.uam.ejemplo_api.models.Estudiante;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteController {

    @PostMapping
    public Map<String, String> EvaluaNota(@RequestBody Estudiante est) {
        Map<String, String> respuesta = new HashMap<>();

        if (est.getCorte1() < 0 || est.getCorte1() > 100) {
            respuesta.put("ERROR", "La nota 1 no es valida");
            return respuesta;}

        if (est.getCorte2() < 0 || est.getCorte2() > 100) {
            respuesta.put("ERROR", "La nota 2 no es valida");
            return respuesta;}

        if (est.getCorte3() < 0 || est.getCorte3() > 100) {
            respuesta.put("ERROR", "La nota 3 no es valida");
            return respuesta;}

        int notaFinal = getNotaFinal(est.getCorte1(), est.getCorte2(), est.getCorte3());
        String aprendizaje = "";

        if (notaFinal >= 0 && notaFinal <= 69) {
            aprendizaje = "Aprendizaje Inicial";
        } else if (notaFinal >= 70 && notaFinal <= 79) {
            aprendizaje = "Aprendizaje Fundamental";
        } else if (notaFinal >= 80 && notaFinal <= 89) {
            aprendizaje = "Aprendizaje Satisfactorio";
        } else if (notaFinal >= 90 && notaFinal <= 100) {
            aprendizaje = "Aprendizaje Avanzado";}

        respuesta.put("Estudiante", est.getNombre());
        respuesta.put("Asignatura", est.getAsignatura());
        respuesta.put("Nota Final", String.valueOf(notaFinal));
        respuesta.put("Aprendizaje", aprendizaje);

        return respuesta;
    }

    private int getNotaFinal(int nota1, int nota2, int nota3) {
        return (nota1 + nota2 + nota3) / 3;
    }
}