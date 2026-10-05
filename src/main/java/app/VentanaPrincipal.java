package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VentanaPrincipal extends JFrame {

    private final DefaultTableModel modeloEstudiantes =
            new DefaultTableModel(new Object[]{"ID","Matricula","Nombre","Apellido","Correo"}, 0);
    private final JTable tablaEstudiantes = new JTable(modeloEstudiantes);
    private final JTextField txtIdEst = new JTextField();
    private final JTextField txtMatricula = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtCorreo = new JTextField();

    private final DefaultTableModel modeloInscripciones =
            new DefaultTableModel(new Object[]{"ID","Estudiante","Curso","Periodo"}, 0);
    private final JTable tablaInscripciones = new JTable(modeloInscripciones);
    private final JTextField txtIdIns = new JTextField();
    private final JComboBox<Item> cmbEstudiante = new JComboBox<>();
    private final JComboBox<Item> cmbCurso = new JComboBox<>();
    private final JTextField txtPeriodo = new JTextField();

    private final DefaultTableModel modeloJoin =
            new DefaultTableModel(new Object[]{"Matricula","Estudiante","Curso","Creditos","Periodo"}, 0);
    private final JTable tablaJoin = new JTable(modeloJoin);

    public VentanaPrincipal() {
        setTitle("Sistema de Control Escolar");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Estudiantes", crearPanelEstudiantes());
        tabs.addTab("Inscripciones", crearPanelInscripciones());
        tabs.addTab("Consulta JOIN", crearPanelJoin());
        add(tabs);

        cargarEstudiantes();
        cargarCombos();
        cargarInscripciones();
        cargarJoin();
    }

    private JPanel crearPanelEstudiantes() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        txtIdEst.setEditable(false);
        form.add(new JLabel("ID:")); form.add(txtIdEst);
        form.add(new JLabel("Matricula:")); form.add(txtMatricula);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Apellido:")); form.add(txtApellido);
        form.add(new JLabel("Correo:")); form.add(txtCorreo);

        JPanel botones = new JPanel();
        JButton bCrear = new JButton("Crear");
        JButton bActualizar = new JButton("Actualizar");
        JButton bEliminar = new JButton("Eliminar");
        JButton bLimpiar = new JButton("Limpiar");
        botones.add(bCrear); botones.add(bActualizar); botones.add(bEliminar); botones.add(bLimpiar);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);

        bCrear.addActionListener(e -> crearEstudiante());
        bActualizar.addActionListener(e -> actualizarEstudiante());
        bEliminar.addActionListener(e -> eliminarEstudiante());
        bLimpiar.addActionListener(e -> limpiarEstudiante());

        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            int r = tablaEstudiantes.getSelectedRow();
            if (r >= 0) {
                txtIdEst.setText(modeloEstudiantes.getValueAt(r, 0).toString());
                txtMatricula.setText(modeloEstudiantes.getValueAt(r, 1).toString());
                txtNombre.setText(modeloEstudiantes.getValueAt(r, 2).toString());
                txtApellido.setText(modeloEstudiantes.getValueAt(r, 3).toString());
                Object correo = modeloEstudiantes.getValueAt(r, 4);
                txtCorreo.setText(correo == null ? "" : correo.toString());
            }
        });

        return p;
    }

    private JPanel crearPanelInscripciones() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
        txtIdIns.setEditable(false);
        form.add(new JLabel("ID:")); form.add(txtIdIns);
        form.add(new JLabel("Estudiante:")); form.add(cmbEstudiante);
        form.add(new JLabel("Curso:")); form.add(cmbCurso);
        form.add(new JLabel("Periodo:")); form.add(txtPeriodo);

        JPanel botones = new JPanel();
        JButton bCrear = new JButton("Crear");
        JButton bActualizar = new JButton("Actualizar");
        JButton bEliminar = new JButton("Eliminar");
        JButton bRefrescar = new JButton("Refrescar");
        botones.add(bCrear); botones.add(bActualizar); botones.add(bEliminar); botones.add(bRefrescar);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaInscripciones), BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);

        bCrear.addActionListener(e -> crearInscripcion());
        bActualizar.addActionListener(e -> actualizarInscripcion());
        bEliminar.addActionListener(e -> eliminarInscripcion());
        bRefrescar.addActionListener(e -> refrescarTodo());

        tablaInscripciones.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tablaInscripciones.getSelectedRow();
            if (r >= 0) {
                Object id = modeloInscripciones.getValueAt(r, 0);
                if (id != null) {
                    cargarInscripcionSeleccionada(Integer.parseInt(id.toString()));
                }
            }
        });

        return p;
    }

    private JPanel crearPanelJoin() {
        JPanel p = new JPanel(new BorderLayout());
        JButton btn = new JButton("Actualizar consulta JOIN");
        btn.addActionListener(e -> cargarJoin());
        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaJoin), BorderLayout.CENTER);
        return p;
    }

    private void crearEstudiante() {
        if (txtMatricula.getText().isBlank() || txtNombre.getText().isBlank() || txtApellido.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Matricula, nombre y apellido son obligatorios.");
            return;
        }

        String sql = "INSERT INTO estudiantes(matricula,nombre,apellido,correo) VALUES(?,?,?,?)";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtMatricula.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtApellido.getText().trim());
            ps.setString(4, txtCorreo.getText().trim());
            ps.executeUpdate();
            limpiarEstudiante();
            refrescarTodo();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void actualizarEstudiante() {
        if (txtIdEst.getText().isBlank()) return;
        String sql = "UPDATE estudiantes SET matricula=?,nombre=?,apellido=?,correo=? WHERE id=?";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtMatricula.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtApellido.getText().trim());
            ps.setString(4, txtCorreo.getText().trim());
            ps.setInt(5, Integer.parseInt(txtIdEst.getText()));
            ps.executeUpdate();
            limpiarEstudiante();
            refrescarTodo();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void eliminarEstudiante() {
        if (txtIdEst.getText().isBlank()) return;
        if (JOptionPane.showConfirmDialog(this, "Eliminar estudiante?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM estudiantes WHERE id=?";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(txtIdEst.getText()));
            ps.executeUpdate();
            limpiarEstudiante();
            refrescarTodo();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void cargarEstudiantes() {
        modeloEstudiantes.setRowCount(0);
        String sql = "SELECT id,matricula,nombre,apellido,correo FROM estudiantes ORDER BY id";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloEstudiantes.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("matricula"), rs.getString("nombre"),
                        rs.getString("apellido"), rs.getString("correo")
                });
            }
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void crearInscripcion() {
        Item e = (Item) cmbEstudiante.getSelectedItem();
        Item curso = (Item) cmbCurso.getSelectedItem();
        if (e == null || curso == null || txtPeriodo.getText().isBlank()) return;

        String sql = "INSERT INTO inscripciones(id_estudiante,id_curso,periodo) VALUES(?,?,?)";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.id);
            ps.setInt(2, curso.id);
            ps.setString(3, txtPeriodo.getText().trim());
            ps.executeUpdate();
            limpiarInscripcion();
            refrescarTodo();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void actualizarInscripcion() {
        if (txtIdIns.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción para actualizar.");
            return;
        }
        Item e = (Item) cmbEstudiante.getSelectedItem();
        Item curso = (Item) cmbCurso.getSelectedItem();
        if (e == null || curso == null || txtPeriodo.getText().isBlank()) return;

        String sql = "UPDATE inscripciones SET id_estudiante=?,id_curso=?,periodo=? WHERE id_inscripcion=?";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.id);
            ps.setInt(2, curso.id);
            ps.setString(3, txtPeriodo.getText().trim());
            ps.setInt(4, Integer.parseInt(txtIdIns.getText()));
            ps.executeUpdate();
            limpiarInscripcion();
            refrescarTodo();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void eliminarInscripcion() {
        if (txtIdIns.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción para eliminar.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar inscripción?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM inscripciones WHERE id_inscripcion=?";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(txtIdIns.getText()));
            ps.executeUpdate();
            limpiarInscripcion();
            refrescarTodo();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void cargarInscripcionSeleccionada(int idInscripcion) {
        String sql = "SELECT id_inscripcion,id_estudiante,id_curso,periodo FROM inscripciones WHERE id_inscripcion=?";
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtIdIns.setText(String.valueOf(rs.getInt("id_inscripcion")));
                    seleccionarItemPorId(cmbEstudiante, rs.getInt("id_estudiante"));
                    seleccionarItemPorId(cmbCurso, rs.getInt("id_curso"));
                    txtPeriodo.setText(rs.getString("periodo"));
                }
            }
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void seleccionarItemPorId(JComboBox<Item> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Item item = combo.getItemAt(i);
            if (item != null && item.id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void cargarInscripciones() {
        modeloInscripciones.setRowCount(0);
        String sql = """
                SELECT i.id_inscripcion,
                       CONCAT(e.nombre,' ',e.apellido) AS estudiante,
                       c.nombre_curso,
                       i.periodo
                FROM inscripciones i
                JOIN estudiantes e ON e.id=i.id_estudiante
                JOIN cursos c ON c.id_curso=i.id_curso
                ORDER BY i.id_inscripcion
                """;
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloInscripciones.addRow(new Object[]{
                        rs.getInt("id_inscripcion"), rs.getString("estudiante"),
                        rs.getString("nombre_curso"), rs.getString("periodo")
                });
            }
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void cargarJoin() {
        modeloJoin.setRowCount(0);
        String sql = """
                SELECT e.matricula,
                       CONCAT(e.nombre,' ',e.apellido) AS estudiante,
                       c.nombre_curso,
                       c.creditos,
                       i.periodo
                FROM inscripciones i
                JOIN estudiantes e ON e.id = i.id_estudiante
                JOIN cursos c ON c.id_curso = i.id_curso
                ORDER BY i.id_inscripcion
                """;
        try (Connection c = ConexionDB.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloJoin.addRow(new Object[]{
                        rs.getString("matricula"), rs.getString("estudiante"),
                        rs.getString("nombre_curso"), rs.getInt("creditos"),
                        rs.getString("periodo")
                });
            }
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void cargarCombos() {
        cmbEstudiante.removeAllItems();
        cmbCurso.removeAllItems();

        try (Connection c = ConexionDB.obtenerConexion()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id,matricula,nombre,apellido FROM estudiantes ORDER BY id");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbEstudiante.addItem(new Item(rs.getInt("id"),
                            rs.getString("matricula") + " - " +
                                    rs.getString("nombre") + " " + rs.getString("apellido")));
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id_curso,nombre_curso FROM cursos ORDER BY id_curso");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbCurso.addItem(new Item(rs.getInt("id_curso"), rs.getString("nombre_curso")));
                }
            }
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void refrescarTodo() {
        cargarEstudiantes();
        cargarCombos();
        cargarInscripciones();
        cargarJoin();
    }

    private void limpiarEstudiante() {
        txtIdEst.setText("");
        txtMatricula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
        tablaEstudiantes.clearSelection();
    }

    private void limpiarInscripcion() {
        txtIdIns.setText("");
        txtPeriodo.setText("");
        tablaInscripciones.clearSelection();
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class Item {
        final int id;
        final String texto;

        Item(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }
}
