package scripts;

import org.tribot.script.sdk.GrandExchange;
import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.GrandExchangeOffer;
import org.tribot.script.sdk.util.TribotRandom;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static scripts.ErrorHelper.throwError;
import static scripts.WaitHelper.*;
import static scripts.WalkHelper.walkToGrandExchange;

public class GrandExchangeHelper {

    public static boolean buyItemFromGE(String itemName, int quantity, int initialPrice) {
        final int maxRetries = 3; // Maximum number of retries for buying an item
        final double priceIncreaseFactor = 2; // Price increase factor for each retry (100%)
        int currentPrice = initialPrice;
        boolean offerCompleted = false;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            if (!GrandExchange.isOpen() && !GrandExchange.open()) {
                Log.info("Failed to open the Grand Exchange.");
                if (attempt < maxRetries - 1) {
                    Waiting.waitNormal(3000, 1000); // Random wait between retries
                    continue;
                } else {
                    return false;
                }
            }

            Waiting.waitUntil(9000, GrandExchange::isOpen);

            GrandExchange.CreateOfferConfig offerConfig = GrandExchange.CreateOfferConfig.builder()
                    .itemName(itemName)
                    .quantity(quantity)
                    .price(currentPrice)
                    .build();

            if (!GrandExchange.placeOffer(offerConfig)) {
                Log.info("Failed to place buy offer for " + itemName + " at price " + currentPrice);
                if (attempt < maxRetries - 1) {
                    Waiting.waitNormal(3000, 1000); // Random wait between retries
                    continue;
                } else {
                    return false;
                }
            }

            Log.info("Buy offer for " + itemName + " placed at price " + currentPrice + ". Waiting for completion...");
            Waiting.waitNormal(5000, 750); // Wait a bit for the offer to complete

            Optional<GrandExchangeOffer> offerOpt = Query.grandExchangeOffers()
                    .itemNameEquals(itemName)
                    .stream()
                    .findFirst();

            if (offerOpt.isPresent()) {
                GrandExchangeOffer offer = offerOpt.get();
                if (offer.getStatus() == GrandExchangeOffer.Status.COMPLETED) {
                    Log.info("Offer completed.");
                    if (GrandExchange.collectAll(GrandExchange.CollectMethod.BANK)) {
                        Log.info("Items collected to bank.");
                        offerCompleted = true;
                        break; // Exit the loop if offer is successfully collected
                    }
                } else {
                    Log.info("Offer did not complete. Increasing price and retrying...");
                    currentPrice = (int) (currentPrice * priceIncreaseFactor); // Increase the price by 10%
                    GrandExchange.abort(offer.getSlot());
                    Waiting.waitNormal(4000, 1000); // Wait to check offer is aborted
                    GrandExchange.collectAll();
                }
            } else {
                Log.error("Failed to find the placed offer.");
                return false;
            }
        }

        if (!offerCompleted) {
            Log.error("Failed to complete the buy offer for " + itemName + " after " + maxRetries + " attempts.");
            return false;
        }

        return true;
    }

    public static boolean purchaseMissingItems(List<String> missingItems) {
        boolean allItemsPurchased = true;
        Random random = new Random();

        for (String item : missingItems) {
            int quantityToBuy;
            int price = 2;

            switch (item) {
                case "Pure essence":
                    quantityToBuy = 600 + random.nextInt(50);
                    price = 1;
                    break;
                case "Air tiara":
                    quantityToBuy = 1;
                    price = 55;
                    break;
                default:
                    quantityToBuy = 1;
                    break;
            }

            // Navigate to the Grand Exchange if not already there
            if (!GrandExchange.isNearby()) {
                walkToGrandExchange();
                Log.info("Navigating to the Grand Exchange...");
            }

            if (!buyItemFromGE(item, quantityToBuy, price)) {
                Log.info("Failed to buy " + item);
                allItemsPurchased = false;
            } else {
                Log.info("Successfully placed a buy offer for " + quantityToBuy + " of " + item);
            }
        }

        // Close the Grand Exchange interface if open
        if (GrandExchange.isOpen()) {
            GrandExchange.close();
        }

        return allItemsPurchased;
    }

}
