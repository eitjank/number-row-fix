package com.numberrowfix;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.gameval.VarClientID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Number Row Fix",
	description = "Makes the number row type digits on non-US keyboard layouts",
	tags = {"keyboard", "layout", "azerty", "qwertz", "lithuanian", "czech", "dialogue", "numbers"}
)
public class NumberRowFixPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private NumberRowFixConfig config;

	private final NumberRowListener listener = new NumberRowListener();
	private boolean onlyWhenChatEmpty;

	@Provides
	NumberRowFixConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NumberRowFixConfig.class);
	}

	@Override
	protected void startUp()
	{
		applyConfig();
		keyManager.registerKeyListener(listener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(listener);
		listener.reset();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (NumberRowFixConfig.GROUP.equals(event.getGroup()))
		{
			applyConfig();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		// Key events arrive on the AWT thread, so game state is read here and cached for the listener
		if (onlyWhenChatEmpty)
		{
			listener.setChatInputEmpty(Strings.isNullOrEmpty(client.getVarcStrValue(VarClientID.CHATINPUT)));
		}
	}

	private void applyConfig()
	{
		onlyWhenChatEmpty = config.onlyWhenChatEmpty();
		listener.configure(config.mode(), config.customMapping(), onlyWhenChatEmpty);
	}
}
