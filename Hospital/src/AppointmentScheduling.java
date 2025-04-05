package hospital.management.system;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;

public class AppointmentScheduling extends JFrame implements ActionListener {
    private JTextField patientIdField, patientNameField;
    private JComboBox<String> doctorDropdown, timeDropdown;
    private JDateChooser dateChooser;
    private JTextArea remarksArea;
    private JButton submitBtn, cancelBtn;
    private String loggedInUser;
    private List<Date> bookedDates = new ArrayList<>();
    private List<String> bookedTimes = new ArrayList<>();

    public AppointmentScheduling(String username) {
        this.loggedInUser = username;
        setTitle("Appointment Scheduling");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // **Main Panel**
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255, 180)); // Semi-transparent background
        panel.setPreferredSize(new Dimension(600, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = 1;

        JLabel heading = new JLabel("Schedule an Appointment");
        heading.setFont(new Font("Tahoma", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(heading, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        JLabel lblPatientId = new JLabel("Patient ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPatientId, gbc);
        patientIdField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(patientIdField, gbc);
        patientIdField.setEditable(false);

        JLabel lblPatientName = new JLabel("Patient Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblPatientName, gbc);
        patientNameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(patientNameField, gbc);
        patientNameField.setEditable(false);
        fetchPatientDetails(username);

        JLabel lblDoctor = new JLabel("Doctor:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblDoctor, gbc);
        String[] doctors = {"Select", "Dr. Smith", "Dr. Johnson", "Dr. Brown", "Dr. Williams"};
        doctorDropdown = new JComboBox<>(doctors);
        gbc.gridx = 1;
        panel.add(doctorDropdown, gbc);

        JLabel lblDate = new JLabel("Date:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblDate, gbc);
        dateChooser = new JDateChooser();
        dateChooser.setMinSelectableDate(new Date());
        gbc.gridx = 1;
        panel.add(dateChooser, gbc);

        JLabel lblTime = new JLabel("Time:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblTime, gbc);
        timeDropdown = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(timeDropdown, gbc);

        JLabel lblRemarks = new JLabel("Remarks:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblRemarks, gbc);
        remarksArea = new JTextArea(3, 20);
        remarksArea.setLineWrap(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        gbc.gridx = 1;
        panel.add(remarksScroll, gbc);

        JPanel buttonPanel = new JPanel();
        submitBtn = new JButton("Submit");
        cancelBtn = new JButton("Cancel");
        submitBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);
        doctorDropdown.addActionListener(e -> fetchBookedDatesAndTimes());
        dateChooser.getJCalendar().getDayChooser().addPropertyChangeListener("day", evt -> updateAvailableTimes());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchPatientDetails(String username) {
        try (Connection conn = new Conn().connection) {
            String query = "SELECT ID, name FROM patients WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        patientIdField.setText(rs.getString("ID"));
                        patientNameField.setText(rs.getString("name"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBookedDatesAndTimes() {
        bookedDates.clear();
        bookedTimes.clear();

        try (Connection conn = new Conn().connection) {
            String query = "SELECT appointment_date, time FROM appointments WHERE doctor = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, (String) doctorDropdown.getSelectedItem());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookedDates.add(rs.getDate("appointment_date"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAvailableTimes() {
        timeDropdown.removeAllItems();
        String[] allTimeSlots = {"10:00 AM", "11:00 AM", "12:00 PM", "2:00 PM", "3:00 PM", "4:00 PM"};

        if (dateChooser.getDate() == null) return;

        try (Connection conn = new Conn().connection) {
            String query = "SELECT time FROM appointments WHERE doctor = ? AND appointment_date = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, (String) doctorDropdown.getSelectedItem());
                pstmt.setDate(2, new java.sql.Date(dateChooser.getDate().getTime()));
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookedTimes.add(rs.getString("time"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String time : allTimeSlots) {
            if (!bookedTimes.contains(time)) {
                timeDropdown.addItem(time);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            String patientId = patientIdField.getText();
            String patientName = patientNameField.getText();
            String doctor = (String) doctorDropdown.getSelectedItem();
            java.sql.Date appointmentDate = new java.sql.Date(dateChooser.getDate().getTime());
            String time = (String) timeDropdown.getSelectedItem();
            String remarks = remarksArea.getText();

            try (Connection conn = new Conn().connection) {
                String insertQuery = "INSERT INTO appointments (patient_id, patient_name, doctor, appointment_date, time, remarks) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setString(1, patientId);
                    pstmt.setString(2, patientName);
                    pstmt.setString(3, doctor);
                    pstmt.setDate(4, appointmentDate);
                    pstmt.setString(5, time);
                    pstmt.setString(6, remarks);
                    pstmt.executeUpdate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "Appointment scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new HomePage();
        } else if (e.getSource() == cancelBtn) {
            dispose();
            new HomePage();
        }
    }

    public static void main(String[] args) {
        new AppointmentScheduling("TestUser");
    }
}
