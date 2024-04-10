package scripts;

import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.InventoryItem;

import java.util.Optional;

import static scripts.BankHelper.withdrawFromBank;
import static scripts.Utils.getRequiredTiara;
import static scripts.WaitHelper.wait1Second;

public class EquipmentHelper {

    public static boolean withdrawAndEquipTiara(String selectedRuneType) {
        String requiredTiara = getRequiredTiara(selectedRuneType);

        boolean isTiaraEquipped = Query.equipment()
                .nameContains(requiredTiara)
                .findFirst()
                .isPresent();

        if (isTiaraEquipped) {
            System.out.println(requiredTiara + " is already equipped.");
            return true; // No need to withdraw and equip if already wearing the tiara
        }

        if (requiredTiara == null) {
            System.err.println("No tiara is required for the selected rune type: " + selectedRuneType);
            return false;
        }

        if (!withdrawFromBank(requiredTiara, 1)) {
            System.err.println("Failed to withdraw the required tiara: " + requiredTiara);
            return false;
        }

        wait1Second();

        return equipItem(requiredTiara);
    }

    public static boolean equipItem(String itemName) {
        Optional<InventoryItem> itemOpt = Query.inventory().nameEquals(itemName).findFirst();
        if (!itemOpt.isPresent()) {
            System.err.println(itemName + " not found in inventory.");
            return false;
        }

        InventoryItem item = itemOpt.get();
        if (item.click("Wear")) {
            System.out.println("Equipping " + itemName + "...");
            Waiting.waitNormal(1000, 250); // Wait for a bit after equipping. Adjust timings as needed.
            return true;
        } else {
            System.err.println("Failed to equip " + itemName + ".");
            return false;
        }
    }

}
