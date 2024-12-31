package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private int cornerRadius;

    public RoundedPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Define the gradient start and end points
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, getHeight());

        // Convert CSS-style color values to Java Color objects
        Color color1 = new Color(66, 66, 74);
        Color color2 = new Color(25, 25, 25);

        // Create a gradient paint object
        LinearGradientPaint gradientPaint = new LinearGradientPaint(start, end, new float[]{0.0f, 1.0f}, new Color[]{color1, color2});

        // Set rendering hints for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Create a rounded rectangle shape
        Shape roundedRectangle = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Set the paint for the graphics context
        g2d.setPaint(gradientPaint);

        // Fill the background with the gradient paint within the rounded rectangle
        g2d.fill(roundedRectangle);

        // Dispose of the graphics context to free up resources
        g2d.dispose();
    }
}
