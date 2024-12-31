package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwitchButton extends JToggleButton {
    private Color switchColor = Color.GRAY;  // Default color when the switch is off

    private String onTxt;
    private String offTxt;

    public SwitchButton(String onTxt, String offTxt) {
        super();

        this.onTxt = onTxt;
        this.offTxt = offTxt;

        setPreferredSize(new Dimension(70, 30));  // Set the size of the switch
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        // Add ActionListener to toggle the switch
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the color based on the state of the button
                if (isSelected()) {
                    switchColor = new Color(50, 205, 50); // Switch is ON (Green)
                } else {
                    switchColor = Color.GRAY;  // Switch is OFF (Gray)
                }
                repaint(); // Repaint the button with the new color
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Set up 2D graphics for smoother rendering
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background switch shape (rounded rectangle)
        g2.setColor(switchColor);
        g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 26, 26);  // Background shape

        // Draw the knob (circle) based on the toggle state
        if (isSelected()) {
            g2.setColor(Color.WHITE);
            g2.fillOval(getWidth() - getHeight(), 0, getHeight(), getHeight());  // Knob when ON (Right)
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(0, 0, getHeight(), getHeight());  // Knob when OFF (Left)
        }

        // Draw "ON" or "OFF" text inside the switch
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String text = isSelected() ? onTxt : offTxt;
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int xText = (getWidth() - textWidth) / 2 + (isSelected() ? -12 : 12);
        int yText = (getHeight() + textHeight) / 2;
        g2.drawString(text, xText, yText);

        super.paintComponent(g);
    }
}