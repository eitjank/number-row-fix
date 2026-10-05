package com.numberrowfix;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NumberRowFixPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NumberRowFixPlugin.class);
		RuneLite.main(args);
	}
}
