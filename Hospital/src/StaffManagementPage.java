package hospital.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StaffManagementPage extends JFrame {
    private JPanel rightPanel;
    private JTable table;
    private DefaultTableModel tableModel;

    public StaffManagementPage() {
        setTitle("Staff Management Portal");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // LEFT PANEL (Buttons)
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton btnStaffDetails = new JButton("Staff Details");
        JButton btnLoginDetails = new JButton("Login Details");

        btnStaffDetails.setFont(new Font("Arial", Font.BOLD, 14));
        btnLoginDetails.setFont(new Font("Arial", Font.BOLD, 14));

        btnStaffDetails.setPreferredSize(new Dimension(180, 40));
        btnLoginDetails.setPreferredSize(new Dimension(180, 40));

        leftPanel.add(btnStaffDetails);
        leftPanel.add(btnLoginDetails);

        // RIGHT PANEL (Default: Image)
        rightPanel = new JPanel(new BorderLayout());
        displayImage();  // Initially show the image

        // Add Panels to Frame
        add(leftPanel);
        add(rightPanel);

        // Button Actions
        btnStaffDetails.addActionListener(e -> fetchStaffDetails());
        btnLoginDetails.addActionListener(e -> fetchLoginDetails());

        setVisible(true);
    }

    // Function to Display Default Image
    private void displayImage() {
        rightPanel.removeAll();
        ImageIcon icon = new ImageIcon("src/icon/staff.jpeg"); // Change path if needed
        Image img = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        rightPanel.add(imageLabel, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // Function to Fetch Staff Details
    private void fetchStaffDetails() {
        rightPanel.removeAll();

        // Add Heading
        JLabel heading = new JLabel("Staff Details", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        rightPanel.add(heading, BorderLayout.NORTH);

        // Table Model Setup
        String[] columns = {"Emp ID", "Name", "Role", "Department", "Contact", "Shift"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 40));
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {
            String query = "SELECT emp_id, name, role, department, contact, shift FROM staff_details";

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("emp_id"),
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getString("department"),
                            rs.getString("contact"),
                            rs.getString("shift")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
        }

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // Function to Fetch Login Details
    private void fetchLoginDetails() {
        rightPanel.removeAll();

        // Add Heading
        JLabel heading = new JLabel("Login Details", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        rightPanel.add(heading, BorderLayout.NORTH);

        // Table Model Setup
        String[] columns = {"Emp ID", "Role", "Login Time", "Feature Accessed"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 40));

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {
            String query = "SELECT emp_id, role, login_time, feature_accessed FROM staff_logins";

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("emp_id"),
                            rs.getString("role"),
                            rs.getString("login_time"),
                            rs.getString("feature_accessed")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
        }

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // Function to Track Logins (if needed)
    public static void trackLogin(String empId, String role) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {
            String query = "INSERT INTO staff_logins (emp_id, role, login_time) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, empId);
                pstmt.setString(2, role);
                pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StaffManagementPage();
    }
}
