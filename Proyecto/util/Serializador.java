package util;

import java.io.*;

public class Serializador {

    private static final String RUTA = "data/";

    public static void serializar(Object obj, String archivo) {
        File dir = new File(RUTA);
        if (!dir.exists()) dir.mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(RUTA + archivo))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Error durante la serializacion: " + e.getMessage());
        }
    }

    public static Object deserializar(String archivo) {
        File f = new File(RUTA + archivo);
        if (!f.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(RUTA + archivo))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error durante la deserializacion: " + e.getMessage());
            return null;
        }
    }

    public static boolean existeArchivo(String archivo) {
        return new File(RUTA + archivo).exists();
    }

    public static boolean eliminarArchivo(String archivo) {
        File f = new File(RUTA + archivo);
        if (f.exists()) return f.delete();
        return false;
    }
}