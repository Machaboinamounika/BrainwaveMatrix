package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends JFrame implements ActionListener {
    JTextField textField;
    JPasswordField jPasswordField;
    JButton b1, b2;

    LoginPage() {
        JLabel nameLabel = new JLabel("Username");
        nameLabel.setBounds(40, 20, 100, 30);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        add(nameLabel);

        JLabel password = new JLabel("Password");
        password.setBounds(40, 70, 100, 30);
        password.setFont(new Font("Tahoma", Font.BOLD, 16));
        add(password);

        textField = new JTextField();
        textField.setBounds(150, 20, 150, 30);
        textField.setFont(new Font("Tahoma", Font.BOLD, 15));
        textField.setBackground(new Color(210, 122, 145));
        add(textField);
        jPasswordField = new JPasswordField();
        jPasswordField.setBounds(150, 70, 150, 30);
        jPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        jPasswordField.setBackground(new Color(210, 122, 145));
        add(jPasswordField);

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bg.png"));
        Image i1 = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon imageIcon1 = new ImageIcon(i1);
        JLabel label = new JLabel(imageIcon1);
        label.setBounds(300, -30, 400, 300);
        add(label);

        b1 = new JButton("Login");
        b1.setBounds(40, 140, 120, 30);
        b1.setFont(new Font("Serif", Font.BOLD, 15));
        b1.addActionListener(this);
        add(b1);
        b2 = new JButton("Cancel");
        b2.setBounds(180, 140, 120, 30);
        b2.setFont(new Font("Serif", Font.BOLD, 15));
        b2.addActionListener(this);
        add(b2);
        // "Go Back to Home" Hyperlink
        JLabel homeLink = new JLabel("← Go back to Home");
        homeLink.setBounds(100, 180, 200, 30);
        homeLink.setFont(new Font("Tahoma", Font.BOLD, 14));
        homeLink.setForeground(Color.BLUE);
        homeLink.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
        add(homeLink);

// Click Event for Home Redirection
        homeLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                new HomePage(); // Redirect to Home Page
            }
        });


        // Color(210, 122, 145)
        getContentPane().setBackground(new Color(255, 255, 255));
        setSize(720, 380);
        setLocation(500, 200);
        setLayout(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginPage();


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            try {
                Conn c = new Conn();
                Connection conn = c.statement.getConnection();  // Get connection object
                String user = textField.getText().trim();
                String pass = new String(jPasswordField.getPassword()).trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!", "Login Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String q = "SELECT * FROM login WHERE ID = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(q);
                pstmt.setString(1, user);
                pstmt.setString(2, pass);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);

                    // Open an appointmentScheduling
                    new AppointmentScheduling(user);

                    setVisible(false); // Hide the login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == b2) {
            System.exit(0);
        }
    }

}

