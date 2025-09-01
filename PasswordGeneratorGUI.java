import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGeneratorGUI extends JFrame {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.<>/?";

    private JTextField lengthField;
    private JTextArea resultArea;
    private JLabel strengthLabel;

    private JCheckBox upperBox, lowerBox, digitBox, symbolBox;

    public PasswordGeneratorGUI() {
        setTitle("🔑 Password Generator");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for input
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Password Length:"));
        lengthField = new JTextField("12", 5); // default 12
        inputPanel.add(lengthField);

        // Checkboxes
        upperBox = new JCheckBox("Uppercase", true);
        lowerBox = new JCheckBox("Lowercase", true);
        digitBox = new JCheckBox("Digits", true);
        symbolBox = new JCheckBox("Symbols", true);

        JPanel checkPanel = new JPanel();
        checkPanel.add(upperBox);
        checkPanel.add(lowerBox);
        checkPanel.add(digitBox);
        checkPanel.add(symbolBox);

        // Buttons
        JButton generateBtn = new JButton("Generate");
        JButton copyBtn = new JButton("Copy to Clipboard");

        // Result area
        resultArea = new JTextArea(3, 30);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Strength label
        strengthLabel = new JLabel("Strength: -");
        strengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        strengthLabel.setFont(strengthLabel.getFont().deriveFont(14f));

        // Actions
        generateBtn.addActionListener(e -> generatePassword());
        copyBtn.addActionListener(e -> copyToClipboard());

        // Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(checkPanel, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.add(strengthLabel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        btnPanel.add(generateBtn);
        btnPanel.add(copyBtn);
        resultPanel.add(btnPanel, BorderLayout.SOUTH);

        add(resultPanel, BorderLayout.SOUTH);
    }

    private void generatePassword() {
        try {
            int length = Integer.parseInt(lengthField.getText().trim());

            if (length < 4) {
                JOptionPane.showMessageDialog(this, "Password length must be at least 4.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Build pool
            String pool = "";
            if (upperBox.isSelected()) pool += UPPER;
            if (lowerBox.isSelected()) pool += LOWER;
            if (digitBox.isSelected()) pool += DIGITS;
            if (symbolBox.isSelected()) pool += SYMBOLS;

            if (pool.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select at least one character type!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String password = generate(length, pool);
            resultArea.setText(password);
            updateStrength(password, pool.length());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyToClipboard() {
        String text = resultArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No password to copy!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String generate(int length, String pool) {
        SecureRandom rnd = new SecureRandom();
        List<Character> pw = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            pw.add(pool.charAt(rnd.nextInt(pool.length())));
        }

        Collections.shuffle(pw, rnd);

        StringBuilder sb = new StringBuilder();
        for (char c : pw) sb.append(c);
        return sb.toString();
    }

    private void updateStrength(String password, int poolSize) {
        // Entropy formula ≈ length * log2(pool_size)
        double entropy = password.length() * (Math.log(poolSize) / Math.log(2));

        String strength;
        Color color;

        if (entropy < 40) {
            strength = "Weak";
            color = Color.RED;
        } else if (entropy < 60) {
            strength = "Medium";
            color = Color.ORANGE;
        } else if (entropy < 80) {
            strength = "Strong";
            color = Color.GREEN.darker();
        } else {
            strength = "Very Strong";
            color = Color.BLUE;
        }

        strengthLabel.setText("Strength: " + strength + " (Entropy: " + (int) entropy + " bits)");
        strengthLabel.setForeground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordGeneratorGUI().setVisible(true));
    }
}
