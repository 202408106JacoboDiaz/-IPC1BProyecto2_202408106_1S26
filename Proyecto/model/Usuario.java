package model;

import java.io.Serializable;

public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cod; private String nomb;
    private String cumple; private String genero;
    private String contra;

    public Usuario(String cod, String nomb, String fechaNacimiento, String genero, String contra) {
        this.cod = cod; this.nomb = nomb;
        this.cumple = fechaNacimiento; this.genero = genero;
        this.contra = contra;
    }
    //Getters
    public String getCod() { return cod; }
    public String getNomb() { return nomb; }
    public String getCumple() { return cumple; }
    public String getGenero() { return genero; }
    public String getContra() { return contra; }
    //Setters
    public void setCod(String codigo) { this.cod = codigo; }
    public void setNomb(String nombre) { this.nomb = nombre; }
    public void setCumple(String fechaNacimiento) { this.cumple = fechaNacimiento; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setContra(String contrasena) { this.contra = contrasena; }
    //Cada rol interpreta el metodo de forma distinta
    public abstract String getRol();

    @Override
    public String toString() {
        return "Codigo: " + cod + " || Nombre: " + nomb + " || Rol: " + getRol();
    }
}