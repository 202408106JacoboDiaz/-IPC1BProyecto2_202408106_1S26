package controller;

import model.*;
import util.Serializador;

public class DataStore {

    //Capacidades maximas
    private static final int MAX_INSTRUCTORES = 100;
    private static final int MAX_ESTUDIANTES = 500;
    private static final int MAX_CURSOS = 100;
    private static final int MAX_SECCIONES = 200;
    private static final int MAX_NOTAS = 5000;

    //Arreglos de datos
    private static Instructor[] instructores = new Instructor[MAX_INSTRUCTORES];
    private static Estudiante[] estudiantes  = new Estudiante[MAX_ESTUDIANTES];
    private static Curso[] cursos = new Curso[MAX_CURSOS];
    private static Seccion[] secciones = new Seccion[MAX_SECCIONES];
    private static Nota[] notas = new Nota[MAX_NOTAS];

    //Contadores
    private static int totalInstructores = 0;
    private static int totalEstudiantes = 0;
    private static int totalCursos = 0;
    private static int totalSecciones = 0;
    private static int totalNotas = 0;

    //Administrador precargado
    private static Admin admin = new Admin(
            "admin", "Administrador", "01/01/2000", "N/A", "IPC1B");

    //Archivos serializados
    private static final String FILE_INSTRUCTORES = "instructores.ser";
    private static final String FILE_ESTUDIANTES = "estudiantes.ser";
    private static final String FILE_CURSOS = "cursos.ser";
    private static final String FILE_SECCIONES = "secciones.ser";
    private static final String FILE_NOTAS = "notas.ser";

    //Cargar datos
    public static void cargarDatos() {
        Object obj;

        obj = Serializador.deserializar(FILE_INSTRUCTORES);
        if (obj != null) instructores = (Instructor[]) obj;
        obj = Serializador.deserializar(FILE_ESTUDIANTES);
        if (obj != null) estudiantes = (Estudiante[]) obj;
        obj = Serializador.deserializar(FILE_CURSOS);
        if (obj != null) cursos = (Curso[]) obj;
        obj = Serializador.deserializar(FILE_SECCIONES);
        if (obj != null) secciones = (Seccion[]) obj;
        obj = Serializador.deserializar(FILE_NOTAS);
        if (obj != null) notas = (Nota[]) obj;

        //Recalcular contadores
        totalInstructores = contarNoNulos(instructores, MAX_INSTRUCTORES);
        totalEstudiantes = contarNoNulos(estudiantes, MAX_ESTUDIANTES);
        totalCursos = contarNoNulos(cursos, MAX_CURSOS);
        totalSecciones = contarNoNulos(secciones, MAX_SECCIONES);
        totalNotas = contarNoNulos(notas, MAX_NOTAS);
    }

    private static int contarNoNulos(Object[] arr, int max) {
        int count = 0;
        for (int i = 0; i < max; i++) {
            if (arr[i] != null) count++;
        }
        return count;
    }

    //Guardar datos
    public static void guardarDatos() {
        Serializador.serializar(instructores, FILE_INSTRUCTORES);
        Serializador.serializar(estudiantes,  FILE_ESTUDIANTES);
        Serializador.serializar(cursos,       FILE_CURSOS);
        Serializador.serializar(secciones,    FILE_SECCIONES);
        Serializador.serializar(notas,        FILE_NOTAS);
    }

    //Getters (Instructores)
    public static Instructor[] getInstructores() { return instructores; }
    public static int getTotalInstructores() { return totalInstructores; }
    public static int getMaxInstructores() { return MAX_INSTRUCTORES; }

    public static Instructor buscarInstructor(String cod) {
        for (int i = 0; i < totalInstructores; i++) {
            if (instructores[i].getCod().equals(cod)) return instructores[i];
        }
        return null;
    }

    public static boolean agregarInstructor(Instructor ins) {
        if (totalInstructores >= MAX_INSTRUCTORES) return false;
        if (buscarInstructor(ins.getCod()) != null) return false;
        instructores[totalInstructores++] = ins;
        guardarDatos();
        return true;
    }

    public static boolean eliminarInstructor(String cod) {
        for (int i = 0; i < totalInstructores; i++) {
            if (instructores[i].getCod().equals(cod)) {
                for (int j = i; j < totalInstructores - 1; j++) {
                    instructores[j] = instructores[j + 1];
                }
                instructores[totalInstructores - 1] = null;
                totalInstructores--;
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    //Getters (Estudiantes)
    public static Estudiante[] getEstudiantes() { return estudiantes; }
    public static int getTotalEstudiantes() { return totalEstudiantes; }
    public static int getMaxEstudiantes() { return MAX_ESTUDIANTES; }

    public static Estudiante buscarEstudiante(String cod) {
        for (int i = 0; i < totalEstudiantes; i++) {
            if (estudiantes[i].getCod().equals(cod)) return estudiantes[i];
        }
        return null;
    }

    public static boolean agregarEstudiante(Estudiante est) {
        if (totalEstudiantes >= MAX_ESTUDIANTES) return false;
        if (buscarEstudiante(est.getCod()) != null) return false;
        estudiantes[totalEstudiantes++] = est;
        guardarDatos();
        return true;
    }

    public static boolean eliminarEstudiante(String cod) {
        for (int i = 0; i < totalEstudiantes; i++) {
            if (estudiantes[i].getCod().equals(cod)) {
                for (int j = i; j < totalEstudiantes - 1; j++) {
                    estudiantes[j] = estudiantes[j + 1];
                }
                estudiantes[totalEstudiantes - 1] = null;
                totalEstudiantes--;
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    //Getters (cursos)
    public static Curso[] getCursos() { return cursos; }
    public static int getTotalCursos() { return totalCursos; }
    public static int getMaxCursos() { return MAX_CURSOS; }

    public static Curso buscarCurso(String cod) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i].getCod().equals(cod)) return cursos[i];
        }
        return null;
    }

    public static boolean agregarCurso(Curso curso) {
        if (totalCursos >= MAX_CURSOS) return false;
        if (buscarCurso(curso.getCod()) != null) return false;
        cursos[totalCursos++] = curso;
        guardarDatos();
        return true;
    }

    public static boolean eliminarCurso(String cod) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i].getCod().equals(cod)) {
                for (int j = i; j < totalCursos - 1; j++) {
                    cursos[j] = cursos[j + 1];
                }
                cursos[totalCursos - 1] = null;
                totalCursos--;
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    //Getters (Secciones)
    public static Seccion[] getSecciones() { return secciones; }
    public static int getTotalSecciones() { return totalSecciones; }
    public static int getMaxSecciones() { return MAX_SECCIONES; }

    public static Seccion buscarSeccion(String cod) {
        for (int i = 0; i < totalSecciones; i++) {
            if (secciones[i].getCod().equals(cod)) return secciones[i];
        }
        return null;
    }

    public static boolean agregarSeccion(Seccion sec) {
        if (totalSecciones >= MAX_SECCIONES) return false;
        if (buscarSeccion(sec.getCod()) != null) return false;
        secciones[totalSecciones++] = sec;
        guardarDatos();
        return true;
    }

    public static boolean eliminarSeccion(String cod) {
        for (int i = 0; i < totalSecciones; i++) {
            if (secciones[i].getCod().equals(cod)) {
                for (int j = i; j < totalSecciones - 1; j++) {
                    secciones[j] = secciones[j + 1];
                }
                secciones[totalSecciones - 1] = null;
                totalSecciones--;
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    //Getters (Notas)
    public static Nota[] getNotas() { return notas; }
    public static int getTotalNotas() { return totalNotas; }
    public static int getMaxNotas() { return MAX_NOTAS; }

    public static boolean agregarNota(Nota nota) {
        if (totalNotas >= MAX_NOTAS) return false;
        notas[totalNotas++] = nota;
        guardarDatos();
        return true;
    }

    public static boolean eliminarNota(int idx) {
        if (idx < 0 || idx >= totalNotas) return false;
        for (int j = idx; j < totalNotas - 1; j++) {
            notas[j] = notas[j + 1];
        }
        notas[totalNotas - 1] = null;
        totalNotas--;
        guardarDatos();
        return true;
    }

    //Getter (Admin)
    public static Admin getAdmin() { return admin; }

    //Estadisticas
    public static int getTotalNotasRegistradas() { return totalNotas; }
}