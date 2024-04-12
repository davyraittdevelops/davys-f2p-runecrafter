package scripts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class BotStartGUI {
    private JFrame frame;
    private JButton startButton;
    private JComboBox<String> runeTypeDropdown;

    public BotStartGUI(Runnable onStart) {
        frame = new JFrame("Bot Start Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 150);
        frame.setLayout(new FlowLayout());

        // Create the dropdown with rune options
        String[] runeOptions = {"Craft air runes", "Craft earth runes", "Craft fire runes", "Craft body runes"};
        runeTypeDropdown = new JComboBox<>(runeOptions);
        frame.add(runeTypeDropdown);

        startButton = new JButton("Start Bot");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedOption = (String) runeTypeDropdown.getSelectedItem();
                    JSONObject botSettings = new JSONObject();
                    botSettings.put("selectedRuneType", selectedOption);

                    // Write JSON to a file
                    writeFile("botSettings.json", botSettings.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                onStart.run();
                frame.dispose(); // Close the GUI once the bot starts
            }
        });

        frame.add(startButton);
        frame.setLocationRelativeTo(null); // Center on screen
    }

    private void writeFile(String fileName, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(Paths.get(fileName).toFile());
        fileWriter.write(content);
        fileWriter.close();
    }

    public void show() {
        frame.setVisible(true);
    }
}
