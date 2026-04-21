package model;

public class Admin extends Usuario {

    private static final long serialVersionUID = 1L;

    public Admin(String cod, String nomb, String cumple,
                         String genero, String contra) {
        super(cod, nomb, cumple, genero, contra);
    }

    @Override
    public String getRol() {
        return "ADMINISTRADOR";
    }
    @Override
    public String toString() {
        return "Administrador || " + super.toString();
    }
}