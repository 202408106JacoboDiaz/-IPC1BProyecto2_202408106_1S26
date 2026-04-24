package controller;

import model.*;
import util.Bitacora;
import threads.MonitorSesiones;
import threads.SimuladorInscripciones;

public class AuthController {

    private static Usuario usuarioActivo = null;

    public static Usuario login(String cod, String contra) {
        //Verificar administrador/es
        Admin admin = DataStore.getAdmin();
        if (admin.getCod().equals(cod) && admin.getContra().equals(contra)) {
            usuarioActivo = admin;
            Bitacora.registrar("ADMINISTRADOR", cod, "LOGIN", "EXITOSA",
                    "Inicio de sesion exitoso");
            MonitorSesiones.incrementar();
            return admin;
        }
        //Verificar instructor/es
        Instructor[] instructores = DataStore.getInstructores();
        int total = DataStore.getTotalInstructores();
        for (int i = 0; i < total; i++) {
            if (instructores[i].getCod().equals(cod) &&
                    instructores[i].getContra().equals(contra)) {
                usuarioActivo = instructores[i];
                Bitacora.registrar("INSTRUCTOR", cod, "LOGIN", "EXITOSA",
                        "Inicio de sesion exitoso");
                MonitorSesiones.incrementar();
                return instructores[i];
            }
        }
        //Verificar estudiante/s
        Estudiante[] estudiantes = DataStore.getEstudiantes();
        int totalEst = DataStore.getTotalEstudiantes();
        for (int i = 0; i < totalEst; i++) {
            if (estudiantes[i].getCod().equals(cod) &&
                    estudiantes[i].getContra().equals(contra)) {
                usuarioActivo = estudiantes[i];
                Bitacora.registrar("ESTUDIANTE", cod, "LOGIN", "EXITOSA",
                        "Inicio de sesion exitoso");
                MonitorSesiones.incrementar();
                return estudiantes[i];
            }
        }
        //En caso de credenciales incorrectas
        Bitacora.registrar("DESCONOCIDO", cod, "LOGIN", "FALLIDA",
                "Credenciales incorrectas");
        return null;
    }

    public static void logout() {
        if (usuarioActivo != null) {
            Bitacora.registrar(usuarioActivo.getRol(),
                    usuarioActivo.getCod(), "LOGOUT", "EXITOSA", "Cierre de sesion");
            MonitorSesiones.decrementar();
            usuarioActivo = null;
        }
    }
    public static Usuario getUsuarioActivo() {
        return usuarioActivo;
    }
    public static boolean hayUsuarioActivo() {
        return usuarioActivo != null;
    }
}