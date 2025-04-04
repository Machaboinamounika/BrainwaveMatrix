package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class PatientRegistration extends JFrame implements ActionListener {
    JTextField nameField, ageField, addressField, phoneField, emailField, emergencyContactField, insuranceField, usernameField;
    JPasswordField passwordField;
    JComboBox<String> genderBox, bloodGroupBox, diseaseBox;
    JButton registerButton, backButton;
    JTextField otherDiseaseField;

    PatientRegistration() {
        setTitle("Patient Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setLayout(null);

        // Background Image
        JLabel background = new JLabel(new ImageIcon("src/icon/bg.png"));
        background.setBounds(0, 0, 1366, 768);
        add(background);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 2, 20, 20)); // Grid Layout (7 rows, 2 columns)
        formPanel.setBounds(300, 100, 800, 500);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        background.add(formPanel);

        // Fields
        nameField = new JTextField();
        ageField = new JTextField();
        genderBox = new JComboBox<>(new String[]{"Select", "Male", "Female", "Other"});
        bloodGroupBox = new JComboBox<>(new String[]{"Select", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});

        diseaseBox = new JComboBox<>(new String[]{"Select", "Diabetes", "Hypertension", "Asthma", "Heart Disease", "COVID-19", "Cancer", "Other"});
        otherDiseaseField = new JTextField();
        otherDiseaseField.setVisible(false);
        diseaseBox.addActionListener(e -> otherDiseaseField.setVisible(diseaseBox.getSelectedItem().equals("Other")));

        addressField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        emergencyContactField = new JTextField();
        insuranceField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // Buttons
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        backButton = new JButton("Back");
        backButton.addActionListener(this);

        // Adding Components in 2-column format
        formPanel.add(createLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(createLabel("Age:"));
        formPanel.add(ageField);

        formPanel.add(createLabel("Gender:"));
        formPanel.add(genderBox);

        formPanel.add(createLabel("Blood Group:"));
        formPanel.add(bloodGroupBox);

        formPanel.add(createLabel("Disease:"));
        formPanel.add(diseaseBox);

        formPanel.add(createLabel("Other Disease:"));
        formPanel.add(otherDiseaseField);

        formPanel.add(createLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(createLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(createLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(createLabel("Emergency Contact:"));
        formPanel.add(emergencyContactField);

        formPanel.add(createLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(createLabel("Password:"));
        formPanel.add(passwordField);

        formPanel.add(createLabel("Insurance Provider:"));
        formPanel.add(insuranceField);

        // Buttons row
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        formPanel.add(new JLabel()); // Empty cell to align
        formPanel.add(buttonPanel);

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.PLAIN, 16));
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String name = nameField.getText();
            String age = ageField.getText();
            String gender = (String) genderBox.getSelectedItem();
            String bloodGroup = (String) bloodGroupBox.getSelectedItem();
            String disease = diseaseBox.getSelectedItem().equals("Other") ? otherDiseaseField.getText() : (String) diseaseBox.getSelectedItem();
            String address = addressField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String emergencyContact = emergencyContactField.getText();
            String insuranceProvider = insuranceField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_mngt_sys?useSSL=false&serverTimezone=UTC", "root", "root");
                String query = "INSERT INTO patients(name, age, gender, blood_group, disease, address, phone, email, emergency_contact, insurance, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, age);
                pstmt.setString(3, gender);
                pstmt.setString(4, bloodGroup);
                pstmt.setString(5, disease);
                pstmt.setString(6, address);
                pstmt.setString(7, phone);
                pstmt.setString(8, email);
                pstmt.setString(9, emergencyContact);
                pstmt.setString(10, insuranceProvider);
                pstmt.setString(11, username);
                pstmt.setString(12, password);
                pstmt.executeUpdate();
                // Insert into login table
                String loginQuery = "INSERT INTO login (ID, password) VALUES (?, ?)";
                PreparedStatement loginStmt = conn.prepareStatement(loginQuery);
                loginStmt.setString(1, username);  // Store username as ID
                loginStmt.setString(2, password);  // Store password as PW
                loginStmt.executeUpdate();
                loginStmt.close();
                JOptionPane.showMessageDialog(null, "Registration Successful!");
                setVisible(false);
                new LoginPage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == backButton) {
            setVisible(false);
            new HomePage();
        }
    }

    public static void main(String[] args) {
        new PatientRegistration();
    }
}
