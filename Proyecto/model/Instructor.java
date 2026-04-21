package model;

public class Instructor extends Usuario {

    private static final long serialVersionUID = 1L;

    private String[] seccionesAsignadas;
    private int totalSecciones;
    private static final int MAX_SECCIONES = 10;

    public Instructor(String cod, String nomb, String cumple, String genero, String contra) {
        super(cod, nomb, cumple, genero, contra);
        this.seccionesAsignadas = new String[MAX_SECCIONES];
        this.totalSecciones = 0;
    }

    public boolean agregarSeccion(String codSeccion) {
        if (totalSecciones >= MAX_SECCIONES) return false;
        seccionesAsignadas[totalSecciones] = codSeccion;
        totalSecciones++;
        return true;
    }

    public boolean eliminarSeccion(String codSeccion) {
        for (int i = 0; i < totalSecciones; i++) {
            if (seccionesAsignadas[i].equals(codSeccion)) {
                for (int j = i; j < totalSecciones - 1; j++) {
                    seccionesAsignadas[j] = seccionesAsignadas[j + 1];
                }
                seccionesAsignadas[totalSecciones - 1] = null;
                totalSecciones--;
                return true;
            }
        }
        return false;
    }
    //Getters
    public String[] getSeccionesAsignadas() { return seccionesAsignadas; }
    public int getTotalSecciones() { return totalSecciones; }

    @Override
    public String getRol() { return "INSTRUCTOR"; }

    @Override
    public String toString() {
        return "Instructor || " + super.toString() +
                " | Secciones asignadas: " + totalSecciones;
    }
}