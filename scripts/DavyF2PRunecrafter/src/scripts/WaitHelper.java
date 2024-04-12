package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Waiting;

public class WaitHelper {

    public static void miniBreak() {
        Log.info("Taking a well deserved mini break ;) ");
        Waiting.waitNormal(30000, 15000);
    }


}
