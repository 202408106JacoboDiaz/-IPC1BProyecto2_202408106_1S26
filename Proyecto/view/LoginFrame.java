package view;

import controller.AuthController;
import controller.DataStore;
import model.*;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtCod; private JPasswordField txtContra;
    private JButton btnLogin; private JButton btnSalir;
    private JLabel lblMensaje;

    public LoginFrame() {
        initComponents();
        DataStore.cargarDatos();
    }

    private void initComponents() {
        setTitle("Sancarlista Academy - Inicio de Sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        //Panel principal
        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        //Titulo
        JLabel lblTitulo = new JLabel("SANCARLISTA ACADEMY", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        pnlMain.add(lblTitulo, gbc);

        //Codigo
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        pnlMain.add(new JLabel("Codigo:"), gbc);

        txtCod = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        pnlMain.add(txtCod, gbc);

        //Contrasena
        gbc.gridx = 0; gbc.gridy = 2;
        pnlMain.add(new JLabel("Contrasena:"), gbc);

        txtContra = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        pnlMain.add(txtContra, gbc);

        //Mensaje
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        pnlMain.add(lblMensaje, gbc);

        //BTNs
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnLogin = new JButton("Ingresar");
        btnSalir = new JButton("Salir");

        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        pnlBotones.add(btnLogin);
        pnlBotones.add(btnSalir);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        pnlMain.add(pnlBotones, gbc);

        add(pnlMain);

        //Acciones
        btnLogin.addActionListener(e -> login());
        btnSalir.addActionListener(e -> System.exit(0));

        //Enter LOGIN
        txtContra.addActionListener(e -> login());
    }

    private void login() {
        String cod = txtCod.getText().trim();
        String contra = new String(txtContra.getPassword()).trim();

        if (cod.isEmpty() || contra.isEmpty()) {
            lblMensaje.setText("Ingrese codigo y contraseña");
            return;
        }

        Usuario usuario = AuthController.login(cod, contra);

        if (usuario == null) {
            lblMensaje.setText("Credenciales incorrectas");
            txtContra.setText("");
            return;
        }

        //Redirigir (segun rol)
        switch (usuario.getRol()) {
            case "ADMINISTRADOR":
                new AdminPanel((Admin) usuario).setVisible(true);
                break;
            case "INSTRUCTOR":
                new InstructorPanel((Instructor) usuario).setVisible(true);
                break;
            case "ESTUDIANTE":
                new EstudiantePanel((Estudiante) usuario).setVisible(true);
                break;
        }

        dispose();
    }
}