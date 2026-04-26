package util;

import controller.DataStore;
import controller.EstudianteController;
import controller.InstructorController;
import model.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportadorCSV {

    private static final String RUTA = "reportes/";
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");

    private static void crearDirectorio() {
        java.io.File dir = new java.io.File(RUTA);
        if (!dir.exists()) dir.mkdirs();
    }

    private static String nombreArchivo(String tipo) {
        return RUTA + LocalDateTime.now().format(fmt) + "_" + tipo + ".csv";
    }

    //Exportar instructores

    public static String exportarInstructores() {
        crearDirectorio(); String archivo = nombreArchivo("Instructores");

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("Codigo,Nombre,Cumpleaños,Genero,Secciones");

            Instructor[] ins = DataStore.getInstructores();
            int total = DataStore.getTotalInstructores();

            for (int i = 0; i < total; i++) {
                pw.println(
                        ins[i].getCod() + "," + ins[i].getNomb() + "," + ins[i].getCumple() + "," +
                                ins[i].getGenero() + "," + ins[i].getTotalSecciones()
                );
            }

            Bitacora.registrar("SISTEMA", "sistema", "EXPORTAR_CSV",
                    "EXITOSA", "Instructores exportados: " + archivo);
            return "OK: Exportado en " + archivo;

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Exportar estudiantes

    public static String exportarEstudiantes() {
        crearDirectorio(); String archivo = nombreArchivo("Estudiantes");

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("Codigo,Nombre,Cumpleaños,Genero,CursosInscritos");

            Estudiante[] ests = DataStore.getEstudiantes();
            int total = DataStore.getTotalEstudiantes();

            for (int i = 0; i < total; i++) {
                pw.println(ests[i].getCod() + "," + ests[i].getNomb() + "," + ests[i].getCumple() + "," +
                        ests[i].getGenero() + "," + ests[i].getTotalCursos()
                );
            }

            Bitacora.registrar("SISTEMA", "sistema", "EXPORTAR_CSV",
                    "EXITOSA", "Estudiantes exportados: " + archivo);
            return "OK: Exportado en " + archivo;

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Exportar cursos
    public static String exportarCursos() {
        crearDirectorio(); String archivo = nombreArchivo("Cursos");

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("Codigo,Nombre,Descripcion,Creditos,Secciones");

            Curso[] cursos = DataStore.getCursos(); int total = DataStore.getTotalCursos();

            for (int i = 0; i < total; i++) {
                pw.println(cursos[i].getCod() + "," + cursos[i].getNomb() + "," + cursos[i].getDesc() + "," +
                        cursos[i].getCreditosCLAR() + "," + cursos[i].getTotalSecciones()
                );
            }
            Bitacora.registrar("SISTEMA", "sistema", "EXPORTAR_CSV",
                    "EXITOSA", "Cursos exportados: " + archivo);
            return "OK: Exportado en " + archivo;

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Exportar notas (por seccion)

    public static String exportarNotasSeccion(String codSec, String codUsuario) {
        crearDirectorio(); String archivo = nombreArchivo("NotasSeccion_" + codSec);

        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) return "ERROR: Seccion no encontrada";

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("CodEstudiante,Nombre,Etiqueta,Ponderacion,Nota,Fecha,Estado,Promedio");

            String[] ests = sec.getEstudiantes(); int totalEsts = sec.getTotalEstudiantes();

            for (int i = 0; i < totalEsts; i++) {
                String codEst = ests[i]; Estudiante est = DataStore.buscarEstudiante(codEst);
                Nota[] notas = InstructorController.getNotasPorEstudiante(codEst, codSec);
                double prom = InstructorController.calcularPromedio(codEst, codSec);

                for (int j = 0; j < notas.length; j++) {
                    pw.println(codEst + "," + (est != null ? est.getNomb() : "N/A") + "," +
                            notas[j].getEtiqueta() + "," + notas[j].getPonderacion() + "," +
                            notas[j].getNota() + "," + notas[j].getFecha() + "," +
                            (notas[j].esAprobado() ? "Aprobado" : "Reprobado") + "," + String.format("%.2f", prom)
                    );
                }
            }

            Bitacora.registrar("SISTEMA", codUsuario, "EXPORTAR_CSV",
                    "EXITOSA", "NotasSeccion exportadas: " + archivo);
            return "OK: Exportado en " + archivo;

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Exportar historial del estudiante

    public static String exportarHistorialEstudiante(String codEst) {
        crearDirectorio(); String archivo = nombreArchivo("HistorialEstudiante_" + codEst);

        Estudiante est = DataStore.buscarEstudiante(codEst);
        if (est == null) return "ERROR: Estudiante no encontrado";

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("Seccion,Curso,Semestre,Etiqueta,Ponderacion,Nota,Fecha,Promedio,Estado");

            Seccion[] secs = EstudianteController.getSeccionesInscritas(codEst);

            for (int i = 0; i < secs.length; i++) {
                String codSec = secs[i].getCod();
                Nota[] notas = EstudianteController.getNotasPorSeccion(codEst, codSec);
                double prom = EstudianteController.calcularPromedio(codEst, codSec);

                for (int j = 0; j < notas.length; j++) {
                    pw.println(codSec + "," + secs[i].getCodCurso() + "," + secs[i].getSemestre() + "," +
                            notas[j].getEtiqueta() + "," + notas[j].getPonderacion() + "," +
                            notas[j].getNota() + "," + notas[j].getFecha() + "," +
                            String.format("%.2f", prom) + "," + (prom >= 61 ? "Aprobado" : "Reprobado")
                    );
                }
            }
            Bitacora.registrar("ESTUDIANTE", codEst, "EXPORTAR_CSV", "EXITOSA",
                    "HistorialEstudiante exportado: " + archivo);
            return "OK: Exportado en " + archivo;

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Exportar bitacora
    public static String exportarBitacora() {
        crearDirectorio(); String archivo = nombreArchivo("Bitacora");

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("Fecha,TipoUsuario,Codigo,Operacion,Estado,Descripcion");

            String[] registros = Bitacora.leerBitacora();
            for (int i = 0; i < registros.length; i++) {
                pw.println(registros[i]);
            }
            return "OK: Bitacora exportada en " + archivo;

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }
}