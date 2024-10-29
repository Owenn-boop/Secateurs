package com.secateurs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("secateurs")
public interface SecateursConfig extends Config
{
	@ConfigItem(
		keyName = "onlyHerbs",
		name = "Only Notify for Herbs",
		description = "If this is enabled, patches such as bushes and limpwurt will not ask you to get your magic secateurs out.<br>"
					+ "Only herb patches will ask you to get your magic secateurs out."
	)
	default boolean onlyHerbs()
	{
		return false;
	}
}
