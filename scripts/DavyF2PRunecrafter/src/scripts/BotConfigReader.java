package scripts;

import org.json.JSONObject;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;

public class BotConfigReader {
    /**
     * Reads the JSON configuration from a file and converts it into a JSONObject.
     * @param fileName The name of the file containing the JSON data.
     * @return JSONObject containing the configuration settings.
     * @throws IOException If there is an error reading the file.
     */
    public static JSONObject readConfig(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(Paths.get(fileName).toFile()));
        StringBuilder jsonStringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonStringBuilder.append(line);
        }
        reader.close();

        // Parse the string into a JSONObject
        return new JSONObject(jsonStringBuilder.toString());
    }
}
