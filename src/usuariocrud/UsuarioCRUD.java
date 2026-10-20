/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package usuariocrud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsuarioCRUD extends JFrame {

    // Componentes de la interfaz
    private JTextField tituloField, precioField, stockField;
    private JComboBox<String> consolaCombo, generoCombo;
    private JButton guardarButton, eliminarButton, conectarButton;
    private JTable tablaJuegos;
    private DefaultTableModel modeloTabla;
    private Connection conexion;

    public UsuarioCRUD() {
        super("GameStore - Inventario de Videojuegos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Encabezado con estilo Gamer
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(108, 92, 231)); // Morado Gamer
        headerPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        JLabel tituloLabel = new JLabel("🎮 GAMESTORE - REGISTRO DE VIDEOJUEGOS");
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(tituloLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Panel Central (Formulario y Botones)
        JPanel centroPanel = new JPanel(new BorderLayout(10, 10));
        centroPanel.setBorder(new EmptyBorder(10, 25, 10, 25));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 12, 12));
        Font labelFont = new Font("SansSerif", Font.BOLD, 13);

        tituloField = new JTextField();
        precioField = new JTextField();
        stockField = new JTextField();

        String[] consolas = {"PlayStation 5", "Xbox Series X/S", "Nintendo Switch", "PC (Steam/Epic)", "PlayStation 4"};
        consolaCombo = new JComboBox<>(consolas);

        String[] generos = {"Acción / Aventura", "RPG / Mundo Abierto", "Shooter (FPS)", "Deportes / Carreras", "Terror / Survival", "Estrategia"};
        generoCombo = new JComboBox<>(generos);

        JLabel lblTitulo = new JLabel("Título del Videojuego:");
        lblTitulo.setFont(labelFont);
        formPanel.add(lblTitulo);
        formPanel.add(tituloField);

        JLabel lblConsola = new JLabel("Consola / Plataforma:");
        lblConsola.setFont(labelFont);
        formPanel.add(lblConsola);
        formPanel.add(consolaCombo);

        JLabel lblGenero = new JLabel("Género:");
        lblGenero.setFont(labelFont);
        formPanel.add(lblGenero);
        formPanel.add(generoCombo);

        JLabel lblPrecio = new JLabel("Precio (Q / $):");
        lblPrecio.setFont(labelFont);
        formPanel.add(lblPrecio);
        formPanel.add(precioField);

        JLabel lblStock = new JLabel("Cantidad en Stock:");
        lblStock.setFont(labelFont);
        formPanel.add(lblStock);
        formPanel.add(stockField);

        centroPanel.add(formPanel, BorderLayout.CENTER);

        // Panel de Botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        
        conectarButton = new JButton("Conectar a la BD");
        conectarButton.setBackground(new Color(45, 52, 54));
        conectarButton.setForeground(Color.WHITE);
        conectarButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        conectarButton.setFocusPainted(false);

        eliminarButton = new JButton("Eliminar Seleccionado");
        eliminarButton.setBackground(new Color(214, 48, 49)); // Rojo
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        eliminarButton.setFocusPainted(false);

        guardarButton = new JButton("Guardar Videojuego");
        guardarButton.setBackground(new Color(0, 184, 148)); // Verde neón
        guardarButton.setForeground(Color.WHITE);
        guardarButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        guardarButton.setFocusPainted(false);

        botonesPanel.add(conectarButton);
        botonesPanel.add(eliminarButton);
        botonesPanel.add(guardarButton);
        centroPanel.add(botonesPanel, BorderLayout.SOUTH);

        add(centroPanel, BorderLayout.CENTER);

        // 3. Panel Inferior (Tabla de Inventario)
        String[] columnas = {"ID", "Videojuego", "Consola", "Género", "Precio", "Stock"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaJuegos = new JTable(modeloTabla);
        tablaJuegos.setRowHeight(22);
        tablaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tablaJuegos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventario Actual (Haz clic en una fila para eliminarla)"));
        scrollPane.setPreferredSize(new Dimension(600, 180));
        add(scrollPane, BorderLayout.SOUTH);

        // Eventos de los botones
        conectarButton.addActionListener(e -> conectarABaseDeDatos());
        guardarButton.addActionListener(e -> guardarVideojuego());
        eliminarButton.addActionListener(e -> eliminarVideojuego());
    }

    private void conectarABaseDeDatos() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/prograii";
        String usuarioDB = "root";
        String contrasenaDB = ""; // Contraseña por defecto en XAMPP

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(jdbcUrl, usuarioDB, contrasenaDB);
            conectarButton.setText("✔ BD Conectada");
            conectarButton.setBackground(new Color(39, 174, 96));
            JOptionPane.showMessageDialog(this, "Conexión exitosa a la base de datos de GameStore");
            cargarInventarioEnTabla();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos");
        }
    }

    private void guardarVideojuego() {
        if (conexion == null) {
            JOptionPane.showMessageDialog(this, "Primero, conecta a la base de datos.");
            return;
        }

        String titulo = tituloField.getText().trim();
        String consola = consolaCombo.getSelectedItem().toString();
        String genero = generoCombo.getSelectedItem().toString();
        String precioTexto = precioField.getText().trim();
        String stockTexto = stockField.getText().trim();

        if (titulo.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto);
            int stock = Integer.parseInt(stockTexto);

            String consultaSQL = "INSERT INTO videojuegos (titulo, consola, genero, precio, stock) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(consultaSQL);
            ps.setString(1, titulo);
            ps.setString(2, consola);
            ps.setString(3, genero);
            ps.setDouble(4, precio);
            ps.setInt(5, stock);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Videojuego guardado correctamente en el inventario");
                tituloField.setText("");
                precioField.setText("");
                stockField.setText("");
                cargarInventarioEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el videojuego");
            }

            ps.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El Precio y el Stock deben ser valores numéricos válidos.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.");
        }
    }

    private void eliminarVideojuego() {
        if (conexion == null) {
            JOptionPane.showMessageDialog(this, "Primero, conecta a la base de datos.");
            return;
        }

        int filaSeleccionada = tablaJuegos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un videojuego de la tabla inferior para eliminarlo.");
            return;
        }

        // Obtener el ID (columna 0) y el Título (columna 1) de la fila seleccionada
        int idJuego = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String tituloJuego = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas eliminar \"" + tituloJuego + "\" del inventario?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM videojuegos WHERE id = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setInt(1, idJuego);

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Videojuego eliminado correctamente.");
                    cargarInventarioEnTabla(); // Actualizar la tabla tras borrar
                }
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar el videojuego.");
            }
        }
    }

    private void cargarInventarioEnTabla() {
        modeloTabla.setRowCount(0);
        try {
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM videojuegos ORDER BY id DESC");
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("consola"),
                    rs.getString("genero"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                });
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsuarioCRUD().setVisible(true));
    }
}