import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameLeaderboardWindow {
    private JFrame frame;
    private String gameName;
    private JFrame parentFrame;

    public GameLeaderboardWindow(Player player, String gameName, JFrame parentFrame) {
        this.gameName = gameName;
        this.parentFrame = parentFrame;

        frame = new JFrame(gameName + " Leaderboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = UIUtils.createLeaderboardThemedPanel(new BorderLayout(0, 0), gameName);
        frame.setContentPane(mainPanel);

        if (parentFrame != null) parentFrame.setVisible(false);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (parentFrame != null) parentFrame.setVisible(true);
            }
        });

        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));

        String emoji = gameName.equalsIgnoreCase("Logic") ? "🧩"
                     : gameName.equalsIgnoreCase("Memory") ? "🧠" : "♟";
        JLabel titleLabel = new JLabel(emoji + "  " + gameName + " Leaderboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Top players ranked by win rate", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(255, 255, 255, 160));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────────────
        List<PlayerLeaderboardEntry> entries = buildEntries();

        String[] columns = {"Rank", "Player", "Games Played", "Wins", "Win Rate", "Time Spent"};
        Object[][] data = new Object[entries.size()][6];
        for (int i = 0; i < entries.size(); i++) {
            PlayerLeaderboardEntry e = entries.get(i);
            String rankStr = i == 0 ? "🥇 1" : i == 1 ? "🥈 2" : i == 2 ? "🥉 3" : String.valueOf(i + 1);
            data[i] = new Object[]{rankStr, e.playerName, e.gamesPlayed, e.wins,
                    String.format("%.1f%%", e.winPercentage), e.timeSpent};
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0
                            ? new Color(255, 255, 255, 230)
                            : new Color(240, 240, 255, 200));
                    c.setForeground(new Color(30, 30, 60));
                } else {
                    c.setBackground(accentColor());
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(46);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setFocusable(false);
        table.setSelectionBackground(accentColor());
        table.setSelectionForeground(Color.WHITE);
        table.setOpaque(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(accentColor().darker());
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 48));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Center all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Column widths
        int[] widths = {80, 200, 140, 100, 110, 160};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Double-click to view profile
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String username = (String) table.getValueAt(row, 1);
                        Player p = DatabaseFactory.getDatabase().getPlayer(username);
                        if (p != null) new ProfileWindow(p, frame, player);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Wrap table in a card
        JPanel cardWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        cardWrapper.add(scroll, BorderLayout.CENTER);

        // Empty state
        if (entries.isEmpty()) {
            JLabel emptyLabel = new JLabel("No games played yet. Be the first!", JLabel.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            emptyLabel.setForeground(new Color(255, 255, 255, 180));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));
            cardWrapper.add(emptyLabel, BorderLayout.SOUTH);
        }

        mainPanel.add(cardWrapper, BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JLabel hint = new JLabel("Double-click a player to view their profile");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        hint.setForeground(new Color(255, 255, 255, 140));
        footer.add(hint);

        JButton backButton = createStyledButton("⬅  Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            if (parentFrame != null) parentFrame.setVisible(true);
        });
        footer.add(backButton);

        mainPanel.add(footer, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private List<PlayerLeaderboardEntry> buildEntries() {
        List<PlayerLeaderboardEntry> entries = new ArrayList<>();
        IUserDatabase db = DatabaseFactory.getDatabase();
        Map<String, Player> users = db.getAllUsers();

        for (Player p : users.values()) {
            GameStats stats = p.getGameStats(gameName);
            if (stats != null && stats.getTotalGames() > 0) {
                entries.add(new PlayerLeaderboardEntry(p.getUsername(), stats));
            }
        }

        entries.sort((a, b) -> {
            int cmp = Double.compare(b.winPercentage, a.winPercentage);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(b.wins, a.wins);
            if (cmp != 0) return cmp;
            return Integer.compare(b.gamesPlayed, a.gamesPlayed);
        });

        return entries;
    }

    private Color accentColor() {
        if ("Logic".equalsIgnoreCase(gameName))  return new Color(25, 103, 210);
        if ("Memory".equalsIgnoreCase(gameName)) return new Color(128, 29, 196);
        return new Color(139, 69, 19); // Chess
    }

    private JButton createStyledButton(String text) {
        Color bg = accentColor();
        Color hover = bg.brighter();
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(160, 44));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private static class PlayerLeaderboardEntry {
        String playerName;
        int gamesPlayed;
        int wins;
        String timeSpent;
        double winPercentage;

        PlayerLeaderboardEntry(String playerName, GameStats stats) {
            this.playerName = playerName;
            this.gamesPlayed = stats.getTotalGames();
            this.wins = stats.getTotalWins();
            this.timeSpent = stats.getTimeSpentFormatted();
            this.winPercentage = stats.getWinPercentage();
        }
    }
}
