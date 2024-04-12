package scripts;

import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.GameObject;

import java.util.Optional;

import static scripts.ErrorHelper.throwError;

public class ObjectHelper {

    public static boolean interactWithObject(String nameObject, String interactAction) {
        boolean areWeThere = Waiting.waitUntil(8000, () -> !MyPlayer.isMoving()); // Adjust timeout as necessary

        // Search for the object nearby
        Optional<GameObject> gameObject = Query.gameObjects()
                .maxDistance(15)
                .nameEquals(nameObject)
                .actionContains(interactAction)
                .findFirst();

        if (!gameObject.isPresent()) {
            Log.info("GameObject not found.");
            throwError("Failed to find object " + nameObject);
        }

        Log.info("Found object: " + nameObject);
        GameObject interactableObject = gameObject.get();

        // Attempt to interact with the object
        if (interactableObject.interact(interactAction)) {
            Log.info("Attempting to " + interactAction + " with " + nameObject + ".");

            boolean completedInteraction = false;

            // Example: Wait for the player to stop moving, indicating the interaction may have completed
            if (nameObject.equals("Mysterious ruins")) {
                completedInteraction = waitForNearbyObject("Altar");
                Log.info("We are now near the altar!");
            }

            if (nameObject.equals("Altar")) {
                completedInteraction = Waiting.waitUntil(9000, () -> !Inventory.contains("Pure essence"));
                Log.info("Crafted runes! Inventory has no Pure essence anymore");
            }

            if (nameObject.equals("Portal")) {
                completedInteraction = waitForNearbyObject("Mysterious ruins");
                Log.info("We are now near the ruins again!");
            }

            if (!completedInteraction) {
                Log.error("Interaction timed out.");
                throwError("Failed to complete interaction with " + nameObject + " within time limit.");
            }

            Log.info("Successfully interacted with " + nameObject + ".");
            return true;
        } else {
            Log.info("Failed to " + interactAction + " with " + nameObject + ".");
            throwError("Failed to " + interactAction + " with " + nameObject + ".");
        }

        return false;
    }

    private static boolean waitForNearbyObject(String name) {
        return Waiting.waitUntil(8000, () -> Query.gameObjects()
                .nameEquals(name)
                .maxDistance(15)
                .findFirst()
                .isPresent());
    }

}
