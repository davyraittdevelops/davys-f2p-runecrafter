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

        if (!withdrawFromBank(requiredTiara, 1)) {
            throwError("Failed to withdraw the required tiara: " + requiredTiara);
            return false;
        }

        Waiting.waitUntil(9000, () -> Inventory.contains(requiredTiara));

        return equipItem(requiredTiara);
    }

    public static boolean equipItem(String itemName) {
        Optional<InventoryItem> itemOpt = Query.inventory().nameEquals(itemName).findFirst();
        if (!itemOpt.isPresent()) {
            throwError(itemName + " not found in inventory.");
            return false;
        }

        InventoryItem item = itemOpt.get();
        if (item.click("Wear")) {
            Log.info("Equipping " + itemName + "...");
            Waiting.waitUntil(9000, () -> Inventory.contains(itemName));
            return true;
        } else {
            Log.info("Failed to equip " + itemName + ".");
            return false;
        }
    }

}
