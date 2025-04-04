package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardPage extends JFrame {

    public DashboardPage() {
        setTitle("Hospital Management System Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        setLayout(null);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, 1400, 900);
        backgroundPanel.setBackground(new Color(255, 255, 255, 180));
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);
        // Add the heading label
        JLabel headingLabel = new JLabel("Welcome to Brainwave Medicare Hospital Dashboard");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headingLabel.setForeground(Color.BLUE);

// Centering the label dynamically
        int labelWidth = headingLabel.getPreferredSize().width;
        int labelHeight = headingLabel.getPreferredSize().height;
        int x = (backgroundPanel.getWidth() - labelWidth) / 2;
        int y = 20;

        headingLabel.setBounds(x, y, labelWidth, labelHeight);
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);

// Add the label to the background panel
        backgroundPanel.add(headingLabel);


        int panelWidth = 900, panelHeight = 550;
        int panelX = (1400 - panelWidth) / 2;
        int panelY = (900 - panelHeight) / 2;

        JPanel panel = new JPanel(null);
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);
        panel.setOpaque(false);
        backgroundPanel.add(panel);

        int imageSize = 200;
        JLabel apptHeading = new JLabel("Scheduled Appointments");
        apptHeading.setFont(new Font("Arial", Font.BOLD, 16));
        apptHeading.setHorizontalAlignment(SwingConstants.CENTER);
        apptHeading.setBounds(50, 20, imageSize, 20);  // Positioning above the image
        panel.add(apptHeading);
        JLabel apptLabel = createImageLabel("src/icon/appointment.jpeg", "Scheduled Appointments");
        apptLabel.setBounds(50, 50, imageSize, imageSize);

        JLabel ehrHeading = new JLabel("Electronic Health Records (EHR)");
        ehrHeading.setFont(new Font("Arial", Font.BOLD, 16));
        ehrHeading.setHorizontalAlignment(SwingConstants.CENTER);
        ehrHeading.setBounds(350, 20, imageSize, 20);  // Positioning above the image
        panel.add(ehrHeading);
        JLabel ehrLabel = createImageLabel("src/icon/ehr3.png", "Electronic Health Records (EHR)");
        ehrLabel.setBounds(350, 50, imageSize, imageSize);

        JLabel billingHeading = new JLabel("Billing & Invoicing");
        billingHeading.setFont(new Font("Arial", Font.BOLD, 16));
        billingHeading.setHorizontalAlignment(SwingConstants.CENTER);
        billingHeading.setBounds(650, 20, imageSize, 20);  // Positioning above the image
        panel.add(billingHeading);
        JLabel billingLabel = createImageLabel("src/icon/billing.jpeg", "Billing & Invoicing");
        billingLabel.setBounds(650, 50, imageSize, imageSize);

        JLabel inventoryHeading = new JLabel("Inventory Management");
        inventoryHeading.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryHeading.setHorizontalAlignment(SwingConstants.CENTER);
        inventoryHeading.setBounds(200, 260, imageSize, 20);  // Positioning above the image
        panel.add(inventoryHeading);
        JLabel inventoryLabel = createImageLabel("src/icon/inventory.jpeg", "Inventory Management");
        inventoryLabel.setBounds(200, 300, imageSize, imageSize);

        JLabel staffHeading = new JLabel("Staff Management");
        staffHeading.setFont(new Font("Arial", Font.BOLD, 16));
        staffHeading.setHorizontalAlignment(SwingConstants.CENTER);
        staffHeading.setBounds(500, 260, imageSize, 20);  // Positioning above the image
        panel.add(staffHeading);
        JLabel staffLabel = createImageLabel("src/icon/staff.jpeg", "Staff Management");
        staffLabel.setBounds(500, 300, imageSize, imageSize);

        panel.add(apptLabel);
        panel.add(ehrLabel);
        panel.add(billingLabel);
        panel.add(inventoryLabel);
        panel.add(staffLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(panelX + (panelWidth / 2) - 75, panelY + panelHeight + 30, 150, 40);
        logoutButton.addActionListener(e -> {
            dispose();
            new HomePage();
        });
        backgroundPanel.add(logoutButton);

        setVisible(true);
    }

    private JLabel createImageLabel(String imagePath, String featureName) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setToolTipText(featureName);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new StaffLogin(featureName); // Pass the clicked feature to StaffLogin
            }
        });

        return label;
    }

    public static void main(String[] args) {
        new DashboardPage();
    }
}
