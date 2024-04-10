package scripts;

import org.tribot.script.sdk.Waiting;

public class WaitHelper {

    public static void wait1Second() {
        Waiting.waitNormal(1000, 333);
    }

    public static void wait2Seconds() {
        Waiting.waitNormal(2000, 333);
    }
    public static void wait3Seconds() {
        Waiting.waitNormal(3000, 333);
    }

    public static void wait4Seconds() {
        Waiting.waitNormal(4000, 333);
    }

    public static void miniBreak() {
        Waiting.waitNormal(30000, 15000);
    }


}
