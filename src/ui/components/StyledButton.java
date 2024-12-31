package ui.components;

import javax.swing.*;
import java.awt.*;

public class StyledButton extends RoundedPanel {

    public StyledButton(String text) {
        super(10); // You can adjust the cornerRadius as needed
        setLayout(new BorderLayout());

        // Create a JLabel for the button text
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        // Add the label to the center of the rounded panel
        add(label, BorderLayout.CENTER);

        // Set additional button properties
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
    }
}
