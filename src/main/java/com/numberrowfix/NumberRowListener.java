package com.numberrowfix;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.client.input.KeyListener;

/**
 * Rewrites number row key events so the game sees digits. Events are mutated in place,
 * the same way the built-in Key Remapping plugin does it; no events are injected.
 */
class NumberRowListener implements KeyListener
{
	private static final String DIGITS = "1234567890";
	private static final int BLOCKING_MODIFIERS = InputEvent.SHIFT_DOWN_MASK
		| InputEvent.CTRL_DOWN_MASK
		| InputEvent.ALT_DOWN_MASK
		| InputEvent.ALT_GRAPH_DOWN_MASK
		| InputEvent.META_DOWN_MASK;

	// Written from config/client thread, read on the AWT thread
	private volatile NumberRowFixConfig.Mode mode = NumberRowFixConfig.Mode.AUTO;
	private volatile Map<Character, Character> customMapping = Collections.emptyMap();
	private volatile boolean onlyWhenChatEmpty;
	private volatile boolean chatInputEmpty = true;

	// AWT thread only. Keyed by extended key code, which is the same on press and release
	// regardless of Shift, so a key is restored consistently even if modifiers change while held.
	private final Map<Integer, Character> pressedDigits = new HashMap<>();
	private char pendingTypedDigit = KeyEvent.CHAR_UNDEFINED;

	void configure(NumberRowFixConfig.Mode mode, String customMapping, boolean onlyWhenChatEmpty)
	{
		this.mode = mode;
		this.customMapping = parseCustomMapping(customMapping);
		this.onlyWhenChatEmpty = onlyWhenChatEmpty;
	}

	void setChatInputEmpty(boolean chatInputEmpty)
	{
		this.chatInputEmpty = chatInputEmpty;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		pendingTypedDigit = KeyEvent.CHAR_UNDEFINED;

		char digit = KeyEvent.CHAR_UNDEFINED;
		if (!onlyWhenChatEmpty || chatInputEmpty)
		{
			digit = resolveDigit(mode, customMapping, e.getKeyCode(), e.getKeyChar(), e.getModifiersEx(), e.getKeyLocation());
		}

		if (digit == KeyEvent.CHAR_UNDEFINED)
		{
			pressedDigits.remove(e.getExtendedKeyCode());
			return;
		}

		pressedDigits.put(e.getExtendedKeyCode(), digit);
		pendingTypedDigit = digit;
		// VK_0..VK_9 have the same values as '0'..'9'
		e.setKeyCode(digit);
		e.setKeyChar(digit);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// KEY_TYPED has no key code, so reuse the decision from the KEY_PRESSED just before it
		if (pendingTypedDigit != KeyEvent.CHAR_UNDEFINED)
		{
			e.setKeyChar(pendingTypedDigit);
			pendingTypedDigit = KeyEvent.CHAR_UNDEFINED;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		Character digit = pressedDigits.remove(e.getExtendedKeyCode());
		if (digit != null)
		{
			pendingTypedDigit = KeyEvent.CHAR_UNDEFINED;
			e.setKeyCode(digit);
			e.setKeyChar(digit);
		}
	}

	@Override
	public void focusLost()
	{
		reset();
	}

	void reset()
	{
		pressedDigits.clear();
		pendingTypedDigit = KeyEvent.CHAR_UNDEFINED;
	}

	/**
	 * @return the digit this key press should produce, or {@link KeyEvent#CHAR_UNDEFINED} to leave it alone
	 */
	static char resolveDigit(NumberRowFixConfig.Mode mode, Map<Character, Character> customMapping,
		int keyCode, char keyChar, int modifiersEx, int keyLocation)
	{
		if ((modifiersEx & BLOCKING_MODIFIERS) != 0
			|| keyLocation == KeyEvent.KEY_LOCATION_NUMPAD
			|| (keyChar >= '0' && keyChar <= '9'))
		{
			return KeyEvent.CHAR_UNDEFINED;
		}

		if (mode != NumberRowFixConfig.Mode.CUSTOM && keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9)
		{
			return (char) keyCode;
		}

		if (mode != NumberRowFixConfig.Mode.AUTO && keyChar != KeyEvent.CHAR_UNDEFINED)
		{
			Character digit = customMapping.get(Character.toLowerCase(keyChar));
			if (digit != null)
			{
				return digit;
			}
		}

		return KeyEvent.CHAR_UNDEFINED;
	}

	/**
	 * Maps the i-th character of the string to the i-th digit of "1234567890". Extra characters
	 * are ignored, whitespace and digits leave their position unmapped, and matching is
	 * case-insensitive so Caps Lock doesn't break it. The first occurrence of a character wins.
	 */
	static Map<Character, Character> parseCustomMapping(String mapping)
	{
		if (mapping == null || mapping.isEmpty())
		{
			return Collections.emptyMap();
		}

		Map<Character, Character> result = new HashMap<>();
		int length = Math.min(mapping.length(), DIGITS.length());
		for (int i = 0; i < length; i++)
		{
			char c = mapping.charAt(i);
			if (Character.isWhitespace(c) || (c >= '0' && c <= '9'))
			{
				continue;
			}
			result.putIfAbsent(Character.toLowerCase(c), DIGITS.charAt(i));
		}
		return Collections.unmodifiableMap(result);
	}
}
