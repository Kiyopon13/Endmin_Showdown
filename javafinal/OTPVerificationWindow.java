import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OTPVerificationWindow extends JFrame {
    private JTextField otpField;
    private JButton verifyButton;
    private JButton resendButton;
    private JButton cancelButton;
    private IUserDatabase database;
    private JLabel messageLabel;
    private JLabel attemptsLabel;
    private String username;

    public OTPVerificationWindow(String username, JFrame parentFrame) {
        this.username = username;
        this.database = UserDatabase.getInstance();

        setTitle("🔐 Verify OTP");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(parentFrame);
        setResizable(false);

        // Create modern gradient background with gaming theme
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(null);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("🔐 Enter OTP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Enter the 4-digit code sent to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // OTP input
        JLabel otpLabel = new JLabel("One-Time Password (OTP)");
        otpLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        otpLabel.setForeground(Color.WHITE);

        otpField = new JTextField();
        otpField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        otpField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        otpField.setBackground(new Color(240, 248, 255));
        otpField.setCaretColor(Color.BLACK);
        otpField.setHorizontalAlignment(JTextField.CENTER);

        // Message label for errors/success
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(255, 100, 100));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Attempts label
        attemptsLabel = new JLabel("Attempts remaining: 3");
        attemptsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        attemptsLabel.setForeground(new Color(255, 200, 100));
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        verifyButton = new JButton("✓ Verify OTP");
        resendButton = new JButton("↻ Resend");
        cancelButton = new JButton("✕ Cancel");

        styleButton(verifyButton);
        styleButton(resendButton);
        styleButton(cancelButton);

        verifyButton.addActionListener(e -> handleVerifyOTP());
        resendButton.addActionListener(e -> handleResend());
        cancelButton.addActionListener(e -> {
            dispose();
        });

        // Allow Enter key to submit
        otpField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleVerifyOTP();
                }
            }
        });

        buttonPanel.add(verifyButton);
        buttonPanel.add(resendButton);
        buttonPanel.add(cancelButton);

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(otpLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(otpField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(attemptsLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        updateAttempts();
        setVisible(true);
    }

    private void handleVerifyOTP() {
        String otp = otpField.getText().trim();

        if (otp.isEmpty()) {
            messageLabel.setText("❌ Please enter the OTP");
            messageLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        if (otp.length() != 4 || !otp.matches("\\d{4}")) {
            messageLabel.setText("❌ OTP must be 4 digits");
            messageLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        // Validate OTP
        if (database.validateOTP(username, otp)) {
            messageLabel.setText("✓ OTP verified successfully!");
            messageLabel.setForeground(new Color(100, 200, 100));
            
            // Wait a moment and then open reset password window
            Timer timer = new Timer(1500, e -> {
                dispose();
                new ResetPasswordWindow(username, null);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // Check remaining attempts
            int attempts = database.getOTPAttempts(username);
            int remaining = Math.max(0, 3 - attempts);

            if (remaining <= 0) {
                messageLabel.setText("❌ Maximum attempts exceeded. Please try again.");
                messageLabel.setForeground(new Color(255, 50, 50));
                verifyButton.setEnabled(false);
                otpField.setEnabled(false);
            } else {
                messageLabel.setText("❌ Invalid OTP. Try again.");
                messageLabel.setForeground(new Color(255, 100, 100));
            }

            otpField.setText("");
            updateAttempts();
        }
    }

    private void handleResend() {
        // Generate new OTP
        String otp = database.generateOTP(username);
        if (otp == null) {
            messageLabel.setText("❌ Error generating new OTP");
            messageLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        // Send OTP via email/SMS
        Player player = database.getPlayer(username);
        if (player != null && player.getEmail() != null && !player.getEmail().isEmpty()) {
            new Thread(() -> {
                EmailService.sendVerificationCode(player.getEmail(), otp);
            }).start();
        }
        
        JOptionPane.showMessageDialog(this,
                "✓ New OTP has been sent to your registered email/mobile.\n\n" +
                "Check your inbox.\n" +
                "Valid for 5 minutes.",
                "OTP Resent Successfully",
                JOptionPane.INFORMATION_MESSAGE);

        messageLabel.setText("✓ New OTP sent to your email/mobile");
        messageLabel.setForeground(new Color(100, 200, 100));
        otpField.setText("");
        updateAttempts();
    }

    private void updateAttempts() {
        int attempts = database.getOTPAttempts(username);
        int remaining = Math.max(0, 3 - attempts);
        attemptsLabel.setText("Attempts remaining: " + remaining);

        if (remaining <= 1) {
            attemptsLabel.setForeground(new Color(255, 100, 100));
        } else if (remaining <= 2) {
            attemptsLabel.setForeground(new Color(255, 200, 100));
        } else {
            attemptsLabel.setForeground(new Color(100, 200, 100));
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(100, 150, 200));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(70, 130, 180));
                }
            }
        });
    }
}
