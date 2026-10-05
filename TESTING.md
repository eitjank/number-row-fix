# Manual test checklist

Run with `./gradlew run`, log in, and keep the default config (Mode: Auto, "Only when chat input is empty" off) unless a step says otherwise.
"Number row" below means the physical 1–0 keys above the letters, pressed **without** Shift, Ctrl, Alt or right Alt (AltGr).

Note: OSRS chat only shows Western European letters (`é à ç ü` …). If your number row types letters outside that set (e.g. Lithuanian `ą č ę`), those keys show **nothing** in chat when the plugin leaves them alone. That's the game, not the plugin.

## Core features

Run this section once with the official Key Remapping plugin **off**.

- [ ] **Chatbox dialogue options**: talk to an NPC with a numbered option list (e.g. a banker). Pressing number row 1–5 picks the matching option.
- [ ] **"Click here to continue"**: Space still works (the plugin doesn't touch it).
- [ ] **Xeric's talisman** (or a spirit tree): number row keys pick the matching destination.
- [ ] **Bank "Enter amount"**: Withdraw-X, press number row keys → the prompt shows digits (e.g. 1 2 5 → `125`). Enter confirms.
- [ ] **Bank PIN**: typing the PIN on the number row enters the right digits. Needs the core Bank plugin's "Keyboard Bankpin" option turned on (that's what makes the PIN typeable at all).
- [ ] **Numpad**: the numpad still types digits in the Enter amount prompt and bank PIN.
- [ ] **Caps Lock on**: number row still gives digits in a dialogue.
- [ ] **Shift is left alone**: in the Enter amount prompt, Shift + number row does **not** enter a digit.

## "Only when chat input is empty" toggle

**Toggle off** (default):

- [ ] Click the chatbox, press the number row 1 key → `1` appears in chat.

**Toggle on:**

- [ ] Empty chatbox, press the number row 1 key → `1` appears.
- [ ] Type `a`, then press the number row 1 key → **no** `1` appears after the `a`. (Lithuanian: nothing appears. French AZERTY: `é` appears.)
- [ ] Clear the chatbox, open a dialogue → number row keys still pick options.
- [ ] Type `a` in the chatbox but don't send it, open a dialogue → number row keys **don't** pick options. Expected and documented; clearing the chatbox fixes it.

## Config changes at runtime

- [ ] Switch Mode to **Custom** with an empty mapping → dialogue options stop working with the number row.
- [ ] Set Custom mapping to what your number row types, 1 to 0 (Lithuanian: `ąčęėįšųū`) → dialogue options work again.
- [ ] Switch Mode back to **Auto** → works. Turn the plugin off → dialogue options stop working; on → work again.

## Alongside the official Key Remapping plugin (WASD mode)

Enable Key Remapping with camera remap (WASD) on, plus its default settings.

- [ ] Chat locked ("Press Enter to Chat..."): dialogue options via number row still work.
- [ ] Chat locked: Withdraw-X → number row gives digits in the amount prompt.
- [ ] Press Enter, type a message using number row keys → same as the toggle tests above. Esc/Enter exits typing mode normally.
- [ ] WASD still moves the camera; no stuck keys after holding a number row key and releasing.
- [ ] **Listener order**: turn Number Row Fix off and back on (it now runs *after* Key Remapping), repeat the dialogue + Withdraw-X checks. Then turn Key Remapping off and on (ours runs *first*) and repeat.

Known, not a bug: Key Remapping's F-key remap (number row → F1–F12) matches on Java's "extended key code", which on layouts like Lithuanian is the accent letter, not `1`. That already doesn't fire without this plugin, and this plugin can't change it. Rebinding those keys in Key Remapping's own config to the actual keys should work.
