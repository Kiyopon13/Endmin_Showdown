import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;

public class EmailLoginWindow implements ActionListener {
    private JFrame frame;
    private JTextField emailField;
    private JButton nextButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;
    private JLabel messageLabel;

    public EmailLoginWindow() {
        UIUtils.initLookAndFeel();

        frame = new JFrame("🎮 Endmin Showdown - Login");
        frame.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create modern gradient background (Green to Teal)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(34, 139, 87),
                        getWidth(), getHeight(), new Color(0, 150, 136));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        frame.setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("🎮 Endmin Showdown");
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 32);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 32);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(40, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Master Your Games");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 255, 230));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 40, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Form Panel (Card)
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints fbgc = new GridBagConstraints();
        fbgc.insets = new Insets(10, 0, 10, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;

        // Step label
        JLabel stepLabel = new JLabel("Step 1: Enter Your Email");
        stepLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stepLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridx = 0; fbgc.gridy = 0; fbgc.weightx = 1.0;
        formPanel.add(stepLabel, fbgc);

        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 1; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(emailLabel, fbgc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 35));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 2; fbgc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(emailField, fbgc);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        messageLabel.setForeground(new Color(200, 50, 50));
        fbgc.gridy = 3; fbgc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(messageLabel, fbgc);

        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsPanel.setOpaque(false);

        nextButton = new JButton("Next");
        styleButton(nextButton, new Color(34, 139, 87));
        nextButton.addActionListener(this);
        buttonsPanel.add(nextButton);

        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(0, 150, 136));
        registerButton.addActionListener(this);
        buttonsPanel.add(registerButton);

        forgotPasswordButton = new JButton("Forgot Password");
        styleButton(forgotPasswordButton, new Color(255, 152, 0));
        forgotPasswordButton.addActionListener(this);
        buttonsPanel.add(forgotPasswordButton);

        fbgc.gridy = 4; fbgc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonsPanel, fbgc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 30, 100, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(formPanel, gbc);

        frame.setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, bgColor),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            handleNext();
        } else if (e.getSource() == registerButton) {
            handleRegister();
        } else if (e.getSource() == forgotPasswordButton) {
            frame.dispose();
            new ForgotPasswordWindow(null);
        }
    }

    private void handleNext() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            messageLabel.setText("⚠ Please enter your email!");
            return;
        }

        IUserDatabase db = DatabaseFactory.getDatabase();
        Player player = db.getPlayerByEmail(email);

        if (player == null) {
            messageLabel.setText("❌ Email not found! Register first.");
            return;
        }

        // Proceed to username/password screen
        frame.dispose();
        new UsernamePasswordWindow(email);
    }

    private void handleRegister() {
        frame.dispose();
        new RegistrationWindow();
    }

    private void handleForgotPassword() {
        frame.dispose();
        new ForgotPasswordWindow(null);
    }

    public static void main(String[] args) {
        UIUtils.initLookAndFeel();
        new EmailLoginWindow();
    }
}

// Custom rounded border class
class RoundedBorder extends AbstractBorder {
    private int radius;
    private Color color;

    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }
}
