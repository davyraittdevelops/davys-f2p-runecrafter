package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.types.WorldTile;
import org.tribot.script.sdk.walking.GlobalWalking;

import static scripts.ErrorHelper.throwError;
import static scripts.RunHelper.checkIfWeShouldRun;

public class WalkHelper {

    private static final int TOLERANCE = 4; // Tiles
    private static final int MAX_RETRIES = 3; // Maximum number of retries

    public static boolean walkToGrandExchange() {
        checkIfWeShouldRun();
        return walkTo(new WorldTile(3164, 3492, 0), "Grand Exchange");
    }

    public static boolean walkToFallyBank() {
        checkIfWeShouldRun();
        return walkTo(new WorldTile(3011, 3357, 0), "Fally Bank");
    }

    public static boolean walkToAirAltar() {
        checkIfWeShouldRun();
        return walkTo(new WorldTile(2986, 3294, 0), "Air Altar");
    }

    private static boolean walkTo(WorldTile destination, String destinationName) {
        WorldTile currentPosition = MyPlayer.getTile();

        // Check if we are within a tolerance distance from the destination
        if (currentPosition.distanceTo(destination) <= TOLERANCE) {
            Log.info("Already close to " + destinationName + ".");
            return true; // Exit the method early since we're close enough to the destination
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            Log.info("Attempt " + attempt + " to walk to " + destinationName);
            checkIfWeShouldRun();
            boolean success = GlobalWalking.walkTo(destination);

            if (success) {
                Log.info("Walking to " + destinationName + ".");
                // Wait until the player stops moving to consider the walk finished.
                boolean reachedDestination = Waiting.waitUntil(15000, () -> MyPlayer.getTile().distanceTo(destination) <= TOLERANCE && !MyPlayer.isMoving());

                if (reachedDestination) {
                    Log.info("Successfully arrived at " + destinationName + ".");
                    return true;
                } else {
                    Log.info("Failed to reach " + destinationName + " on attempt " + attempt + ".");
                }
            } else {
                Log.error("Failed to initiate walk to " + destinationName + " on attempt " + attempt + ".");
            }

            if (attempt < MAX_RETRIES) {
                // Wait a random time between retries
                Waiting.waitNormal(3000, 1000);
            }
        }

        Log.error("Failed to walk to " + destinationName + " after " + MAX_RETRIES + " attempts.");
        return false;
    }
}
