package model;

public class Estudiante extends Usuario {

    private static final long serialVersionUID = 1L;

    private String[] cursosInscritos;
    private int totalCursos;
    private static final int MAX_CURSOS = 20;

    public Estudiante(String cod, String nomb, String cumple, String genero, String contra) {
        super(cod, nomb, cumple, genero, contra);
        this.cursosInscritos = new String[MAX_CURSOS];
        this.totalCursos = 0;
    }
    public boolean inscribirCurso(String codSeccion) {
        if (totalCursos >= MAX_CURSOS) return false;
        cursosInscritos[totalCursos] = codSeccion;
        totalCursos++;
        return true;
    }
    //El metodo mas importante
    public boolean desasignarCurso(String codSeccion) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursosInscritos[i].equals(codSeccion)) {
                for (int j = i; j < totalCursos - 1; j++) {
                    cursosInscritos[j] = cursosInscritos[j + 1];
                }
                cursosInscritos[totalCursos - 1] = null;
                totalCursos--;
                return true;
            }
        }
        return false;
    }
    public boolean estaInscrito(String codSeccion) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursosInscritos[i].equals(codSeccion)) return true;
        }
        return false;
    }
    //Getters
    public String[] getCursosInscritos() { return cursosInscritos; }
    public int getTotalCursos() { return totalCursos; }

    @Override
    public String getRol() { return "ESTUDIANTE"; }

    @Override
    public String toString() {
        return "Estudiante || " + super.toString() +
                " | Cursos inscritos: " + totalCursos;
    }
}