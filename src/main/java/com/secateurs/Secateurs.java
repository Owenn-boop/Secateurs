package com.secateurs;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.ItemContainerChanged;

import java.util.function.Consumer;

@Slf4j
@PluginDescriptor(
	name = "Secateurs", description = "Reminds you to use your Magic Secateurs when farming", tags = {"skilling", "farming"}
)
public class Secateurs extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SecateursConfig config;

	private final int MAGIC_SECATEURS = 7409;

	private boolean getSecateurs(){
		if (client.getItemContainer(InventoryID.INVENTORY).contains(MAGIC_SECATEURS)) {
			return true;
		}

		if(client.getItemContainer(InventoryID.EQUIPMENT).contains(MAGIC_SECATEURS)){
			return true;
		}

		return false;
	}

	private boolean isSecateursNeeded(MenuEntry entry){
		if(hasSecateurs){
			return false;
		}

		String target = entry.getTarget();
		String option = entry.getOption();

		target = target.toLowerCase();
		option = option.toLowerCase();

		// if the target is an herb
		if(target.contains("herbs")) {
			return option.contains("pick");
		}

		// if the user has set the config to only work on herb patches
		if(config.onlyHerbs()) {
			return false;
		}

		// allotment and some others
		if(option.contains("harvest")) {
			return true;
		}

		// bushes limpwurts and grape vines
		if(target.contains("bush") || target.contains("limpwurt") || target.contains("grape")){
			return option.contains("pick");
		}

		return false;
	}

	/*
	Does the player have magic secateurs equipped.
	 */
	private boolean hasSecateurs = false;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			hasSecateurs = getSecateurs();
		}
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event){
		hasSecateurs = getSecateurs();
		log.info("Does the player have Magic Secateurs? {}", hasSecateurs);
	}

	@Subscribe
	public void onPostMenuSort(PostMenuSort postMenuSort){
		if (client.isMenuOpen()) {return;}

		Menu root = client.getMenu();
		MenuEntry[] entries = root.getMenuEntries();

		for (MenuEntry entry : entries) {
			if (isSecateursNeeded(entry)) {
				log.info("Balls: {}", entry.toString());

				// creates the menu item that tells the player to get their magic secateurs
				// sends a chat message if clicked
				root.createMenuEntry(-1)
						.setOption("GET YOUR MAGIC SECATEURS")
						.setForceLeftClick(true)
						.onClick(menuEntry -> client.addChatMessage(
								ChatMessageType.OBJECT_EXAMINE,
								"",
								entry.getTarget() + ": Please go get your magic secateurs.",
								""));
				break;
			}
		}
	}

	@Provides
	SecateursConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SecateursConfig.class);
	}
}
