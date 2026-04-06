import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

public class LichessLeaderboardWindow extends JFrame {

    private JTable leaderboardTable;
    private DefaultTableModel model;
    private JFrame parentFrame;
    private JPanel mainPanel;
    private JPanel controlsPanel;
    private JPanel tablePanel;
    private JLabel titleLabel;
    private String currentTheme = "light";
    private Preferences prefs;

    public LichessLeaderboardWindow() {
        this(null);
    }

    public LichessLeaderboardWindow(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.prefs = Preferences.userNodeForPackage(LichessLeaderboardWindow.class);
        this.currentTheme = prefs.get("theme", "light");

        setTitle("♟ Lichess Blitz Leaderboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Hide parent frame while leaderboard is open
        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });

        // Create main panel with gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if ("dark".equals(currentTheme)) {
                    g2d.setColor(new Color(20, 20, 20));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), getWidth(), getHeight(), new Color(100, 149, 237));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        // Title Panel
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if ("dark".equals(currentTheme)) {
                    setBackground(new Color(30, 30, 30));
                } else {
                    setBackground(new Color(240, 248, 255));
                }
                super.paintComponent(g);
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        titleLabel = new JLabel("♟ Lichess Blitz Leaderboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground("dark".equals(currentTheme) ? Color.WHITE : Color.BLACK);

        titlePanel.add(titleLabel);

        // Controls Panel
        controlsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if ("dark".equals(currentTheme)) {
                    setBackground(new Color(30, 30, 30));
                } else {
                    setBackground(new Color(240, 248, 255));
                }
                super.paintComponent(g);
            }
        };
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        controlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton refreshButton = new JButton("↻ Refresh");
        JButton backButton = new JButton("← Back");

        styleButton(refreshButton);
        styleButton(backButton);

        refreshButton.addActionListener(e -> loadLeaderboard());
        backButton.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });

        controlsPanel.add(refreshButton);
        controlsPanel.add(backButton);

        // Table Panel
        tablePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if ("dark".equals(currentTheme)) {
                    setBackground(new Color(25, 25, 25));
                } else {
                    setBackground(new Color(245, 245, 245));
                }
                super.paintComponent(g);
            }
        };
        tablePanel.setOpaque(false);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Create table
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "🏅 Rank", "👤 Username", "⭐ Rating", "🏆 Title"
        });

        leaderboardTable = new JTable(model);
        leaderboardTable.setRowHeight(28);
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leaderboardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if ("dark".equals(currentTheme)) {
                        if (isSelected) {
                            c.setBackground(new Color(70, 130, 180));
                            c.setForeground(Color.WHITE);
                        } else if (row % 2 == 0) {
                            c.setBackground(new Color(35, 35, 35));
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(new Color(45, 45, 45));
                            c.setForeground(Color.WHITE);
                        }
                    } else {
                        if (isSelected) {
                            c.setBackground(new Color(70, 130, 180));
                            c.setForeground(Color.WHITE);
                        } else if (row % 2 == 0) {
                            c.setBackground(new Color(220, 240, 255));
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    }

                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.getViewport().setBackground("dark".equals(currentTheme) ? new Color(35, 35, 35) : Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add all panels to main
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(controlsPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);

        applyTheme(currentTheme);
        loadLeaderboard();

        if (parentFrame != null && (parentFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(8, 2));
        button.setPreferredSize(new Dimension(120, 40));

        if ("dark".equals(currentTheme)) {
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 150, 200));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }

    private void applyTheme(String theme) {
        currentTheme = theme;
        prefs.put("theme", theme);

        if ("dark".equals(currentTheme)) {
            titleLabel.setForeground(Color.WHITE);
        } else {
            titleLabel.setForeground(Color.BLACK);
        }

        mainPanel.repaint();
        controlsPanel.repaint();
        tablePanel.repaint();
        leaderboardTable.repaint();
    }

    private void loadLeaderboard() {
        new Thread(() -> {
            try {
                model.setRowCount(0);

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://lichess.org/api/player/top/10/blitz"))
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                JSONObject json = new JSONObject(response.body());
                JSONArray users = json.getJSONArray("users");

                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject player = users.getJSONObject(i);

                        int rank = i + 1;
                        String username = player.getString("username");
                        int rating = player.getJSONObject("perfs")
                                .getJSONObject("blitz")
                                .getInt("rating");

                        String title = player.has("title")
                                ? player.getString("title")
                                : "-";

                        model.addRow(new Object[]{
                                rank, username, rating, title
                        });
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(LichessLeaderboardWindow.this,
                            "Failed to load Lichess leaderboard.\nCheck internet connection.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    class RoundedBorder extends AbstractBorder {
        private int radius;
        private int thickness;

        public RoundedBorder(int radius, int thickness) {
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(c.getBackground());
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}