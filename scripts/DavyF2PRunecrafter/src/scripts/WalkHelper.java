package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.types.WorldTile;
import org.tribot.script.sdk.walking.GlobalWalking;

import static scripts.ErrorHelper.throwError;
import static scripts.RunHelper.checkIfWeShouldRun;
import static scripts.WaitHelper.*;

public class WalkHelper {

    private static final int TOLERANCE = 3; // Tiles

    public static void walkToGrandExchange() {
        walkTo(new WorldTile(3164, 3492, 0), "Grand Exchange");
    }

    public static void walkToFallyBank() {
        checkIfWeShouldRun();
        walkTo(new WorldTile(3011, 3357, 0), "Fally Bank");
    }

    public static void walkToAirAltar() {
        checkIfWeShouldRun();
        walkTo(new WorldTile(2986, 3294, 0), "Air Altar");
    }

    private static void walkTo(WorldTile destination, String destinationName) {
        WorldTile currentPosition = MyPlayer.getTile();

        // Check if we are within a tolerance distance from the destination
        if (currentPosition.distanceTo(destination) <= TOLERANCE) {
            Log.info("Already close to " + destinationName + ".");
            return; // Exit the method early since we're close enough to the destination
        }

        checkIfWeShouldRun();

        boolean success = GlobalWalking.walkTo(destination);

        if (success) {
            Log.info("Walking to " + destinationName + ".");
            // Wait until the player stops moving to consider the walk finished.
            boolean reachedDestination = Waiting.waitUntil(15000, () -> MyPlayer.getTile().distanceTo(destination) <= 5 && !MyPlayer.isMoving());


            if (reachedDestination) {
                Log.info("Successfully arrived at " + destinationName + ".");
            } else {
                Log.error("Timed out waiting to reach " + destinationName + ".");
                throwError("Failed to walk to " + destinationName + " within time limit.");
            }
        } else {
            Log.error("Failed to initiate walk to " + destinationName + ".");
            throwError("Failed to walk to " + destinationName + ".");
        }
    }
}
