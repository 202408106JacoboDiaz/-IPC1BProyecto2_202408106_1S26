package view;

import controller.*;
import model.*;
import threads.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class EstudiantePanel extends JFrame {

    private Estudiante estudiante; private JTextArea areaThreads;
    private MonitorSesiones monitorSesiones; private SimuladorInscripciones simInscripciones;
    private GeneradorEstadisticas genEstadisticas;
    private Thread hiloSesiones, hiloInscripciones, hiloEstadisticas;

    public EstudiantePanel(Estudiante estudiante) {
        this.estudiante = estudiante;initComponents(); iniciarThreads();
    }

    private void initComponents() {
        setTitle("Sancarlista Academy - Estudiante: " + estudiante.getNomb());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); setSize(900, 650);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarSesion();
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cursos Disponibles", crearPanelCursosDisponibles());
        tabs.addTab("Mis Cursos", crearPanelMisCursos());
        tabs.addTab("Calificaciones", crearPanelCalificaciones());
        tabs.addTab("Reportes", crearPanelReportes());
        tabs.addTab("Mi Perfil", crearPanelPerfil());
        tabs.addTab("Monitor", crearPanelMonitor());

        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.setBackground(new Color(200, 50, 50)); btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false); btnLogout.addActionListener(e -> cerrarSesion());

        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pnlSur.add(btnLogout);

        add(tabs, BorderLayout.CENTER); add(pnlSur, BorderLayout.SOUTH);
    }

    //Panel de cursos disponibles
    private JPanel crearPanelCursosDisponibles() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Cod Seccion", "Curso", "Instructor", "Horario", "Semestre", "Inscritos"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); cargarSeccionesDisponibles(mdl); JScrollPane scroll = new JScrollPane(tbl);

        //Filtros
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JTextField txtFiltroSem = new JTextField(10); JButton btnFiltrar  = new JButton("Filtrar");
        JButton btnRecargar = new JButton("Recargar");

        pnlFiltros.add(new JLabel("Semestre:")); pnlFiltros.add(txtFiltroSem); pnlFiltros.add(btnFiltrar);
        pnlFiltros.add(btnRecargar);

        //BTNs accion
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnInscribir = new JButton("Inscribirse"); pnlBtns.add(btnInscribir);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        pnl.add(pnlFiltros, BorderLayout.NORTH); pnl.add(scroll, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout()); pnlSur.add(pnlBtns, BorderLayout.CENTER);
        pnlSur.add(lblMsg, BorderLayout.SOUTH);pnl.add(pnlSur, BorderLayout.SOUTH);

        btnRecargar.addActionListener(e -> cargarSeccionesDisponibles(mdl));

        btnFiltrar.addActionListener(e -> {
            mdl.setRowCount(0);
            Seccion[] disp = EstudianteController.getSeccionesDisponibles(estudiante.getCod());
            String sem = txtFiltroSem.getText().trim();
            for (int i = 0; i < disp.length; i++) {
                if (!sem.isEmpty() && !disp[i].getSemestre().contains(sem)) continue;
                Instructor ins = DataStore.buscarInstructor(disp[i].getCodInstructor());
                mdl.addRow(new Object[]{ disp[i].getCod(), disp[i].getCodCurso(),
                        ins != null ? ins.getNomb() : "N/A", disp[i].getHorario(), disp[i].getSemestre(),
                        disp[i].getTotalEstudiantes()
                });
            }
        });

        btnInscribir.addActionListener(e -> {
            int fila = tbl.getSelectedRow();
            if (fila < 0) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seleccione una seccion");
                return;
            }
            String codSec = (String) mdl.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Inscribirse en la seccion " + codSec + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = EstudianteController.inscribirSeccion(estudiante.getCod(), codSec);
                lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
                lblMsg.setText(res); cargarSeccionesDisponibles(mdl);
            }
        });
        return pnl;
    }

    private void cargarSeccionesDisponibles(DefaultTableModel mdl) {
        mdl.setRowCount(0);
        Seccion[] disp = EstudianteController.getSeccionesDisponibles(estudiante.getCod());
        for (int i = 0; i < disp.length; i++) {
            Instructor ins = DataStore.buscarInstructor(disp[i].getCodInstructor());
            mdl.addRow(new Object[]{disp[i].getCod(), disp[i].getCodCurso(), ins != null ? ins.getNomb() : "N/A",
                    disp[i].getHorario(), disp[i].getSemestre(), disp[i].getTotalEstudiantes()
            });
        }
    }

    //Panel de mis cursos
    private JPanel crearPanelMisCursos() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Cod Seccion", "Curso", "Instructor", "Horario", "Semestre", "Promedio", "Estado"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); cargarMisCursos(mdl); JScrollPane scroll = new JScrollPane(tbl);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnDesasignar = new JButton("Desasignar"); JButton btnRecargar = new JButton("Recargar");
        pnlBtns.add(btnDesasignar); pnlBtns.add(btnRecargar);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);

        pnl.add(scroll, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout()); pnlSur.add(pnlBtns, BorderLayout.CENTER);
        pnlSur.add(lblMsg,  BorderLayout.SOUTH); pnl.add(pnlSur, BorderLayout.SOUTH);

        btnRecargar.addActionListener(e -> cargarMisCursos(mdl));

        btnDesasignar.addActionListener(e -> {
            int fila = tbl.getSelectedRow();
            if (fila < 0) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Seleccione una seccion");
                return;
            }
            String codSec = (String) mdl.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desasignarse de la seccion " + codSec + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = EstudianteController.desasignarSeccion(estudiante.getCod(), codSec);
                lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
                lblMsg.setText(res); cargarMisCursos(mdl);
            }
        });

        return pnl;
    }

    private void cargarMisCursos(DefaultTableModel mdl) {
        mdl.setRowCount(0);
        Seccion[] inscritas = EstudianteController.getSeccionesInscritas(estudiante.getCod());
        for (int i = 0; i < inscritas.length; i++) {
            Instructor ins = DataStore.buscarInstructor(inscritas[i].getCodInstructor());
            double prom = EstudianteController.calcularPromedio(estudiante.getCod(), inscritas[i].getCod());
            String estado = prom >= 61 ? "Aprobado" : "Reprobado";
            mdl.addRow(new Object[]{inscritas[i].getCod(), inscritas[i].getCodCurso(),
                    ins != null ? ins.getNomb() : "N/A", inscritas[i].getHorario(), inscritas[i].getSemestre(),
                    String.format("%.2f", prom), estado
            });
        }
    }

    //Panel de calificaciones
    private JPanel crearPanelCalificaciones() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JTextField txtSec = new JTextField(10); JTextField txtSem = new JTextField(10);
        JButton btnPorSec = new JButton("Por Seccion"); JButton btnPorSem = new JButton("Por Semestre");

        pnlFiltros.add(new JLabel("Seccion:")); pnlFiltros.add(txtSec); pnlFiltros.add(btnPorSec);
        pnlFiltros.add(new JLabel("Semestre:")); pnlFiltros.add(txtSem); pnlFiltros.add(btnPorSem);

        String[] cols = {"Curso", "Seccion", "Etiqueta", "Ponderacion", "Nota", "Fecha", "Estado"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); JScrollPane scroll = new JScrollPane(tbl);

        JLabel lblProm = new JLabel("Promedio: --", SwingConstants.CENTER);
        lblProm.setFont(new Font("Arial", Font.BOLD, 14));

        pnl.add(pnlFiltros, BorderLayout.NORTH);pnl.add(scroll, BorderLayout.CENTER);
        pnl.add(lblProm, BorderLayout.SOUTH);

        btnPorSec.addActionListener(e -> {
            String codSec = txtSec.getText().trim();
            if (codSec.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese codigo de seccion");
                return;
            }
            mdl.setRowCount(0);
            Nota[] notas = EstudianteController.getNotasPorSeccion(estudiante.getCod(), codSec);
            for (int i = 0; i < notas.length; i++) {
                mdl.addRow(new Object[]{
                        notas[i].getCodCurso(), notas[i].getCodSeccion(), notas[i].getEtiqueta(),
                        notas[i].getPonderacion(), notas[i].getNota(), notas[i].getFecha(),
                        notas[i].esAprobado() ? "Aprobado" : "Reprobado"
                });
            }
            double prom = EstudianteController.calcularPromedio(estudiante.getCod(), codSec);
            lblProm.setText(String.format("Promedio seccion: %.2f | %s",
                    prom, prom >= 61 ? "APROBADO" : "REPROBADO"));
        });

        btnPorSem.addActionListener(e -> {
            String sem = txtSem.getText().trim();
            if (sem.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese semestre");
                return;
            }
            mdl.setRowCount(0);
            Nota[] notas = EstudianteController
                    .getNotasPorSemestre(estudiante.getCod(), sem);
            for (int i = 0; i < notas.length; i++) {
                mdl.addRow(new Object[]{notas[i].getCodCurso(), notas[i].getCodSeccion(),
                        notas[i].getEtiqueta(), notas[i].getPonderacion(), notas[i].getNota(),
                        notas[i].getFecha(), notas[i].esAprobado() ? "Aprobado" : "Reprobado"});
            }
            double prom = EstudianteController.calcularPromedioSemestral(estudiante.getCod(), sem);
            lblProm.setText(String.format("Promedio semestral: %.2f | %s", prom,
                    prom >= 61 ? "APROBADO" : "REPROBADO"));
        });
        return pnl;
    }

    //Panel de perfil
    private JPanel crearPanelPerfil() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtNomb   = new JTextField(estudiante.getNomb());
        JTextField txtCumple = new JTextField(estudiante.getCumple());
        JTextField txtGenero = new JTextField(estudiante.getGenero());
        JPasswordField txtContraActual = new JPasswordField();
        JPasswordField txtNuevaContra  = new JPasswordField();

        gbc.gridx = 0; gbc.gridy = 0; pnl.add(new JLabel("Codigo:"), gbc);
        gbc.gridx = 1; pnl.add(new JLabel(estudiante.getCod()), gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnl.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; pnl.add(txtNomb, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnl.add(new JLabel("Cumpleaños:"), gbc);
        gbc.gridx = 1; pnl.add(txtCumple, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnl.add(new JLabel("Genero:"), gbc);
        gbc.gridx = 1; pnl.add(txtGenero, gbc);

        gbc.gridx = 0; gbc.gridy = 4; pnl.add(new JLabel("Contra actual:"), gbc);
        gbc.gridx = 1; pnl.add(txtContraActual, gbc);

        gbc.gridx = 0; gbc.gridy = 5; pnl.add(new JLabel("Nueva contra:"), gbc);
        gbc.gridx = 1; pnl.add(txtNuevaContra, gbc);

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setBackground(new Color(0, 102, 204));
        btnGuardar.setForeground(Color.WHITE); btnGuardar.setFocusPainted(false);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        pnl.add(btnGuardar, gbc);

        gbc.gridy = 7; pnl.add(lblMsg, gbc);

        btnGuardar.addActionListener(e -> {
            String res = EstudianteController.actualizarPerfil(estudiante.getCod(), txtNomb.getText().trim(),
                    txtCumple.getText().trim(), txtGenero.getText().trim(),
                    new String(txtContraActual.getPassword()).trim(),
                    new String(txtNuevaContra.getPassword()).trim());
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });
        return pnl;
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
        pnl.add(scroll, BorderLayout.CENTER);
        pnl.add(btnLimpiar, BorderLayout.SOUTH);
        return pnl;
    }

    //Threads hilos
    private void iniciarThreads() {
        monitorSesiones  = new MonitorSesiones(areaThreads);
        simInscripciones = new SimuladorInscripciones(areaThreads);
        genEstadisticas  = new GeneradorEstadisticas(areaThreads);

        hiloSesiones = new Thread(monitorSesiones); hiloInscripciones = new Thread(simInscripciones);
        hiloEstadisticas = new Thread(genEstadisticas);

        hiloSesiones.setDaemon(true); hiloInscripciones.setDaemon(true); hiloEstadisticas.setDaemon(true);

        hiloSesiones.start(); hiloInscripciones.start(); hiloEstadisticas.start();
    }

    private void detenerThreads() {
        monitorSesiones.detener(); simInscripciones.detener(); genEstadisticas.detener();
    }

    //Panel reportes
    private JPanel crearPanelReportes() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Informacion del estudiante
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInfo.add(new JLabel("Estudiante: " + estudiante.getNomb() + " | Codigo: "
                + estudiante.getCod()));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Mi informacion"));

        //Parametro seccion
        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtCodSec = new JTextField(); pnlForm.add(new JLabel("Cod Seccion (para reporte):"));
        pnlForm.add(txtCodSec); pnlForm.setBorder(BorderFactory.createTitledBorder("Parametros"));

        //BTNs PDF
        JPanel pnlPDF = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlPDF.setBorder(BorderFactory.createTitledBorder("Reportes PDF"));
        JButton btnHistorial = new JButton("Mi Historial Academico (PDF)");
        JButton btnCalSec = new JButton("Mis Calificaciones por Seccion (PDF)");
        pnlPDF.add(btnHistorial);
        pnlPDF.add(btnCalSec);

        //BTNs CSV
        JPanel pnlCSV = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlCSV.setBorder(BorderFactory.createTitledBorder("Exportar CSV"));
        JButton btnCsvHistorial = new JButton("Exportar Historial (CSV)");
        JButton btnCsvNotas = new JButton("Exportar Notas por Seccion (CSV)");
        pnlCSV.add(btnCsvHistorial); pnlCSV.add(btnCsvNotas);

        //Mensaje
        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Arial", Font.BOLD, 12));

        //Layout
        JPanel pnlNorte = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlNorte.add(pnlInfo);
        pnlNorte.add(pnlForm);

        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCentro.add(pnlPDF); pnlCentro.add(pnlCSV);

        pnl.add(pnlNorte, BorderLayout.NORTH); pnl.add(pnlCentro, BorderLayout.CENTER);
        pnl.add(lblMsg, BorderLayout.SOUTH);

        //Acciones PDF
        btnHistorial.addActionListener(e -> {
            String res = util.ReportePDF.reporteIndividualEstudiante(estudiante.getCod());
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        btnCalSec.addActionListener(e -> {
            String cod = txtCodSec.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de seccion");
                return;
            }
            //Validar inscripcion del estudiante
            Seccion sec = DataStore.buscarSeccion(cod);
            if (sec == null || !sec.estaInscrito(estudiante.getCod())) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: No esta inscrito en esta seccion");
                return;
            }
            String res = util.ReportePDF.reporteCalificacionesSeccion(cod, estudiante.getCod());
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        //Acciones CSV
        btnCsvHistorial.addActionListener(e -> {
            String res = util.ExportadorCSV.exportarHistorialEstudiante(estudiante.getCod());
            lblMsg.setForeground(res.startsWith("OK") ? new Color(0,130,0) : Color.RED);
            lblMsg.setText(res);
        });

        btnCsvNotas.addActionListener(e -> {
            String cod = txtCodSec.getText().trim();
            if (cod.isEmpty()) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: Ingrese codigo de seccion");
                return;
            }
            Seccion sec = DataStore.buscarSeccion(cod);
            if (sec == null || !sec.estaInscrito(estudiante.getCod())) {
                lblMsg.setForeground(Color.RED); lblMsg.setText("ERROR: No esta inscrito en esa seccion");
                return;
            }
            String res = util.ExportadorCSV.exportarNotasSeccion(cod, estudiante.getCod());
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