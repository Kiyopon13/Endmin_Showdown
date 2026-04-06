import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Utilities for configuring a more modern, "optimistic" look and feel for the
 * application.  Call {@link #initLookAndFeel()} once before constructing any
 * frames.
 */
public class UIUtils {
    public static void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Could not set Nimbus LAF, falling back to default: " + e);
        }

        Font uiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
        if (uiFont.getName().equals("Dialog")) {
            uiFont = new Font("DejaVu Sans", Font.PLAIN, 14);
        }
        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("PasswordField.font", uiFont);
        UIManager.put("ComboBox.font", uiFont);
        UIManager.put("Table.font", uiFont);

        UIManager.put("Panel.background", new Color(245, 245, 245));
        UIManager.put("Button.background", new Color(100, 150, 255));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.DARK_GRAY);
        UIManager.put("Label.foreground", Color.DARK_GRAY);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    // ── A) animateFadeIn ──────────────────────────────────────────────────────
    public static void animateFadeIn(JFrame frame, int durationMs) {
        try {
            frame.setOpacity(0f);
            int steps = Math.max(1, durationMs / 16);
            float delta = 1f / steps;
            float[] op = {0f};
            javax.swing.Timer t = new javax.swing.Timer(16, null);
            t.addActionListener(ev -> {
                op[0] = Math.min(op[0] + delta, 1f);
                try { frame.setOpacity(op[0]); } catch (Exception ignored) {}
                if (op[0] >= 1f) t.stop();
            });
            t.start();
        } catch (Exception ignored) {}
    }

    // ── B) createFadeInPanel ──────────────────────────────────────────────────
    public static JPanel createFadeInPanel(LayoutManager layout, int delayMs) {
        final float[] alpha = {0f};
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        javax.swing.Timer delay = new javax.swing.Timer(delayMs, null);
        delay.setRepeats(false);
        delay.addActionListener(ev -> {
            javax.swing.Timer fade = new javax.swing.Timer(16, null);
            fade.addActionListener(e2 -> {
                alpha[0] = Math.min(alpha[0] + 0.04f, 1f);
                panel.repaint();
                if (alpha[0] >= 1f) fade.stop();
            });
            fade.start();
        });
        delay.start();

        return panel;
    }

    // ── C) createSlideInPanel ─────────────────────────────────────────────────
    public static JPanel createSlideInPanel(LayoutManager layout, String direction, int delayMs) {
        final int[] offsetX = {0};
        final int[] offsetY = {0};
        final boolean[] initialized = {false};

        JPanel panel = new JPanel(layout) {
            @Override
            public void addNotify() {
                super.addNotify();
                if (!initialized[0]) {
                    initialized[0] = true;
                    int w = getWidth()  > 0 ? getWidth()  : 800;
                    int h = getHeight() > 0 ? getHeight() : 600;
                    switch (direction) {
                        case "LEFT":   offsetX[0] = -w; break;
                        case "RIGHT":  offsetX[0] =  w; break;
                        case "TOP":    offsetY[0] = -h; break;
                        case "BOTTOM": offsetY[0] =  h; break;
                    }
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(offsetX[0], offsetY[0]);
                super.paintComponent(g2d);
                g2d.dispose();
            }

            @Override
            protected void paintChildren(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(offsetX[0], offsetY[0]);
                super.paintChildren(g2d);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        // Set initial large offset before panel is shown
        switch (direction) {
            case "LEFT":   offsetX[0] = -1200; break;
            case "RIGHT":  offsetX[0] =  1200; break;
            case "TOP":    offsetY[0] = -800;  break;
            case "BOTTOM": offsetY[0] =  800;  break;
        }

        javax.swing.Timer delay = new javax.swing.Timer(delayMs, null);
        delay.setRepeats(false);
        delay.addActionListener(ev -> {
            javax.swing.Timer slide = new javax.swing.Timer(16, null);
            slide.addActionListener(e2 -> {
                offsetX[0] = (int)(offsetX[0] * 0.82);
                offsetY[0] = (int)(offsetY[0] * 0.82);
                if (Math.abs(offsetX[0]) < 2) offsetX[0] = 0;
                if (Math.abs(offsetY[0]) < 2) offsetY[0] = 0;
                panel.repaint();
                if (offsetX[0] == 0 && offsetY[0] == 0) slide.stop();
            });
            slide.start();
        });
        delay.start();

        return panel;
    }

    // ── D) createRippleButton ─────────────────────────────────────────────────
    public static JButton createRippleButton(String text, Color bg, Color rippleColor) {
        final int[] rippleX = {0};
        final int[] rippleY = {0};
        final int[] rippleRadius = {0};
        final int[] rippleAlpha = {0};
        final javax.swing.Timer[] rippleTimer = {null};

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded background
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Draw ripple
                if (rippleRadius[0] > 0 && rippleAlpha[0] > 0) {
                    Color rc = new Color(
                        rippleColor.getRed(),
                        rippleColor.getGreen(),
                        rippleColor.getBlue(),
                        Math.min(rippleAlpha[0], 255)
                    );
                    g2d.setColor(rc);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        Math.min(rippleAlpha[0] / 255f, 1f)));
                    int r = rippleRadius[0];
                    g2d.fillOval(rippleX[0] - r, rippleY[0] - r, r * 2, r * 2);
                }

                // Draw text
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics(getFont());
                g2d.setFont(getFont());
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), tx, ty);

                g2d.dispose();
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                rippleX[0] = e.getX();
                rippleY[0] = e.getY();
                rippleRadius[0] = 0;
                rippleAlpha[0] = 120;

                if (rippleTimer[0] != null) rippleTimer[0].stop();
                int maxR = (int) Math.sqrt(btn.getWidth() * btn.getWidth() + btn.getHeight() * btn.getHeight()) + 10;
                rippleTimer[0] = new javax.swing.Timer(16, null);
                rippleTimer[0].addActionListener(ev -> {
                    rippleRadius[0] += 8;
                    rippleAlpha[0] = Math.max(0, rippleAlpha[0] - 4);
                    btn.repaint();
                    if (rippleRadius[0] > maxR || rippleAlpha[0] <= 0) {
                        rippleRadius[0] = 0;
                        rippleAlpha[0] = 0;
                        rippleTimer[0].stop();
                    }
                });
                rippleTimer[0].start();
            }
        });

        return btn;
    }

    // ── E) createTypewriterLabel ──────────────────────────────────────────────
    public static JLabel createTypewriterLabel(String fullText, Font font, Color color, int charDelayMs) {
        JLabel label = new JLabel("");
        label.setFont(font);
        label.setForeground(color);
        final int[] idx = {0};
        javax.swing.Timer t = new javax.swing.Timer(charDelayMs, null);
        t.addActionListener(ev -> {
            if (idx[0] <= fullText.length()) {
                label.setText(fullText.substring(0, idx[0]));
                idx[0]++;
            } else {
                t.stop();
            }
        });
        t.start();
        return label;
    }

    // ── F) createPulsePanel ───────────────────────────────────────────────────
    public static JPanel createPulsePanel(Color glowColor, int radius) {
        final float[] pulseAlpha = {0f};
        final boolean[] up = {true};

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g2d);

                int w = getWidth(), h = getHeight();
                for (int i = radius; i > 0; i -= 4) {
                    float a = (i / (float) radius) * 0.6f * pulseAlpha[0];
                    if (a > 0) {
                        g2d.setColor(new Color(
                            glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                            (int)(a * 255)));
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.drawRoundRect(i, i, w - i * 2, h - i * 2, 20, 20);
                    }
                }
                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        javax.swing.Timer t = new javax.swing.Timer(30, null);
        t.addActionListener(ev -> {
            if (up[0]) { pulseAlpha[0] += 0.03f; if (pulseAlpha[0] >= 1f) up[0] = false; }
            else        { pulseAlpha[0] -= 0.03f; if (pulseAlpha[0] <= 0f) up[0] = true;  }
            panel.repaint();
        });
        t.start();

        return panel;
    }

    // ── G) createSakuraBackground ─────────────────────────────────────────────
    public static JPanel createSakuraBackground(LayoutManager layout) {
        final int PETAL_COUNT = 25;
        final float[] px    = new float[PETAL_COUNT];
        final float[] py    = new float[PETAL_COUNT];
        final float[] pspd  = new float[PETAL_COUNT];
        final float[] prot  = new float[PETAL_COUNT];
        final float[] prspd = new float[PETAL_COUNT];
        final float[] psway = new float[PETAL_COUNT];
        final float[] pswoff= new float[PETAL_COUNT];
        final int[]   psize = new int[PETAL_COUNT];
        final Color[] PETAL_COLORS = {
            new Color(255, 182, 193, 160),
            new Color(255, 105, 180, 140),
            new Color(255,  20, 147, 120)
        };

        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < PETAL_COUNT; i++) {
            px[i]    = rng.nextFloat() * 1920;
            py[i]    = rng.nextFloat() * 1080;
            pspd[i]  = 1.5f + rng.nextFloat() * 2f;
            prot[i]  = rng.nextFloat() * (float)Math.PI * 2;
            prspd[i] = (rng.nextFloat() - 0.5f) * 0.06f;
            psway[i] = rng.nextFloat() * (float)Math.PI * 2;
            pswoff[i]= rng.nextFloat() * (float)Math.PI * 2;
            psize[i] = 8 + rng.nextInt(7);
        }

        JPanel panel = new JPanel(layout) {
            private int frameCount = 0;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Dark anime gradient background
                g2d.setPaint(new GradientPaint(0, 0, new Color(5, 5, 25), 0, h, new Color(30, 10, 50)));
                g2d.fillRect(0, 0, w, h);

                // Subtle grid lines (cyber feel)
                g2d.setColor(new Color(255, 0, 255, 18));
                int horizon = h / 2 + 50;
                for (int i = -w * 2; i < w * 3; i += 80) {
                    g2d.drawLine(w / 2, horizon, i, h);
                }
                for (int i = 0; i < 20; i++) {
                    int yPos = horizon + (int)(Math.pow((i + (frameCount % 25) / 25.0), 1.8) * 2);
                    if (yPos > horizon && yPos <= h) g2d.drawLine(0, yPos, w, yPos);
                }

                // Draw sakura petals
                for (int i = 0; i < PETAL_COUNT; i++) {
                    Color c = PETAL_COLORS[i % PETAL_COLORS.length];
                    g2d.setColor(c);
                    java.awt.geom.AffineTransform old = g2d.getTransform();
                    g2d.translate(px[i], py[i]);
                    g2d.rotate(prot[i]);
                    int s = psize[i];
                    g2d.fillOval(-s / 2, -s / 4, s, s / 2);
                    g2d.setTransform(old);
                }

                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        javax.swing.Timer t = new javax.swing.Timer(30, null);
        t.addActionListener(ev -> {
            int h = panel.getHeight() > 0 ? panel.getHeight() : 1080;
            int w = panel.getWidth()  > 0 ? panel.getWidth()  : 1920;
            for (int i = 0; i < PETAL_COUNT; i++) {
                py[i]    += pspd[i];
                px[i]    += (float)(Math.sin(pswoff[i]) * 0.8f);
                pswoff[i]+= 0.04f;
                prot[i]  += prspd[i];
                if (py[i] > h + 20) {
                    py[i] = -20;
                    px[i] = (float)(Math.random() * w);
                }
            }
            panel.repaint();
        });
        t.start();

        return panel;
    }

    // ── H) animateButtonHover ─────────────────────────────────────────────────
    public static void animateButtonHover(JButton btn, Color normalBg, Color hoverBg,
                                          Color normalFg, Color hoverFg) {
        final float[] t = {0f};
        final boolean[] hovering = {false};
        javax.swing.Timer timer = new javax.swing.Timer(16, null);
        timer.addActionListener(ev -> {
            if (hovering[0]) {
                t[0] = Math.min(t[0] + 0.1f, 1f);
            } else {
                t[0] = Math.max(t[0] - 0.1f, 0f);
            }
            float f = t[0];
            Color bg = new Color(
                (int)(normalBg.getRed()   + (hoverBg.getRed()   - normalBg.getRed())   * f),
                (int)(normalBg.getGreen() + (hoverBg.getGreen() - normalBg.getGreen()) * f),
                (int)(normalBg.getBlue()  + (hoverBg.getBlue()  - normalBg.getBlue())  * f)
            );
            Color fg = new Color(
                (int)(normalFg.getRed()   + (hoverFg.getRed()   - normalFg.getRed())   * f),
                (int)(normalFg.getGreen() + (hoverFg.getGreen() - normalFg.getGreen()) * f),
                (int)(normalFg.getBlue()  + (hoverFg.getBlue()  - normalFg.getBlue())  * f)
            );
            btn.setBackground(bg);
            btn.setForeground(fg);
            btn.repaint();
            if (t[0] == 0f || t[0] == 1f) timer.stop();
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovering[0] = true;
                if (!timer.isRunning()) timer.start();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovering[0] = false;
                if (!timer.isRunning()) timer.start();
            }
        });
    }


    // ── GradientPanel (kept for compatibility) ────────────────────────────────
    public static class GradientPanel extends JPanel {
        private final LayoutManager layout;
        public GradientPanel(LayoutManager layout) {
            this.layout = layout;
            setLayout(layout);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color start = new Color(200, 230, 255);
            Color end   = new Color(255, 255, 255);
            int w = getWidth(), h = getHeight();
            g2d.setPaint(new GradientPaint(0, 0, start, 0, h, end));
            g2d.fillRect(0, 0, w, h);
        }
    }

    // ── createPremiumBackgroundPanel ──────────────────────────────────────────
    public static JPanel createPremiumBackgroundPanel(LayoutManager layout) {
        return new JPanel(layout) {
            private int frameCount = 0;
            private Timer timer;
            {
                timer = new Timer(30, e -> { frameCount++; repaint(); });
                timer.start();
            }

            private void drawTetromino(Graphics2D g2d, int x, int y, int type, float rot, int size, Color c) {
                g2d.translate(x, y);
                g2d.rotate(rot);
                g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
                switch (type) {
                    case 0:
                        g2d.fillRect(-size, -size, size, size);
                        g2d.fillRect(0, -size, size, size);
                        g2d.fillRect(-size, 0, size, size);
                        g2d.fillRect(0, 0, size, size);
                        break;
                    case 1:
                        g2d.fillRect(-size*2, -size/2, size, size);
                        g2d.fillRect(-size, -size/2, size, size);
                        g2d.fillRect(0, -size/2, size, size);
                        g2d.fillRect(size, -size/2, size, size);
                        break;
                    case 2:
                        g2d.fillRect(-size, 0, size, size);
                        g2d.fillRect(0, 0, size, size);
                        g2d.fillRect(size, 0, size, size);
                        g2d.fillRect(0, -size, size, size);
                        break;
                }
                g2d.rotate(-rot);
                g2d.translate(-x, -y);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();

                GradientPaint bgGradient = new GradientPaint(0, 0, new Color(5, 5, 25), 0, h, new Color(50, 10, 60));
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, w, h);

                g2d.setColor(new Color(255, 0, 255, 50));
                int horizon = h / 2 + 50;
                for (int i = -w * 2; i < w * 3; i += 80) g2d.drawLine(w / 2, horizon, i, h);
                for (int i = 0; i < 25; i++) {
                    int yPos = horizon + (int)(Math.pow((i + (frameCount % 25) / 25.0), 1.8) * 2);
                    if (yPos > horizon && yPos <= h) g2d.drawLine(0, yPos, w, yPos);
                }

                int[][] shapes = {{0,200,0,255,255},{1,150,255,0,255},{2,180,255,255,0},{0,220,0,255,0},{2,170,255,100,0}};
                for (int i = 0; i < 5; i++) {
                    int type = shapes[i][0], speed = shapes[i][1];
                    Color c = new Color(shapes[i][2], shapes[i][3], shapes[i][4]);
                    int x = (w / 5) * i + (w / 10);
                    int dy = (int)((frameCount * (1000.0 / speed)) % (h + 200)) - 100;
                    float rot = frameCount * 0.015f * (i % 2 == 0 ? 1 : -1);
                    drawTetromino(g2d, x, dy, type, rot, 25, c);
                }

                int pacX = (frameCount * 3) % (w + 400) - 200;
                int pacY = 80;
                int mouthAngle = (int)(Math.abs(Math.sin(frameCount * 0.2)) * 35);
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillArc(pacX, pacY, 70, 70, mouthAngle, 360 - mouthAngle * 2);
                int ghostX = pacX - 110;
                g2d.setColor(new Color(255, 50, 50, 100));
                g2d.fillArc(ghostX, pacY, 70, 70, 0, 180);
                g2d.fillRect(ghostX, pacY + 35, 70, 35);
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ghostX + 15, pacY + 15, 15, 15);
                g2d.fillOval(ghostX + 40, pacY + 15, 15, 15);
                g2d.setColor(new Color(0, 0, 255, 150));
                g2d.fillOval(ghostX + 20, pacY + 20, 6, 6);
                g2d.fillOval(ghostX + 45, pacY + 20, 6, 6);
                g2d.dispose();
            }
        };
    }

    // ── createChessThemedPanel ────────────────────────────────────────────────
    public static JPanel createChessThemedPanel(LayoutManager layout) {
        return new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 28, 16),
                        getWidth(), getHeight(), new Color(120, 90, 50));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(0, 0, 0, 20));
                int squareSize = 40;
                for (int x = 0; x < getWidth(); x += squareSize * 2)
                    for (int y = 0; y < getHeight(); y += squareSize * 2) {
                        g2d.fillRect(x, y, squareSize, squareSize);
                        g2d.fillRect(x + squareSize, y + squareSize, squareSize, squareSize);
                    }
            }
        };
    }

    // ── createMemoryThemedPanel ───────────────────────────────────────────────
    public static JPanel createMemoryThemedPanel(LayoutManager layout) {
        return new JPanel(layout) {
            private long startTime = System.currentTimeMillis();
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color1 = new Color(138, 43, 226);
                Color color2 = new Color(0, 206, 209);
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 30));
                int circleSize = 60;
                for (int x = 0; x < getWidth(); x += 120)
                    for (int y = 0; y < getHeight(); y += 120)
                        g2d.fillOval(x, y, circleSize, circleSize);
            }
        };
    }

    // ── createLogicThemedPanel ────────────────────────────────────────────────
    public static JPanel createLogicThemedPanel(LayoutManager layout) {
        return new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(15, 32, 80),
                        getWidth(), getHeight(), new Color(75, 15, 100));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(100, 150, 255, 40));
                int gridSize = 50;
                for (int x = 0; x < getWidth(); x += gridSize) g2d.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += gridSize) g2d.drawLine(0, y, getWidth(), y);
            }
        };
    }

    // ── createLeaderboardThemedPanel ──────────────────────────────────────────
    public static JPanel createLeaderboardThemedPanel(LayoutManager layout, String gameType) {
        return new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient;
                if ("chess".equalsIgnoreCase(gameType)) {
                    gradient = new GradientPaint(0, 0, new Color(60, 40, 20), getWidth(), getHeight(), new Color(140, 110, 70));
                } else if ("memory".equalsIgnoreCase(gameType)) {
                    gradient = new GradientPaint(0, 0, new Color(128, 29, 196), getWidth(), getHeight(), new Color(0, 188, 212));
                } else if ("logic".equalsIgnoreCase(gameType)) {
                    gradient = new GradientPaint(0, 0, new Color(25, 45, 110), getWidth(), getHeight(), new Color(85, 25, 130));
                } else {
                    gradient = new GradientPaint(0, 0, new Color(34, 139, 87), getWidth(), getHeight(), new Color(0, 150, 136));
                }
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    // ── createLeaderboardCardPanel ────────────────────────────────────────────
    public static JPanel createLeaderboardCardPanel(String gameType) {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                Color borderColor;
                if ("chess".equalsIgnoreCase(gameType))       borderColor = new Color(139, 69, 19);
                else if ("memory".equalsIgnoreCase(gameType)) borderColor = new Color(147, 51, 219);
                else if ("logic".equalsIgnoreCase(gameType))  borderColor = new Color(25, 103, 210);
                else                                           borderColor = new Color(34, 139, 87);
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        cardPanel.setOpaque(false);
        return cardPanel;
    }
}
