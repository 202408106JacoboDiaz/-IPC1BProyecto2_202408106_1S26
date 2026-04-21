package model;

import java.io.Serializable;

public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cod; private String nomb;
    private String desc; private int creditosCLAR;
    private static final int MAX_SECCIONES = 10;
    private String[] secciones;
    private int totalSecciones;

    public Curso(String cod, String nomb, String desc, int creditosCLAR) {
        this.cod = cod; this.nomb = nomb;
        this.desc = desc; this.creditosCLAR = creditosCLAR;
        this.secciones = new String[MAX_SECCIONES];
        this.totalSecciones = 0;
    }

    public boolean agregarSeccion(String codSeccion) {
        if (totalSecciones >= MAX_SECCIONES) return false;
        secciones[totalSecciones] = codSeccion;
        totalSecciones++;
        return true;
    }

    public boolean eliminarSeccion(String codSeccion) {
        for (int i = 0; i < totalSecciones; i++) {
            if (secciones[i].equals(codSeccion)) {
                for (int j = i; j < totalSecciones - 1; j++) {
                    secciones[j] = secciones[j + 1];
                }
                secciones[totalSecciones - 1] = null;
                totalSecciones--;
                return true;
            }
        }
        return false;
    }
    //Getters
    public String getCod() { return cod; }
    public String getNomb() { return nomb; }
    public String getDesc() { return desc; }
    public int getCreditosCLAR() { return creditosCLAR; }
    public String[] getSecciones() { return secciones; }
    public int getTotalSecciones() { return totalSecciones; }
    //Setters
    public void setCod(String cod) { this.cod = cod; }
    public void setNomb(String nomb) { this.nomb = nomb; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setCreditosCLAR(int creditosCLAR) { this.creditosCLAR = creditosCLAR; }

    @Override
    public String toString() {
        return "Curso || Cod: " + cod + " || Nombre: " + nomb +
                " || Creditos: " + creditosCLAR + " || Secciones: " + totalSecciones;
    }
}