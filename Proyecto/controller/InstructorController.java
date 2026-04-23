package controller;

import model.*;
import util.Bitacora;
import util.CSVLoader;

public class InstructorController {

    //Gestion de notas
    public static String crearNota(String codCurso, String codSec, String codEst, String etiqueta,
                                   double pond, double nota, String fecha, String codInstructor) {

        //Validar que el instructor tenga asignada la seccion
        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) {
            return "ERROR: Seccion " + codSec + " no encontrada";
        }
        if (!sec.getCodInstructor().equals(codInstructor)) {
            return "ERROR: El instructor no tiene asignada la seccion " + codSec;
        }
        //Validar que el estudiante este inscrito
        if (!sec.estaInscrito(codEst)) {
            return "ERROR: Estudiante " + codEst + " no inscrito en seccion " + codSec;
        }
        //Validar rangos de notas
        if (nota < 0 || nota > 100) {
            return "ERROR: La nota debe estar entre 0 y 100";
        }
        if (pond <= 0) {
            return "ERROR: La ponderacion debe ser mayor a 0";
        }
        //Verificar duplicado de etiqueta
        Nota[] notas = DataStore.getNotas();
        int total = DataStore.getTotalNotas();
        for (int i = 0; i < total; i++) {
            if (notas[i].getCodSeccion().equals(codSec) &&
                    notas[i].getCodEstudiante().equals(codEst) &&
                    notas[i].getEtiqueta().equals(etiqueta)) {
                return "ERROR: Ya existe una nota con etiqueta " +
                        etiqueta + " para este estudiante en esta seccion";
            }
        }
        Nota nuevaNota = new Nota(codCurso, codSec, codEst,
                etiqueta, pond, nota, fecha);
        DataStore.agregarNota(nuevaNota);
        Bitacora.registrar("INSTRUCTOR", codInstructor, "CREAR_NOTA",
                "EXITOSA", "Nota " + etiqueta + " creada para estudiante " +
                        codEst + " en seccion " + codSec);
        return "OK: Nota registrada exitosamente";
    }

    public static String actualizarNota(int idx, double nuevaPond, double nuevaNota,
                                        String codInstructor) {
        Nota[] notas = DataStore.getNotas();
        int total = DataStore.getTotalNotas();

        if (idx < 0 || idx >= total) {
            return "ERROR: Indice de nota invalido";
        }

        Nota n = notas[idx];

        //Validar que el instructor tenga la seccion asignada
        Seccion sec = DataStore.buscarSeccion(n.getCodSeccion());
        if (sec == null || !sec.getCodInstructor().equals(codInstructor)) {
            return "ERROR: No tiene permisos para modificar esta nota";
        }

        //Validar rangos
        if (nuevaNota < 0 || nuevaNota > 100) {
            return "ERROR: La nota debe estar entre 0 y 100";
        }
        if (nuevaPond <= 0) {
            return "ERROR: La ponderacion debe ser mayor a 0";
        }

        n.setPonderacion(nuevaPond);
        n.setNota(nuevaNota);
        DataStore.guardarDatos();
        Bitacora.registrar("INSTRUCTOR", codInstructor, "ACTUALIZAR_NOTA", "EXITOSA",
                "Nota actualizada en seccion " + n.getCodSeccion() +
                        " para estudiante " + n.getCodEstudiante());
        return "Nota actualizada exitosamente";
    }

    public static String eliminarNota(int idx, String codInstructor) {
        Nota[] notas = DataStore.getNotas();
        int total = DataStore.getTotalNotas();

        if (idx < 0 || idx >= total) {
            return "ERROR: Indice de nota no valido";
        }

        Nota n = notas[idx];

        //Validar que el instructor tenga la seccion asignada
        Seccion sec = DataStore.buscarSeccion(n.getCodSeccion());
        if (sec == null || !sec.getCodInstructor().equals(codInstructor)) {
            return "ERROR: No tiene permisos para eliminar esta nota";
        }

        DataStore.eliminarNota(idx);
        Bitacora.registrar("INSTRUCTOR", codInstructor, "ELIMINAR_NOTA", "EXITOSA",
                "Nota eliminada en seccion " + n.getCodSeccion() +
                        " para estudiante " + n.getCodEstudiante());
        return "Nota eliminada exitosamente";
    }

    public static String cargarNotasCSV(String ruta, String codInstructor) {
        String resumen = CSVLoader.cargarNotas(
                ruta,
                DataStore.getNotas(),
                DataStore.getTotalNotas(),
                DataStore.getMaxNotas());
        DataStore.guardarDatos();
        Bitacora.registrar("INSTRUCTOR", codInstructor, "CARGAR_CSV_NOTAS", "EXITOSA",
                resumen);
        return resumen;
    }

    //Consultas de Notas
    public static Nota[] getNotasPorSeccion(String codSec) {
        Nota[] todas = DataStore.getNotas();
        int total = DataStore.getTotalNotas();
        int count = 0;

        for (int i = 0; i < total; i++) {
            if (todas[i].getCodSeccion().equals(codSec)) count++;
        }

        Nota[] resultado = new Nota[count];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            if (todas[i].getCodSeccion().equals(codSec)) {
                resultado[idx++] = todas[i];
            }
        }
        return resultado;
    }

    public static Nota[] getNotasPorEstudiante(String codEst, String codSec) {
        Nota[] todas = DataStore.getNotas();
        int total = DataStore.getTotalNotas();
        int count = 0;

        for (int i = 0; i < total; i++) {
            if (todas[i].getCodEstudiante().equals(codEst) &&
                    todas[i].getCodSeccion().equals(codSec)) count++;
        }

        Nota[] resultado = new Nota[count];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            if (todas[i].getCodEstudiante().equals(codEst) &&
                    todas[i].getCodSeccion().equals(codSec)) {
                resultado[idx++] = todas[i];
            }
        }
        return resultado;
    }

    public static double calcularPromedio(String codEst, String codSec) {
        Nota[] notas = getNotasPorEstudiante(codEst, codSec);
        if (notas.length == 0) return 0.0;

        double sumPond = 0; double sumNota = 0;
        for (int i = 0; i < notas.length; i++) {
            sumNota += notas[i].getNota() * notas[i].getPonderacion();
            sumPond += notas[i].getPonderacion();
        }
        if (sumPond == 0) return 0.0;
        return sumNota / sumPond;
    }

    //Secciones del instructor
    public static Seccion[] getSeccionesDeInstructor(String codInstructor) {
        Seccion[] todas = DataStore.getSecciones();
        int total = DataStore.getTotalSecciones();
        int count = 0;

        for (int i = 0; i < total; i++) {
            if (todas[i].getCodInstructor().equals(codInstructor)) count++;
        }

        Seccion[] resultado = new Seccion[count];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            if (todas[i].getCodInstructor().equals(codInstructor)) {
                resultado[idx++] = todas[i];
            }
        }
        return resultado;
    }
}