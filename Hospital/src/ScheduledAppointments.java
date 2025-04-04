package hospital.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ScheduledAppointments extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ScheduledAppointments() {
        setTitle("Scheduled Appointments");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        // Heading Label
        JLabel headingLabel = new JLabel("Scheduled Appointments", JLabel.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bold, Larger Font
        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding

        // **Back Button (Top-right corner)**
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close current window
                new DashboardPage(); // Open DashboardPage
            }
        });
        JPanel backBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backBtnPanel.add(backBtn);

        // Add heading and back button panel to the top panel
        topPanel.add(headingLabel, BorderLayout.CENTER);
        topPanel.add(backBtnPanel, BorderLayout.EAST);

        // Add topPanel to the North of the frame
        add(topPanel, BorderLayout.NORTH);

        // Table Columns
        String[] columns = {"Serial No.", "Patient ID", "Patient Name", "Doctor", "Date", "Time", "Status", "Action"};

        // Table Model
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7; // Only Status & Action columns are editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(30); // Increased row height for better visibility
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Larger font
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14)); // Bold headers

        // Set custom renderers and editors
        table.getColumnModel().getColumn(6).setCellRenderer(new ComboBoxRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ComboBoxEditor());
        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load Data from Database
        loadAppointments();

        setVisible(true);
    }

    private void loadAppointments() {
        model.setRowCount(0); // Clear table before reloading
        try (Connection conn = new Conn().connection) {
            String query = "SELECT id, patient_id, patient_name, doctor, appointment_date, time, status FROM appointments";
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                int serialNumber = 1;
                while (rs.next()) {
                    String status = rs.getString("status");
                    Vector<Object> row = new Vector<>();
                    row.add(serialNumber++); // Serial Number
                    row.add(rs.getInt("patient_id"));
                    row.add(rs.getString("patient_name"));
                    row.add(rs.getString("doctor"));
                    row.add(rs.getString("appointment_date"));
                    row.add(rs.getString("time"));
                    row.add(status != null ? status : "Pending"); // Default to Pending if NULL
                    row.add("Update");

                    model.addRow(row);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Custom Renderer for ComboBox
    class ComboBoxRenderer extends JComboBox<String> implements TableCellRenderer {
        public ComboBoxRenderer() {
            super(new String[]{"Pending", "Completed"});
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelectedItem(value);
            setEnabled(!"Completed".equals(value)); // Disable dropdown if status is Completed
            return this;
        }
    }

    // Custom Editor for ComboBox
    class ComboBoxEditor extends DefaultCellEditor {
        JComboBox<String> comboBox;

        public ComboBoxEditor() {
            super(new JComboBox<>(new String[]{"Pending", "Completed"}));
            comboBox = (JComboBox<String>) getComponent();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            comboBox.setSelectedItem(value);
            comboBox.setEnabled(!"Completed".equals(value)); // Disable if Completed
            return comboBox;
        }

        @Override
        public Object getCellEditorValue() {
            return comboBox.getSelectedItem();
        }
    }

    // Custom Renderer for Button
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Update");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Custom Editor for Button with ActionListener
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton("Update");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newStatus = (String) table.getValueAt(selectedRow, 6);
                    int patientId = (int) table.getValueAt(selectedRow, 1); // Get patient_id

                    if (newStatus.equals("Completed")) {
                        button.setEnabled(false); // Disable button after marking as completed
                        table.setValueAt("Completed", selectedRow, 6);
                        updateDatabaseStatus(patientId, "Completed");
                        JOptionPane.showMessageDialog(null, "Appointment for Patient " + patientId + " marked as Completed.");
                    } else {
                        updateDatabaseStatus(patientId, "Pending");
                        JOptionPane.showMessageDialog(null, "Appointment status updated for Patient " + patientId);
                    }

                    // Reload table to reflect disabled dropdown for Completed status
                    loadAppointments();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }
    }

    // Method to update the status in the database
    private void updateDatabaseStatus(int patientId, String status) {
        try (Connection conn = new Conn().connection) {
            String query = "UPDATE appointments SET status = ? WHERE patient_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, patientId);
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ScheduledAppointments();
    }
}
