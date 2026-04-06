import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ProfileWindow {
    private JFrame frame;
    private JFrame parentFrame;
    private Player player;

    private static final Color BG1      = new Color(8,  12, 28);
    private static final Color BG2      = new Color(18, 24, 52);
    private static final Color CARD     = new Color(22, 32, 62);
    private static final Color ACCENT   = new Color(0,  210, 160);
    private static final Color ACCENT2  = new Color(100, 80, 255);
    private static final Color TEXT_DIM = new Color(160, 175, 210);
    private static final Color CHESS_C  = new Color(184, 134, 11);
    private static final Color LOGIC_C  = new Color(25,  103, 210);
    private static final Color MEM_C    = new Color(147, 51,  219);

    // who is viewing — null means the player is viewing their own profile
    private Player viewerPlayer;

    // Particle fields
    private float[][] particles;
    private javax.swing.Timer particleTimer;
    private javax.swing.Timer glowTimer;
    private float glowAlpha = 0f;
    private boolean glowUp = true;

    // Progress animation
    private float[] barProgress = {0f, 0f, 0f};
    private float[] barTargets  = {0f, 0f, 0f};
    private javax.swing.Timer barTimer;

    public ProfileWindow(Player player) { this(player, null, null); }
    public ProfileWindow(Player player, JFrame parentFrame) { this(player, parentFrame, null); }

    /** viewerPlayer = the logged-in player looking at someone else's profile. Pass null for own profile. */
    public ProfileWindow(Player player, JFrame parentFrame, Player viewerPlayer) {
        this.player = player;
        this.parentFrame = parentFrame;
        this.viewerPlayer = viewerPlayer;

        frame = new JFrame("Profile — " + player.getUsername());
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
        initBarTargets();

        JPanel root = createBgPanel();
        root.setLayout(new BorderLayout(0, 0));
        frame.setContentPane(root);

        root.add(buildBanner(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(16, 32, 0, 32));
        content.add(buildLeftColumn(), BorderLayout.WEST);
        content.add(buildRightColumn(), BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);

        startTimers(root);
        frame.setVisible(true);
        startBarAnimation();
    }

    // ── Particle init ─────────────────────────────────────────────────────────
    private void initParticles() {
        particles = new float[30][5]; // x, y, speed, alpha, size
        Random rng = new Random();
        for (int i = 0; i < 30; i++) {
            particles[i][0] = rng.nextFloat() * 1920;
            particles[i][1] = rng.nextFloat() * 1080;
            particles[i][2] = 0.3f + rng.nextFloat() * 0.7f;
            particles[i][3] = 0.2f + rng.nextFloat() * 0.5f;
            particles[i][4] = 2f   + rng.nextFloat() * 3f;
        }
    }

    private void initBarTargets() {
        GameStats cs = player.getChessStats();
        GameStats ls = player.getLogicStats();
        GameStats ms = player.getMemoryStats();
        barTargets[0] = (float) cs.getWinPercentage() / 100f;
        barTargets[1] = (float) ls.getWinPercentage() / 100f;
        barTargets[2] = (float) ms.getWinPercentage() / 100f;
    }

    private void startTimers(JPanel root) {
        particleTimer = new javax.swing.Timer(50, e -> {
            for (float[] p : particles) {
                p[1] -= p[2];
                if (p[1] < -10) {
                    p[1] = root.getHeight() + 10;
                    p[0] = (float)(Math.random() * root.getWidth());
                }
            }
            root.repaint();
        });
        particleTimer.start();

        glowTimer = new javax.swing.Timer(30, e -> {
            if (glowUp) { glowAlpha += 0.03f; if (glowAlpha >= 1f) glowUp = false; }
            else        { glowAlpha -= 0.03f; if (glowAlpha <= 0f) glowUp = true;  }
            root.repaint();
        });
        glowTimer.start();
    }

    private void startBarAnimation() {
        barTimer = new javax.swing.Timer(16, null);
        barTimer.addActionListener(e -> {
            boolean done = true;
            for (int i = 0; i < 3; i++) {
                if (barProgress[i] < barTargets[i]) {
                    barProgress[i] = Math.min(barProgress[i] + 0.016f, barTargets[i]);
                    done = false;
                }
            }
            frame.repaint();
            if (done) barTimer.stop();
        });
        barTimer.start();
    }

    private void stopTimers() {
        if (particleTimer != null) particleTimer.stop();
        if (glowTimer     != null) glowTimer.stop();
        if (barTimer      != null) barTimer.stop();
    }

    // ── Background panel with particles ──────────────────────────────────────
    private JPanel createBgPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // diagonal stripe overlay
                g2.setColor(new Color(255, 255, 255, 6));
                for (int x = -getHeight(); x < getWidth() + getHeight(); x += 28) {
                    g2.drawLine(x, 0, x + getHeight(), getHeight());
                }
                // particles
                for (float[] p : particles) {
                    int alpha = (int)(p[3] * 200);
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), alpha));
                    int sz = (int) p[4];
                    g2.fillOval((int)p[0], (int)p[1], sz, sz);
                }
                g2.dispose();
            }
        };
    }

    // ── Banner ────────────────────────────────────────────────────────────────
    private JPanel buildBanner() {
        JPanel banner = new JPanel(new BorderLayout(20, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(14, 20, 48), getWidth(), 0, new Color(28, 38, 80)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // diagonal stripes
                g2.setColor(new Color(255, 255, 255, 8));
                for (int x = -getHeight(); x < getWidth() + getHeight(); x += 22)
                    g2.drawLine(x, 0, x + getHeight(), getHeight());
                // bottom border
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));
        banner.setPreferredSize(new Dimension(0, 180));

        // Avatar
        JPanel avatarWrap = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // glow ring
                int glowA = (int)(glowAlpha * 120);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), glowA));
                g2.setStroke(new BasicStroke(6f));
                g2.drawOval(4, 4, 112, 112);
                // avatar circle gradient
                Color c1 = avatarColor(player.getUsername());
                g2.setPaint(new GradientPaint(10, 10, c1, 110, 110, c1.darker().darker()));
                g2.fillOval(10, 10, 100, 100);
                // initial letter
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
                FontMetrics fm = g2.getFontMetrics();
                String letter = player.getUsername().substring(0, 1).toUpperCase();
                g2.drawString(letter, 60 - fm.stringWidth(letter)/2, 60 + fm.getAscent()/2 - 4);
                g2.dispose();
            }
        };
        avatarWrap.setOpaque(false);
        avatarWrap.setPreferredSize(new Dimension(120, 120));

        // Info block
        JPanel infoBlock = new JPanel();
        infoBlock.setLayout(new BoxLayout(infoBlock, BoxLayout.Y_AXIS));
        infoBlock.setOpaque(false);

        JLabel nameLabel = new JLabel(player.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        nameLabel.setForeground(Color.WHITE);
        infoBlock.add(nameLabel);
        infoBlock.add(Box.createVerticalStrut(6));

        // Rank badge
        String rank = calcRank(player.getTotalGamesWon());
        Color rankColor = rankColor(rank);
        JLabel rankBadge = new JLabel("  " + rank + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(rankColor.getRed(), rankColor.getGreen(), rankColor.getBlue(), 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(rankColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        rankBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rankBadge.setForeground(rankColor);
        rankBadge.setOpaque(false);
        rankBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoBlock.add(rankBadge);
        infoBlock.add(Box.createVerticalStrut(10));

        // Stat chips row
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        int total = player.getTotalGamesPlayed();
        int wins  = player.getTotalGamesWon();
        double wr = total > 0 ? (wins * 100.0 / total) : 0;
        chips.add(statChip("🎮 " + total + " played", new Color(40, 60, 100)));
        chips.add(statChip("🏆 " + wins + " wins",    new Color(40, 80, 60)));
        chips.add(statChip(String.format("📈 %.0f%% WR", wr), new Color(60, 40, 100)));
        infoBlock.add(chips);

        // Right side buttons — differ based on own vs other's profile
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.setOpaque(false);

        if (viewerPlayer == null) {
            // Own profile — show Edit + Friends
            JButton editBtn = pillBtn("✏ Edit Profile", ACCENT, ACCENT.darker());
            editBtn.addActionListener(e -> openLichessLinkDialog());
            JButton friendsBtn = pillBtn("👥 Friends", ACCENT2, ACCENT2.darker());
            friendsBtn.addActionListener(e -> {
                frame.dispose();
                new FriendsWindow(player, parentFrame);
            });
            rightBtns.add(editBtn);
            rightBtns.add(friendsBtn);
        } else {
            // Viewing someone else — show Add/Remove Friend
            boolean isFriend = viewerPlayer.getFriends().contains(player.getUsername());
            if (isFriend) {
                JButton removeBtn = pillBtn("✖ Remove Friend", new Color(160, 40, 40), new Color(200, 60, 60));
                removeBtn.addActionListener(e -> {
                    viewerPlayer.removeFriend(player.getUsername());
                    DatabaseFactory.getDatabase().save();
                    frame.dispose();
                });
                rightBtns.add(removeBtn);
            } else {
                JButton addBtn = pillBtn("＋ Add Friend", ACCENT, ACCENT.darker());
                addBtn.addActionListener(e -> {
                    viewerPlayer.addFriend(player.getUsername());
                    DatabaseFactory.getDatabase().save();
                    // swap button to Remove Friend
                    rightBtns.removeAll();
                    JButton removeBtn = pillBtn("✖ Remove Friend", new Color(160, 40, 40), new Color(200, 60, 60));
                    removeBtn.addActionListener(ev -> {
                        viewerPlayer.removeFriend(player.getUsername());
                        DatabaseFactory.getDatabase().save();
                        frame.dispose();
                    });
                    rightBtns.add(removeBtn);
                    rightBtns.revalidate();
                    rightBtns.repaint();
                });
                rightBtns.add(addBtn);
            }
        }

        banner.add(avatarWrap, BorderLayout.WEST);
        banner.add(infoBlock,  BorderLayout.CENTER);
        banner.add(rightBtns,  BorderLayout.EAST);
        return banner;
    }

    // ── Left column: Game Stats ───────────────────────────────────────────────
    private JPanel buildLeftColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setPreferredSize(new Dimension(380, 0));

        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel title = sectionTitle("GAME STATS");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        GameStats[] stats  = {player.getChessStats(), player.getLogicStats(), player.getMemoryStats()};
        String[]    names  = {"♟ Chess", "🧩 Logic", "🧠 Memory"};
        Color[]     colors = {CHESS_C, LOGIC_C, MEM_C};

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            card.add(buildGameStatRow(names[i], stats[i], colors[i], idx));
            if (i < 2) card.add(Box.createVerticalStrut(18));
        }

        card.add(Box.createVerticalStrut(18));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,20));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(12));

        int totalSec = player.getTotalTimeSpent();
        int h = totalSec/3600, m = (totalSec%3600)/60, s = totalSec%60;
        JLabel timeLabel = new JLabel(String.format("⏱ Total time: %dh %dm %ds", h, m, s));
        timeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        timeLabel.setForeground(TEXT_DIM);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(timeLabel);

        col.add(card);
        return col;
    }

    private JPanel buildGameStatRow(String name, GameStats stats, Color color, int idx) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(nameLabel);
        row.add(Box.createVerticalStrut(6));

        // Animated progress bar
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // track
                g2.setColor(new Color(255,255,255,20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                // fill
                int fillW = (int)(getWidth() * barProgress[idx]);
                if (fillW > 0) {
                    g2.setPaint(new GradientPaint(0, 0, color, fillW, 0, color.brighter()));
                    g2.fillRoundRect(0, 0, fillW, getHeight(), getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 10));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(bar);
        row.add(Box.createVerticalStrut(5));

        double wr = stats.getWinPercentage();
        JLabel detail = new JLabel(String.format("%d played  •  %d won  •  %.0f%% WR",
            stats.getTotalGames(), stats.getTotalWins(), wr));
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detail.setForeground(TEXT_DIM);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(detail);

        return row;
    }

    // ── Right column: Achievements + Account Info ─────────────────────────────
    private JPanel buildRightColumn() {
        JPanel col = new JPanel(new GridLayout(2, 1, 0, 16));
        col.setOpaque(false);
        col.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        col.add(buildAchievementsCard());
        col.add(buildAccountCard());
        return col;
    }

    private JPanel buildAchievementsCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel title = sectionTitle("ACHIEVEMENTS");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        java.util.List<String[]> achs = computeAchievements();

        if (achs.isEmpty()) {
            JLabel empty = new JLabel("Play games to unlock achievements!");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(TEXT_DIM);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(empty);
        } else {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            wrap.setOpaque(false);
            wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (String[] ach : achs) {
                wrap.add(achievementBadge(ach[0], ach[1]));
            }
            card.add(wrap);
        }
        return card;
    }

    private java.util.List<String[]> computeAchievements() {
        java.util.List<String[]> list = new ArrayList<>();
        int totalWins   = player.getTotalGamesWon();
        int totalGames  = player.getTotalGamesPlayed();
        double wr       = totalGames > 0 ? (totalWins * 100.0 / totalGames) : 0;

        if (totalWins >= 1)  list.add(new String[]{"🥇", "First Win"});
        if (player.getLogicStats().getTotalWins() >= 5)  list.add(new String[]{"🧩", "Logic Master"});
        if (player.getMemoryStats().getTotalWins() >= 5) list.add(new String[]{"🧠", "Memory Champion"});
        if (player.getLichessUsername() != null && !player.getLichessUsername().isEmpty())
            list.add(new String[]{"♟", "Chess Enthusiast"});
        if (totalGames >= 20) list.add(new String[]{"🎖", "Veteran"});
        if (wr >= 70)         list.add(new String[]{"⚡", "Unstoppable"});
        return list;
    }

    private JLabel achievementBadge(String icon, String name) {
        JLabel badge = new JLabel("  " + icon + " " + name + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        badge.setForeground(ACCENT);
        badge.setOpaque(false);
        return badge;
    }

    private JPanel buildAccountCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel title = sectionTitle("ACCOUNT INFO");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        // Email masked
        String email = player.getEmail();
        String maskedEmail = maskEmail(email);
        card.add(infoRow("📧", "Email", maskedEmail));
        card.add(Box.createVerticalStrut(10));

        // Lichess
        String lichess = player.getLichessUsername();
        if (lichess != null && !lichess.isEmpty()) {
            card.add(infoRow("♟", "Lichess", lichess));
        } else if (viewerPlayer == null) {
            // Only show "Link Now" on own profile
            JPanel lichessRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            lichessRow.setOpaque(false);
            lichessRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel lbl = new JLabel("♟  Lichess:  Not linked");
            lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            lbl.setForeground(TEXT_DIM);
            JButton linkBtn = pillBtn("Link Now", ACCENT, ACCENT.darker());
            linkBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            linkBtn.addActionListener(e -> openLichessLinkDialog());
            lichessRow.add(lbl);
            lichessRow.add(linkBtn);
            card.add(lichessRow);
        } else {
            card.add(infoRow("♟", "Lichess", "Not linked"));
        }
        card.add(Box.createVerticalStrut(10));
        card.add(infoRow("👥", "Friends", String.valueOf(player.getFriends().size())));
        card.add(Box.createVerticalStrut(10));
        card.add(infoRow("👤", "Username", player.getUsername()));

        return card;
    }

    private JPanel infoRow(String icon, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(icon + "  " + label + ":");
        lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        lbl.setForeground(TEXT_DIM);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(Color.WHITE);
        row.add(lbl);
        row.add(val);
        return row;
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "Not set";
        int at = email.indexOf('@');
        if (at < 0) return email;
        String local  = email.substring(0, at);
        String domain = email.substring(at);
        if (local.length() <= 2) return local + "***" + domain;
        return local.substring(0, 2) + "***" + domain;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 32, 20, 32));
        JButton backBtn = pillBtn("⬅ Back", new Color(50, 60, 100), new Color(70, 80, 130));
        backBtn.addActionListener(e -> frame.dispose());
        bar.add(backBtn, BorderLayout.WEST);
        return bar;
    }

    // ── Lichess dialog ────────────────────────────────────────────────────────
    private void openLichessLinkDialog() {
        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.setSize(460, 280);
        dialog.setLocationRelativeTo(frame);

        JPanel dp = new JPanel(new BorderLayout(0, 16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        dialog.setContentPane(dp);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel dlgTitle = new JLabel("♟  Link Lichess Account");
        dlgTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        dlgTitle.setForeground(ACCENT);
        header.add(dlgTitle, BorderLayout.WEST);
        JButton xBtn = new JButton("×");
        xBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        xBtn.setForeground(new Color(255, 100, 100));
        xBtn.setContentAreaFilled(false);
        xBtn.setBorderPainted(false);
        xBtn.setFocusPainted(false);
        xBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        xBtn.addActionListener(e -> dialog.dispose());
        header.add(xBtn, BorderLayout.EAST);
        dp.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setOpaque(false);
        JLabel hint = new JLabel("Enter your Lichess username to link your account.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(TEXT_DIM);
        form.add(hint);

        JTextField field = new JTextField(player.getLichessUsername() != null ? player.getLichessUsername() : "");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(14, 22, 48));
        field.setForeground(Color.WHITE);
        field.setCaretColor(ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        form.add(field);
        dp.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(1, 2, 12, 0));
        footer.setOpaque(false);
        JButton saveBtn = pillBtn("Save", ACCENT, ACCENT.darker());
        saveBtn.addActionListener(e -> {
            String val = field.getText().trim();
            if (val.isEmpty()) return;
            player.setLichessUsername(val);
            DatabaseFactory.getDatabase().save();
            dialog.dispose();
            frame.dispose();
            new ProfileWindow(player, parentFrame);
        });
        JButton cancelBtn = pillBtn("Cancel", new Color(80, 40, 40), new Color(110, 60, 60));
        cancelBtn.addActionListener(e -> dialog.dispose());
        footer.add(saveBtn);
        footer.add(cancelBtn);
        dp.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel createCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
    }

    private JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(ACCENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        return lbl;
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

// ── RoundedBorderPW ───────────────────────────────────────────────────────────
class RoundedBorderPW extends javax.swing.border.AbstractBorder {
    private int radius;
    private Color color;
    public RoundedBorderPW(int radius, Color color) {
        this.radius = radius;
        this.color  = color;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
    @Override
    public Insets getBorderInsets(Component c) { return new Insets(2, 2, 2, 2); }
}
