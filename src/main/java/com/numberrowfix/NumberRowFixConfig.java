package com.numberrowfix;

import lombok.AllArgsConstructor;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NumberRowFixConfig.GROUP)
public interface NumberRowFixConfig extends Config
{
	String GROUP = "numberrowfix";

	@AllArgsConstructor
	enum Mode
	{
		AUTO("Auto"),
		CUSTOM("Custom"),
		BOTH("Both");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@ConfigItem(
		keyName = "mode",
		name = "Mode",
		description = "Auto: number row keys type digits based on their physical position.<br>"
			+ "Custom: characters in the custom mapping below are turned into digits.<br>"
			+ "Both: Auto first, then Custom.",
		position = 0
	)
	default Mode mode()
	{
		return Mode.AUTO;
	}

	@ConfigItem(
		keyName = "customMapping",
		name = "Custom mapping",
		description = "The characters your number row types for 1 2 3 4 5 6 7 8 9 0, in that order.<br>"
			+ "Example for French AZERTY: &é\"'(-è_çà<br>"
			+ "Use a space to leave a position unmapped. Only used in Custom or Both mode.",
		position = 1
	)
	default String customMapping()
	{
		return "";
	}

	@ConfigItem(
		keyName = "onlyWhenChatEmpty",
		name = "Only when chat input is empty",
		description = "For layouts whose number row letters OSRS accepts in chat (e.g. French é è ç à,<br>"
			+ "Czech/Slovak é á í ý). Only remaps while the chatbox is empty, so those letters can<br>"
			+ "still be typed after the first character of a message.<br>"
			+ "Unsent chat text also stops the number row working in dialogues until cleared.<br>"
			+ "Not needed if OSRS can't show your number row letters anyway (e.g. Lithuanian).",
		position = 2
	)
	default boolean onlyWhenChatEmpty()
	{
		return false;
	}
}
