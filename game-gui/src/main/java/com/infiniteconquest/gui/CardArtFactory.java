package com.infiniteconquest.gui;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/** Deterministic, original placeholder art: every card gets a stable visual identity. */
final class CardArtFactory {
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    private CardArtFactory() { }

    static ImageIcon iconFor(CardDefinition card, int width, int height) {
        String key = card.id() + ":" + width + "x" + height;
        return CACHE.computeIfAbsent(key, unused -> render(card, width, height));
    }

    static ImageIcon boardIconFor(CardDefinition card) {
        String key = card.id() + ":board";
        return CACHE.computeIfAbsent(key, unused -> render(card, 112, 56));
    }

    private static ImageIcon render(CardDefinition card, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color accent = faction(card.faction());
        g.setPaint(new GradientPaint(0, 0, accent.darker().darker(), width, height, accent.brighter()));
        g.fillRoundRect(0, 0, width, height, 14, 14);

        Random random = new Random(card.id().hashCode());
        g.setComposite(AlphaComposite.SrcOver.derive(.18f));
        for (int i = 0; i < 8; i++) {
            int size = 12 + random.nextInt(Math.max(13, height / 2));
            g.setColor(i % 2 == 0 ? Color.WHITE : Color.BLACK);
            g.fill(new Ellipse2D.Double(random.nextInt(width), random.nextInt(height), size, size));
        }
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(new Color(8, 13, 22, 175));
        g.fill(new RoundRectangle2D.Double(width * .32, height * .12, width * .36, height * .76, 16, 16));
        g.setColor(new Color(255, 232, 168));
        g.setStroke(new BasicStroke(Math.max(2f, width / 70f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawType(g, card.type(), width / 2, height / 2, Math.min(width, height) / 3);
        g.dispose();
        return new ImageIcon(image);
    }

    private static void drawType(Graphics2D g, CardType type, int x, int y, int radius) {
        switch (type) {
            case CHARACTER -> { // crossed blades
                g.drawLine(x - radius, y + radius, x + radius, y - radius);
                g.drawLine(x - radius, y - radius, x + radius, y + radius);
                g.drawLine(x - radius, y + radius / 2, x - radius / 2, y + radius);
                g.drawLine(x + radius / 2, y + radius, x + radius, y + radius / 2);
            }
            case LAND -> {
                Path2D p = new Path2D.Double();
                p.moveTo(x - radius, y + radius); p.lineTo(x, y - radius); p.lineTo(x + radius, y + radius); p.closePath();
                g.draw(p); g.drawLine(x - radius / 2, y, x, y + radius);
            }
            case STRUCTURE -> {
                g.drawRect(x - radius, y - radius / 2, radius * 2, radius * 3 / 2);
                g.drawLine(x - radius, y - radius / 2, x, y - radius);
                g.drawLine(x, y - radius, x + radius, y - radius / 2);
                g.drawRect(x - radius / 4, y + radius / 4, radius / 2, radius * 3 / 4);
            }
            case SPELL -> {
                Path2D bolt = new Path2D.Double();
                bolt.moveTo(x + radius / 5, y - radius); bolt.lineTo(x - radius / 2, y);
                bolt.lineTo(x, y); bolt.lineTo(x - radius / 5, y + radius);
                bolt.lineTo(x + radius / 2, y - radius / 5); bolt.lineTo(x, y - radius / 5); bolt.closePath();
                g.draw(bolt);
            }
            case CAPITAL -> {
                Path2D crown = new Path2D.Double();
                crown.moveTo(x - radius, y - radius / 2); crown.lineTo(x - radius / 2, y);
                crown.lineTo(x, y - radius); crown.lineTo(x + radius / 2, y);
                crown.lineTo(x + radius, y - radius / 2); crown.lineTo(x + radius * 3 / 4, y + radius);
                crown.lineTo(x - radius * 3 / 4, y + radius); crown.closePath(); g.draw(crown);
            }
        }
    }

    private static Color faction(String faction) {
        return switch (faction.toUpperCase(Locale.ROOT)) {
            case "ARES" -> new Color(193, 61, 55); case "ATHENA" -> new Color(85, 128, 177);
            case "HADES" -> new Color(111, 70, 143); case "HEPHAESTUS" -> new Color(191, 103, 45);
            case "POSEIDON" -> new Color(32, 137, 171); case "ZEUS" -> new Color(178, 154, 63);
            default -> new Color(93, 110, 130);
        };
    }
}
