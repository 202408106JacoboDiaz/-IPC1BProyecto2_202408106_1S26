package util;

import model.*;
import java.io.*;

public class CSVLoader {

    //Instructores (Codigo, Nombre, FechaNacimiento, Genero, Contraseña)
    public static String cargarInstructores(String rutaArchivo, Instructor[] instructores, int totalActual,
                                            int maxTotal) {
        int insertados = 0; int errores = 0;
        int duplicados = 0; StringBuilder resumen = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length != 5) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": formato invalido\n");
                    errores++;
                    continue;
                }

                String cod = partes[0].trim(); String nomb = partes[1].trim();
                String cumple = partes[2].trim(); String genero = partes[3].trim();
                String contra = partes[4].trim();

                //Comprobar si hay duplicados
                boolean existe = false;
                for (int i = 0; i < totalActual + insertados; i++) {
                    if (instructores[i].getCod().equals(cod)) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": duplicado cod=").append(cod).append("\n");
                    duplicados++;
                    continue;
                }
                if (totalActual + insertados >= maxTotal) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": capacidad maxima alcanzada\n");
                    errores++;
                    continue;
                }
                instructores[totalActual + insertados] =
                        new Instructor(cod, nomb, cumple, genero, contra);
                insertados++;
            }
        } catch (IOException e) {
            return "Error al leer archivo: " + e.getMessage();
        }

        return "Instructores cargados: " + insertados +
                " | Duplicados: " + duplicados +
                " | Errores: " + errores + "\n" + resumen;
    }

    //Estudiantes (Codigo, Nombre, FechaNacimiento, Genero, Contraseña)
    public static String cargarEstudiantes(String rutaArchivo, Estudiante[] estudiantes, int totalActual,
                                           int maxTotal) {
        int insertados = 0; int errores = 0;
        int duplicados = 0; StringBuilder resumen = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length != 5) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": formato invalido\n");
                    errores++;
                    continue;
                }

                String cod = partes[0].trim(); String nomb = partes[1].trim();
                String cumple = partes[2].trim(); String genero = partes[3].trim();
                String contra = partes[4].trim();

                //Comprobar si hay duplicados
                boolean existe = false;
                for (int i = 0; i < totalActual + insertados; i++) {
                    if (estudiantes[i].getCod().equals(cod)) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": duplicado cod=").append(cod).append("\n");
                    duplicados++;
                    continue;
                }
                if (totalActual + insertados >= maxTotal) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": capacidad maxima alcanzada\n");
                    errores++;
                    continue;
                }
                estudiantes[totalActual + insertados] =
                        new Estudiante(cod, nomb, cumple, genero, contra);
                insertados++;
            }
        } catch (IOException e) {
            return "Error al leer archivo: " + e.getMessage();
        }

        return "Estudiantes cargados: " + insertados +
                " | Duplicados: " + duplicados +
                " | Errores: " + errores + "\n" + resumen;
    }

    //Cursos (Codigo, NombreCurso, Descripcion, Creditos, Seccion)
    public static String cargarCursos(String rutaArchivo, Curso[] cursos, int totalActual,
                                      int maxTotal) {
        int insertados = 0; int errores = 0;
        int duplicados = 0; StringBuilder resumen = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length != 5) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": formato invalido\n");
                    errores++;
                    continue;
                }

                String cod = partes[0].trim();
                String nomb = partes[1].trim();
                String desc = partes[2].trim();
                int creditos;
                try {
                    creditos = Integer.parseInt(partes[3].trim());
                } catch (NumberFormatException e) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": creditos invalidos\n");
                    errores++;
                    continue;
                }
                String seccion = partes[4].trim();

                //Comprobar si hay duplicados
                boolean existe = false;
                for (int i = 0; i < totalActual + insertados; i++) {
                    if (cursos[i].getCod().equals(cod)) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": duplicado cod=").append(cod).append("\n");
                    duplicados++;
                    continue;
                }
                if (totalActual + insertados >= maxTotal) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": capacidad maxima alcanzada\n");
                    errores++;
                    continue;
                }
                Curso c = new Curso(cod, nomb, desc, creditos);
                c.agregarSeccion(seccion);
                cursos[totalActual + insertados] = c;
                insertados++;
            }
        } catch (IOException e) {
            return "Error al leer archivo: " + e.getMessage();
        }

        return "Cursos cargados: " + insertados +
                " | Duplicados: " + duplicados +
                " | Errores: " + errores + "\n" + resumen;
    }

    //Notas (CodigoCurso, CodigoSeccion, CodigoEstudiante, Ponderacion, Nota, Fecha (YYYY-MM-DD))
    public static String cargarNotas(String rutaArchivo, Nota[] notas, int totalActual,
                                     int maxTotal) {
        int insertados = 0; int errores = 0;
        int duplicados = 0; StringBuilder resumen = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length != 7) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": formato invalido\n");
                    errores++;
                    continue;
                }

                String codCurso = partes[0].trim();
                String codSec = partes[1].trim();
                String codEst = partes[2].trim();
                String etiqueta = partes[3].trim();
                double pond;
                double nota;

                try {
                    pond = Double.parseDouble(partes[4].trim());
                    nota = Double.parseDouble(partes[5].trim());
                } catch (NumberFormatException e) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": valores numericos invalidos\n");
                    errores++;
                    continue;
                }
                String fecha = partes[6].trim();

                //Validar rangos
                if (nota < 0 || nota > 100 || pond <= 0) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": nota o ponderacion fuera de rango\n");
                    errores++;
                    continue;
                }

                //Comprobar si hay duplicados (misma etiqueta, seccion y estudiante)
                boolean existe = false;
                for (int i = 0; i < totalActual + insertados; i++) {
                    if (notas[i].getCodSeccion().equals(codSec) &&
                            notas[i].getCodEstudiante().equals(codEst) &&
                            notas[i].getEtiqueta().equals(etiqueta)) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": duplicado etiqueta=").append(etiqueta).append("\n");
                    duplicados++;
                    continue;
                }
                if (totalActual + insertados >= maxTotal) {
                    resumen.append("Linea ").append(numLinea)
                            .append(": capacidad maxima alcanzada\n");
                    errores++;
                    continue;
                }
                notas[totalActual + insertados] =
                        new Nota(codCurso, codSec, codEst, etiqueta, pond, nota, fecha);
                insertados++;
            }
        } catch (IOException e) {
            return "Error al leer archivo: " + e.getMessage();
        }
        return "Notas cargadas: " + insertados +
                " | Duplicados: " + duplicados +
                " | Errores: " + errores + "\n" + resumen;
    }
}