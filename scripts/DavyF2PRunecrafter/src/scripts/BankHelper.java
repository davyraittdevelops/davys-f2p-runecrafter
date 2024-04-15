package scripts;

import org.tribot.script.sdk.Bank;
import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static scripts.ErrorHelper.throwError;
import static scripts.Utils.getRequiredTiara;
import static scripts.WaitHelper.*;

public class BankHelper {

    private static final int MAX_RETRIES = 3; // Maximum number of retries

    public static boolean withdrawFromBank(String itemName, int count) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            if (!Bank.ensureOpen()) {
                Log.error("Attempt " + attempt + ": Could not open the bank.");
                if (attempt == MAX_RETRIES) {
                    throwError("Could not open the bank after " + MAX_RETRIES + " attempts.");
                    return false;
                }
                Waiting.waitNormal(2000, 1000);
                continue;
            }

            Waiting.waitUntil(5000, Bank::isOpen);

            if (!Bank.contains(itemName)) {
                Log.error("Bank does not contain the required item: " + itemName);
                throwError("Bank does not contain the required item: " + itemName);
                return false;
            }

            if (Bank.withdraw(itemName, count)) {
                Waiting.waitUntil(6000, () -> Inventory.contains(itemName));
                if (!Bank.close()) {
                    Log.error("Failed to close the bank properly, but item was withdrawn.");
                    throwError("Failed to close the bank properly, but item was withdrawn.");
                }
                Waiting.waitUntil(15000, () -> !Bank.isOpen());
                return true;
            } else {
                Log.error("Attempt " + attempt + ": Failed to withdraw the required item: " + itemName);
                if (attempt < MAX_RETRIES) {
                    Waiting.waitNormal(3000, 1000); // Wait between retries
                }
            }
        }
        throwError("Failed to withdraw the required item: " + itemName + " after " + MAX_RETRIES + " attempts.");
        return false;
    }

    public static List<String> checkBankForSupplies(String selectedRuneType) {
        String requiredTiara = getRequiredTiara(selectedRuneType);
        List<String> missingItems = new ArrayList<>();

        Waiting.waitUntil(15000, Bank::isOpen);

        if (!Bank.isOpen() && !Bank.ensureOpen()) {
            Log.info("Failed to open bank.");
            throwError("Failed to open bank");
            return Arrays.asList("Bank could not be opened");
        }

        if (!Bank.depositInventory()) {
            Log.info("Failed to deposit inventory or equipment.");
            throwError("Failed to deposit inventory or equipment");
            return Arrays.asList("Failed to deposit items");
        }

        Waiting.waitUntil(15000, Inventory::isEmpty);

        Log.info("Checking if bank contains " + requiredTiara);

        if (requiredTiara != null && !Bank.contains(requiredTiara)) {
            Log.info("Bank does not contain " + requiredTiara);

            boolean isTiaraEquipped = Query.equipment()
                    .nameContains(requiredTiara)
                    .findFirst()
                    .isPresent();

            if (isTiaraEquipped) {
                Log.info("We have it equipped though! ");
            } else {
                missingItems.add(requiredTiara);
            }

        } else {
            Log.info("Bank does contain " + requiredTiara);
        }

        Random random = new Random();
        int requiredEssenceAmount = 100 + random.nextInt(50); // Generate required essence amount

        int essenceCount = Bank.getCount("Pure essence");
        if (essenceCount < requiredEssenceAmount) {
            Log.info("Not enough Pure essence. Needed: " + requiredEssenceAmount + ", Found: " + essenceCount);
            // Ensure "Pure essence" is only added once, regardless of where the check fails
            if (!missingItems.contains("Pure essence")) {
                missingItems.add("Pure essence");
            }
        } else {
            Log.info("We have " + essenceCount + " pure essence.");
        }

        return missingItems;
    }

    public static boolean depositInventoryToBankAndKeepOpen() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            if (!Bank.ensureOpen()) {
                Log.error("Attempt " + attempt + ": Could not open the bank to deposit inventory.");
                if (attempt == MAX_RETRIES) {
                    throwError("Could not open the bank to deposit inventory after " + MAX_RETRIES + " attempts.");
                    return false;
                }
                Waiting.waitNormal(2000, 1000); // Random wait between 2 and 3 seconds
                continue;
            }

            Waiting.waitUntil(15000, Bank::isOpen);

            if (Bank.depositInventory()) {
                Waiting.waitUntil(15000, Inventory::isEmpty);
                if (!Inventory.isEmpty()) {
                    throwError("Failed to empty inventory after deposit.");
                }
                return true;
            } else {
                Log.error("Attempt " + attempt + ": Failed to deposit inventory.");
                if (attempt < MAX_RETRIES) {
                    Waiting.waitNormal(3000, 1000); // Wait between retries
                }
            }
        }
        throwError("Failed to deposit inventory after " + MAX_RETRIES + " attempts.");
        return false;
    }

}
