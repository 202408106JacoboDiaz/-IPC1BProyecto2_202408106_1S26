package util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bitacora {

    private static final String RUTA = "data/bitacora.txt";
    private static final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void registrar(String tipoUsuario, String codUsuario, String operacion, String estado,
                                 String desc) {
        File dir = new File("data/");
        if (!dir.exists()) dir.mkdirs();

        String registro = "[" + LocalDateTime.now().format(fmt) + "] | " +
                tipoUsuario + " | " +
                codUsuario + " | " +
                operacion + " | " +
                estado + " | " +
                desc;

        try (PrintWriter pw = new PrintWriter(
                new FileWriter(RUTA, true))) {
            pw.println(registro);
        } catch (IOException e) {
            System.out.println("Error al escribir: " + e.getMessage());
        }
    }

    public static String[] leerBitacora() {
        File f = new File(RUTA);
        if (!f.exists()) return new String[0];

        int totalLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA))) {
            while (br.readLine() != null) totalLineas++;
        } catch (IOException e) {
            System.out.println("Error al leer bitacora: " + e.getMessage());
            return new String[0];
        }

        String[] registros = new String[totalLineas];
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                registros[idx++] = linea;
            }
        } catch (IOException e) {
            System.out.println("Error al leer bitacora: " + e.getMessage());
        }

        return registros;
    }

    public static String[] filtrarPorTipoUsuario(String tipoUsuario) {
        String[] todos = leerBitacora();
        int count = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].contains("| " + tipoUsuario + " |")) count++;
        }

        String[] filtrados = new String[count];
        int idx = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].contains("| " + tipoUsuario + " |")) {
                filtrados[idx++] = todos[i];
            }
        }
        return filtrados;
    }

    public static String[] filtrarPorCodUsuario(String cod) {
        String[] todos = leerBitacora();
        int count = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].contains("| " + cod + " |")) count++;
        }

        String[] filtrados = new String[count];
        int idx = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].contains("| " + cod + " |")) {
                filtrados[idx++] = todos[i];
            }
        }
        return filtrados;
    }

    public static String[] filtrarPorOperacion(String operacion) {
        String[] todos = leerBitacora();
        int count = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].contains("| " + operacion + " |")) count++;
        }

        String[] filtrados = new String[count];
        int idx = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].contains("| " + operacion + " |")) {
                filtrados[idx++] = todos[i];
            }
        }
        return filtrados;
    }
}