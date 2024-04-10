package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Options;
import org.tribot.script.sdk.antiban.Antiban;

public class RunHelper {
    public static void checkIfWeShouldRun() {
        if (Antiban.shouldTurnOnRun()) {
            Options.setRunEnabled(true);
        }
    }
}
