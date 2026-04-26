package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import controller.DataStore;
import model.*;
import controller.InstructorController;
import controller.EstudianteController;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class ReportePDF {

    private static final String RUTA = "reportes/";
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
    private static final DateTimeFormatter fmtLeg = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Font TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,
            new BaseColor(0, 102, 204));
    private static final Font SUBTITULO = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,
            new BaseColor(50, 50, 50));
    private static final Font NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font HEADER_TABLA = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,
            BaseColor.WHITE);

    //Utilidades internas
    private static java.io.File crearDirectorio() {
        java.io.File dir = new java.io.File(RUTA); if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private static String nombreArchivo(String tipo) {
        return RUTA + LocalDateTime.now().format(fmt) + "_" + tipo + ".pdf";
    }

    private static void agregarEncabezado(Document doc, String titulo, String subtitulo) throws DocumentException {
        Paragraph tit = new Paragraph("Sancarlista Academy", TITULO);
        tit.setAlignment(Element.ALIGN_CENTER); doc.add(tit);

        Paragraph sub = new Paragraph(titulo, SUBTITULO); sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingBefore(5); doc.add(sub);

        if (!subtitulo.isEmpty()) {
            Paragraph det = new Paragraph(subtitulo, NORMAL); det.setAlignment(Element.ALIGN_CENTER);
            doc.add(det);
        }

        Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(fmtLeg), NORMAL);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(10); doc.add(fecha);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);
    }

    private static PdfPCell celdaHeader(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, HEADER_TABLA));
        cell.setBackgroundColor(new BaseColor(0, 102, 204));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
    private static PdfPCell celdaNormal(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, NORMAL));
        cell.setPadding(4);
        return cell;
    }
    private static PdfPCell celdaCentro(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4);
        return cell;
    }

    //Reporte top mejores estudiantes
    public static String reporteTop5Mejores(String codSec) {
        crearDirectorio();
        String archivo = nombreArchivo("Top5Mejores_" + codSec);

        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) return "ERROR: Seccion no encontrada";

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            agregarEncabezado(doc, "Top 5 Estudiantes con Mejor Desempeno", "Seccion: " + codSec +
                    " | Curso: " + sec.getCodCurso());
            //Obtener promedios academicos
            String[] ests = sec.getEstudiantes();
            int total = sec.getTotalEstudiantes();
            double[] promedios = new double[total];
            String[] codigos = new String[total];

            for (int i = 0; i < total; i++) {
                codigos[i] = ests[i];
                promedios[i] = InstructorController.calcularPromedio(ests[i], codSec);
            }

            //Ordenar burbuja descendente
            for (int i = 0; i < total - 1; i++) {
                for (int j = 0; j < total - i - 1; j++) {
                    if (promedios[j] < promedios[j + 1]) {
                        double tmpD = promedios[j]; promedios[j] = promedios[j + 1];
                        promedios[j + 1] = tmpD; String tmpS = codigos[j];
                        codigos[j] = codigos[j + 1]; codigos[j + 1] = tmpS;
                    }
                }
            }

            //Tabla
            PdfPTable tabla = new PdfPTable(5); tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1, 2, 3, 2, 2});

            tabla.addCell(celdaHeader("Posicion")); tabla.addCell(celdaHeader("Codigo"));
            tabla.addCell(celdaHeader("Nombre")); tabla.addCell(celdaHeader("Promedio"));
            tabla.addCell(celdaHeader("Estado"));

            int limite = Math.min(5, total);
            for (int i = 0; i < limite; i++) {
                Estudiante est = DataStore.buscarEstudiante(codigos[i]);
                String estado = promedios[i] >= 61 ? "Aprobado" : "Reprobado";
                tabla.addCell(celdaCentro("#" + (i + 1)));
                tabla.addCell(celdaNormal(codigos[i]));
                tabla.addCell(celdaNormal(est != null ? est.getNomb() : "N/A"));
                tabla.addCell(celdaCentro(String.format("%.2f", promedios[i])));
                tabla.addCell(celdaCentro(estado));
            }

            doc.add(tabla); doc.close();

            Bitacora.registrar("SISTEMA", "sistema", "GENERAR_REPORTE",
                    "EXITOSA", "Top5Mejores generado: " + archivo);
            return "OK: Reporte generado en " + archivo;

        } catch (DocumentException | IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Reporte top peores estudiantes

    public static String reporteTop5Peores(String codSec) {
        crearDirectorio(); String archivo = nombreArchivo("Top5Peores_" + codSec);

        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) return "ERROR: Seccion no encontrada";

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            agregarEncabezado(doc, "Top 5 Estudiantes con Bajo Desempeno", "Seccion: " + codSec
                    + " | Curso: " + sec.getCodCurso());

            String[] ests = sec.getEstudiantes();
            int total = sec.getTotalEstudiantes();
            double[] promedios = new double[total];
            String[] codigos = new String[total];

            for (int i = 0; i < total; i++) {
                codigos[i] = ests[i];
                promedios[i] = InstructorController.calcularPromedio(ests[i], codSec);
            }

            // Ordenar burbuja ascendente
            for (int i = 0; i < total - 1; i++) {
                for (int j = 0; j < total - i - 1; j++) {
                    if (promedios[j] > promedios[j + 1]) {
                        double tmpD = promedios[j];
                        promedios[j] = promedios[j + 1];
                        promedios[j + 1] = tmpD;
                        String tmpS = codigos[j];
                        codigos[j] = codigos[j + 1];
                        codigos[j + 1] = tmpS;
                    }
                }
            }

            PdfPTable tabla = new PdfPTable(6); tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1, 2, 3, 2, 2, 3});

            tabla.addCell(celdaHeader("Posicion")); tabla.addCell(celdaHeader("Codigo"));
            tabla.addCell(celdaHeader("Nombre")); tabla.addCell(celdaHeader("Promedio"));
            tabla.addCell(celdaHeader("Estado")); tabla.addCell(celdaHeader("Recomendacion"));

            int limite = Math.min(5, total);
            for (int i = 0; i < limite; i++) {
                Estudiante est = DataStore.buscarEstudiante(codigos[i]);
                String estado = promedios[i] >= 61 ? "Aprobado" : "Reprobado";
                String recom  = promedios[i] < 61 ? "Tutoria y reforzamiento" : "Mantener ritmo";
                tabla.addCell(celdaCentro("#" + (i + 1)));
                tabla.addCell(celdaNormal(codigos[i]));
                tabla.addCell(celdaNormal(est != null ? est.getNomb() : "N/A"));
                tabla.addCell(celdaCentro(String.format("%.2f", promedios[i])));
                tabla.addCell(celdaCentro(estado));
                tabla.addCell(celdaNormal(recom));
            }
            doc.add(tabla); doc.close();

            Bitacora.registrar("SISTEMA", "sistema", "GENERAR_REPORTE",
                    "EXITOSA", "Top5Peores generado: " + archivo);
            return "OK: Reporte generado en " + archivo;

        } catch (DocumentException | IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Reporte de calificaciones segun seccion

    public static String reporteCalificacionesSeccion(String codSec, String codInstructor) {
        crearDirectorio();
        String archivo = nombreArchivo("CalificacionesSeccion_" + codSec);

        Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) return "ERROR: Seccion no encontrada";

        try {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            Instructor ins = DataStore.buscarInstructor(codInstructor);
            agregarEncabezado(doc, "Reporte de Calificaciones por Seccion", "Seccion: " + codSec +
                    " | Curso: " + sec.getCodCurso() + " | Instructor: " + (ins != null ? ins.getNomb() : "N/A") +
                    " | Semestre: " + sec.getSemestre());

            String[] ests = sec.getEstudiantes(); int totalEsts = sec.getTotalEstudiantes();

            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 3, 3, 2, 2, 2});

            tabla.addCell(celdaHeader("Cod Estudiante")); tabla.addCell(celdaHeader("Nombre"));
            tabla.addCell(celdaHeader("Etiqueta")); tabla.addCell(celdaHeader("Ponderacion"));
            tabla.addCell(celdaHeader("Nota")); tabla.addCell(celdaHeader("Estado"));

            for (int i = 0; i < totalEsts; i++) {
                String codEst = ests[i];
                Estudiante est = DataStore.buscarEstudiante(codEst);
                Nota[] notas = InstructorController.getNotasPorEstudiante(codEst, codSec);

                for (int j = 0; j < notas.length; j++) {
                    tabla.addCell(celdaNormal(codEst));
                    tabla.addCell(celdaNormal(est != null ? est.getNomb() : "N/A"));
                    tabla.addCell(celdaNormal(notas[j].getEtiqueta()));
                    tabla.addCell(celdaCentro(notas[j].getPonderacion() + "%"));
                    tabla.addCell(celdaCentro(String.format("%.2f", notas[j].getNota())));
                    tabla.addCell(celdaCentro(notas[j].esAprobado() ? "Aprobado" : "Reprobado"));
                }

                //Fila de promedio
                double prom = InstructorController.calcularPromedio(codEst, codSec);
                PdfPCell celdaProm = new PdfPCell(
                        new Phrase("Promedio: " + String.format("%.2f", prom), BOLD));
                celdaProm.setColspan(5);
                celdaProm.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaProm.setBackgroundColor(new BaseColor(230, 230, 230));
                tabla.addCell(celdaProm);
                PdfPCell celdaEst = new PdfPCell(new Phrase(prom >= 61 ? "APROBADO" : "REPROBADO", BOLD));
                celdaEst.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaEst.setBackgroundColor(prom >= 61 ? new BaseColor(200, 255, 200) :
                        new BaseColor(255, 200, 200));
                tabla.addCell(celdaEst);
            }
            doc.add(tabla); doc.close();

            Bitacora.registrar("INSTRUCTOR", codInstructor, "GENERAR_REPORTE",
                    "EXITOSA", "CalificacionesSeccion generado: " + archivo);
            return "OK: Reporte generado en " + archivo;

        } catch (DocumentException | IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Reporte individual de estudiante

    public static String reporteIndividualEstudiante(String codEst) {
        crearDirectorio();
        String archivo = nombreArchivo("ReporteEstudiante_" + codEst);

        Estudiante est = DataStore.buscarEstudiante(codEst);
        if (est == null) return "ERROR: Estudiante no encontrado";

        try {
            Document doc = new Document(); PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            agregarEncabezado(doc, "Historial Academico del Estudiante", "Estudiante: "
                    + est.getNomb() + " | Codigo: " + codEst);

            //Informacion del estudiante
            doc.add(new Paragraph("Datos del Estudiante:", BOLD));
            doc.add(new Paragraph("Codigo: " + est.getCod(), NORMAL));
            doc.add(new Paragraph("Nombre: " + est.getNomb(), NORMAL));
            doc.add(new Paragraph("Cumpleaños: " + est.getCumple(), NORMAL));
            doc.add(new Paragraph("Genero: " + est.getGenero(), NORMAL));
            doc.add(Chunk.NEWLINE);
            //Secciones inscritas
            Seccion[] secs = EstudianteController.getSeccionesInscritas(codEst);

            PdfPTable tabla = new PdfPTable(5); tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 2, 2, 2, 2});

            tabla.addCell(celdaHeader("Seccion")); tabla.addCell(celdaHeader("Curso"));
            tabla.addCell(celdaHeader("Semestre")); tabla.addCell(celdaHeader("Promedio"));
            tabla.addCell(celdaHeader("Estado"));

            for (int i = 0; i < secs.length; i++) {
                double prom = EstudianteController.calcularPromedio(codEst, secs[i].getCod());
                tabla.addCell(celdaNormal(secs[i].getCod()));
                tabla.addCell(celdaNormal(secs[i].getCodCurso()));
                tabla.addCell(celdaNormal(secs[i].getSemestre()));
                tabla.addCell(celdaCentro(String.format("%.2f", prom)));
                tabla.addCell(celdaCentro(prom >= 61 ? "Aprobado" : "Reprobado"));
            }

            doc.add(new Paragraph("Cursos Inscritos:", BOLD)); doc.add(Chunk.NEWLINE);
            doc.add(tabla); doc.close();

            Bitacora.registrar("SISTEMA", codEst, "GENERAR_REPORTE", "EXITOSA",
                    "ReporteIndividual generado: " + archivo);
            return "OK: Reporte generado en " + archivo;

        } catch (DocumentException | IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Reporte de secciones (segun rendimiento)

    public static String reporteSeccionesPorRendimiento(String codCurso) {
        crearDirectorio(); String archivo = nombreArchivo("RendimientoSecciones_" + codCurso);

        Curso curso = DataStore.buscarCurso(codCurso);
        if (curso == null) return "ERROR: Curso no encontrado";

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(archivo)); doc.open();

            agregarEncabezado(doc, "Reporte de Secciones por Rendimiento", "Curso: " +
                    curso.getNomb() + " | Codigo: " + codCurso);

            Seccion[] todas = DataStore.getSecciones(); int total = DataStore.getTotalSecciones();

            PdfPTable tabla = new PdfPTable(6); tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 2, 3, 2, 2, 2});

            tabla.addCell(celdaHeader("Seccion")); tabla.addCell(celdaHeader("Semestre"));
            tabla.addCell(celdaHeader("Instructor")); tabla.addCell(celdaHeader("Promedio"));
            tabla.addCell(celdaHeader("Aprobados")); tabla.addCell(celdaHeader("Reprobados"));

            for (int i = 0; i < total; i++) {
                if (!todas[i].getCodCurso().equals(codCurso)) continue;

                Seccion sec = todas[i];
                Instructor ins = DataStore.buscarInstructor(sec.getCodInstructor());
                String[] ests = sec.getEstudiantes();int totalEsts = sec.getTotalEstudiantes();

                double sumProm = 0; int aprobados = 0; int reprobados = 0;

                for (int j = 0; j < totalEsts; j++) {
                    double prom = InstructorController.calcularPromedio(ests[j], sec.getCod());
                    sumProm += prom;
                    if (prom >= 61) aprobados++;
                    else reprobados++;
                }
                double promGeneral = totalEsts > 0 ? sumProm / totalEsts : 0;

                tabla.addCell(celdaNormal(sec.getCod()));
                tabla.addCell(celdaNormal(sec.getSemestre()));
                tabla.addCell(celdaNormal(ins != null ? ins.getNomb() : "N/A"));
                tabla.addCell(celdaCentro(String.format("%.2f", promGeneral)));
                tabla.addCell(celdaCentro(String.valueOf(aprobados)));
                tabla.addCell(celdaCentro(String.valueOf(reprobados)));
            }
            doc.add(tabla); doc.close();

            Bitacora.registrar("SISTEMA", "sistema", "GENERAR_REPORTE",
                    "EXITOSA", "RendimientoSecciones generado: " + archivo);
            return "OK: Reporte generado en " + archivo;

        } catch (DocumentException | IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    //Reporte bitacora

    public static String reporteBitacora() {
        crearDirectorio(); String archivo = nombreArchivo("Bitacora");

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            agregarEncabezado(doc, "Bitacora del Sistema", "");

            String[] registros = Bitacora.leerBitacora();

            PdfPTable tabla = new PdfPTable(1); tabla.setWidthPercentage(100);
            tabla.addCell(celdaHeader("Registro"));

            for (int i = 0; i < registros.length; i++) {
                tabla.addCell(celdaNormal(registros[i]));
            }
            doc.add(tabla); doc.close();

            return "OK: Bitacora exportada en " + archivo;

        } catch (DocumentException | IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }
}