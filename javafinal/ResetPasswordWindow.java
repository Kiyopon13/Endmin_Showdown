import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ResetPasswordWindow extends JFrame {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton;
    private JButton cancelButton;
    private IUserDatabase database;
    private JLabel messageLabel;
    private String username;

    public ResetPasswordWindow(String username, JFrame parentFrame) {
        this.username = username;
        this.database = UserDatabase.getInstance();

        setTitle("🔑 Reset Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(parentFrame);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 
                        getWidth(), getHeight(), new Color(100, 149, 237));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("🔑 Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Enter your new password below");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // New password label and field
        JLabel newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        newPasswordLabel.setForeground(Color.WHITE);

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        newPasswordField.setBackground(new Color(240, 248, 255));
        newPasswordField.setCaretColor(Color.BLACK);

        // Confirm password label and field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmPasswordLabel.setForeground(Color.WHITE);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        confirmPasswordField.setBackground(new Color(240, 248, 255));
        confirmPasswordField.setCaretColor(Color.BLACK);

        // Password requirements
        JLabel requirementsLabel = new JLabel("Password must be at least 6 characters");
        requirementsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        requirementsLabel.setForeground(new Color(220, 220, 220));

        // Message label for errors/success
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(255, 100, 100));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        resetButton = new JButton("✓ Reset Password");
        cancelButton = new JButton("✕ Cancel");

        styleButton(resetButton);
        styleButton(cancelButton);

        resetButton.addActionListener(e -> handleResetPassword());
        cancelButton.addActionListener(e -> {
            dispose();
            new LoginWindow();
        });

        // Allow Enter key to submit
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleResetPassword();
                }
            }
        });

        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(newPasswordLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(newPasswordField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(confirmPasswordLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(requirementsLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setVisible(true);
    }

    private void handleResetPassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("❌ Please fill in all fields");
            messageLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        if (newPassword.length() < 6) {
            messageLabel.setText("❌ Password must be at least 6 characters");
            messageLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("❌ Passwords do not match");
            messageLabel.setForeground(new Color(255, 100, 100));
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        // Update password in database
        database.resetPassword(username, newPassword);

        messageLabel.setText("✓ Password reset successfully!");
        messageLabel.setForeground(new Color(100, 200, 100));

        // Show success message and redirect to login
        JOptionPane.showMessageDialog(this,
                "Your password has been reset successfully!\n\nPlease login with your new password.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new LoginWindow();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 150, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }
}
