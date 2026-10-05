# Number Row Fix — agent notes

Trimmed from the runelite/example-plugin AGENTS.md to what applies to this plugin.

## Project

- Plugin mutates number row KeyEvents in place (like the official Key Remapping plugin). See README.md for behaviour, TESTING.md for the in-game checklist.
- Build/test: `./gradlew build`. Run dev client: `./gradlew run`.
- If Gradle fails with "Unable to establish loopback connection" (Java 23 AF_UNIX pipe issue in some shells), set `JAVA_TOOL_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:/nrf-nonexistent` for that run. Don't commit this.

## RuneLite / Plugin Hub rules that apply here

- Java 11 compatible; `build.gradle` must keep the example-plugin structure and target Java 11.
- No reflection, JNI/JNA, external processes, dynamic class loading, or Java serialization.
- **No injecting input events.** Mutating an existing KeyEvent is fine; creating/dispatching new ones is not. No autotyping into the chatbox.
- Use `log.debug()`, never per-event `log.info()`.
- Key listeners run on the AWT thread. Read game state on the client thread (e.g. `ClientTick`) and cache it.
- Use `net.runelite.api.gameval` constants (`VarClientID`, `InterfaceID`, …), no magic numbers.
- **Never rename a config key or the `numberrowfix` config group** without a migration; it silently resets users' settings.
- Clean up listeners in `shutDown()`. Remove unused config, fields and imports.
- Don't commit build artifacts. Keep the BSD-2 license.

## Testing

You cannot verify plugin behaviour yourself. **Never use screen-capture or computer-use tools to interact with RuneScape** — automating game input violates Jagex's rules and can get the user banned. Offer to launch `./gradlew run`, tell the user exactly what to test, and wait for their confirmation. Login for the dev client: https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts

## Submitting

PRs to runelite/plugin-hub end the description with:

Generated-by: Claude Code
