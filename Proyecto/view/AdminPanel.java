package view;

import controller.*;
import model.*;
import threads.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class AdminPanel extends JFrame {

    private Admin admin;
    private JTextArea areaThreads;
    private MonitorSesiones monitorSesiones;
    private SimuladorInscripciones simInscripciones;
    private GeneradorEstadisticas genEstadisticas;
    private Thread hiloSesiones, hiloInscripciones, hiloEstadisticas;

    //Tablas
    private JTable tblInstructores, tblEstudiantes, tblCursos, tblSecciones;
    private DefaultTableModel mdlInstructores, mdlEstudiantes;
    private DefaultTableModel mdlCursos, mdlSecciones;

    public AdminPanel(Admin admin) {
        this.admin = admin;
        initComponents();
        iniciarThreads();
    }

    private void initComponents() {
        setTitle("Sancarlista Academy - Administrador: " + admin.getNomb());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        //Cerrar sesion al cerrar ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarSesion();
            }
        });

        //Panel de tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Instructores", crearPanelInstructores());
        tabs.addTab("Estudiantes", crearPanelEstudiantes());
        tabs.addTab("Cursos", crearPanelCursos());
        tabs.addTab("Secciones", crearPanelSecciones());
        tabs.addTab("Bitacora", crearPanelBitacora());
        tabs.addTab("Monitor", crearPanelMonitor());

        //Btn cerrar sesion
        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.setBackground(new Color(200, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> cerrarSesion());

        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSur.add(btnLogout);

        add(tabs, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);
    }

    //Panel instructores
    private JPanel crearPanelInstructores() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Tabla
        String[] cols = {"Codigo", "Nombre", "Cumpleanos", "Genero", "Secciones"};
        mdlInstructores = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInstructores = new JTable(mdlInstructores);
        cargarTablaInstructores();

        JScrollPane scroll = new JScrollPane(tblInstructores);

        //Formulario
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField txtCod = new JTextField(); JTextField txtNomb = new JTextField();
        JTextField txtCumple = new JTextField(); JTextField txtGenero = new JTextField();
        JTextField txtContra = new JTextField();

        pnlForm.add(new JLabel("Codigo:")); pnlForm.add(txtCod);
        pnlForm.add(new JLabel("Nombre:")); pnlForm.add(txtNomb);
        pnlForm.add(new JLabel("Cumpleanos:")); pnlForm.add(txtCumple);
        pnlForm.add(new JLabel("Genero:")); pnlForm.add(txtGenero);
        pnlForm.add(new JLabel("Contrasena:")); pnlForm.add(txtContra);

        //BTNs
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCSV = new JButton("Cargar CSV");
        JButton btnLimpiar = new JButton("Limpiar");

        pnlBtns.add(btnCrear); pnlBtns.add(btnActualizar);
        pnlBtns.add(btnEliminar); pnlBtns.add(btnCSV);
        pnlBtns.add(btnLimpiar);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        lblMsg.setForeground(new Color(0, 130, 0));

        JPanel pnlDerecha = new JPanel(new BorderLayout(5, 5));
        pnlDerecha.add(pnlForm, BorderLayout.CENTER); pnlDerecha.add(pnlBtns, BorderLayout.SOUTH);

        pnl.add(scroll, BorderLayout.CENTER); pnl.add(pnlDerecha, BorderLayout.EAST);
        pnl.add(lblMsg, BorderLayout.SOUTH);

        //Seleccionar fila
        tblInstructores.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblInstructores.getSelectedRow();
            if (fila >= 0) {
                txtCod.setText((String) mdlInstructores.getValueAt(fila, 0));
                txtNomb.setText((String) mdlInstructores.getValueAt(fila, 1));
                txtCumple.setText((String) mdlInstructores.getValueAt(fila, 2));
                txtGenero.setText((String) mdlInstructores.getValueAt(fila, 3));
            }
        });

        //Acciones
        btnCrear.addActionListener(e -> {
            String res = AdminController.crearInstructor(txtCod.getText().trim(), txtNomb.getText().trim(),
                    txtCumple.getText().trim(), txtGenero.getText().trim(), txtContra.getText().trim());
            lblMsg.setForeground(res.startsWith("OK") ?
                    new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
            cargarTablaInstructores();
        });

        btnActualizar.addActionListener(e -> {
            String res = AdminController.actualizarInstructor(txtCod.getText().trim(), txtNomb.getText().trim(),
                    txtContra.getText().trim());
            lblMsg.setForeground(res.startsWith("OK") ?
                    new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
            cargarTablaInstructores();
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar instructor " + txtCod.getText() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = AdminController.eliminarInstructor(txtCod.getText().trim());
                lblMsg.setForeground(res.startsWith("OK") ?
                        new Color(0,130,0) : Color.RED);
                lblMsg.setText(res);
                cargarTablaInstructores();
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = AdminController.cargarInstructoresCSV(
                        fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, res, "Resultado CSV",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarTablaInstructores();
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtCod.setText(""); txtNomb.setText(""); txtCumple.setText(""); txtGenero.setText("");
            txtContra.setText(""); lblMsg.setText("");
        });
        return pnl;
    }

    private void cargarTablaInstructores() {
        mdlInstructores.setRowCount(0);
        Instructor[] ins = DataStore.getInstructores();
        int total = DataStore.getTotalInstructores();
        for (int i = 0; i < total; i++) {
            mdlInstructores.addRow(new Object[]{
                    ins[i].getCod(), ins[i].getNomb(),
                    ins[i].getCumple(), ins[i].getGenero(),
                    ins[i].getTotalSecciones()
            });
        }
    }

    //Panel estudiantes
    private JPanel crearPanelEstudiantes() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Codigo", "Nombre", "Cumpleanos", "Genero", "Cursos"};
        mdlEstudiantes = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEstudiantes = new JTable(mdlEstudiantes);
        cargarTablaEstudiantes();

        JScrollPane scroll = new JScrollPane(tblEstudiantes);

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField txtCod = new JTextField(); JTextField txtNomb = new JTextField();
        JTextField txtCumple = new JTextField(); JTextField txtGenero = new JTextField();
        JTextField txtContra = new JTextField();

        pnlForm.add(new JLabel("Codigo:")); pnlForm.add(txtCod);
        pnlForm.add(new JLabel("Nombre:")); pnlForm.add(txtNomb);
        pnlForm.add(new JLabel("Cumpleanos:")); pnlForm.add(txtCumple);
        pnlForm.add(new JLabel("Genero:")); pnlForm.add(txtGenero);
        pnlForm.add(new JLabel("Contrasena:")); pnlForm.add(txtContra);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCSV = new JButton("Cargar CSV");
        JButton btnLimpiar = new JButton("Limpiar");

        pnlBtns.add(btnCrear); pnlBtns.add(btnActualizar); pnlBtns.add(btnEliminar); pnlBtns.add(btnCSV);
        pnlBtns.add(btnLimpiar);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        lblMsg.setForeground(new Color(0, 130, 0));

        JPanel pnlDerecha = new JPanel(new BorderLayout(5, 5));
        pnlDerecha.add(pnlForm, BorderLayout.CENTER);
        pnlDerecha.add(pnlBtns, BorderLayout.SOUTH);

        pnl.add(scroll, BorderLayout.CENTER); pnl.add(pnlDerecha, BorderLayout.EAST);
        pnl.add(lblMsg, BorderLayout.SOUTH);

        tblEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblEstudiantes.getSelectedRow();
            if (fila >= 0) {
                txtCod.setText((String) mdlEstudiantes.getValueAt(fila, 0));
                txtNomb.setText((String) mdlEstudiantes.getValueAt(fila, 1));
                txtCumple.setText((String) mdlEstudiantes.getValueAt(fila, 2));
                txtGenero.setText((String) mdlEstudiantes.getValueAt(fila, 3));
            }
        });

        btnCrear.addActionListener(e -> {
            String res = AdminController.crearEstudiante(
                    txtCod.getText().trim(), txtNomb.getText().trim(), txtCumple.getText().trim(),
                    txtGenero.getText().trim(), txtContra.getText().trim());
            lblMsg.setForeground(res.startsWith("OK") ?
                    new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
            cargarTablaEstudiantes();
        });

        btnActualizar.addActionListener(e -> {
            String res = AdminController.actualizarEstudiante(
                    txtCod.getText().trim(), txtNomb.getText().trim(), txtContra.getText().trim());
            lblMsg.setForeground(res.startsWith("OK") ?
                    new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
            cargarTablaEstudiantes();
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar estudiante " + txtCod.getText() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = AdminController.eliminarEstudiante(txtCod.getText().trim());
                lblMsg.setForeground(res.startsWith("OK") ?
                        new Color(0,130,0) : Color.RED);
                lblMsg.setText(res);
                cargarTablaEstudiantes();
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = AdminController.cargarEstudiantesCSV(fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, res, "Resultado CSV",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarTablaEstudiantes();
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtCod.setText(""); txtNomb.setText(""); txtCumple.setText(""); txtGenero.setText("");
            txtContra.setText(""); lblMsg.setText("");
        });

        return pnl;
    }

    private void cargarTablaEstudiantes() {
        mdlEstudiantes.setRowCount(0);
        Estudiante[] ests = DataStore.getEstudiantes();
        int total = DataStore.getTotalEstudiantes();
        for (int i = 0; i < total; i++) {
            mdlEstudiantes.addRow(new Object[]{
                    ests[i].getCod(), ests[i].getNomb(),
                    ests[i].getCumple(), ests[i].getGenero(),
                    ests[i].getTotalCursos()
            });
        }
    }

    //Panel cursos
    private JPanel crearPanelCursos() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Codigo", "Nombre", "Descripcion", "Creditos", "Secciones"};
        mdlCursos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCursos = new JTable(mdlCursos);
        cargarTablaCursos();

        JScrollPane scroll = new JScrollPane(tblCursos);

        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField txtCod = new JTextField(); JTextField txtNomb = new JTextField();
        JTextField txtDesc = new JTextField(); JTextField txtCreditos = new JTextField();

        pnlForm.add(new JLabel("Codigo:")); pnlForm.add(txtCod);
        pnlForm.add(new JLabel("Nombre:")); pnlForm.add(txtNomb);
        pnlForm.add(new JLabel("Descripcion:")); pnlForm.add(txtDesc);
        pnlForm.add(new JLabel("Creditos:")); pnlForm.add(txtCreditos);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnCrear = new JButton("Crear"); JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar"); JButton btnCSV = new JButton("Cargar CSV");
        JButton btnLimpiar = new JButton("Limpiar");

        pnlBtns.add(btnCrear); pnlBtns.add(btnActualizar); pnlBtns.add(btnEliminar); pnlBtns.add(btnCSV);
        pnlBtns.add(btnLimpiar);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);

        JPanel pnlDerecha = new JPanel(new BorderLayout(5, 5));
        pnlDerecha.add(pnlForm, BorderLayout.CENTER); pnlDerecha.add(pnlBtns, BorderLayout.SOUTH);

        pnl.add(scroll, BorderLayout.CENTER); pnl.add(pnlDerecha, BorderLayout.EAST);
        pnl.add(lblMsg, BorderLayout.SOUTH);

        tblCursos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblCursos.getSelectedRow();
            if (fila >= 0) {
                txtCod.setText((String) mdlCursos.getValueAt(fila, 0));
                txtNomb.setText((String) mdlCursos.getValueAt(fila, 1));
                txtDesc.setText((String) mdlCursos.getValueAt(fila, 2));
                txtCreditos.setText(mdlCursos.getValueAt(fila, 3).toString());
            }
        });

        btnCrear.addActionListener(e -> {
            try {
                int cred = Integer.parseInt(txtCreditos.getText().trim());
                String res = AdminController.crearCurso(txtCod.getText().trim(), txtNomb.getText().trim(),
                        txtDesc.getText().trim(), cred);
                lblMsg.setForeground(res.startsWith("OK") ?
                        new Color(0,130,0) : Color.RED);
                lblMsg.setText(res);
                cargarTablaCursos();
            } catch (NumberFormatException ex) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Creditos debe ser un numero");
            }
        });

        btnActualizar.addActionListener(e -> {
            try {
                int cred = txtCreditos.getText().trim().isEmpty() ? 0 :
                        Integer.parseInt(txtCreditos.getText().trim());
                String res = AdminController.actualizarCurso(
                        txtCod.getText().trim(), txtNomb.getText().trim(), txtDesc.getText().trim(), cred);
                lblMsg.setForeground(res.startsWith("OK") ?
                        new Color(0,130,0) : Color.RED);
                lblMsg.setText(res);
                cargarTablaCursos();
            } catch (NumberFormatException ex) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Creditos debe ser un numero");
            }
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar curso " + txtCod.getText() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = AdminController.eliminarCurso(txtCod.getText().trim());
                lblMsg.setForeground(res.startsWith("OK") ?
                        new Color(0,130,0) : Color.RED); lblMsg.setText(res);
                cargarTablaCursos();
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = AdminController.cargarCursosCSV(
                        fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, res, "Resultado CSV",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarTablaCursos();
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtCod.setText(""); txtNomb.setText(""); txtDesc.setText(""); txtCreditos.setText("");
            lblMsg.setText("");
        });
        return pnl;
    }

    private void cargarTablaCursos() {
        mdlCursos.setRowCount(0);
        Curso[] cursos = DataStore.getCursos();
        int total = DataStore.getTotalCursos();
        for (int i = 0; i < total; i++) {
            mdlCursos.addRow(new Object[]{
                    cursos[i].getCod(), cursos[i].getNomb(), cursos[i].getDesc(), cursos[i].getCreditosCLAR(),
                    cursos[i].getTotalSecciones()
            });
        }
    }

    //Panel de secciones
    private JPanel crearPanelSecciones() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Codigo", "Curso", "Instructor", "Horario", "Semestre", "Estudiantes"};
        mdlSecciones = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSecciones = new JTable(mdlSecciones);
        cargarTablaSecciones();

        JScrollPane scroll = new JScrollPane(tblSecciones);

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField txtCod = new JTextField(); JTextField txtCurso = new JTextField();
        JTextField txtInst = new JTextField(); JTextField txtHorario = new JTextField();
        JTextField txtSemestre = new JTextField();

        pnlForm.add(new JLabel("Codigo:")); pnlForm.add(txtCod);
        pnlForm.add(new JLabel("Cod Curso:")); pnlForm.add(txtCurso);
        pnlForm.add(new JLabel("Cod Instructor:")); pnlForm.add(txtInst);
        pnlForm.add(new JLabel("Horario:")); pnlForm.add(txtHorario);
        pnlForm.add(new JLabel("Semestre:")); pnlForm.add(txtSemestre);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnCrear = new JButton("Crear"); JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        pnlBtns.add(btnCrear); pnlBtns.add(btnEliminar); pnlBtns.add(btnLimpiar);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);

        JPanel pnlDerecha = new JPanel(new BorderLayout(5, 5));
        pnlDerecha.add(pnlForm, BorderLayout.CENTER); pnlDerecha.add(pnlBtns, BorderLayout.SOUTH);

        pnl.add(scroll, BorderLayout.CENTER); pnl.add(pnlDerecha, BorderLayout.EAST);
        pnl.add(lblMsg, BorderLayout.SOUTH);

        tblSecciones.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblSecciones.getSelectedRow();
            if (fila >= 0) {
                txtCod.setText((String) mdlSecciones.getValueAt(fila, 0));
                txtCurso.setText((String) mdlSecciones.getValueAt(fila, 1));
                txtInst.setText((String) mdlSecciones.getValueAt(fila, 2));
                txtHorario.setText((String) mdlSecciones.getValueAt(fila, 3));
                txtSemestre.setText((String) mdlSecciones.getValueAt(fila, 4));
            }
        });

        btnCrear.addActionListener(e -> {
            String res = AdminController.crearSeccion(txtCod.getText().trim(), txtCurso.getText().trim(),
                    txtInst.getText().trim(), txtHorario.getText().trim(),
                    txtSemestre.getText().trim());
            lblMsg.setForeground(res.startsWith("OK") ?
                    new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
            cargarTablaSecciones();
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar seccion " + txtCod.getText() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = AdminController.eliminarSeccion(txtCod.getText().trim());
                lblMsg.setForeground(res.startsWith("OK") ?
                        new Color(0,130,0) : Color.RED);
                lblMsg.setText(res);
                cargarTablaSecciones();
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtCod.setText(""); txtCurso.setText(""); txtInst.setText(""); txtHorario.setText("");
            txtSemestre.setText(""); lblMsg.setText("");
        });
        return pnl;
    }

    private void cargarTablaSecciones() {
        mdlSecciones.setRowCount(0);
        Seccion[] secs = DataStore.getSecciones();
        int total = DataStore.getTotalSecciones();
        for (int i = 0; i < total; i++) {
            mdlSecciones.addRow(new Object[]{
                    secs[i].getCod(), secs[i].getCodCurso(), secs[i].getCodInstructor(), secs[i].getHorario(),
                    secs[i].getSemestre(), secs[i].getTotalEstudiantes()
            });
        }
    }

    //Panel bitacora
    private JPanel crearPanelBitacora() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea areaBit = new JTextArea(); areaBit.setEditable(false);
        areaBit.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(areaBit);

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JTextField txtFiltro = new JTextField(15);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Todos", "ADMINISTRADOR",
                "INSTRUCTOR", "ESTUDIANTE"});
        JButton btnFiltrar  = new JButton("Filtrar"); JButton btnRecargar = new JButton("Recargar");

        pnlFiltros.add(new JLabel("Tipo:")); pnlFiltros.add(cmbTipo);
        pnlFiltros.add(new JLabel("Codigo:")); pnlFiltros.add(txtFiltro);
        pnlFiltros.add(btnFiltrar); pnlFiltros.add(btnRecargar);

        pnl.add(pnlFiltros, BorderLayout.NORTH);
        pnl.add(scroll, BorderLayout.CENTER);

        btnRecargar.addActionListener(e -> {
            areaBit.setText("");
            String[] registros = util.Bitacora.leerBitacora();
            for (String r : registros) areaBit.append(r + "\n");
        });

        btnFiltrar.addActionListener(e -> {
            areaBit.setText(""); String tipo = (String) cmbTipo.getSelectedItem();
            String cod  = txtFiltro.getText().trim(); String[] registros;

            if (!cod.isEmpty()) {
                registros = util.Bitacora.filtrarPorCodUsuario(cod);
            } else if (!tipo.equals("Todos")) {
                registros = util.Bitacora.filtrarPorTipoUsuario(tipo);
            } else {
                registros = util.Bitacora.leerBitacora();
            }
            for (String r : registros) areaBit.append(r + "\n");
        });
        return pnl;
    }

    //Panel monitor threads
    private JPanel crearPanelMonitor() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaThreads = new JTextArea(); areaThreads.setEditable(false);
        areaThreads.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaThreads.setBackground(new Color(30, 30, 30));
        areaThreads.setForeground(new Color(0, 255, 0));

        JScrollPane scroll = new JScrollPane(areaThreads);

        JButton btnLimpiar = new JButton("Limpiar consola");
        btnLimpiar.addActionListener(e -> areaThreads.setText(""));

        pnl.add(new JLabel("Monitor de procesos en tiempo real:"), BorderLayout.NORTH);
        pnl.add(scroll, BorderLayout.CENTER);
        pnl.add(btnLimpiar, BorderLayout.SOUTH);
        return pnl;
    }

    //Threads/hilos
    private void iniciarThreads() {
        monitorSesiones = new MonitorSesiones(areaThreads);
        simInscripciones = new SimuladorInscripciones(areaThreads);
        genEstadisticas = new GeneradorEstadisticas(areaThreads);

        hiloSesiones = new Thread(monitorSesiones); hiloInscripciones = new Thread(simInscripciones);
        hiloEstadisticas = new Thread(genEstadisticas);

        hiloSesiones.setDaemon(true); hiloInscripciones.setDaemon(true); hiloEstadisticas.setDaemon(true);

        hiloSesiones.start(); hiloInscripciones.start(); hiloEstadisticas.start();
    }

    private void detenerThreads() {
        monitorSesiones.detener();simInscripciones.detener(); genEstadisticas.detener();
    }

    //Cerrar sesion
    private void cerrarSesion() {
        detenerThreads(); AuthController.logout(); new LoginFrame().setVisible(true); dispose();
    }
}