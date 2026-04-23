import controller.DataStore;
import view.LoginFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        //Cargar datos persistidos
        DataStore.cargarDatos();

        //Lanzar interfaz en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                //Apariencia del SO
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Look and feel no disponible: "
                        + e.getMessage());
            }

            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}