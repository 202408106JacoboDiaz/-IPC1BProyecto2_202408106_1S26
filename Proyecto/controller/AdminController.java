package controller;

import model.*;
import util.Bitacora;
import util.CSVLoader;

public class AdminController {

    private static final String COD_ADMIN = "admin";

    //Gestion de instructores
    public static String crearInstructor(String cod, String nomb, String cumple, String genero,
                                         String contra) {
        if (cod.isEmpty() || nomb.isEmpty() || contra.isEmpty()) {
            return "ERROR: Campos vacios";
        }
        if (DataStore.buscarInstructor(cod) != null) {
            return "ERROR: Ya existe un instructor con codigo: " + cod;
        }
        Instructor ins = new Instructor(cod, nomb, cumple, genero, contra);
        DataStore.agregarInstructor(ins);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CREAR_INSTRUCTOR",
                "EXITOSA", "Instructor " + cod + " creado");
        return "Instructor " + cod + " registrado exitosamente";
    }

    public static String actualizarInstructor(String cod, String nuevoNomb, String nuevaContra) {
        Instructor ins = DataStore.buscarInstructor(cod);
        if (ins == null) {
            return "ERROR: Instructor con codigo " + cod + " no encontrado";
        }
        if (!nuevoNomb.isEmpty()) ins.setNomb(nuevoNomb);
        if (!nuevaContra.isEmpty()) ins.setContra(nuevaContra);
        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ACTUALIZAR_INSTRUCTOR",
                "EXITOSA", "Instructor " + cod + " actualizado");
        return "Instructor " + cod + " actualizado exitosamente";
    }

    public static String eliminarInstructor(String cod) {
        if (DataStore.buscarInstructor(cod) == null) {
            return "ERROR: Instructor no encontrado";
        }
        DataStore.eliminarInstructor(cod);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ELIMINAR_INSTRUCTOR",
                "EXITOSA", "Instructor " + cod + " eliminado");
        return "Instructor " + cod + " eliminado exitosamente";
    }

    public static String cargarInstructoresCSV(String ruta) {
        String resumen = CSVLoader.cargarInstructores(ruta, DataStore.getInstructores(),
                DataStore.getTotalInstructores(), DataStore.getMaxInstructores());
        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CARGAR_CSV_INSTRUCTORES",
                "EXITOSA", resumen);
        return resumen;
    }

    //Gestion de estudiantes
    public static String crearEstudiante(String cod, String nomb, String cumple, String genero,
                                         String contra) {
        if (cod.isEmpty() || nomb.isEmpty() || contra.isEmpty()) {
            return "ERROR: Campos vacios";
        }
        if (DataStore.buscarEstudiante(cod) != null) {
            return "ERROR: Ya existe un estudiante con codigo " + cod;
        }
        Estudiante est = new Estudiante(cod, nomb, cumple, genero, contra);
        DataStore.agregarEstudiante(est);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CREAR_ESTUDIANTE",
                "EXITOSA", "Estudiante " + cod + " creado");
        return "OK: Estudiante " + cod + " registrado exitosamente";
    }

    public static String actualizarEstudiante(String cod, String nuevoNomb, String nuevaContra) {
        Estudiante est = DataStore.buscarEstudiante(cod);
        if (est == null) {
            return "ERROR: Estudiante no encontrado";
        }
        if (!nuevoNomb.isEmpty()) est.setNomb(nuevoNomb);
        if (!nuevaContra.isEmpty()) est.setContra(nuevaContra);
        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ACTUALIZAR_ESTUDIANTE",
                "EXITOSA", "Estudiante " + cod + " actualizado");
        return "Estudiante " + cod + " actualizado exitosamente";
    }

    public static String eliminarEstudiante(String cod) {
        if (DataStore.buscarEstudiante(cod) == null) {
            return "ERROR: Estudiante no encontrado";
        }
        DataStore.eliminarEstudiante(cod);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ELIMINAR_ESTUDIANTE",
                "EXITOSA", "Estudiante " + cod + " eliminado");
        return "Estudiante " + cod + " eliminado exitosamente";
    }

    public static String cargarEstudiantesCSV(String ruta) {
        String resumen = CSVLoader.cargarEstudiantes(ruta, DataStore.getEstudiantes(),
                DataStore.getTotalEstudiantes(), DataStore.getMaxEstudiantes());
        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CARGAR_CSV_ESTUDIANTES",
                "EXITOSA", resumen);
        return resumen;
    }

    //Gestion de cursos
    public static String crearCurso(String cod, String nomb, String desc, int creditos) {
        if (cod.isEmpty() || nomb.isEmpty()) {
            return "ERROR: Campos vacios";
        }
        if (DataStore.buscarCurso(cod) != null) {
            return "ERROR: Ya existe un curso con codigo " + cod;
        }
        Curso curso = new Curso(cod, nomb, desc, creditos);
        DataStore.agregarCurso(curso);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CREAR_CURSO", "EXITOSA",
                "Curso " + cod + " creado");
        return "Curso " + cod + " registrado exitosamente";
    }

    public static String actualizarCurso(String cod, String nuevoNomb, String nuevaDesc,
                                         int nuevosCreditos) {
        Curso curso = DataStore.buscarCurso(cod);
        if (curso == null) {
            return "ERROR: Curso no encontrado";
        }
        if (!nuevoNomb.isEmpty()) curso.setNomb(nuevoNomb);
        if (!nuevaDesc.isEmpty()) curso.setDesc(nuevaDesc);
        if (nuevosCreditos > 0) curso.setCreditosCLAR(nuevosCreditos);
        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ACTUALIZAR_CURSO", "EXITOSA",
                "Curso " + cod + " actualizado");
        return "Curso " + cod + " actualizado exitosamente";
    }

    public static String eliminarCurso(String cod) {
        if (DataStore.buscarCurso(cod) == null) {
            return "ERROR: Curso no encontrado";
        }
        DataStore.eliminarCurso(cod);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ELIMINAR_CURSO", "EXITOSA",
                "Curso " + cod + " eliminado");
        return "Curso " + cod + " eliminado exitosamente";
    }

    public static String cargarCursosCSV(String ruta) {
        String resumen = CSVLoader.cargarCursos(ruta, DataStore.getCursos(), DataStore.getTotalCursos(),
                DataStore.getMaxCursos());
        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CARGAR_CSV_CURSOS",
                "EXITOSA", resumen);
        return resumen;
    }

    //Gestion de secciones
    public static String crearSeccion(String cod, String codCurso, String codInstructor, String horario,
                                      String semestre) {
        if (cod.isEmpty() || codCurso.isEmpty() || codInstructor.isEmpty()) {
            return "ERROR: Campos vacios";
        }
        if (DataStore.buscarSeccion(cod) != null) {
            return "ERROR: Ya existe una seccion con codigo " + cod;
        }
        if (DataStore.buscarCurso(codCurso) == null) {
            return "ERROR: Curso " + codCurso + " no existe";
        }
        if (DataStore.buscarInstructor(codInstructor) == null) {
            return "ERROR: Instructor " + codInstructor + " no existe";
        }

        Seccion sec = new Seccion(cod, codCurso, codInstructor, horario, semestre);
        DataStore.agregarSeccion(sec);

        //Asignar seccion al instructor
        Instructor ins = DataStore.buscarInstructor(codInstructor);
        ins.agregarSeccion(cod);

        //Asignar seccion al curso
        Curso curso = DataStore.buscarCurso(codCurso);
        curso.agregarSeccion(cod);

        DataStore.guardarDatos();
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "CREAR_SECCION",
                "EXITOSA", "Seccion " + cod + " creada para curso " +
                        codCurso + ", instructor " + codInstructor);
        return "Seccion " + cod + " creada exitosamente";
    }

    public static String eliminarSeccion(String cod) {
        Seccion sec = DataStore.buscarSeccion(cod);
        if (sec == null) {
            return "ERROR: Seccion con codigo " + cod + " no encontrada";
        }

        //Quitar seccion del instructor
        Instructor ins = DataStore.buscarInstructor(sec.getCodInstructor());
        if (ins != null) ins.eliminarSeccion(cod);

        //Quitar seccion del curso
        Curso curso = DataStore.buscarCurso(sec.getCodCurso());
        if (curso != null) curso.eliminarSeccion(cod);

        DataStore.eliminarSeccion(cod);
        Bitacora.registrar("ADMINISTRADOR", COD_ADMIN, "ELIMINAR_SECCION",
                "EXITOSA", "Seccion " + cod + " eliminada");
        return "Seccion " + cod + " eliminada exitosamente";
    }
}