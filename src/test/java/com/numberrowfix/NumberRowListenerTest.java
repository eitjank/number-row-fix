package com.numberrowfix;

import static com.numberrowfix.NumberRowFixConfig.Mode.AUTO;
import static com.numberrowfix.NumberRowFixConfig.Mode.BOTH;
import static com.numberrowfix.NumberRowFixConfig.Mode.CUSTOM;
import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import static java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD;
import static java.awt.event.KeyEvent.KEY_LOCATION_STANDARD;
import static java.awt.event.KeyEvent.KEY_LOCATION_UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class NumberRowListenerTest
{
	private static final Map<Character, Character> NO_CUSTOM = Collections.emptyMap();
	private static final Component SOURCE = new Canvas();

	private NumberRowListener listener;

	@Before
	public void setUp()
	{
		listener = new NumberRowListener();
	}

	// --- Auto rule ---

	@Test
	public void autoMapsAccentOnVk1ToDigit()
	{
		assertEquals('1', resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'ą', 0));
		assertEquals('8', resolve(AUTO, NO_CUSTOM, KeyEvent.VK_8, 'ū', 0));
		assertEquals('0', resolve(AUTO, NO_CUSTOM, KeyEvent.VK_0, 'à', 0));
	}

	@Test
	public void autoMapsCapsLockUppercaseWithoutModifiers()
	{
		assertEquals('1', resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'Ą', 0));
	}

	@Test
	public void autoIgnoresKeyAlreadyTypingDigit()
	{
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_9, '9', 0));
	}

	@Test
	public void autoIgnoresModifiers()
	{
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'Ą', InputEvent.SHIFT_DOWN_MASK));
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'ą', InputEvent.CTRL_DOWN_MASK));
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'ą', InputEvent.ALT_DOWN_MASK));
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'ą', InputEvent.ALT_GRAPH_DOWN_MASK));
		// Windows reports AltGr as Ctrl+Alt
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'ą', InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
	}

	@Test
	public void autoIgnoresHeldMouseButton()
	{
		assertEquals('1', resolve(AUTO, NO_CUSTOM, KeyEvent.VK_1, 'ą', InputEvent.BUTTON1_DOWN_MASK));
	}

	@Test
	public void autoIgnoresNumpad()
	{
		assertEquals(CHAR_UNDEFINED, NumberRowListener.resolveDigit(AUTO, NO_CUSTOM, KeyEvent.VK_NUMPAD1, '1', 0, KEY_LOCATION_NUMPAD));
		// Numpad with Num Lock off: no char, still not touched
		assertEquals(CHAR_UNDEFINED, NumberRowListener.resolveDigit(AUTO, NO_CUSTOM, KeyEvent.VK_END, CHAR_UNDEFINED, 0, KEY_LOCATION_NUMPAD));
	}

	@Test
	public void autoIgnoresOtherKeys()
	{
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_UNDEFINED, 'ž', 0));
		assertEquals(CHAR_UNDEFINED, resolve(AUTO, NO_CUSTOM, KeyEvent.VK_A, 'a', 0));
	}

	// --- Custom / Both rule ---

	@Test
	public void customUsesCharacterNotKeyCode()
	{
		Map<Character, Character> custom = NumberRowListener.parseCustomMapping("&é\"'(-è_çà");
		assertEquals('2', resolve(CUSTOM, custom, KeyEvent.VK_UNDEFINED, 'é', 0));
		assertEquals('0', resolve(CUSTOM, custom, KeyEvent.VK_UNDEFINED, 'à', 0));
		// Custom mode alone does not apply the Auto rule
		assertEquals(CHAR_UNDEFINED, resolve(CUSTOM, custom, KeyEvent.VK_1, 'ą', 0));
	}

	@Test
	public void customMatchesUppercaseCharFromCapsLock()
	{
		Map<Character, Character> custom = NumberRowListener.parseCustomMapping("ěš");
		assertEquals('1', resolve(CUSTOM, custom, KeyEvent.VK_UNDEFINED, 'Ě', 0));
	}

	@Test
	public void customRespectsModifiersAndDigits()
	{
		Map<Character, Character> custom = NumberRowListener.parseCustomMapping("ěš");
		assertEquals(CHAR_UNDEFINED, resolve(CUSTOM, custom, KeyEvent.VK_UNDEFINED, 'Ě', InputEvent.SHIFT_DOWN_MASK));
		assertEquals(CHAR_UNDEFINED, resolve(CUSTOM, custom, KeyEvent.VK_UNDEFINED, 'x', 0));
	}

	@Test
	public void bothTriesAutoThenCustom()
	{
		Map<Character, Character> custom = NumberRowListener.parseCustomMapping("  é");
		assertEquals('1', resolve(BOTH, custom, KeyEvent.VK_1, 'ą', 0));
		assertEquals('3', resolve(BOTH, custom, KeyEvent.VK_UNDEFINED, 'é', 0));
	}

	// --- Custom string parsing ---

	@Test
	public void parseFullString()
	{
		Map<Character, Character> m = NumberRowListener.parseCustomMapping("ąčęėįšųū„“");
		assertEquals(10, m.size());
		assertEquals(Character.valueOf('1'), m.get('ą'));
		assertEquals(Character.valueOf('8'), m.get('ū'));
		assertEquals(Character.valueOf('0'), m.get('“'));
	}

	@Test
	public void parseShortString()
	{
		Map<Character, Character> m = NumberRowListener.parseCustomMapping("ąč");
		assertEquals(2, m.size());
		assertEquals(Character.valueOf('2'), m.get('č'));
	}

	@Test
	public void parseLongStringIgnoresExtra()
	{
		Map<Character, Character> m = NumberRowListener.parseCustomMapping("abcdefghijXYZ");
		assertEquals(10, m.size());
		assertEquals(Character.valueOf('0'), m.get('j'));
		assertTrue(!m.containsKey('x'));
	}

	@Test
	public void parseUppercaseIsStoredLowercase()
	{
		Map<Character, Character> m = NumberRowListener.parseCustomMapping("ĄČ");
		assertEquals(Character.valueOf('1'), m.get('ą'));
		assertEquals(Character.valueOf('2'), m.get('č'));
	}

	@Test
	public void parseSkipsWhitespaceAndDigitsKeepingPositions()
	{
		Map<Character, Character> m = NumberRowListener.parseCustomMapping("ą 3é");
		assertEquals(2, m.size());
		assertEquals(Character.valueOf('1'), m.get('ą'));
		assertEquals(Character.valueOf('4'), m.get('é'));
	}

	@Test
	public void parseDuplicateFirstWins()
	{
		Map<Character, Character> m = NumberRowListener.parseCustomMapping("aA");
		assertEquals(1, m.size());
		assertEquals(Character.valueOf('1'), m.get('a'));
	}

	@Test
	public void parseEmptyAndNull()
	{
		assertTrue(NumberRowListener.parseCustomMapping("").isEmpty());
		assertTrue(NumberRowListener.parseCustomMapping(null).isEmpty());
	}

	// --- Listener event sequences ---

	@Test
	public void pressTypedReleaseAllBecomeDigit()
	{
		listener.configure(AUTO, "", false);

		KeyEvent pressed = pressed(KeyEvent.VK_1, 'ą', 0);
		KeyEvent typed = typed('ą', 0);
		KeyEvent released = released(KeyEvent.VK_1, 'ą', 0);
		listener.keyPressed(pressed);
		listener.keyTyped(typed);
		listener.keyReleased(released);

		assertEquals(KeyEvent.VK_1, pressed.getKeyCode());
		assertEquals('1', pressed.getKeyChar());
		assertEquals('1', typed.getKeyChar());
		assertEquals(KeyEvent.VK_UNDEFINED, typed.getKeyCode());
		assertEquals(KeyEvent.VK_1, released.getKeyCode());
		assertEquals('1', released.getKeyChar());
	}

	@Test
	public void releaseMatchesPressEvenIfShiftPressedWhileHeld()
	{
		listener.configure(AUTO, "", false);

		listener.keyPressed(pressed(KeyEvent.VK_1, 'ą', 0));
		KeyEvent released = released(KeyEvent.VK_1, 'Ą', InputEvent.SHIFT_DOWN_MASK);
		listener.keyReleased(released);

		assertEquals('1', released.getKeyChar());
	}

	@Test
	public void unmappedPressLeavesTypedAlone()
	{
		listener.configure(AUTO, "", false);

		KeyEvent pressed = pressed(KeyEvent.VK_1, 'Ą', InputEvent.SHIFT_DOWN_MASK);
		KeyEvent typed = typed('Ą', InputEvent.SHIFT_DOWN_MASK);
		listener.keyPressed(pressed);
		listener.keyTyped(typed);

		assertEquals('Ą', pressed.getKeyChar());
		assertEquals('Ą', typed.getKeyChar());
	}

	@Test
	public void pendingDigitDoesNotLeakToNextKey()
	{
		listener.configure(AUTO, "", false);

		// A press with no following KEY_TYPED, then a different key
		listener.keyPressed(pressed(KeyEvent.VK_1, 'ą', 0));
		listener.keyPressed(pressed(KeyEvent.VK_A, 'a', 0));
		KeyEvent typed = typed('a', 0);
		listener.keyTyped(typed);

		assertEquals('a', typed.getKeyChar());
	}

	@Test
	public void customModeSetsKeyCodeOnPress()
	{
		listener.configure(CUSTOM, "ąčęėįšųū„“", false);

		KeyEvent pressed = pressed(KeyEvent.VK_UNDEFINED, '„', 0);
		KeyEvent typed = typed('„', 0);
		listener.keyPressed(pressed);
		listener.keyTyped(typed);

		assertEquals(KeyEvent.VK_9, pressed.getKeyCode());
		assertEquals('9', pressed.getKeyChar());
		assertEquals('9', typed.getKeyChar());
	}

	@Test
	public void onlyWhenChatEmptyGatesRemapping()
	{
		listener.configure(AUTO, "", true);

		listener.setChatInputEmpty(false);
		KeyEvent blocked = pressed(KeyEvent.VK_1, 'ą', 0);
		listener.keyPressed(blocked);
		assertEquals('ą', blocked.getKeyChar());

		listener.setChatInputEmpty(true);
		KeyEvent allowed = pressed(KeyEvent.VK_1, 'ą', 0);
		listener.keyPressed(allowed);
		assertEquals('1', allowed.getKeyChar());
	}

	private static char resolve(NumberRowFixConfig.Mode mode, Map<Character, Character> custom, int keyCode, char keyChar, int modifiersEx)
	{
		return NumberRowListener.resolveDigit(mode, custom, keyCode, keyChar, modifiersEx, KEY_LOCATION_STANDARD);
	}

	private static KeyEvent pressed(int keyCode, char keyChar, int modifiersEx)
	{
		return new KeyEvent(SOURCE, KeyEvent.KEY_PRESSED, 0L, modifiersEx, keyCode, keyChar, KEY_LOCATION_STANDARD);
	}

	private static KeyEvent typed(char keyChar, int modifiersEx)
	{
		return new KeyEvent(SOURCE, KeyEvent.KEY_TYPED, 0L, modifiersEx, KeyEvent.VK_UNDEFINED, keyChar, KEY_LOCATION_UNKNOWN);
	}

	private static KeyEvent released(int keyCode, char keyChar, int modifiersEx)
	{
		return new KeyEvent(SOURCE, KeyEvent.KEY_RELEASED, 0L, modifiersEx, keyCode, keyChar, KEY_LOCATION_STANDARD);
	}
}
