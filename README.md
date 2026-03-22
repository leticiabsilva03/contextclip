# ContextClip

`ContextClip` is a system tray clipboard manager written in Java that remembers where every copy came from. Inspired by the simplicity of local-first tools, it explores clipboard interception, native OS integration via JNA, and full-text search, without frameworks, without Spring, without shortcuts.

<!-- ![ContextClip Demo](docs/demo.gif) -->

---

## Features

- Intercepts every `Ctrl+C` and saves it locally with window title and timestamp
- `Ctrl+Shift+V` opens a searchable history popup from any focused window
- FTS5-powered full-text search
- 100% local
- Sensitive apps (KeePass, 1Password, Bitwarden) are blocked by default
- Auto-cleanup of entries older than N days on startup (starred entries are kept)
- Configurable via `~/.contextclip/config.properties`

---

## Project Structure

```
src/main/java/dev/contextclip/
├── app/           Main — wires all components together
├── capture/       ClipboardWatcher, WindowsCaptor, HotkeyManager
├── config/        AppConfig — reads ~/.contextclip/config.properties
├── domain/        ClipEntry, WindowContext (immutable records)
├── repository/    ClipRepository (interface), SqliteRepository, DbMigration
└── ui/            SystemTrayManager, HistoryPopup
```

---

## Tech stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 25 | Language and runtime |
| Gradle | 9.2 | Build system |
| SQLite (xerial JDBC) | 3.45.1 | Local database |
| JNA Platform | 5.14 | Windows API — window title capture |
| JNativeHook | 2.2.2 | Global keyboard hook |
| AWT / Swing | JDK built-in | System tray and popup UI |

---

## Prerequisites

- Windows 10 or later
- No JDK required for the installer — the runtime is bundled

---

## Installation

Download the latest `.exe` installer from the [Releases](https://github.com/leticiabsilva03/contextclip/releases) page and run it.

---

## Building from source

Requirements: JDK 25, Gradle 9.2+

```bash
git clone https://github.com/leticiabsilva03/contextclip.git
cd contextclip
./gradlew run
```

To build a standalone installer:

```bash
./gradlew jpackage
```

The installer is generated in `build/jpackage/`.

---

## Configuration

On first run, ContextClip creates `~/.contextclip/config.properties` with defaults:

```properties
retentionDays=30
maxEntries=5000
blockedApps=KeePass,1Password,Bitwarden
```

Edit with any text editor. Changes take effect on the next startup.

---

## Keyboard shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl+Shift+V` | Open history popup |
| `Enter` | Copy selected item back to clipboard |
| `Escape` | Close popup |

---

## License

This project is open-source and available under the [MIT License](LICENSE).
