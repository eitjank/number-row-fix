# Number Row Fix

Makes the number row type digits in Old School RuneScape on keyboard layouts where it normally doesn't.

## The problem

On many non-US layouts the keys above the letters don't type digits without a modifier:

| Layout | Number row types |
|---|---|
| French / Belgian AZERTY | `& é " ' ( - è _ ç à` |
| Czech / Slovak | `+ ě š č ř ž ý á í é` / `+ ľ š č ť ž ý á í é` |

Others (Lithuanian, Vietnamese, Thai, Persian, …) have the same issue. OSRS features that listen for number keys then don't respond:

- chatbox dialogue options
- numbered menus (Xeric's talisman, spirit trees, make-X)
- the "Enter amount" prompt (bank, Grand Exchange)
- bank PIN (with the core Bank plugin's "Keyboard Bankpin" option on)

RuneLite's built-in Key Remapping plugin doesn't cover the number row.

## How it works

**Auto** (default) fixes keys by physical position, with no per-language setup. Java usually reports the number row keys as `1`–`0` even when the layout types something else (confirmed on Windows), so when you press one of them **without** Shift, Ctrl, Alt or right Alt (AltGr) and it isn't already typing a digit, the plugin makes the game see the digit instead.

- On a US layout the number row already types digits, so the plugin does nothing.
- Combinations with Shift or right Alt are left alone, so `Shift + 1` and `right Alt + 1` still type whatever your layout puts there.
- The numpad is never touched.
- Caps Lock alone counts as "no modifier", so the number row still gives digits with Caps Lock on.
- The plugin is inactive on the login screen.

Key events are changed in place before the game sees them, the same way the official Key Remapping plugin works. No input is generated.

## Settings

| Setting | Default | What it does |
|---|---|---|
| Mode | Auto | **Auto**, **Custom** or **Both** (Auto first, then Custom). |
| Custom mapping | empty | The characters your number row types for `1 2 3 4 5 6 7 8 9 0`, in that order. |
| Only when chat input is empty | off | Only remap while the chatbox is empty. See below. |

### When to use Custom

Use Custom (or Both) if Auto doesn't work for you, which can happen on some layouts or operating systems where Java doesn't report the number row as `1`–`0`.

Type the characters your number row produces, left to right, for 1 through 0. For French AZERTY that's:

```
&é"'(-è_çà
```

Uppercase/lowercase doesn't matter. Use a space for a position you want to leave alone; anything after the 10th character is ignored.

### Typing accents in chat

With the plugin on, the number row types digits in chat too.

OSRS chat only accepts Western European letters (`é à ç á í ý ü ñ` …).

- **Layouts whose number row letters OSRS can't show** (Lithuanian, Thai, Persian, …): this costs nothing, those letters can't be typed in OSRS anyway.
- **French, Czech, Slovak:** some of your number row letters (`é è ç à`, `é á í ý`) *do* work in chat. Turn on **Only when chat input is empty** to keep them: the number row then types digits only while the chatbox is empty, so accents work after the first character of a message. A word that *starts* with one of those letters will still become a digit; turn the plugin off if you need that. Also, if you leave unsent text in the chatbox, the number row won't type digits in dialogues or the Enter amount prompt until you clear it.

## Key Remapping compatibility

Works alongside the official Key Remapping plugin, including WASD camera mode.

Key Remapping's F-key remap (number row → F1–F12) matches keys by the character your layout assigns to them, so on many non-US layouts its defaults don't fire, with or without this plugin. Rebinding those keys in Key Remapping's own settings to the keys you actually press should fix that.

## Please report your layout

So far Auto has only been tested on one layout (Lithuanian, Windows). If you use any other layout or OS (macOS, Linux), please [open an issue](https://github.com/eitjank/number-row-fix/issues) saying:

- your layout and OS
- whether Auto works for dialogue options and the Enter amount prompt
- if it doesn't: what your number row types, left to right, and whether a Custom mapping fixed it

## License

BSD 2-Clause, see [LICENSE](LICENSE).
