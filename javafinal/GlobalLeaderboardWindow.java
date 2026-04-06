import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.Timer;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ItemEvent;

public class GlobalLeaderboardWindow {
    private JFrame frame;
    private JFrame parentFrame;
    private JTable leaderboardTable;
    private JComboBox<String> gameSelector;
    private JComboBox<String> sortSelector;
    private JLabel titleLabel;
    private JPanel mainPanel;
    private JPanel controlsPanel;
    private JPanel tablePanel;
    private JPanel bottomPanel;
    private Timer liveRefreshTimer;
    private Preferences prefs;
    private String currentSortMode;
    private Player self; // current logged-in player
    private String currentTheme;

    private void applyTheme(String theme) {
        currentTheme = theme;
        
        if ("Dark".equals(theme)) {
            // Dark theme - full black background
            frame.getContentPane().setBackground(new Color(20, 20, 20));
            
            if (titleLabel != null) {
                titleLabel.setForeground(Color.WHITE);
            }
            
            if (controlsPanel != null) {
                controlsPanel.setOpaque(true);
                controlsPanel.setBackground(new Color(30, 30, 30));
            }
            
            if (tablePanel != null) {
                tablePanel.setOpaque(true);
                tablePanel.setBackground(new Color(20, 20, 20));
            }
            
            if (bottomPanel != null) {
                bottomPanel.setOpaque(true);
                bottomPanel.setBackground(new Color(20, 20, 20));
            }
            
            if (leaderboardTable != null) {
                leaderboardTable.setBackground(new Color(30, 30, 30));
                leaderboardTable.setForeground(Color.WHITE);
                leaderboardTable.setGridColor(new Color(50, 50, 50));
                
                JTableHeader header = leaderboardTable.getTableHeader();
                header.setBackground(new Color(25, 25, 25));
                header.setForeground(Color.WHITE);
                
                leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (row % 2 == 0) {
                            c.setBackground(new Color(35, 35, 35));
                        } else {
                            c.setBackground(new Color(30, 30, 30));
                        }
                        if (isSelected) {
                            c.setBackground(new Color(0, 150, 136));
                        }
                        c.setForeground(Color.WHITE);
                        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                        return c;
                    }
                });
            }
        } else {
            // Light theme - green gradient background
            frame.getContentPane().setBackground(new Color(34, 139, 87));
            
            if (titleLabel != null) {
                titleLabel.setForeground(Color.WHITE);
            }
            
            if (controlsPanel != null) {
                controlsPanel.setOpaque(false);
            }
            
            if (tablePanel != null) {
                tablePanel.setOpaque(false);
            }
            
            if (bottomPanel != null) {
                bottomPanel.setOpaque(false);
            }
            
            if (leaderboardTable != null) {
                leaderboardTable.setBackground(Color.WHITE);
                leaderboardTable.setForeground(Color.BLACK);
                leaderboardTable.setGridColor(new Color(200, 200, 200));
                
                JTableHeader header = leaderboardTable.getTableHeader();
                header.setBackground(new Color(34, 139, 87));
                header.setForeground(Color.WHITE);
                
                leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (row % 2 == 0) {
                            c.setBackground(new Color(245, 250, 248));
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                        if (isSelected) {
                            c.setBackground(new Color(200, 230, 220));
                        }
                        c.setForeground(Color.BLACK);
                        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                        return c;
                    }
                });
            }
        }
        
        if (mainPanel != null) mainPanel.repaint();
        if (controlsPanel != null) controlsPanel.repaint();
        if (tablePanel != null) tablePanel.repaint();
        frame.repaint();
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, bgColor),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    public GlobalLeaderboardWindow(Player player) {
        this(player, null);
    }

    public GlobalLeaderboardWindow(Player player, JFrame parentFrame) {
        this.self = player;
        this.parentFrame = parentFrame;
        this.currentTheme = "Light";
        
        frame = new JFrame("🏆 Global Leaderboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Hide parent frame while leaderboard is open
        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });

        // user preferences
        prefs = Preferences.userNodeForPackage(GlobalLeaderboardWindow.class);
        currentSortMode = prefs.get("sortMode", "Win %");

        // Create gradient background panel
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if ("Dark".equals(currentTheme)) {
                    g2d.setColor(new Color(20, 20, 20));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(34, 139, 87),
                            getWidth(), getHeight(), new Color(0, 150, 136));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        frame.setContentPane(mainPanel);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titleLabel = new JLabel("🏆 Global Leaderboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Controls Panel
        controlsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if ("Dark".equals(currentTheme)) {
                    g2d.setColor(new Color(30, 30, 30, 245));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.setColor(new Color(255, 255, 255, 20));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 245));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                }
            }
        };
        controlsPanel.setOpaque(false);
        controlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gameSelector = new JComboBox<>(new String[]{"All Games", "Chess", "Live Chess", "Friends", "Logic", "Memory"});
        gameSelector.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gameSelector.setPreferredSize(new Dimension(120, 32));
        controlsPanel.add(new JLabel("Game:"));
        controlsPanel.add(gameSelector);

        sortSelector = new JComboBox<>(new String[]{"Win %", "Games", "Wins", "Time"});
        sortSelector.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortSelector.setPreferredSize(new Dimension(100, 32));
        sortSelector.setSelectedItem(currentSortMode);
        controlsPanel.add(new JLabel("Sort:"));
        controlsPanel.add(sortSelector);

        JComboBox<String> themeSelector = new JComboBox<>(new String[]{"Light", "Dark"});
        themeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        themeSelector.setPreferredSize(new Dimension(100, 32));
        themeSelector.setSelectedItem(prefs.get("theme", "Light"));
        themeSelector.addActionListener(ev -> {
            String theme = (String) themeSelector.getSelectedItem();
            prefs.put("theme", theme);
            applyTheme(theme);
        });
        controlsPanel.add(new JLabel("Theme:"));
        controlsPanel.add(themeSelector);

        JButton refreshButton = new JButton("↻ Refresh");
        styleButton(refreshButton, new Color(34, 139, 87));
        refreshButton.addActionListener(e -> {
            currentSortMode = (String) sortSelector.getSelectedItem();
            prefs.put("lastFilter", (String) gameSelector.getSelectedItem());
            prefs.put("sortMode", currentSortMode);
            updateLeaderboardTable();
        });
        controlsPanel.add(refreshButton);

        JButton copyButton = new JButton("◉ Copy");
        styleButton(copyButton, new Color(0, 150, 136));
        copyButton.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < leaderboardTable.getRowCount(); row++) {
                for (int col = 0; col < leaderboardTable.getColumnCount(); col++) {
                    sb.append(leaderboardTable.getValueAt(row, col)).append(" | ");
                }
                sb.append("\n");
            }
            StringSelection sel = new StringSelection(sb.toString());
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            cb.setContents(sel, sel);
            JOptionPane.showMessageDialog(frame, "✓ Leaderboard copied to clipboard!");
        });
        controlsPanel.add(copyButton);

        JButton saveButton = new JButton("▼ Save CSV");
        styleButton(saveButton, new Color(255, 152, 0));
        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                    for (int row = 0; row < leaderboardTable.getRowCount(); row++) {
                        for (int col = 0; col < leaderboardTable.getColumnCount(); col++) {
                            fw.write(leaderboardTable.getValueAt(row, col).toString());
                            if (col < leaderboardTable.getColumnCount() - 1) fw.write(",");
                        }
                        fw.write("\n");
                    }
                    JOptionPane.showMessageDialog(frame, "✓ Saved successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "❌ Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        controlsPanel.add(saveButton);

        mainPanel.add(controlsPanel, BorderLayout.NORTH);

        // Leaderboard Table Panel
        tablePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if ("Dark".equals(currentTheme)) {
                    g2d.setColor(new Color(30, 30, 30, 250));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.setColor(new Color(255, 255, 255, 15));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 250));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.setColor(new Color(0, 0, 0, 15));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                }
            }
        };
        tablePanel.setOpaque(false);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        leaderboardTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leaderboardTable.setRowHeight(28);
        leaderboardTable.setShowGrid(false);
        leaderboardTable.setIntercellSpacing(new Dimension(0, 0));
        
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(34, 139, 87));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 250, 248));
                } else {
                    c.setBackground(Color.WHITE);
                }
                if (isSelected) {
                    c.setBackground(new Color(200, 230, 220));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(leaderboardTable);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Bottom Panel with Close Button
        bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(180, 50, 50));
        closeButton.addActionListener(e -> {
            if (liveRefreshTimer != null) {
                liveRefreshTimer.stop();
            }
            frame.dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Timer for live updates
        liveRefreshTimer = new Timer(30000, ev -> {
            if ("Live Chess".equals(gameSelector.getSelectedItem())) {
                updateLeaderboardTable();
            }
        });
        
        gameSelector.addItemListener(ev -> {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                String sel = (String) ev.getItem();
                if ("Live Chess".equals(sel)) {
                    liveRefreshTimer.start();
                } else {
                    liveRefreshTimer.stop();
                }
            }
        });

        // Restore last filter
        String last = prefs.get("lastFilter", "All Games");
        gameSelector.setSelectedItem(last);
        if ("Live Chess".equals(last)) {
            liveRefreshTimer.start();
        }

        // Initial data load
        updateLeaderboardTable();
        
        // Apply saved theme
        applyTheme(prefs.get("theme", "Light"));

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void updateLeaderboardTable() {
        String gameFilter = (String) gameSelector.getSelectedItem();
        java.util.List<String[]> data = buildLeaderboardData(gameFilter);
        
        DefaultTableModel model = new DefaultTableModel(
            data.toArray(new String[0][]),
            new String[]{"🏅 Rank", "👤 Player Name", "🎮 Games Played", "🏆 Games Won", "📊 Win %", "⏱️ Time"}
        );
        leaderboardTable.setModel(model);
        
        // Set column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        leaderboardTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        leaderboardTable.getColumnModel().getColumn(5).setPreferredWidth(120);
    }
    
    private java.util.List<String[]> buildLeaderboardData(String gameFilter) {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        UserDatabase db = UserDatabase.getInstance();

        if ("Live Chess".equals(gameFilter)) {
            return fetchLiveLichessData();
        }
        
        if ("Friends".equals(gameFilter)) {
            return buildFriendsLeaderboardData();
        }

        java.util.List<GlobalPlayerEntry> entries = new java.util.ArrayList<>();

        for (Player p : db.getAllUsers().values()) {
            int totalGames = p.getTotalGamesPlayed();
            int totalWins = p.getTotalGamesWon();
            
            if (p.getLichessUsername() != null) {
                Map<String, Object> lichessStats = fetchLichessStats(p.getLichessUsername());
                if (lichessStats != null) {
                    int lichessGames = (int) lichessStats.getOrDefault("games", 0);
                    int lichessWins = (int) lichessStats.getOrDefault("wins", 0);
                    totalGames += lichessGames;
                    totalWins += lichessWins;
                }
            }
            
            if (totalGames > 0) {
                entries.add(new GlobalPlayerEntry(p, totalGames, totalWins));
            }
        }

        // Sort by win rate (default), or by selected sort mode
        entries.sort((a, b) -> {
            switch (currentSortMode) {
                case "Games":
                    return Integer.compare(b.totalGamesPlayed, a.totalGamesPlayed);
                case "Wins":
                    return Integer.compare(b.totalWins, a.totalWins);
                case "Time":
                    return Integer.compare(b.totalTimeSeconds, a.totalTimeSeconds);
                case "Win %":
                default:
                    double winRateA = (a.totalWins * 100.0) / Math.max(1, a.totalGamesPlayed);
                    double winRateB = (b.totalWins * 100.0) / Math.max(1, b.totalGamesPlayed);
                    return Double.compare(winRateB, winRateA);
            }
        });

        int rank = 1;
        for (GlobalPlayerEntry entry : entries) {
            rows.add(new String[]{
                String.valueOf(rank++),
                entry.playerName,
                String.valueOf(entry.totalGamesPlayed),
                String.valueOf(entry.totalWins),
                String.format("%.1f%%", (entry.totalWins * 100.0) / Math.max(1, entry.totalGamesPlayed)),
                entry.totalTimeSpent
            });
        }
        return rows;
    }

    /**
     * Returns a formatted text representation of the current top blitz
     * leaderboard from Lichess. This is used when the user selects the
     * "Live Chess" filter so the window displays a truly live global
     * ranking instead of local database stats.
     */
    private String fetchLiveLichessLeaderboard() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-15s | %-6s | %-5s%n", "Rank", "Username", "Rating", "Title"));
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://lichess.org/api/player/top/20/blitz"))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            org.json.JSONArray users = json.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject player = users.getJSONObject(i);
                int rank = i + 1;
                String username = player.getString("username");
                int rating = player.getJSONObject("perfs").getJSONObject("blitz").getInt("rating");
                String title = player.has("title") ? player.getString("title") : "-";
                sb.append(String.format("%-5d | %-15s | %-6d | %-5s%n", rank, username, rating, title));
            }
        } catch (Exception e) {
            sb.append("(failed to fetch live leaderboard - check connection)");
        }
        return sb.toString();
    }

    /**
     * Build leaderboard containing only the current player's friends
     */
    private String buildFriendsLeaderboard() {
        if (self == null) return "";
        StringBuilder sb = new StringBuilder();
        UserDatabase db = UserDatabase.getInstance();
        java.util.List<GlobalPlayerEntry> entries = new java.util.ArrayList<>();
        for (String friend : self.getFriends()) {
            Player p = db.getPlayer(friend);
            if (p == null) continue;
            int totalGames = p.getTotalGamesPlayed();
            int totalWins = p.getTotalGamesWon();
            if (totalGames > 0) {
                entries.add(new GlobalPlayerEntry(p, totalGames, totalWins));
            }
        }
        // sort using same rules as "All Games"
        entries.sort((a, b) -> {
            double winRateA = (a.totalWins * 100.0) / a.totalGamesPlayed;
            double winRateB = (b.totalWins * 100.0) / b.totalGamesPlayed;
            int cmpWinRate = Double.compare(winRateB, winRateA);
            if (cmpWinRate != 0) return cmpWinRate;
            int cmpWins = Integer.compare(b.totalWins, a.totalWins);
            if (cmpWins != 0) return cmpWins;
            return Integer.compare(b.totalGamesPlayed, a.totalGamesPlayed);
        });
        for (GlobalPlayerEntry entry : entries) {
            String line = String.format("%-20s | %14d | %11d | %s\n", 
                    entry.playerName,
                    entry.totalGamesPlayed,
                    entry.totalWins,
                    entry.totalTimeSpent);
            sb.append(line);
        }
        return sb.toString();
    }
    
    private java.util.List<String[]> fetchLiveLichessData() {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        UserDatabase db = UserDatabase.getInstance();
        java.util.List<GlobalPlayerEntry> entries = new java.util.ArrayList<>();
        
        for (Player p : db.getAllUsers().values()) {
            if (p.getLichessUsername() != null) {
                Map<String, Object> lichessStats = fetchLichessStats(p.getLichessUsername());
                if (lichessStats != null) {
                    int rating = (Integer) lichessStats.getOrDefault("rating", 0);
                    int games = (Integer) lichessStats.getOrDefault("games", 0);
                    entries.add(new GlobalPlayerEntry(p, games, (Integer) lichessStats.getOrDefault("wins", 0), rating));
                }
            }
        }
        
        entries.sort((a, b) -> Integer.compare(b.lichessRating, a.lichessRating));
        
        int rank = 1;
        for (GlobalPlayerEntry entry : entries) {
            rows.add(new String[]{
                String.valueOf(rank++),
                entry.playerName,
                String.valueOf(entry.totalGamesPlayed),
                String.valueOf(entry.totalWins),
                String.format("%.1f%%", (entry.totalWins * 100.0) / Math.max(1, entry.totalGamesPlayed)),
                entry.totalTimeSpent
            });
        }
        return rows;
    }
    
    private java.util.List<String[]> buildFriendsLeaderboardData() {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        if (self == null) return rows;
        
        UserDatabase db = UserDatabase.getInstance();
        java.util.List<GlobalPlayerEntry> entries = new java.util.ArrayList<>();
        
        for (String friend : self.getFriends()) {
            Player p = db.getPlayer(friend);
            if (p == null) continue;
            int totalGames = p.getTotalGamesPlayed();
            int totalWins = p.getTotalGamesWon();
            if (totalGames > 0) {
                entries.add(new GlobalPlayerEntry(p, totalGames, totalWins));
            }
        }
        
        entries.sort((a, b) -> {
            double winRateA = (a.totalWins * 100.0) / a.totalGamesPlayed;
            double winRateB = (b.totalWins * 100.0) / b.totalGamesPlayed;
            int cmpWinRate = Double.compare(winRateB, winRateA);
            if (cmpWinRate != 0) return cmpWinRate;
            int cmpWins = Integer.compare(b.totalWins, a.totalWins);
            if (cmpWins != 0) return cmpWins;
            return Integer.compare(b.totalGamesPlayed, a.totalGamesPlayed);
        });
        
        int rank = 1;
        for (GlobalPlayerEntry entry : entries) {
            rows.add(new String[]{
                String.valueOf(rank++),
                entry.playerName,
                String.valueOf(entry.totalGamesPlayed),
                String.valueOf(entry.totalWins),
                String.format("%.1f%%", (entry.totalWins * 100.0) / Math.max(1, entry.totalGamesPlayed)),
                entry.totalTimeSpent
            });
        }
        return rows;
    }

    private static Map<String, Object> fetchLichessStats(String lichessUsername) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://lichess.org/api/user/" + lichessUsername))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                Map<String, Object> stats = new java.util.HashMap<>();
                
                // Extract blitz stats
                if (json.has("perfs") && json.getJSONObject("perfs").has("blitz")) {
                    JSONObject blitz = json.getJSONObject("perfs").getJSONObject("blitz");
                    stats.put("rating", blitz.optInt("rating", 0));
                    stats.put("games", blitz.optInt("games", 0));
                    stats.put("wins", blitz.optInt("wins", 0));
                    // Estimate time spent (rough estimate: average 5 minutes per game)
                    stats.put("timeSpent", blitz.optInt("games", 0) * 300);
                }
                
                return stats;
            }
        } catch (Exception e) {
            // Silently ignore Lichess API errors to not break the leaderboard display
        }
        return null;
    }

    private static class GlobalPlayerEntry {
        String playerName;
        int totalGamesPlayed;
        int totalWins;
        String totalTimeSpent;
        int totalTimeSeconds;
        int lichessRating;

        GlobalPlayerEntry(Player player, int totalGames, int totalWins) {
            this.playerName = player.getUsername();
            this.totalGamesPlayed = totalGames;
            this.totalWins = totalWins;
            this.lichessRating = 0;

            int totalTime = player.getTotalTimeSpent();
            // Add Lichess time estimate if available
            if (player.getLichessUsername() != null) {
                Map<String, Object> lichessStats = fetchLichessStats(player.getLichessUsername());
                if (lichessStats != null) {
                    totalTime += (int) lichessStats.getOrDefault("timeSpent", 0);
                }
            }

            int hours = totalTime / 3600;
            int minutes = (totalTime % 3600) / 60;
            int seconds = totalTime % 60;
            this.totalTimeSpent = hours + "h " + minutes + "m " + seconds + "s";
            this.totalTimeSeconds = totalTime;
        }
        
        GlobalPlayerEntry(Player player, int totalGames, int totalWins, int lichessRating) {
            this.playerName = player.getUsername();
            this.totalGamesPlayed = totalGames;
            this.totalWins = totalWins;
            this.lichessRating = lichessRating;

            int totalTime = player.getTotalTimeSpent();
            int hours = totalTime / 3600;
            int minutes = (totalTime % 3600) / 60;
            int seconds = totalTime % 60;
            this.totalTimeSpent = hours + "h " + minutes + "m " + seconds + "s";
            this.totalTimeSeconds = totalTime;
        }

        GlobalPlayerEntry(Player player) {
            this.playerName = player.getUsername();
            this.totalGamesPlayed = player.getTotalGamesPlayed();
            this.totalWins = player.getTotalGamesWon();

            int totalTime = player.getTotalTimeSpent();
            int hours = totalTime / 3600;
            int minutes = (totalTime % 3600) / 60;
            int seconds = totalTime % 60;
            this.totalTimeSpent = hours + "h " + minutes + "m " + seconds + "s";
            this.totalTimeSeconds = totalTime;
        }
    }
}

// Custom rounded border class
class GLRoundedBorder extends javax.swing.border.AbstractBorder {
    private int radius;
    private Color color;

    public GLRoundedBorder(int radius, Color color) {
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