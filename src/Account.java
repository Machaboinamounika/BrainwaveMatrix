import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Account {
    private double balance;
    private int pin;
    private boolean isAuthenticated;
    private static Set<Integer> existingPins = new HashSet<>();
    private List<String> transactions = new ArrayList<>();

    public Account(double initialBalance, int pin) throws Exception {
        if (existingPins.contains(pin)) {
            throw new Exception("PIN already exists. Choose a different PIN.");
        }
        this.balance = initialBalance;
        this.pin = pin;
        this.isAuthenticated = false;
        existingPins.add(pin);
        transactions.add("Account created with initial balance: $" + initialBalance);
    }

    public boolean authenticate(int enteredPin) {
        if (enteredPin == this.pin) {
            this.isAuthenticated = true;
            return true;
        }
        return false;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add("Deposited: $" + amount + " | New Balance: $" + balance);
            JOptionPane.showMessageDialog(null, "Deposit successful! New Balance: $" + balance);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add("Withdrawn: $" + amount + " | New Balance: $" + balance);
            JOptionPane.showMessageDialog(null, "Withdrawal successful! New Balance: $" + balance);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid amount or insufficient funds.");
        }
    }

    public String getTransactionHistory() {
        if (transactions.isEmpty()) {
            return "No transactions available.";
        }
        return String.join("\n", transactions);
    }
}
