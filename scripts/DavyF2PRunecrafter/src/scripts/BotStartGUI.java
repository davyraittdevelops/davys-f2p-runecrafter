package scripts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BotStartGUI {
    private JFrame frame;
    private JButton startButton;
    private JComboBox<String> runeTypeDropdown;

    public BotStartGUI(Runnable onStart) {
        frame = new JFrame("Bot Start Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 150); // Adjusted size to accommodate the dropdown
        frame.setLayout(new FlowLayout());

        // Create the dropdown with rune options
        String[] runeOptions = {"Craft air runes", "Craft earth runes", "Craft fire runes", "Craft body runes"};
        runeTypeDropdown = new JComboBox<>(runeOptions);
        frame.add(runeTypeDropdown); // Add the dropdown to the frame

        startButton = new JButton("Start Bot");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) runeTypeDropdown.getSelectedItem();
                System.setProperty("selectedRuneType", selectedOption);

                onStart.run();
                frame.dispose(); // Close the GUI once the bot starts
            }
        });

        frame.add(startButton);
        frame.setLocationRelativeTo(null); // Center on screen
    }

    public void show() {
        frame.setVisible(true);
    }
}
