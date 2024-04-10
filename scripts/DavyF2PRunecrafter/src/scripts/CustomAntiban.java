package scripts;

import org.tribot.script.sdk.input.Mouse;

import java.util.Random;

import static scripts.WaitHelper.miniBreak;

public class CustomAntiban {

    public static void chanceOfFakeBreak() {
        Random random = new Random();
        int chance = random.nextInt(50);

        if (chance == 0) {
            Mouse.leaveScreen();
            System.out.println("Taking a fake break: Mouse has left the screen.");
            miniBreak();
        }
    }

}
