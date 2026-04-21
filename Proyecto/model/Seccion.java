package model;

import java.io.Serializable;

public class Seccion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cod; private String codCurso;
    private String codInstructor; private String horario;
    private String semestre; private static final int MAX_ESTUDIANTES = 30;
    private String[] estudiantes; private int totalEstudiantes;

    public Seccion(String cod, String codCur, String codInstructor,  String horario, String semestre) {
        this.cod = cod; this.codCurso = codCurso;
        this.codInstructor = codInstructor; this.horario = horario;
        this.semestre = semestre; this.estudiantes = new String[MAX_ESTUDIANTES];
        this.totalEstudiantes = 0;
    }

    public boolean agregarEstudiante(String codEstudiante) {
        if (totalEstudiantes >= MAX_ESTUDIANTES) return false;
        for (int i = 0; i < totalEstudiantes; i++) {
            if (estudiantes[i].equals(codEstudiante)) return false;
        }
        estudiantes[totalEstudiantes] = codEstudiante;
        totalEstudiantes++;
        return true;
    }
    public boolean eliminarEstudiante(String codEstudiante) {
        for (int i = 0; i < totalEstudiantes; i++) {
            if (estudiantes[i].equals(codEstudiante)) {
                for (int j = i; j < totalEstudiantes - 1; j++) {
                    estudiantes[j] = estudiantes[j + 1];
                }
                estudiantes[totalEstudiantes - 1] = null;
                totalEstudiantes--;
                return true;
            }
        }
        return false;
    }

    public boolean estaInscrito(String codEstudiante) {
        for (int i = 0; i < totalEstudiantes; i++) {
            if (estudiantes[i].equals(codEstudiante)) return true;
        }
        return false;
    }
    //Getters
    public String getCod() { return cod; }
    public String getCodCurso() { return codCurso; }
    public String getCodInstructor() { return codInstructor; }
    public String getHorario() { return horario; }
    public String getSemestre() { return semestre; }
    public String[] getEstudiantes() { return estudiantes; }
    public int getTotalEstudiantes() { return totalEstudiantes; }
    //Setters
    public void setCod(String cod) { this.cod = cod; }
    public void setCodCurso(String codCurso) { this.codCurso = codCurso; }
    public void setCodInstructor(String codInstructor) { this.codInstructor = codInstructor; }
    public void setHorario(String horario) { this.horario = horario; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    @Override
    public String toString() {
        return "Seccion || Cod: " + cod + " || Curso: " + codCurso +
                " || Instructor: " + codInstructor + " || Semestre: " + semestre +
                " || Estudiantes: " + totalEstudiantes + "/" + MAX_ESTUDIANTES;
    }
}