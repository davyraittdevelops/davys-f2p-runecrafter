package scripts;

import org.json.JSONObject;
import org.tribot.script.sdk.*;
import org.tribot.script.sdk.antiban.Antiban;
import org.tribot.script.sdk.interfaces.Positionable;
import org.tribot.script.sdk.painting.Painting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.script.TribotScriptManifest;
import org.tribot.script.sdk.types.*;
import org.tribot.script.sdk.util.Resources;
import org.tribot.script.sdk.walking.GlobalWalking;
import org.tribot.script.sdk.walking.adapter.DaxWalkerAdapter;
import org.tribot.script.sdk.Bank;
import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.query.Query;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.concurrent.CountDownLatch;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import java.util.Random;

import static scripts.BankHelper.*;
import static scripts.BotConfigReader.readConfig;
import static scripts.CustomAntiban.chanceOfFakeBreak;
import static scripts.EquipmentHelper.withdrawAndEquipTiara;
import static scripts.ErrorHelper.throwError;
import static scripts.GrandExchangeHelper.purchaseMissingItems;
import static scripts.LoginHelper.attemptLogin;
import static scripts.ObjectHelper.interactWithObject;
import static scripts.RunHelper.checkIfWeShouldRun;
import static scripts.Utils.formatTime;
import static scripts.WaitHelper.*;
import static scripts.WalkHelper.*;


@TribotScriptManifest(name = "V1_DavyF2PRunecrafter", author = "D@VY", category = "DavysBots", description = "Crafts all F2P runes, and even buys supplies for you ;)")
public class DavyF2PRunecrafter implements TribotScript {

	private WorldTile locationBeforeBanking;
	private DaxWalkerAdapter daxWalker;
	private int tripsDone = 0;
	private long startTime;
	private String selectedRuneType = "None";


	Consumer<Graphics2D> paintListener = g -> {
		g.setColor(Color.WHITE); // Set the color for the text
		g.drawString("Trips done: " + tripsDone, 20, 100);
		g.drawString("Selected Rune: " + selectedRuneType, 20, 120);

		long runTime = System.currentTimeMillis() - startTime;
		g.drawString("XP Gained: " + (tripsDone * 140), 20, 140);
		g.drawString("Time Running: " + formatTime(runTime), 20, 160);
	};


	@Override
	public void execute(final String args) {
		final CountDownLatch latch = new CountDownLatch(1);

		// Invoke the GUI here
		SwingUtilities.invokeLater(() -> {
			BotStartGUI gui = new BotStartGUI(latch::countDown);
			gui.show();
		});

		try {
			latch.await(); // This line ensures your script waits until the GUI is closed before continuing.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			JSONObject botSettings = readConfig("botSettings.json");
			selectedRuneType = botSettings.optString("selectedRuneType", "None");
			Log.info("Selected Rune Type: " + selectedRuneType);
		} catch (IOException e) {
			e.printStackTrace();
			throwError(e.toString());
		}

		initializeDaxWalker();

		Painting.addPaint(paintListener);

		startTime = System.currentTimeMillis();

		Antiban.setScriptAiAntibanEnabled(true);

		GlobalWalking.setEngine(daxWalker);

		if (!attemptLogin()) {
			return;
		}

		GlobalWalking.walkToBank();

		List<String> missingItems = checkBankForSupplies(selectedRuneType);

		if (!missingItems.isEmpty()) {
			// Missing items found, handle accordingly
			Log.info("Missing items: " + String.join(", ", missingItems));
			purchaseMissingItems(missingItems);
		} else {
			Log.info("All required supplies for crafting " + selectedRuneType + " are present.");
		}

		// Got all the items we need, equip them and get ready to craft!!
		withdrawAndEquipTiara(selectedRuneType);

		switch (selectedRuneType) {
			case "Craft air runes":
				craftAirRunes();

			case "Craft earth runes":

			case "Craft fire runes":

			case "Craft body runes":

		}

	}

	private void craftAirRunes() {
		walkToFallyBank();

		while(true) {
			// Check if the Bank contains enough pure essence and if we are still wearing the tiara
			List<String> missingItems = checkBankForSupplies(selectedRuneType);

			if (missingItems.isEmpty()) {
				// If everything is as should be, do another round
				withdrawFromBank("Pure essence", 28);

				walkToAirAltar();

				interactWithObject("Mysterious ruins", "Enter");

				interactWithObject("Altar", "Craft-rune");

				interactWithObject("Portal", "Use");

				walkToFallyBank();

				chanceOfFakeBreak();

				depositInventoryToBankAndKeepOpen();

				chanceOfFakeBreak();

				tripsDone++;
			} else {
				// If we are missing stuf, go and purchase it
				purchaseMissingItems(missingItems);
				walkToFallyBank();
			}

		}


	}


	private void initializeDaxWalker() {
		try {
			String env = Resources.getString("scripts/env.txt");
			String[] apiKeys = env.split(",");
			if (apiKeys.length != 2) {
				throw new IllegalArgumentException("Invalid format for API keys in env.txt");
			}
			this.daxWalker = new DaxWalkerAdapter(apiKeys[0].trim(), apiKeys[1].trim());
		} catch (Resources.ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}


}
