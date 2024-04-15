package scripts;

import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.InventoryItem;

import java.util.Optional;

import static scripts.BankHelper.withdrawFromBank;
import static scripts.ErrorHelper.throwError;
import static scripts.Utils.getRequiredTiara;

public class EquipmentHelper {

    private static final int MAX_RETRIES = 3;

    public static boolean withdrawAndEquipTiara(String selectedRuneType) {
        String requiredTiara = getRequiredTiara(selectedRuneType);

        boolean isTiaraEquipped = Query.equipment()
                .nameContains(requiredTiara)
                .findFirst()
                .isPresent();

        if (isTiaraEquipped) {
            Log.info(requiredTiara + " is already equipped.");
            return true; // No need to withdraw and equip if already wearing the tiara
        }

        if (requiredTiara == null) {
            throwError("No tiara is required for the selected rune type: " + selectedRuneType);
            return false;
        }

        boolean success = false;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            if (withdrawFromBank(requiredTiara, 1)) {
                Log.info("Successfully withdrew " + requiredTiara + " on attempt " + attempt);
                success = equipItem(requiredTiara);
                if (success) {
                    return true;
                } else {
                    Log.info("Failed to equip " + requiredTiara + " on attempt " + attempt);
                }
            } else {
                Log.error("Failed to withdraw " + requiredTiara + " on attempt " + attempt);
            }
            Waiting.waitNormal(3000, 1000); // Wait between retries
        }

        throwError("Failed to withdraw and equip " + requiredTiara + " after " + MAX_RETRIES + " attempts.");
        return false;
    }

    public static boolean equipItem(String itemName) {
        boolean success = false;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            Optional<InventoryItem> itemOpt = Query.inventory().nameEquals(itemName).findFirst();
            if (!itemOpt.isPresent()) {
                Log.error("Attempt " + attempt + ": " + itemName + " not found in inventory.");
                Waiting.waitNormal(3000, 1000); // Wait between retries
                continue;
            }

            InventoryItem item = itemOpt.get();
            if (item.click("Wear")) {
                Log.info("Equipping " + itemName + "...");
                Waiting.waitUntil(9000, () -> Query.equipment().nameEquals(itemName).findFirst().isPresent());
                success = true;
                break;
            } else {
                Log.error("Attempt " + attempt + ": Failed to equip " + itemName);
            }
        }

        if (!success) {
            throwError("Failed to equip " + itemName + " after " + MAX_RETRIES + " attempts.");
        }
        return success;
    }

}
