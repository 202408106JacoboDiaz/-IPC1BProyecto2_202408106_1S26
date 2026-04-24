package threads;

import controller.DataStore;
import javax.swing.JTextArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorEstadisticas implements Runnable {

    private static final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JTextArea areaMonitor;
    private volatile boolean activo;

    public GeneradorEstadisticas(JTextArea areaMonitor) {
        this.areaMonitor = areaMonitor;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                int cursos = DataStore.getTotalCursos();
                int estudiantes = DataStore.getTotalEstudiantes();
                int notas = DataStore.getTotalNotasRegistradas();

                String msg = "[Thread-Estadisticas] Cursos Activos: " + cursos + " | Estudiantes Registrados: "
                        + estudiantes + " | Calificaciones Registradas: " + notas +
                        " | " + LocalDateTime.now().format(fmt);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (areaMonitor != null) {
                        areaMonitor.append(msg + "\n");
                    }
                });

                Thread.sleep(15000); // cada 15 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    public void detener() {
        activo = false;
    }
}