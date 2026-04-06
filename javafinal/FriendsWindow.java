import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FriendsWindow {
    private JFrame frame;
    private JFrame parentFrame;
    private Player player;

    private static final Color BG1        = new Color(8,  12, 28);
    private static final Color BG2        = new Color(18, 24, 52);
    private static final Color SIDEBAR_BG = new Color(12, 18, 38);
    private static final Color CARD       = new Color(22, 32, 62);
    private static final Color ACCENT     = new Color(0,  210, 160);
    private static final Color ACCENT2    = new Color(100, 80, 255);
    private static final Color TEXT_DIM   = new Color(160, 175, 210);
    private static final Color CHESS_C    = new Color(184, 134, 11);
    private static final Color LOGIC_C    = new Color(25,  103, 210);
    private static final Color MEM_C      = new Color(147, 51,  219);

    // Sidebar state
    private JPanel friendListPanel;
    private JLabel friendCountLabel;
    private JTextField searchField;
    private String selectedFriend = null;
    private JPanel rightPanel;

    // Particles for right panel placeholder
    private float[][] particles;
    private javax.swing.Timer particleTimer;

    // Bar animation for friend profile
    private float[] barProgress = {0f, 0f, 0f};
    private float[] barTargets  = {0f, 0f, 0f};
    private javax.swing.Timer barTimer;

    public FriendsWindow(Player player) { this(player, null); }

    public FriendsWindow(Player player, JFrame parentFrame) {
        this.player = player;
        this.parentFrame = parentFrame;

        frame = new JFrame("Friends — " + player.getUsername());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);

        if (parentFrame != null) parentFrame.setVisible(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                stopTimers();
                if (parentFrame != null) parentFrame.setVisible(true);
            }
        });

        initParticles();

        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        frame.setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);

        rightPanel = buildRightPlaceholder();
        root.add(rightPanel, BorderLayout.CENTER);

        startParticleTimer(root);
        frame.setVisible(true);
    }

    // ── Particles ─────────────────────────────────────────────────────────────
    private void initParticles() {
        particles = new float[30][5];
        Random rng = new Random();
        for (int i = 0; i < 30; i++) {
            particles[i][0] = rng.nextFloat() * 1920;
            particles[i][1] = rng.nextFloat() * 1080;
            particles[i][2] = 0.3f + rng.nextFloat() * 0.7f;
            particles[i][3] = 0.15f + rng.nextFloat() * 0.4f;
            particles[i][4] = 2f + rng.nextFloat() * 3f;
        }
    }

    private void startParticleTimer(JPanel root) {
        particleTimer = new javax.swing.Timer(50, e -> {
            for (float[] p : particles) {
                p[1] -= p[2];
                if (p[1] < -10) {
                    p[1] = root.getHeight() + 10;
                    p[0] = (float)(Math.random() * root.getWidth());
                }
            }
            if (rightPanel != null) rightPanel.repaint();
        });
        particleTimer.start();
    }

    private void stopTimers() {
        if (particleTimer != null) particleTimer.stop();
        if (barTimer      != null) barTimer.stop();
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // right border
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setOpaque(false);

        // Header
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(24, 18, 12, 18));

        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setOpaque(false);
        friendCountLabel = new JLabel("FRIENDS (" + player.getFriends().size() + ")");
        friendCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        friendCountLabel.setForeground(ACCENT);
        titleRow.add(friendCountLabel, BorderLayout.CENTER);

        JButton addBtn = sidebarBtn("＋ Add");
        addBtn.addActionListener(e -> openAddFriendDialog());
        titleRow.add(addBtn, BorderLayout.EAST);
        header.add(titleRow, BorderLayout.NORTH);
        header.add(Box.createVerticalStrut(10), BorderLayout.CENTER);

        // Search
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(new Color(20, 30, 58));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(ACCENT);
        searchField.putClientProperty("JTextField.placeholderText", "Search friends...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,40), 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2),
                    BorderFactory.createEmptyBorder(6, 9, 6, 9)));
            }
            public void focusLost(FocusEvent e) {
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255,255,255,40), 1),
                    BorderFactory.createEmptyBorder(7, 10, 7, 10)));
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { rebuildFriendList(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { rebuildFriendList(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { rebuildFriendList(); }
        });
        header.add(searchField, BorderLayout.SOUTH);
        sidebar.add(header, BorderLayout.NORTH);

        // Friend list
        friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));
        friendListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(friendListPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        sidebar.add(scroll, BorderLayout.CENTER);

        // Back button at bottom
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 16, 8));
        JButton backBtn = pillBtn("⬅ Back", new Color(50, 60, 100), new Color(70, 80, 130));
        backBtn.addActionListener(e -> frame.dispose());
        bottomBar.add(backBtn);
        sidebar.add(bottomBar, BorderLayout.SOUTH);

        rebuildFriendList();
        return sidebar;
    }

    private void rebuildFriendList() {
        friendListPanel.removeAll();
        String query = searchField != null ? searchField.getText().toLowerCase() : "";
        Set<String> friends = player.getFriends();

        if (friends.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setOpaque(false);
            empty.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));
            JLabel msg = new JLabel("<html><center>No friends yet<br>Click ＋ Add to find players</center></html>");
            msg.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            msg.setForeground(TEXT_DIM);
            msg.setHorizontalAlignment(JLabel.CENTER);
            empty.add(msg, BorderLayout.CENTER);
            friendListPanel.add(empty);
        } else {
            for (String username : friends) {
                if (!query.isEmpty() && !username.toLowerCase().contains(query)) continue;
                friendListPanel.add(buildFriendRow(username));
            }
        }
        friendListPanel.revalidate();
        friendListPanel.repaint();
        if (friendCountLabel != null)
            friendCountLabel.setText("FRIENDS (" + friends.size() + ")");
    }

    private JPanel buildFriendRow(String username) {
        Player friend = DatabaseFactory.getDatabase().getPlayer(username);
        boolean isSelected = username.equals(selectedFriend);

        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    public void mouseClicked(MouseEvent e) {
                        selectedFriend = username;
                        rebuildFriendList();
                        showFriendProfile(username);
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = username.equals(selectedFriend);
                Color bg = sel ? new Color(30, 50, 90) : (hovered ? new Color(20, 35, 65) : new Color(0,0,0,0));
                g2.setColor(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (sel) {
                    g2.setColor(ACCENT);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setPreferredSize(new Dimension(0, 60));
        row.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Avatar
        JPanel avatar = miniAvatar(username, 40);
        row.add(avatar, BorderLayout.WEST);

        // Text
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        text.add(nameLabel);

        String sub = "No games yet";
        if (friend != null) {
            int g = friend.getTotalGamesPlayed();
            sub = g + " game" + (g != 1 ? "s" : "");
        }
        JLabel subLabel = new JLabel(sub);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(TEXT_DIM);
        text.add(subLabel);
        row.add(text, BorderLayout.CENTER);

        // Status dot
        Color dotColor = Color.GRAY;
        if (friend != null) {
            if (friend.getTotalGamesWon() > 0) dotColor = new Color(0, 200, 100);
            else if (friend.getTotalGamesPlayed() > 0) dotColor = new Color(220, 180, 0);
        }
        final Color dc = dotColor;
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dc);
                g2.fillOval(0, 0, 10, 10);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 10));
        JPanel dotWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        dotWrap.setOpaque(false);
        dotWrap.setPreferredSize(new Dimension(18, 60));
        dotWrap.add(dot);
        row.add(dotWrap, BorderLayout.EAST);

        return row;
    }

    // ── Right panel: placeholder ──────────────────────────────────────────────
    private JPanel buildRightPlaceholder() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // diagonal stripes
                g2.setColor(new Color(255,255,255,5));
                for (int x = -getHeight(); x < getWidth()+getHeight(); x += 28)
                    g2.drawLine(x, 0, x+getHeight(), getHeight());
                // particles
                for (float[] p : particles) {
                    int alpha = (int)(p[3] * 180);
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), alpha));
                    int sz = (int) p[4];
                    g2.fillOval((int)p[0], (int)p[1], sz, sz);
                }
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel emoji = new JLabel("👥");
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(emoji);
        center.add(Box.createVerticalStrut(16));

        JLabel msg = new JLabel("Select a friend to view their profile");
        msg.setFont(new Font("Segoe UI", Font.BOLD, 18));
        msg.setForeground(new Color(200, 215, 255));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(msg);
        center.add(Box.createVerticalStrut(8));

        JLabel sub = new JLabel("Choose someone from the sidebar");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_DIM);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(sub);

        panel.add(center);
        return panel;
    }

    // ── Friend profile view ───────────────────────────────────────────────────
    private void showFriendProfile(String username) {
        Player friend = DatabaseFactory.getDatabase().getPlayer(username);

        // Reset bar animation
        if (barTimer != null) barTimer.stop();
        barProgress = new float[]{0f, 0f, 0f};
        if (friend != null) {
            barTargets[0] = (float) friend.getChessStats().getWinPercentage() / 100f;
            barTargets[1] = (float) friend.getLogicStats().getWinPercentage() / 100f;
            barTargets[2] = (float) friend.getMemoryStats().getWinPercentage() / 100f;
        }

        JPanel profilePanel = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        profilePanel.setOpaque(false);

        if (friend == null) {
            JLabel err = new JLabel("Player data not found.");
            err.setForeground(TEXT_DIM);
            err.setHorizontalAlignment(JLabel.CENTER);
            profilePanel.add(err, BorderLayout.CENTER);
        } else {
            JScrollPane scroll = new JScrollPane(buildFriendContent(friend));
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            profilePanel.add(scroll, BorderLayout.CENTER);
        }

        // Replace right panel
        Container parent = rightPanel.getParent();
        if (parent != null) {
            parent.remove(rightPanel);
            rightPanel = profilePanel;
            parent.add(rightPanel, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        }

        startBarAnimation();
    }

    private JPanel buildFriendContent(Player friend) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Banner
        content.add(buildFriendBanner(friend));
        content.add(Box.createVerticalStrut(20));

        // Game stat cards row
        JPanel gameRow = new JPanel(new GridLayout(1, 3, 16, 0));
        gameRow.setOpaque(false);
        gameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        gameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameRow.add(buildMiniGameCard("♟ Chess",  friend.getChessStats(),  CHESS_C, 0));
        gameRow.add(buildMiniGameCard("🧩 Logic",  friend.getLogicStats(),  LOGIC_C, 1));
        gameRow.add(buildMiniGameCard("🧠 Memory", friend.getMemoryStats(), MEM_C,   2));
        content.add(gameRow);
        content.add(Box.createVerticalStrut(20));

        // Comparison row
        content.add(buildComparisonRow(friend));
        content.add(Box.createVerticalStrut(20));

        // Bottom action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton removeBtn = pillBtn("Remove Friend", new Color(160, 40, 40), new Color(200, 60, 60));
        removeBtn.addActionListener(e -> confirmRemove(friend.getUsername()));

        JButton viewBtn = pillBtn("View Full Profile", ACCENT, ACCENT.darker());
        viewBtn.addActionListener(e -> new ProfileWindow(friend, frame, player));

        btnRow.add(removeBtn);
        btnRow.add(viewBtn);
        content.add(btnRow);

        return content;
    }

    private JPanel buildFriendBanner(Player friend) {
        JPanel banner = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(14,20,48), getWidth(), 0, new Color(28,38,80)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(255,255,255,15));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = avatarColor(friend.getUsername());
                g2.setPaint(new GradientPaint(0, 0, c1, 80, 80, c1.darker().darker()));
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                String l = friend.getUsername().substring(0,1).toUpperCase();
                g2.drawString(l, 40 - fm.stringWidth(l)/2, 40 + fm.getAscent()/2 - 3);
                g2.dispose();
            }
        };
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(80, 80));
        banner.add(av, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(friend.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        info.add(nameLabel);
        info.add(Box.createVerticalStrut(4));

        String rank = calcRank(friend.getTotalGamesWon());
        Color rc = rankColor(rank);
        JLabel rankBadge = new JLabel("  " + rank + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(rc.getRed(), rc.getGreen(), rc.getBlue(), 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(rc);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        rankBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rankBadge.setForeground(rc);
        rankBadge.setOpaque(false);
        rankBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(rankBadge);
        info.add(Box.createVerticalStrut(8));

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        int total = friend.getTotalGamesPlayed();
        int wins  = friend.getTotalGamesWon();
        double wr = total > 0 ? (wins * 100.0 / total) : 0;
        chips.add(statChip("🎮 " + total, new Color(40,60,100)));
        chips.add(statChip("🏆 " + wins,  new Color(40,80,60)));
        chips.add(statChip(String.format("📈 %.0f%%", wr), new Color(60,40,100)));
        info.add(chips);

        banner.add(info, BorderLayout.CENTER);
        return banner;
    }

    private JPanel buildMiniGameCard(String name, GameStats stats, Color color, int idx) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(8));

        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                int fw = (int)(getWidth() * barProgress[idx]);
                if (fw > 0) {
                    g2.setPaint(new GradientPaint(0, 0, color, fw, 0, color.brighter()));
                    g2.fillRoundRect(0, 0, fw, getHeight(), getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 8));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bar);
        card.add(Box.createVerticalStrut(6));

        JLabel detail = new JLabel(stats.getTotalGames() + " played  /  " + stats.getTotalWins() + " won");
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detail.setForeground(TEXT_DIM);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(detail);

        return card;
    }

    private JPanel buildComparisonRow(Player friend) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("VS YOU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        String[] gameNames = {"♟ Chess", "🧩 Logic", "🧠 Memory"};
        GameStats[] myStats     = {player.getChessStats(), player.getLogicStats(), player.getMemoryStats()};
        GameStats[] theirStats  = {friend.getChessStats(), friend.getLogicStats(), friend.getMemoryStats()};
        Color[] colors          = {CHESS_C, LOGIC_C, MEM_C};

        for (int i = 0; i < 3; i++) {
            card.add(buildCompRow(gameNames[i], myStats[i].getWinPercentage(),
                theirStats[i].getWinPercentage(), colors[i]));
            if (i < 2) card.add(Box.createVerticalStrut(10));
        }
        return card;
    }

    private JPanel buildCompRow(String name, double myWR, double theirWR, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        nameLabel.setForeground(TEXT_DIM);
        nameLabel.setPreferredSize(new Dimension(90, 20));
        row.add(nameLabel, BorderLayout.WEST);

        JPanel bars = new JPanel(new GridLayout(1, 2, 4, 0));
        bars.setOpaque(false);

        final double myWRf = myWR, theirWRf = theirWR;
        JPanel myBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,15));
                g2.fillRoundRect(0, 4, getWidth(), getHeight()-8, 6, 6);
                int fw = (int)(getWidth() * myWRf / 100.0);
                if (fw > 0) {
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 4, fw, getHeight()-8, 6, 6);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString(String.format("You %.0f%%", myWRf), 4, getHeight()/2 + 4);
                g2.dispose();
            }
        };
        myBar.setOpaque(false);

        JPanel theirBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,15));
                g2.fillRoundRect(0, 4, getWidth(), getHeight()-8, 6, 6);
                int fw = (int)(getWidth() * theirWRf / 100.0);
                if (fw > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(0, 4, fw, getHeight()-8, 6, 6);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString(String.format("Them %.0f%%", theirWRf), 4, getHeight()/2 + 4);
                g2.dispose();
            }
        };
        theirBar.setOpaque(false);

        bars.add(myBar);
        bars.add(theirBar);
        row.add(bars, BorderLayout.CENTER);

        String verdict = myWR > theirWR ? "You lead" : (theirWR > myWR ? "They lead" : "Tied");
        Color vc = myWR > theirWR ? ACCENT : (theirWR > myWR ? new Color(255,100,100) : TEXT_DIM);
        JLabel vLabel = new JLabel(verdict);
        vLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        vLabel.setForeground(vc);
        vLabel.setPreferredSize(new Dimension(70, 20));
        vLabel.setHorizontalAlignment(JLabel.RIGHT);
        row.add(vLabel, BorderLayout.EAST);

        return row;
    }

    private void startBarAnimation() {
        if (barTimer != null) barTimer.stop();
        barTimer = new javax.swing.Timer(16, null);
        barTimer.addActionListener(e -> {
            boolean done = true;
            for (int i = 0; i < 3; i++) {
                if (barProgress[i] < barTargets[i]) {
                    barProgress[i] = Math.min(barProgress[i] + 0.016f, barTargets[i]);
                    done = false;
                }
            }
            if (rightPanel != null) rightPanel.repaint();
            if (done) barTimer.stop();
        });
        barTimer.start();
    }

    // ── Add Friend dialog ─────────────────────────────────────────────────────
    private void openAddFriendDialog() {
        IUserDatabase db = DatabaseFactory.getDatabase();
        List<String> available = new ArrayList<>();
        for (String u : db.getAllUsers().keySet()) {
            if (!u.equals(player.getUsername()) && !player.getFriends().contains(u))
                available.add(u);
        }

        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.setSize(460, 540);
        dialog.setLocationRelativeTo(frame);

        JPanel dp = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        dialog.setContentPane(dp);

        // Header
        JPanel dHeader = new JPanel(new BorderLayout());
        dHeader.setOpaque(false);
        JLabel dTitle = new JLabel("FIND PLAYERS");
        dTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dTitle.setForeground(ACCENT);
        dHeader.add(dTitle, BorderLayout.WEST);
        JButton xBtn = new JButton("×");
        xBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        xBtn.setForeground(new Color(255, 100, 100));
        xBtn.setContentAreaFilled(false);
        xBtn.setBorderPainted(false);
        xBtn.setFocusPainted(false);
        xBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        xBtn.addActionListener(e -> dialog.dispose());
        dHeader.add(xBtn, BorderLayout.EAST);
        dp.add(dHeader, BorderLayout.NORTH);

        // Search
        JTextField dSearch = new JTextField();
        dSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dSearch.setBackground(new Color(14, 22, 48));
        dSearch.setForeground(Color.WHITE);
        dSearch.setCaretColor(ACCENT);
        dSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // List
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        final String[] selectedUser = {null};

        Runnable populate = () -> {
            listPanel.removeAll();
            String q = dSearch.getText().toLowerCase();
            for (String u : available) {
                if (!q.isEmpty() && !u.toLowerCase().contains(q)) continue;
                Player fp = db.getPlayer(u);

                JPanel row = new JPanel(new BorderLayout(12, 0)) {
                    boolean sel = false;
                    {
                        addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                selectedUser[0] = u;
                                for (Component c : listPanel.getComponents())
                                    if (c instanceof JPanel) ((JPanel)c).putClientProperty("sel", false);
                                putClientProperty("sel", true);
                                listPanel.repaint();
                                if (e.getClickCount() == 2) {
                                    doAddFriend(u, dialog);
                                }
                            }
                            public void mouseEntered(MouseEvent e) { sel = true;  repaint(); }
                            public void mouseExited(MouseEvent e)  {
                                sel = Boolean.TRUE.equals(getClientProperty("sel"));
                                repaint();
                            }
                        });
                    }
                    @Override protected void paintComponent(Graphics g) {
                        boolean isSel = Boolean.TRUE.equals(getClientProperty("sel")) || sel;
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(isSel ? new Color(0,210,160,40) : new Color(255,255,255,8));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        if (isSel) {
                            g2.setColor(ACCENT);
                            g2.setStroke(new BasicStroke(1.5f));
                            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                row.setOpaque(false);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
                row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                row.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JPanel mini = miniAvatar(u, 36);
                row.add(mini, BorderLayout.WEST);

                JPanel textCol = new JPanel(new GridLayout(2, 1, 0, 2));
                textCol.setOpaque(false);
                JLabel uName = new JLabel(u);
                uName.setFont(new Font("Segoe UI", Font.BOLD, 13));
                uName.setForeground(Color.WHITE);
                textCol.add(uName);
                if (fp != null) {
                    JLabel uStats = new JLabel(fp.getTotalGamesPlayed() + " games played");
                    uStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    uStats.setForeground(TEXT_DIM);
                    textCol.add(uStats);
                }
                row.add(textCol, BorderLayout.CENTER);
                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(3));
            }
            listPanel.revalidate();
            listPanel.repaint();
        };
        populate.run();

        dSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { populate.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { populate.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { populate.run(); }
        });

        JScrollPane dScroll = new JScrollPane(listPanel);
        dScroll.setOpaque(false);
        dScroll.getViewport().setOpaque(false);
        dScroll.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,25), 1));
        dScroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        center.add(dSearch, BorderLayout.NORTH);
        center.add(dScroll, BorderLayout.CENTER);
        dp.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(1, 2, 12, 0));
        footer.setOpaque(false);
        JButton addBtn = pillBtn("Add Friend", ACCENT, ACCENT.darker());
        addBtn.addActionListener(e -> {
            if (selectedUser[0] == null) return;
            doAddFriend(selectedUser[0], dialog);
        });
        JButton cancelBtn = pillBtn("Cancel", new Color(80,40,40), new Color(110,60,60));
        cancelBtn.addActionListener(e -> dialog.dispose());
        footer.add(addBtn);
        footer.add(cancelBtn);
        dp.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void doAddFriend(String username, JDialog dialog) {
        player.addFriend(username);
        DatabaseFactory.getDatabase().save();
        dialog.dispose();
        rebuildFriendList();
    }

    // ── Remove friend confirmation ────────────────────────────────────────────
    private void confirmRemove(String username) {
        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.setSize(380, 200);
        dialog.setLocationRelativeTo(frame);

        JPanel dp = new JPanel(new BorderLayout(0, 16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,60,60), 2),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        dialog.setContentPane(dp);

        JLabel msg = new JLabel("<html><center>Remove <b>" + username + "</b> from friends?</center></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        msg.setForeground(Color.WHITE);
        msg.setHorizontalAlignment(JLabel.CENTER);
        dp.add(msg, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(1, 2, 12, 0));
        btns.setOpaque(false);
        JButton yes = pillBtn("Remove", new Color(160,40,40), new Color(200,60,60));
        yes.addActionListener(e -> {
            player.removeFriend(username);
            DatabaseFactory.getDatabase().save();
            dialog.dispose();
            selectedFriend = null;
            rebuildFriendList();
            Container parent = rightPanel.getParent();
            if (parent != null) {
                parent.remove(rightPanel);
                rightPanel = buildRightPlaceholder();
                parent.add(rightPanel, BorderLayout.CENTER);
                parent.revalidate();
                parent.repaint();
            }
        });
        JButton no = pillBtn("Cancel", new Color(50,60,100), new Color(70,80,130));
        no.addActionListener(e -> dialog.dispose());
        btns.add(yes);
        btns.add(no);
        dp.add(btns, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel miniAvatar(String username, int size) {
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = avatarColor(username);
                g2.setPaint(new GradientPaint(0, 0, c1, size, size, c1.darker()));
                g2.fillOval(0, 0, size, size);
                g2.setColor(Color.WHITE);
                int fs = size / 2 - 2;
                g2.setFont(new Font("Segoe UI", Font.BOLD, fs));
                FontMetrics fm = g2.getFontMetrics();
                String l = username.substring(0,1).toUpperCase();
                g2.drawString(l, size/2 - fm.stringWidth(l)/2, size/2 + fm.getAscent()/2 - 2);
                g2.dispose();
            }
        };
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(size, size));
        av.setMinimumSize(new Dimension(size, size));
        av.setMaximumSize(new Dimension(size, size));
        return av;
    }

    private JLabel statChip(String text, Color bg) {
        JLabel chip = new JLabel("  " + text + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        chip.setForeground(new Color(200, 220, 255));
        chip.setOpaque(false);
        return chip;
    }

    private JButton pillBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.brighter(), 1),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton sidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT.darker(), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    private String calcRank(int wins) {
        if (wins <= 5)  return "Rookie";
        if (wins <= 15) return "Challenger";
        if (wins <= 30) return "Expert";
        if (wins <= 50) return "Master";
        return "Legend";
    }

    private Color rankColor(String rank) {
        switch (rank) {
            case "Rookie":     return new Color(150, 180, 200);
            case "Challenger": return new Color(80,  200, 120);
            case "Expert":     return new Color(80,  160, 255);
            case "Master":     return new Color(200, 160, 40);
            default:           return new Color(220, 80,  255);
        }
    }

    private Color avatarColor(String username) {
        Color[] palette = {
            new Color(0, 150, 200), new Color(180, 60, 200),
            new Color(200, 100, 0), new Color(0, 180, 100),
            new Color(200, 50, 80), new Color(80, 120, 220)
        };
        return palette[Math.abs(username.hashCode()) % palette.length];
    }
}

// ── WrapLayout ────────────────────────────────────────────────────────────────
class WrapLayout extends FlowLayout {
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }
    @Override public Dimension minimumLayoutSize(Container target)   { return layoutSize(target, false); }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
            int hgap = getHgap(), vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = targetWidth - insets.left - insets.right;
            int x = 0, y = insets.top + vgap, rowHeight = 0;
            for (Component c : target.getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                if (x > 0 && x + d.width > maxWidth) { y += rowHeight + vgap; x = 0; rowHeight = 0; }
                x += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
            }
            y += rowHeight + vgap + insets.bottom;
            return new Dimension(targetWidth, y);
        }
    }
}
