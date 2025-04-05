package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EHRPage extends JFrame implements ActionListener {
    private JTextField patientIdField;
    private JTextArea diagnosisArea, treatmentArea, medicationArea, notesArea;
    private JButton saveButton, searchButton, backButton;
    private JPanel rightPanel;

    public EHRPage() {
        setTitle("Electronic Health Records (EHR)");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel headingLabel = new JLabel("Electronic Health Records", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headingLabel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        leftPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        patientIdField = new JTextField(15);
        leftPanel.add(patientIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Diagnosis:"), gbc);
        gbc.gridx = 1;
        diagnosisArea = new JTextArea(3, 15);
        leftPanel.add(new JScrollPane(diagnosisArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Treatment:"), gbc);
        gbc.gridx = 1;
        treatmentArea = new JTextArea(3, 15);
        leftPanel.add(new JScrollPane(treatmentArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Medication:"), gbc);
        gbc.gridx = 1;
        medicationArea = new JTextArea(3, 15);
        leftPanel.add(new JScrollPane(medicationArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 15);
        leftPanel.add(new JScrollPane(notesArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));

        saveButton = new JButton("Save EHR");
        searchButton = new JButton("Search EHR");
        backButton = new JButton("Back");

        saveButton.addActionListener(this);
        searchButton.addActionListener(this);
        backButton.addActionListener(this);

        buttonPanel.add(saveButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(backButton);
        leftPanel.add(buttonPanel, gbc);

        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(4, 1, 10, 10));
        updateRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveEHRRecord();
        } else if (e.getSource() == searchButton) {
            searchEHRRecord();
        } else if (e.getSource() == backButton) {
            dispose();
            new DashboardPage();
        }
    }

    private void updateRightPanel() {
        rightPanel.removeAll();
        rightPanel.add(createCard("Patient Profile", "No patient selected"));
        rightPanel.add(createCard("Appointments", "No appointments available"));
        rightPanel.add(createCard("Billing", "No billing records found"));
        rightPanel.add(createCard("EHR Records", "No EHR records found"));
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private JPanel createCard(String title, String content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createTitledBorder(title));
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        card.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return card;
    }

    private void saveEHRRecord() {
        String patientID = patientIdField.getText();
        String diagnosis = diagnosisArea.getText();
        String treatment = treatmentArea.getText();
        String medication = medicationArea.getText();
        String notes = notesArea.getText();

        if (patientID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Patient ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {

            // First check if patient ID exists in appointments table
            String checkQuery = "SELECT * FROM appointments WHERE patient_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, patientID);
            ResultSet checkRs = checkStmt.executeQuery();

            if (!checkRs.next()) {
                JOptionPane.showMessageDialog(this, "Error: Patient ID doesn't exist in the Appointments table!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save EHR if patient exists in appointments
            String query = "INSERT INTO ehr (patient_id, diagnosis, treatment, medication, notes) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, patientID);
            pstmt.setString(2, diagnosis);
            pstmt.setString(3, treatment);
            pstmt.setString(4, medication);
            pstmt.setString(5, notes);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "EHR Record Saved Successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void searchEHRRecord() {
        String patientID = patientIdField.getText();
        if (patientID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Patient ID to search!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {

            String profileQuery = "SELECT * FROM patients WHERE id = ?";
            PreparedStatement profileStmt = conn.prepareStatement(profileQuery);
            profileStmt.setString(1, patientID);
            ResultSet profileRs = profileStmt.executeQuery();

            String profile = "No patient found";
            if (!profileRs.next()) {
                JOptionPane.showMessageDialog(this, "Patient ID doesn't exist in the patients table!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                profile = "Name: " + profileRs.getString("name") + "\n" +
                        "Age: " + profileRs.getInt("age") + "\n" +
                        "Gender: " + profileRs.getString("gender") + "\n" +
                        "Blood Group: " + profileRs.getString("blood_group") + "\n" +
                        "Disease: " + profileRs.getString("disease") + "\n" +
                        "Address: " + profileRs.getString("address") + "\n" +
                        "Phone: " + profileRs.getString("phone") + "\n" +
                        "Email: " + profileRs.getString("email") + "\n" +
                        "Emergency Contact: " + profileRs.getString("emergency_contact") + "\n" +
                        "Insurance: " + profileRs.getString("insurance");
            }

            String appointmentsQuery = "SELECT doctor, appointment_date, time, status, remarks FROM appointments WHERE patient_id = ?";
            PreparedStatement appointmentsStmt = conn.prepareStatement(appointmentsQuery);
            appointmentsStmt.setString(1, patientID);
            ResultSet appointmentsRs = appointmentsStmt.executeQuery();
            StringBuilder appointments = new StringBuilder();
            while (appointmentsRs.next()) {
                appointments.append("Doctor: ").append(appointmentsRs.getString("doctor"))
                        .append(" | Date: ").append(appointmentsRs.getString("appointment_date"))
                        .append(" | Time: ").append(appointmentsRs.getString("time"))
                        .append(" | Status: ").append(appointmentsRs.getString("status"))
                        .append(" | Remarks: ").append(appointmentsRs.getString("remarks"))
                        .append("\n");
            }
            if (appointments.length() == 0) {
                appointments.append("No appointments found.");
            }

            String billingQuery = "SELECT * FROM bills WHERE patient_id = ?";
            PreparedStatement billingStmt = conn.prepareStatement(billingQuery);
            billingStmt.setString(1, patientID);
            ResultSet billingRs = billingStmt.executeQuery();
            StringBuilder billing = new StringBuilder();
            while (billingRs.next()) {
                billing.append("Date: ").append(billingRs.getString("date"))
                        .append(" | Amount: ").append(billingRs.getString("amount"))
                        .append(" | Invoice ID: ").append(billingRs.getString("invoiceID"))
                        .append(" | Paid Amount: ").append(billingRs.getString("paid_amount"))
                        .append(" | Payment Type: ").append(billingRs.getString("payment_type"))
                        .append("\n");
            }
            if (billing.length() == 0) {
                billing.append("No billing records found.");
            }

            String ehrQuery = "SELECT diagnosis, treatment, medication, notes FROM ehr WHERE patient_id = ?";
            PreparedStatement ehrStmt = conn.prepareStatement(ehrQuery);
            ehrStmt.setString(1, patientID);
            ResultSet ehrRs = ehrStmt.executeQuery();
            StringBuilder ehrRecords = new StringBuilder();
            while (ehrRs.next()) {
                ehrRecords.append("Diagnosis: ").append(ehrRs.getString("diagnosis"))
                        .append("\nTreatment: ").append(ehrRs.getString("treatment"))
                        .append("\nMedication: ").append(ehrRs.getString("medication"))
                        .append("\nNotes: ").append(ehrRs.getString("notes"))
                        .append("\n-------------------------\n");
            }
            if (ehrRecords.length() == 0) {
                ehrRecords.append("No EHR records found.");
            }

            rightPanel.removeAll();
            rightPanel.add(createCard("Patient Profile", profile));
            rightPanel.add(createCard("Appointments", appointments.toString()));
            rightPanel.add(createCard("Billing", billing.toString()));
            rightPanel.add(createCard("EHR Records", ehrRecords.toString()));
            rightPanel.revalidate();
            rightPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EHRPage();
    }
}
