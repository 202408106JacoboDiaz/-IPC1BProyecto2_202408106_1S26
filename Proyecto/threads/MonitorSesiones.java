package threads;

import javax.swing.JTextArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MonitorSesiones implements Runnable {

    private static int sesionesActivas = 0;
    private static final Object lock = new Object();
    private static final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JTextArea areaMonitor;
    private volatile boolean activo;

    public MonitorSesiones(JTextArea areaMonitor) {
        this.areaMonitor = areaMonitor;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                String msg = "[Thread-Sesiones] Usuarios Activos: " + sesionesActivas + " - Ultima actividad: " +
                        LocalDateTime.now().format(fmt);

                //Actualizar UI desde hilo secundario
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (areaMonitor != null) {
                        areaMonitor.append(msg + "\n");
                    }
                });

                Thread.sleep(10000); // cada 10 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detener() {
        activo = false;
    }

    public static synchronized void incrementar() {
        synchronized (lock) {
            sesionesActivas++;
        }
    }

    public static synchronized void decrementar() {
        synchronized (lock) {
            if (sesionesActivas > 0) sesionesActivas--;
        }
    }

    public static int getSesionesActivas() {
        return sesionesActivas;
    }
}