package model;

import java.io.Serializable;

public class Nota implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codCurso; private String codSeccion;
    private String codEstudiante; private String etiqueta;
    private double ponderacion; private double nota;
    private String fecha;

    public Nota(String codCurso, String codSeccion, String codEstudiante,  String etiqueta, double ponderacion,
                double nota, String fecha) {
        this.codCurso = codCurso; this.codSeccion = codSeccion;
        this.codEstudiante = codEstudiante; this.etiqueta = etiqueta;
        this.ponderacion = ponderacion; this.nota = nota;
        this.fecha = fecha;
    }

    // Getters
    public String getCodCurso() { return codCurso; }
    public String getCodSeccion() { return codSeccion; }
    public String getCodEstudiante() { return codEstudiante; }
    public String getEtiqueta() { return etiqueta; }
    public double getPonderacion() { return ponderacion; }
    public double getNota() { return nota; }
    public String getFecha() { return fecha; }

    // Setters
    public void setCodCurso(String codCurso) { this.codCurso = codCurso; }
    public void setCodSeccion(String codSeccion) { this.codSeccion = codSeccion; }
    public void setCodEstudiante(String codEstudiante) { this.codEstudiante = codEstudiante; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    public void setPonderacion(double ponderacion) { this.ponderacion = ponderacion; }
    public void setNota(double nota) { this.nota = nota; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public boolean esAprobado() {
        return nota >= 61;
    }

    @Override
    public String toString() {
        return "Nota || Curso: " + codCurso + " || Seccion: " + codSeccion +
                " || Estudiante: " + codEstudiante + " || Etiqueta: " + etiqueta +
                " || Ponderacion: " + ponderacion + "% || Nota: " + nota +
                " || Estado: " + (esAprobado() ? "Aprobado" : "Reprobado");
    }
}