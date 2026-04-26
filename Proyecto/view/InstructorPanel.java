package view;

import controller.*;
import model.*;
import threads.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class InstructorPanel extends JFrame {

    private Instructor instructor; private JTextArea areaThreads;
    private MonitorSesiones monitorSesiones; private SimuladorInscripciones simInscripciones;
    private GeneradorEstadisticas genEstadisticas;
    private Thread hiloSesiones, hiloInscripciones, hiloEstadisticas;

    private DefaultTableModel mdlNotas; private JTable tblNotas;

    public InstructorPanel(Instructor instructor) {
        this.instructor = instructor; initComponents(); iniciarThreads();
    }

    private void initComponents() {
        setTitle("Sancarlista Academy - Instructor: " + instructor.getNomb());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); setSize(900, 650);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarSesion();
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Mis Secciones", crearPanelSecciones());
        tabs.addTab("Notas", crearPanelNotas());
        tabs.addTab("Reportes", crearPanelReportes());
        tabs.addTab("Monitor", crearPanelMonitor());

        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.setBackground(new Color(200, 50, 50)); btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false); btnLogout.setOpaque(true); btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false); btnLogout.addActionListener(e -> cerrarSesion());

        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pnlSur.add(btnLogout);

        add(tabs, BorderLayout.CENTER); add(pnlSur, BorderLayout.SOUTH);
    }

    //Panel secciones
    private JPanel crearPanelSecciones() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Codigo", "Curso", "Horario", "Semestre", "Estudiantes"};
        DefaultTableModel mdlSecs = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblSecs = new JTable(mdlSecs); cargarTablaSecciones(mdlSecs);

        JScrollPane scroll = new JScrollPane(tblSecs);

        //Sub-tabla de estudiantes por seccion
        String[] colsEst = {"Codigo", "Nombre", "Promedio", "Estado"};
        DefaultTableModel mdlEsts = new DefaultTableModel(colsEst, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblEsts = new JTable(mdlEsts); JScrollPane scrollEst = new JScrollPane(tblEsts);
        scrollEst.setBorder(BorderFactory.createTitledBorder("Estudiantes de la seccion"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, scrollEst);
        split.setDividerLocation(250);

        tblSecs.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblSecs.getSelectedRow();
            if (fila >= 0) {
                String codSec = (String) mdlSecs.getValueAt(fila, 0);
                cargarEstudiantesSeccion(mdlEsts, codSec);
            }
        });
        pnl.add(split, BorderLayout.CENTER);
        return pnl;
    }

    private void cargarTablaSecciones(DefaultTableModel mdl) {
        mdl.setRowCount(0);
        Seccion[] secs = InstructorController
                .getSeccionesDeInstructor(instructor.getCod());
        for (int i = 0; i < secs.length; i++) {
            mdl.addRow(new Object[]{secs[i].getCod(), secs[i].getCodCurso(), secs[i].getHorario(),
                    secs[i].getSemestre(), secs[i].getTotalEstudiantes()
            });
        }
    }

    private void cargarEstudiantesSeccion(DefaultTableModel mdl, String codSec) {
        mdl.setRowCount(0); Seccion sec = DataStore.buscarSeccion(codSec);
        if (sec == null) return;

        String[] estudiantes = sec.getEstudiantes();
        int total = sec.getTotalEstudiantes();
        for (int i = 0; i < total; i++) {
            String codEst = estudiantes[i]; Estudiante est = DataStore.buscarEstudiante(codEst);
            double prom = InstructorController.calcularPromedio(codEst, codSec);
            String estado = prom >= 61 ? "Aprobado" : "Reprobado";
            mdl.addRow(new Object[]{ codEst, est != null ? est.getNomb() : "N/A", String.format("%.2f", prom),
                    estado
            });
        }
    }

    //Panel notas
    private JPanel crearPanelNotas() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //Filtros
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JTextField txtFiltroSec = new JTextField(10);
        JTextField txtFiltroEst = new JTextField(10); JButton btnFiltrar = new JButton("Filtrar");

        pnlFiltros.add(new JLabel("Seccion:")); pnlFiltros.add(txtFiltroSec);
        pnlFiltros.add(new JLabel("Estudiante:")); pnlFiltros.add(txtFiltroEst);
        pnlFiltros.add(btnFiltrar);
        //Tabla notas
        String[] cols = {"#", "Curso", "Seccion", "Estudiante", "Etiqueta", "Ponderacion", "Nota", "Fecha",
                "Estado"};
        mdlNotas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNotas = new JTable(mdlNotas); cargarTablaNotas("", "");
        JScrollPane scroll = new JScrollPane(tblNotas);

        //Formulario
        JPanel pnlForm = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField txtCodCurso = new JTextField(); JTextField txtCodSec = new JTextField();
        JTextField txtCodEst = new JTextField(); JTextField txtEtiqueta = new JTextField();
        JTextField txtPond = new JTextField(); JTextField txtNota = new JTextField();
        JTextField txtFecha = new JTextField();

        pnlForm.add(new JLabel("Cod Curso:")); pnlForm.add(txtCodCurso);
        pnlForm.add(new JLabel("Cod Seccion:")); pnlForm.add(txtCodSec);
        pnlForm.add(new JLabel("Cod Estudiante:")); pnlForm.add(txtCodEst);
        pnlForm.add(new JLabel("Etiqueta:")); pnlForm.add(txtEtiqueta);
        pnlForm.add(new JLabel("Ponderacion:")); pnlForm.add(txtPond);
        pnlForm.add(new JLabel("Nota (0-100):")); pnlForm.add(txtNota);
        pnlForm.add(new JLabel("Fecha:")); pnlForm.add(txtFecha);

        //BTNs
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnCrear = new JButton("Registrar"); JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar"); JButton btnCSV = new JButton("Cargar CSV");
        JButton btnLimpiar = new JButton("Limpiar");

        pnlBtns.add(btnCrear); pnlBtns.add(btnActualizar); pnlBtns.add(btnEliminar); pnlBtns.add(btnCSV);
        pnlBtns.add(btnLimpiar);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);

        JPanel pnlDerecha = new JPanel(new BorderLayout(5, 5));
        pnlDerecha.add(pnlForm, BorderLayout.CENTER); pnlDerecha.add(pnlBtns, BorderLayout.SOUTH);

        pnl.add(pnlFiltros, BorderLayout.NORTH); pnl.add(scroll, BorderLayout.CENTER);
        pnl.add(pnlDerecha, BorderLayout.EAST); pnl.add(lblMsg, BorderLayout.SOUTH);

        //Seleccionar fila
        tblNotas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblNotas.getSelectedRow();
            if (fila >= 0) {
                txtCodCurso.setText((String) mdlNotas.getValueAt(fila, 1));
                txtCodSec.setText((String) mdlNotas.getValueAt(fila, 2));
                txtCodEst.setText((String) mdlNotas.getValueAt(fila, 3));
                txtEtiqueta.setText((String) mdlNotas.getValueAt(fila, 4));
                txtPond.setText(mdlNotas.getValueAt(fila, 5).toString());
                txtNota.setText(mdlNotas.getValueAt(fila, 6).toString());
                txtFecha.setText((String) mdlNotas.getValueAt(fila, 7));
            }
        });

        btnFiltrar.addActionListener(e -> {cargarTablaNotas(txtFiltroSec.getText().trim(),
                    txtFiltroEst.getText().trim());
        });

        btnCrear.addActionListener(e -> {
            try {
                double pond = Double.parseDouble(txtPond.getText().trim());
                double nota = Double.parseDouble(txtNota.getText().trim());
                String res  = InstructorController.crearNota(
                        txtCodCurso.getText().trim(), txtCodSec.getText().trim(),
                        txtCodEst.getText().trim(), txtEtiqueta.getText().trim(),
                        pond, nota, txtFecha.getText().trim(), instructor.getCod());
                lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
                lblMsg.setText(res); cargarTablaNotas("", "");
            } catch (NumberFormatException ex) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Nota y ponderacion deben ser numericos");
            }
        });

        btnActualizar.addActionListener(e -> {
            int fila = tblNotas.getSelectedRow();
            if (fila < 0) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seleccione una nota de la tabla");
                return;
            }
            try {
                int idx = (int) mdlNotas.getValueAt(fila, 0) - 1;
                double pond = Double.parseDouble(txtPond.getText().trim());
                double nota = Double.parseDouble(txtNota.getText().trim());
                String res  = InstructorController.actualizarNota(idx, pond, nota, instructor.getCod());
                lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
                lblMsg.setText(res);
                cargarTablaNotas("", "");
            } catch (NumberFormatException ex) { lblMsg.setForeground(Color.RED);
                lblMsg.setText("ERROR: Valores numericos invalidos");
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tblNotas.getSelectedRow();
            if (fila < 0) {
                lblMsg.setForeground(Color.RED);
                lblMsg.setText("ERROR: Seleccione una nota de la tabla");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta nota?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int idx    = (int) mdlNotas.getValueAt(fila, 0) - 1;
                String res = InstructorController.eliminarNota(idx, instructor.getCod());
                lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
                lblMsg.setText(res); cargarTablaNotas("", "");
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = InstructorController.cargarNotasCSV(fc.getSelectedFile().getAbsolutePath(),
                        instructor.getCod());
                JOptionPane.showMessageDialog(this, res, "Resultado CSV",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarTablaNotas("", "");
            }
        });

        btnLimpiar.addActionListener(e -> {txtCodCurso.setText(""); txtCodSec.setText("");
            txtCodEst.setText(""); txtEtiqueta.setText(""); txtPond.setText(""); txtNota.setText("");
            txtFecha.setText(""); lblMsg.setText("");
        });
        return pnl;
    }

    private void cargarTablaNotas(String filtroSec, String filtroEst) {
        mdlNotas.setRowCount(0); Nota[] notas = DataStore.getNotas();
        int total = DataStore.getTotalNotas(); int num = 1;

        //Unicamente notas de secciones asignadas al instructor
        Seccion[] misSecs = InstructorController
                .getSeccionesDeInstructor(instructor.getCod());

        for (int i = 0; i < total; i++) {
            boolean esMia = false;
            for (int j = 0; j < misSecs.length; j++) {
                if (misSecs[j].getCod().equals(notas[i].getCodSeccion())) {
                    esMia = true; break;
                }
            }
            if (!esMia) continue;

            if (!filtroSec.isEmpty() && !notas[i].getCodSeccion().contains(filtroSec)) continue;
            if (!filtroEst.isEmpty() && !notas[i].getCodEstudiante().contains(filtroEst)) continue;

            mdlNotas.addRow(new Object[]{
                    num++, notas[i].getCodCurso(), notas[i].getCodSeccion(), notas[i].getCodEstudiante(),
                    notas[i].getEtiqueta(), notas[i].getPonderacion(), notas[i].getNota(), notas[i].getFecha(),
                    notas[i].esAprobado() ? "Aprobado" : "Reprobado"
            });
        }
    }

    //Panel monitor
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
        pnl.add(scroll, BorderLayout.CENTER); pnl.add(btnLimpiar, BorderLayout.SOUTH);
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
        monitorSesiones.detener(); simInscripciones.detener(); genEstadisticas.detener();
    }

    //Panel de reportes
    private JPanel crearPanelReportes() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Formulario de parametros
        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtCodSec = new JTextField(); JTextField txtCodEst = new JTextField();

        pnlForm.add(new JLabel("Cod Seccion:")); pnlForm.add(txtCodSec);
        pnlForm.add(new JLabel("Cod Estudiante:")); pnlForm.add(txtCodEst);
        pnlForm.setBorder(BorderFactory.createTitledBorder("Parametros"));

        //BTNs PDF
        JPanel pnlPDF = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlPDF.setBorder(BorderFactory.createTitledBorder("Reportes PDF"));

        JButton btnTop5Mejor = new JButton("Top 5 Mejores (por Seccion)");
        JButton btnTop5Peor = new JButton("Top 5 Peores (por Seccion)");
        JButton btnCalSec = new JButton("Calificaciones por Seccion");

        pnlPDF.add(btnTop5Mejor); pnlPDF.add(btnTop5Peor); pnlPDF.add(btnCalSec);

        //BTNs CSV
        JPanel pnlCSV = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlCSV.setBorder(BorderFactory.createTitledBorder("Exportar CSV"));

        JButton btnCsvNotas = new JButton("Exportar Notas de Seccion");
        JButton btnCsvEst   = new JButton("Exportar Historial Estudiante");

        pnlCSV.add(btnCsvNotas);pnlCSV.add(btnCsvEst);

        //Mensaje
        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Arial", Font.BOLD, 12));

        //Layout
        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCentro.add(pnlPDF); pnlCentro.add(pnlCSV);

        pnl.add(pnlForm, BorderLayout.NORTH); pnl.add(pnlCentro, BorderLayout.CENTER);
        pnl.add(lblMsg, BorderLayout.SOUTH);

        //Acciones PDF
        btnTop5Mejor.addActionListener(e -> {
            String cod = txtCodSec.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de seccion");
                return;
            }
            //Validar que la seccion sea del instructor
            Seccion sec = DataStore.buscarSeccion(cod);
            if (sec == null || !sec.getCodInstructor().equals(instructor.getCod())) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seccion no asignada a usted");
                return;
            }
            String res = util.ReportePDF.reporteTop5Mejores(cod);
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        btnTop5Peor.addActionListener(e -> {
            String cod = txtCodSec.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de seccion");
                return;
            }
            Seccion sec = DataStore.buscarSeccion(cod);
            if (sec == null || !sec.getCodInstructor().equals(instructor.getCod())) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seccion no asignada a usted");
                return;
            }
            String res = util.ReportePDF.reporteTop5Peores(cod);
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        btnCalSec.addActionListener(e -> {
            String cod = txtCodSec.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de seccion");
                return;
            }
            Seccion sec = DataStore.buscarSeccion(cod);
            if (sec == null || !sec.getCodInstructor().equals(instructor.getCod())) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seccion no asignada a usted");
                return;
            }
            String res = util.ReportePDF.reporteCalificacionesSeccion(cod, instructor.getCod());
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        //Acciones CSV
        btnCsvNotas.addActionListener(e -> {
            String cod = txtCodSec.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de seccion");
                return;
            }
            Seccion sec = DataStore.buscarSeccion(cod);
            if (sec == null || !sec.getCodInstructor().equals(instructor.getCod())) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seccion no asignada a usted");
                return;
            }
            String res = util.ExportadorCSV.exportarNotasSeccion(cod, instructor.getCod());
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        btnCsvEst.addActionListener(e -> {
            String cod = txtCodEst.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de estudiante");
                return;
            }
            String res = util.ExportadorCSV.exportarHistorialEstudiante(cod);
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });
        return pnl;
    }

    //Cerrar sesion
    private void cerrarSesion() {
        detenerThreads(); AuthController.logout(); new LoginFrame().setVisible(true); dispose();
    }
}