package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class BillingPage extends JFrame implements ActionListener {
    private JTextField patientIdField, paidAmountField;
    private JComboBox<String> serviceComboBox, paymentTypeComboBox;
    private JTextField costField;
    private JButton addServiceButton, generateBillButton, saveButton, backButton;
    private JLabel billDisplayLabel;
    private double totalAmount = 0, paidAmount = 0;
    private DecimalFormat df = new DecimalFormat("0.00");
    // Generate a unique invoice ID (random 6-digit number)
    String invoiceID = String.format("%06d", new Random().nextInt(999999));

    // List to store added services and costs
    private ArrayList<String> serviceList = new ArrayList<>();
    private ArrayList<Double> costList = new ArrayList<>();

    public BillingPage() {
        setTitle("Billing Page");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Panel (Billing Form)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add Heading for Left Panel
        JLabel headingLabel = new JLabel("Billing and Invoicing");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Spans both columns
        leftPanel.add(headingLabel, gbc);

        // Form Fields (Patient ID, Service, Cost, Paid Amount, Payment Type)
        gbc.gridwidth = 1;  // Reset gridwidth to default
        gbc.gridy++;
        leftPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        patientIdField = new JTextField(15);
        leftPanel.add(patientIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Service:"), gbc);
        gbc.gridx = 1;
        String[] services = {"Select", "Consultation", "Tests", "Room Charges", "Surgery", "Medication"};
        serviceComboBox = new JComboBox<>(services);
        leftPanel.add(serviceComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        costField = new JTextField(15);
        leftPanel.add(costField, gbc);

        // Paid Amount field
        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Paid Amount:"), gbc);
        gbc.gridx = 1;
        paidAmountField = new JTextField(15);
        leftPanel.add(paidAmountField, gbc);

        // Payment Type combo box
        gbc.gridx = 0;
        gbc.gridy++;
        leftPanel.add(new JLabel("Payment Type:"), gbc);
        gbc.gridx = 1;
        String[] paymentTypes = {"Select", "UPI", "Online", "Cash"};
        paymentTypeComboBox = new JComboBox<>(paymentTypes);
        leftPanel.add(paymentTypeComboBox, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        addServiceButton = new JButton("Add Service");
        addServiceButton.addActionListener(this);
        buttonPanel.add(addServiceButton);

        generateBillButton = new JButton("Generate Bill");
        generateBillButton.addActionListener(this);
        buttonPanel.add(generateBillButton);

        saveButton = new JButton("Save Bill");
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);
        backButton = new JButton("Back");
        backButton.addActionListener(this);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        leftPanel.add(buttonPanel, gbc);

        // Right Panel (Bill Display)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // Create JLabel to display the bill
        billDisplayLabel = new JLabel("<html><body style='width: 500px;'>Billing details will appear here.<br><br></body></html>");
        billDisplayLabel.setVerticalAlignment(SwingConstants.TOP);
        billDisplayLabel.setHorizontalAlignment(SwingConstants.LEFT);
        billDisplayLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Add JLabel to right panel to show generated bill
        JScrollPane billScrollPane = new JScrollPane(billDisplayLabel);
        billScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  // Enable vertical scroll bar
        billScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // No horizontal scroll bar

        rightPanel.add(billScrollPane, BorderLayout.CENTER);

        // JSplitPane to ensure equal width
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450); // This splits the window equally (half and half)
        splitPane.setResizeWeight(0.5);  // Ensure resizing remains proportional
        add(splitPane, BorderLayout.CENTER);  // Add splitPane to the main frame

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addServiceButton) {
            addServiceToBill();
        } else if (e.getSource() == generateBillButton) {
            generateBill();
        } else if (e.getSource() == saveButton) {
            saveBill();
        }else if (e.getSource() == backButton) {
            dispose();
            new DashboardPage();
        }
    }

    private void addServiceToBill() {
        String patientID = patientIdField.getText().trim();

        // Check if the patient ID is empty
        if (patientID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the patient ID exists in the 'appointment' table
        if (!isPatientInAppointment(patientID)) {
            JOptionPane.showMessageDialog(this, "Invalid Patient ID. Please enter a valid Patient ID from the appointment records.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Proceed with adding the service if the patient exists in the appointment table
        String service = (String) serviceComboBox.getSelectedItem();
        String costText = costField.getText();

        if (costText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a cost for the service!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double cost = Double.parseDouble(costText);
            totalAmount += cost;

            // Add service and cost to lists
            serviceList.add(service);
            costList.add(cost);

            String message = "Added Service: " + service + " with Cost: " + df.format(cost);
            JOptionPane.showMessageDialog(this, message, "Service Added", JOptionPane.INFORMATION_MESSAGE);
            costField.setText(""); // Reset the cost field
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid cost value!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void generateBill() {
        String patientID = patientIdField.getText().trim();
        if (patientID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create database connection using existing Conn class
        Conn conn = new Conn();

        // Retrieve patient and doctor details from the appointments table
        String patientName = "";
        String doctorName = "";
         // Generate a 3-digit invoice ID

        try {
            String query = "SELECT patient_name, doctor FROM appointments WHERE patient_id = ?";
            PreparedStatement pst = conn.connection.prepareStatement(query);
            pst.setString(1, patientID);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                patientName = rs.getString("patient_name");
                doctorName = rs.getString("doctor");
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found in appointments!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving patient data!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get Paid Amount
        double paidAmount = 0;
        try {
            String paidText = paidAmountField.getText().trim();
            if (!paidText.isEmpty()) {
                paidAmount = Double.parseDouble(paidText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Paid Amount!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate balance amount
        double balanceAmount = totalAmount - paidAmount;

        // Generate HTML Invoice Layout
        StringBuilder bill = new StringBuilder();
        bill.append("<html><body style='width: 500px;'>");
        bill.append("<h3 style='text-align: center;'>MEDICAL BILLING INVOICE</h3>");
        bill.append("<hr>");

        // Patient and Doctor Information
        bill.append("<table width='100%'><tr>");
        bill.append("<td><b>Patient Information</b><br>").append(patientName).append("<br>ID: ").append(patientID).append("</td>");
        bill.append("<td><b>Doctor Information</b><br>").append(doctorName).append("</td>");
        bill.append("</tr></table>");

        // Invoice Details
        bill.append("<table width='100%' border='1' style='margin-top:10px;'>");
        bill.append("<tr><th>Invoice Number</th><th>Date</th><th>Due Date</th><th>Amount Due</th></tr>");
        bill.append("<tr><td>").append(invoiceID).append("</td><td>").append(java.time.LocalDate.now()).append("</td>");
        bill.append("<td>").append(java.time.LocalDate.now().plusDays(30)).append("</td><td>$").append(df.format(totalAmount)).append("</td></tr>");
        bill.append("</table>");

        // Services Table
        bill.append("<table width='100%' border='1' style='margin-top:10px;'>");
        bill.append("<tr><th>Item</th><th>Description</th><th>Amount</th></tr>");
        for (int i = 0; i < serviceList.size(); i++) {
            bill.append("<tr><td>").append(serviceList.get(i)).append("</td>");
            bill.append("<td>Service Description</td>");
            bill.append("<td>$").append(df.format(costList.get(i))).append("</td></tr>");
        }
        bill.append("</table>");

        // Payment Details
        bill.append("<p><b>Sub Total:</b> $").append(df.format(totalAmount)).append("</p>");
        bill.append("<p><b>Payment Mode:</b> ").append(paymentTypeComboBox.getSelectedItem()).append("</p>");
        bill.append("<p><b>Total:</b> $").append(df.format(totalAmount)).append("</p>");

        // **Updated Balance Amount Calculation**
        bill.append("<p><b>Paid Amount:</b> $").append(df.format(paidAmount)).append("</p>");
        bill.append("<p><b>Balance Amount:</b> $").append(df.format(balanceAmount)).append("</p>");

        bill.append("</body></html>");

        // Display Bill
        billDisplayLabel.setText(bill.toString());
    }





    private boolean isPatientInAppointment(String patientId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {
            String query = "SELECT COUNT(*) FROM appointments WHERE patient_id = ?";  // Query to check the appointment
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, patientId);  // Set the patient_id to check against
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);  // Get the count of matching records
                return count > 0;  // Return true if the patient exists
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking patient ID in the appointment table.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;  // Return false if the patient is not found
    }

    private void saveBill() {
        String patientID = patientIdField.getText().trim();
        String paymentType = (String) paymentTypeComboBox.getSelectedItem();
        String paidAmountText = paidAmountField.getText().trim();

        // Check if the patient ID is empty
        if (patientID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide Patient ID to save the bill!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate the paid amount field
        double paidAmount;
        try {
            paidAmount = Double.parseDouble(paidAmountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid paid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensure the patient exists in the appointment table
        if (!isPatientInAppointment(patientID)) {
            JOptionPane.showMessageDialog(this, "Invalid Patient ID. Cannot save the bill without a valid appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Save bill to the database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_mngt_sys", "root", "root")) {
            String query = "INSERT INTO bills (patient_id, amount, invoiceID, paid_amount, payment_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, patientID);
            pstmt.setDouble(2, totalAmount);
            pstmt.setString(3, invoiceID);
            pstmt.setDouble(4, paidAmount);
            pstmt.setString(5, paymentType);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bill Saved Successfully!\nInvoice ID: " + invoiceID, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving bill!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        new BillingPage();
    }
}
