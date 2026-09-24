package com.infiniteconquest.gui;

import javax.swing.JPanel;
import java.awt.*;
import static com.infiniteconquest.gui.UiTheme.*;

/** Time-based vector animation, sampled at display cadence rather than discrete coin frames. */
final class InitiativeCoinPanel extends JPanel {
    static final long DURATION_NANOS = 2_200_000_000L;
    private final int winner;
    private double progress;
    InitiativeCoinPanel(int winner) { this.winner = winner; setOpaque(false); }
    void setProgress(double value) { progress = Math.max(0, Math.min(1, value)); repaint(); }
    static double angle(double progress, int winner) {
        double p = Math.max(0, Math.min(1, progress));
        return (8 * Math.PI + winner * Math.PI) * (1 - Math.pow(1 - p, 3));
    }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        double cosine = Math.cos(angle(progress, winner));
        double lift = Math.sin(Math.PI * progress) * 40;
        int centerX = getWidth() / 2;
        int centerY = (int) (132 - lift);
        g.setPaint(new GradientPaint(0, 0, new Color(27, 44, 65), 0, getHeight(), new Color(10, 18, 31)));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(0, 0, 0, (int) (85 - lift)));
        int shadowWidth = (int) (140 - lift);
        g.fillOval(centerX - shadowWidth / 2, 213, shadowWidth, 16);
        Graphics2D coin = (Graphics2D) g.create();
        coin.translate(centerX, centerY);
        coin.scale(Math.max(.055, Math.abs(cosine)), 1);
        coin.setColor(new Color(100, 62, 19)); coin.fillOval(-83, -79, 166, 164);
        coin.setPaint(new GradientPaint(-72, -76, new Color(255, 243, 173), 70, 78, new Color(163, 100, 28)));
        coin.fillOval(-80, -80, 160, 160);
        coin.setStroke(new BasicStroke(2));
        coin.setColor(new Color(255, 236, 151)); coin.drawOval(-74, -74, 148, 148);
        coin.setColor(new Color(135, 83, 25)); coin.drawOval(-63, -63, 126, 126);
        for (int i = 0; i < 40; i++) {
            double a = i * Math.PI / 20;
            coin.drawLine((int)(Math.cos(a)*68), (int)(Math.sin(a)*68), (int)(Math.cos(a)*72), (int)(Math.sin(a)*72));
        }
        coin.setFont(new Font(Font.SERIF, Font.BOLD, 58));
        String face = cosine >= 0 ? "I" : "II";
        int textX = -coin.getFontMetrics().stringWidth(face) / 2;
        coin.setColor(new Color(255, 240, 171)); coin.drawString(face, textX + 1, 21);
        coin.setColor(new Color(92, 54, 18)); coin.drawString(face, textX, 20);
        coin.dispose();
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        g.setColor(progress >= 1 ? GOLD : Color.WHITE);
        centered(g, progress >= 1 ? "PLAYER " + (winner + 1) + " STARTS" : "DECIDING FIRST PLAYER", 271);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); g.setColor(new Color(184, 202, 218));
        centered(g, progress >= 1 ? "The battlefield is ready." : "A toss of fate before the conquest", 299);
        g.dispose();
    }
    private void centered(Graphics2D g, String text, int y) {
        g.drawString(text, (getWidth() - g.getFontMetrics().stringWidth(text)) / 2, y);
    }
}
