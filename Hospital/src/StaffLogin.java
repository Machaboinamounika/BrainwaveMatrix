package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StaffLogin extends JFrame implements ActionListener {
    private JTextField empIdField, usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleDropdown;
    private JButton loginButton, cancelButton;
    private String featureAccessed; // Feature clicked on Dashboard

    public StaffLogin(String feature) {
        this.featureAccessed = feature;

        setTitle("Employee Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Employee ID:"), gbc);
        empIdField = new JTextField(15);
        gbc.gridx = 1;
        add(empIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Role:"), gbc);
        roleDropdown = new JComboBox<>(new String[]{"Select Role", "Admin", "Doctor", "Receptionist", "Pharmacist"});
        gbc.gridx = 1;
        add(roleDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String empId = empIdField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleDropdown.getSelectedItem();
            if (authenticateEmployee(empId, username, password, role)) {
                // First, check access rights
                if ((featureAccessed.equals("Scheduled Appointments") || featureAccessed.equals("Staff Management")) &&
                        !(role.equals("Doctor") || role.equals("Admin"))) {
                    JOptionPane.showMessageDialog(this, "Access Denied: You must be a Doctor or Admin to access this feature.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return; // Don't log or open the feature
                }

                // Authorized access → log and proceed
                logStaffLogin(empId, role, featureAccessed);
                JOptionPane.showMessageDialog(this, "Login Successful! Redirecting to " + featureAccessed);
                dispose();
                openFeaturePage(featureAccessed, role, username);

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials or Role!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    private boolean authenticateEmployee(String empId, String username, String password, String role) {
        try (Connection conn = new Conn().connection) {
            String query = "SELECT * FROM employees WHERE emp_id = ? AND username = ? AND password = ? AND role = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, empId);
                pstmt.setString(2, username);
                pstmt.setString(3, password);
                pstmt.setString(4, role);

                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void logStaffLogin(String empId, String role, String feature) {
        try (Connection conn = new Conn().connection) {
            String query = "INSERT INTO staff_logins (emp_id, role, login_time, date, feature_accessed) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, empId);
                pstmt.setString(2, role);
                pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // Current timestamp
                pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now())); // Manually get today's date
                pstmt.setString(5, feature); // Feature accessed
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openFeaturePage(String feature, String role, String username) {
        // Check role access for certain features
        if (feature.equals("Scheduled Appointments") || feature.equals("Staff Management")) {
            if (!(role.equals("Doctor") || role.equals("Admin"))) {
                JOptionPane.showMessageDialog(this, "Access Denied: You must be a Doctor or Admin to access this feature.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                return; // Prevent access to these features if role is not Doctor or Admin
            }
        }

        switch (feature) {
            case "Scheduled Appointments":
                new ScheduledAppointments();
                break;
            case "Electronic Health Records (EHR)":
                new EHRPage();
                break;
            case "Billing & Invoicing":
                new BillingPage();
                break;
            case "Inventory Management":
                new InventoryPage();
                break;
            case "Staff Management":
                new StaffManagementPage();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown Feature", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new StaffLogin("Test Feature");
    }
}
