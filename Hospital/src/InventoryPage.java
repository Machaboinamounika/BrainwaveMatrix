package hospital.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryPage extends JFrame implements ActionListener {
    private JTextField itemNameField, quantityField, supplierField;
    private JComboBox<String> itemTypeDropdown;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn,backBtn;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private Connection conn;

    public InventoryPage() {
        setTitle("Hospital Inventory Management");
        setSize(900, 600);
        setLayout(new BorderLayout());

        // **Database Connection**
        conn = new Conn().connection;

        // **Main Panel with Scroll**
        JPanel mainPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // **Header**
        JLabel headerLabel = new JLabel("Hospital Inventory Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        gbc.gridwidth = 2;
        mainPanel.add(headerLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;

        // **Input Panel**
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inventory Details"));

        inputPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        inputPanel.add(itemNameField);

        inputPanel.add(new JLabel("Item Type:"));
        String[] itemTypes = {"Select","Medicine", "Equipment", "Consumables"};
        itemTypeDropdown = new JComboBox<>(itemTypes);
        inputPanel.add(itemTypeDropdown);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        inputPanel.add(new JLabel("Supplier:"));
        supplierField = new JTextField();
        inputPanel.add(supplierField);

        gbc.gridy++;
        mainPanel.add(inputPanel, gbc);

        // **Button Panel**
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        addBtn = new JButton("Add Item");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        refreshBtn = new JButton("Refresh");
        backBtn = new JButton("Back");

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        backBtn.addActionListener(this);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        gbc.gridy++;
        mainPanel.add(buttonPanel, gbc);

        // **Table for Inventory List**
        String[] columns = {"ID", "Item Name", "Type", "Quantity", "Supplier"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        loadInventoryData();

        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 250));

        gbc.gridy++;
        mainPanel.add(tableScrollPane, gbc);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    // **Load Inventory from Database**
    private void loadInventoryData() {
        tableModel.setRowCount(0);
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM inventory");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("item_type"),
                        rs.getInt("quantity"),
                        rs.getString("supplier")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            addItem();
        } else if (e.getSource() == updateBtn) {
            updateItem();
        } else if (e.getSource() == deleteBtn) {
            deleteItem();
        } else if (e.getSource() == refreshBtn) {
            loadInventoryData();
        }else if (e.getSource() == backBtn) {
            dispose();
            new DashboardPage();  // Open DashboardPage
        }
    }

    private void addItem() {
        try {
            String query = "INSERT INTO inventory (item_name, item_type, quantity, supplier) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemNameField.getText());
            stmt.setString(2, (String) itemTypeDropdown.getSelectedItem());
            stmt.setInt(3, Integer.parseInt(quantityField.getText()));
            stmt.setString(4, supplierField.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item added successfully!");
            loadInventoryData();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void updateItem() {
        int row = inventoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.");
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            String query = "UPDATE inventory SET item_name=?, item_type=?, quantity=?, supplier=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemNameField.getText());
            stmt.setString(2, (String) itemTypeDropdown.getSelectedItem());
            stmt.setInt(3, Integer.parseInt(quantityField.getText()));
            stmt.setString(4, supplierField.getText());
            stmt.setInt(5, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item updated successfully!");
            loadInventoryData();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteItem() {
        int row = inventoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM inventory WHERE id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item deleted successfully!");
            loadInventoryData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new InventoryPage();
    }
}
