package controller;

import model.*;
import util.Bitacora;
import threads.MonitorSesiones;
import threads.SimuladorInscripciones;

public class EstudianteController {

    //Inscripcion de cursos

    public static String inscribirSeccion(String codEst, String codSec) {
        Estudiante est = DataStore.buscarEstudiante(codEst);
        if (est == null) {
            return "ERROR: Estudiante no encontrado";
        }

        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) {
            return "ERROR: Seccion " + codSec + " no encontrada";
        }

        //Verificar inscripcion duplicada
        if (sec.estaInscrito(codEst)) {
            return "ERROR: Ya esta inscrito en la seccion " + codSec;
        }

        //Verificar traslapes entre horarios
        Seccion[] todas = DataStore.getSecciones();
        int total = DataStore.getTotalSecciones();
        String[] inscritas = est.getCursosInscritos();
        int totalInscritas = est.getTotalCursos();

        for (int i = 0; i < totalInscritas; i++) {
            for (int j = 0; j < total; j++) {
                if (todas[j].getCod().equals(inscritas[i]) &&
                        todas[j].getHorario().equals(sec.getHorario()) &&
                        todas[j].getSemestre().equals(sec.getSemestre())) {
                    return "ERROR: Traslape de horario con seccion " + inscritas[i];
                }
            }
        }

        //Inscribir
        sec.agregarEstudiante(codEst);
        est.inscribirCurso(codSec);
        DataStore.guardarDatos();
        SimuladorInscripciones.incrementar();
        Bitacora.registrar("ESTUDIANTE", codEst, "INSCRIBIR_SECCION", "EXITOSA",
                "Estudiante inscrito en seccion " + codSec);
        return "Inscripcion exitosa en seccion " + codSec;
    }

    public static String desasignarSeccion(String codEst, String codSec) {
        Estudiante est = DataStore.buscarEstudiante(codEst);
        if (est == null) {
            return "ERROR: Estudiante " + codEst + " no encontrado";
        }

        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) {
            return "ERROR: Seccion " + codSec + " no encontrada";
        }
        //Verificar inscripcion
        if (!sec.estaInscrito(codEst)) {
            return "ERROR: El estudiante no esta inscrito en la seccion " + codSec;
        }

        //Verificar que no tenga notas registradas
        Nota[] notas = DataStore.getNotas();
        int totalNotas = DataStore.getTotalNotas();
        for (int i = 0; i < totalNotas; i++) {
            if (notas[i].getCodEstudiante().equals(codEst) &&
                    notas[i].getCodSeccion().equals(codSec)) {
                return "ERROR: No se puede desasignar, " +
                        "el estudiante tiene notas en la seccion " + codSec;
            }
        }

        sec.eliminarEstudiante(codEst);
        est.desasignarCurso(codSec);
        DataStore.guardarDatos();

        SimuladorInscripciones.decrementar();
        Bitacora.registrar("ESTUDIANTE", codEst, "DESASIGNAR_SECCION", "EXITOSA",
                "Estudiante desasignado de seccion " + codSec);
        return "Estudiante desasignado de la seccion " + codSec + " correctamente";
    }

    //Consulta de calificaciones
    public static Nota[] getNotasPorSeccion(String codEst, String codSec) {
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

    public static Nota[] getNotasPorSemestre(String codEst, String semestre) {
        Nota[] todas = DataStore.getNotas();
        int total = DataStore.getTotalNotas();
        int count = 0;

        for (int i = 0; i < total; i++) {
            Seccion sec = DataStore.buscarSeccion(todas[i].getCodSeccion());
            if (todas[i].getCodEstudiante().equals(codEst) &&
                    sec != null && sec.getSemestre().equals(semestre)) count++;
        }

        Nota[] resultado = new Nota[count];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            Seccion sec = DataStore.buscarSeccion(todas[i].getCodSeccion());
            if (todas[i].getCodEstudiante().equals(codEst) &&
                    sec != null && sec.getSemestre().equals(semestre)) {
                resultado[idx++] = todas[i];
            }
        }
        return resultado;
    }

    public static double calcularPromedio(String codEst, String codSec) {
        Nota[] notas = getNotasPorSeccion(codEst, codSec);
        if (notas.length == 0) return 0.0;

        double sumPond = 0; double sumNota = 0;
        for (int i = 0; i < notas.length; i++) {
            sumNota += notas[i].getNota() * notas[i].getPonderacion();
            sumPond += notas[i].getPonderacion();
        }
        if (sumPond == 0) return 0.0;
        return sumNota / sumPond;
    }

    public static double calcularPromedioSemestral(String codEst, String semestre) {
        Nota[] notas = getNotasPorSemestre(codEst, semestre);
        if (notas.length == 0) return 0.0;

        double sumPond = 0; double sumNota = 0;
        for (int i = 0; i < notas.length; i++) {
            sumNota += notas[i].getNota() * notas[i].getPonderacion();
            sumPond += notas[i].getPonderacion();
        }
        if (sumPond == 0) return 0.0;
        return sumNota / sumPond;
    }

    //Secciones disponibles
    public static Seccion[] getSeccionesDisponibles(String codEst) {
        Seccion[] todas = DataStore.getSecciones();
        int total = DataStore.getTotalSecciones();
        Estudiante est = DataStore.buscarEstudiante(codEst);
        int count = 0;

        for (int i = 0; i < total; i++) {
            if (!todas[i].estaInscrito(codEst)) count++;
        }

        Seccion[] disponibles = new Seccion[count];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            if (!todas[i].estaInscrito(codEst)) {
                disponibles[idx++] = todas[i];
            }
        }
        return disponibles;
    }

    public static Seccion[] getSeccionesInscritas(String codEst) {
        Seccion[] todas = DataStore.getSecciones();
        int total = DataStore.getTotalSecciones();
        int count = 0;

        for (int i = 0; i < total; i++) {
            if (todas[i].estaInscrito(codEst)) count++;
        }

        Seccion[] inscritas = new Seccion[count];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            if (todas[i].estaInscrito(codEst)) {
                inscritas[idx++] = todas[i];
            }
        }
        return inscritas;
    }

    //Perfil de estudiante

    public static String actualizarPerfil(String codEst, String nuevoNomb, String nuevoCumple,
                                          String nuevoGenero, String contraActual, String nuevaContra) {
        Estudiante est = DataStore.buscarEstudiante(codEst);
        if (est == null) {
            return "ERROR: Estudiante no encontrado";
        }

        if (!nuevoNomb.isEmpty()) est.setNomb(nuevoNomb);
        if (!nuevoCumple.isEmpty()) est.setCumple(nuevoCumple);
        if (!nuevoGenero.isEmpty()) est.setGenero(nuevoGenero);

        if (!nuevaContra.isEmpty()) {
            if (!est.getContra().equals(contraActual)) {
                return "ERROR: Contraseña actual incorrecta";
            }
            est.setContra(nuevaContra);
        }

        DataStore.guardarDatos();
        Bitacora.registrar("ESTUDIANTE", codEst, "ACTUALIZAR_PERFIL", "EXITOSA",
                "Perfil actualizado");
        return "Perfil actualizado exitosamente";
    }
}