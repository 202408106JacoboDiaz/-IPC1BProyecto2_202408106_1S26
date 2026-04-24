package threads;

import javax.swing.JTextArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimuladorInscripciones implements Runnable {

    private static int inscripcionesPendientes = 0;
    private static final Object lock = new Object();
    private static final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JTextArea areaMonitor;
    private volatile boolean activo;

    public SimuladorInscripciones(JTextArea areaMonitor) {
        this.areaMonitor = areaMonitor;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                String msg = "[Thread-Inscripciones] Inscripciones Pendientes: " + inscripcionesPendientes +
                        " - Procesando... " +
                        LocalDateTime.now().format(fmt);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (areaMonitor != null) {
                        areaMonitor.append(msg + "\n");
                    }
                });

                Thread.sleep(8000); // cada 8 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detener() {
        activo = false;
    }
    public static void incrementar() {
        synchronized (lock) {
            inscripcionesPendientes++;
        }
    }
    public static void decrementar() {
        synchronized (lock) {
            if (inscripcionesPendientes > 0) inscripcionesPendientes--;
        }
    }
    public static int getInscripcionesPendientes() {
        return inscripcionesPendientes;
    }
}