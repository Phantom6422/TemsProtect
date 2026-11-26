# Languages

TemsProtect ships with English messages. Override any phrase in `plugins/TemsProtect/language.yml`; missing phrases fall back to the bundled English text.

TemsProtect does **not** upload phrases or use an online translation service. The `lang/` directory in the source repository contains translation templates that can be edited and contributed manually.

## Adding a translation

1. Copy `lang/en.yml` to a new locale file (for example `lang/de.yml`).
2. Translate the values, keeping the message keys unchanged.
3. Place the file in a pull request, or copy the values into `plugins/TemsProtect/language.yml` on your server.

The `language` option in `config.yml` is retained for compatibility; message selection is driven by the keys present in `language.yml`.
