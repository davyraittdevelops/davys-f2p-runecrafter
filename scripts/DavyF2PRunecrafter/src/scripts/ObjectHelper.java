package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.GameObject;

import java.util.Optional;

import static scripts.ErrorHelper.throwError;
import static scripts.WaitHelper.wait1Second;

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

            // Example: Wait for the player to stop moving, indicating the interaction may have completed
            boolean completedInteraction = Waiting.waitUntil(8000, () -> !MyPlayer.isMoving()); // Adjust timeout as necessary
            wait1Second();

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

        // This line is now redundant but kept for structural completeness
        return false;
    }
}
