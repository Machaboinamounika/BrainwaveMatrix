package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame implements ActionListener {
    JButton homeButton, loginButton, registerButton, dashboardButton,helpButton;

    HomePage() {
        setTitle("Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(500, 500));
        getContentPane().setBackground(Color.WHITE);// Ensure the window is at least 500x500
        setLayout(new BorderLayout());// Full screen layout

        // Hospital Logo (Left Corner)
        ImageIcon hospitalIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bg.png"));
        Image img = hospitalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);
        JLabel hospitalLabel = new JLabel(resizedIcon);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(hospitalLabel);

        // Navigation Buttons (Top Right Corner)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        homeButton = new JButton("Home");
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        dashboardButton = new JButton("Dashboard");
        helpButton = new JButton("Help");

        // Apply uniform styling to buttons
        JButton[] buttons = {homeButton, loginButton, registerButton, dashboardButton, helpButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Serif", Font.BOLD, 16));
            button.setMargin(new Insets(10, 20, 10, 20)); // Uniform padding
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setBackground(Color.WHITE);
            button.addActionListener(this);
            navPanel.add(button);
        }

        // Highlight Home Button (Default)
        homeButton.setBackground(new Color(173, 216, 230)); // Light Blue Highlight

        // Top Panel containing both logo and navigation
        JPanel topPanel = new JPanel(new BorderLayout());
        //topPanel.setBackground(Color.WHITE);
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(navPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Scrolling Welcome Message
        JPanel scrollingPanel = new JPanel(null);
        scrollingPanel.setPreferredSize(new Dimension(getWidth(), 50));
        scrollingPanel.setBackground(Color.WHITE);

        JLabel scrollingLabel = new JLabel(" Welcome to Brainwaves Medicare Hospital ");
        scrollingLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        scrollingLabel.setBounds(-400, 10, 500, 40); // Initial position
        scrollingPanel.add(scrollingLabel);

     // Colors Array (Red, Green, Blue, Orange)
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE};
        final int[] colorIndex = {0}; // Track current color

// Timer for Scrolling & Color Change
        Timer timer = new Timer(50, new ActionListener() {
            int x = -400; // Start position (off-screen left)
            int colorChangeCounter = 0; // Counter to change colors at intervals

            @Override
            public void actionPerformed(ActionEvent e) {
                x += 5; // Move text to the right
                if (x > getWidth()) x = -400; // Reset position when it moves out

                scrollingLabel.setLocation(x, 5); // Update position

                // Change text color every 10 cycles
                if (colorChangeCounter % 10 == 0) {
                    scrollingLabel.setForeground(colors[colorIndex[0]]);
                    colorIndex[0] = (colorIndex[0] + 1) % colors.length; // Rotate colors
                }
                colorChangeCounter++;
            }
        });
        timer.start();

// Wrapper Panel to Hold Navigation & Scrolling Text
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(scrollingPanel, BorderLayout.SOUTH);

// Add to Main Frame
        add(headerPanel, BorderLayout.NORTH);

        // Background Image
        JLabel backgroundLabel = new JLabel(new ImageIcon(ClassLoader.getSystemResource("icon/bg.png")));
        add(backgroundLabel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            setVisible(false);
            new LoginPage();
        } else if (e.getSource() == registerButton) {
            setVisible(false);
            new PatientRegistration();
        } else if (e.getSource() == dashboardButton) {
            setVisible(false);
            new DashboardPage();
        } else if (e.getSource() == helpButton) {
            JOptionPane.showMessageDialog(this, "For assistance, contact hospital support at support@hospital.com", "Help", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new HomePage();
    }
}
