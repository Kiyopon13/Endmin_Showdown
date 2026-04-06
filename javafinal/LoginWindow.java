import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;

public class LoginWindow implements ActionListener {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    public LoginWindow() {
        UIUtils.initLookAndFeel();

        frame = new JFrame("Endmin Showdown - Login");
        frame.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create sakura anime background
        JPanel mainPanel = UIUtils.createSakuraBackground(new GridBagLayout());
        frame.setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // Title with typewriter animation
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 42);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 42);
        }
        JLabel titleLabel = UIUtils.createTypewriterLabel("Endmin Showdown", titleFont, Color.WHITE, 80);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(50, 0, 10, 0);
        mainPanel.add(titleLabel, gbc);

        // Subtitle with more emphasis
        JLabel subtitleLabel = new JLabel("Master Your Games");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(220, 255, 240));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 50, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Form Panel wrapped in fade-in (300ms delay)
        JPanel formPanel = UIUtils.createFadeInPanel(new GridBagLayout(), 300);
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        GridBagConstraints fbgc = new GridBagConstraints();
        fbgc.insets = new Insets(10, 0, 10, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;

        // Username field with enhanced styling
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(new Color(20, 90, 100));
        fbgc.gridx = 0; fbgc.gridy = 0; fbgc.weightx = 1.0;
        formPanel.add(usernameLabel, fbgc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBackground(new Color(240, 248, 255));
        usernameField.setForeground(Color.DARK_GRAY);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 150, 136), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        fbgc.gridy = 1;
        formPanel.add(usernameField, fbgc);

        // Password field with enhanced styling
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(new Color(20, 90, 100));
        fbgc.gridy = 2; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(passwordLabel, fbgc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(new Color(240, 248, 255));
        passwordField.setForeground(Color.DARK_GRAY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 150, 136), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        fbgc.gridy = 3; fbgc.insets = new Insets(0, 0, 10, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;
        fbgc.weightx = 1.0;
        formPanel.add(passwordField, fbgc);

        // Forgot Password link
        JLabel forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        forgotPasswordLink.setForeground(new Color(0, 120, 200));
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fbgc.gridy = 4; fbgc.insets = new Insets(5, 0, 20, 0);
        fbgc.anchor = GridBagConstraints.EAST;
        fbgc.fill = GridBagConstraints.NONE;
        fbgc.weightx = 1.0;
        formPanel.add(forgotPasswordLink, fbgc);

        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new ForgotPasswordWindow(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLink.setForeground(new Color(0, 180, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLink.setForeground(new Color(0, 120, 200));
            }
        });

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        messageLabel.setForeground(new Color(200, 50, 50));
        fbgc.gridy = 5; fbgc.insets = new Insets(10, 0, 15, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;
        fbgc.weightx = 1.0;
        fbgc.anchor = GridBagConstraints.CENTER;
        formPanel.add(messageLabel, fbgc);

        // Buttons with premium styling
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonsPanel.setOpaque(false);

        loginButton = UIUtils.createRippleButton("Login", new Color(0, 150, 136), new Color(0, 255, 200, 80));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(120, 45));
        loginButton.addActionListener(this);
        buttonsPanel.add(loginButton);

        registerButton = UIUtils.createRippleButton("Register", new Color(34, 139, 87), new Color(100, 255, 150, 80));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(120, 45));
        registerButton.addActionListener(this);
        buttonsPanel.add(registerButton);

        fbgc.gridy = 6; fbgc.insets = new Insets(25, 0, 0, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;
        fbgc.anchor = GridBagConstraints.CENTER;
        fbgc.weightx = 1.0;
        formPanel.add(buttonsPanel, fbgc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 30, 100, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(formPanel, gbc);

        frame.setVisible(true);

        // Fade-in animation
        try {
            frame.setOpacity(0f);
            javax.swing.Timer fadeIn = new javax.swing.Timer(16, null);
            float[] op = {0f};
            fadeIn.addActionListener(ev -> {
                op[0] = Math.min(op[0] + 0.05f, 1f);
                try { frame.setOpacity(op[0]); } catch (Exception ignored) {}
                if (op[0] >= 1f) fadeIn.stop();
            });
            fadeIn.start();
        } catch (Exception ignored) {}
    }

    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 45));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(hoverColor, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == registerButton) {
            handleRegister();
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠ Please fill all fields!");
            return;
        }

        IUserDatabase db = DatabaseFactory.getDatabase();
        Player player = db.authenticateUser(username, password);

        if (player != null) {
            frame.dispose();
            new MainGameWindow(player);
        } else {
            messageLabel.setText("❌ Invalid credentials! Try again.");
            passwordField.setText("");
        }
    }

    private void handleRegister() {
        frame.dispose();
        new RegistrationWindow();
    }

    public static void main(String[] args) {
        UIUtils.initLookAndFeel();
        new LoginWindow();
    }
}
