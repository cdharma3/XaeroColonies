senterpi fork
==========
Fork of Artmaster629/XaeroColonies for the AOCA senterpi server. Changes on top of upstream 1.0:

- Client: only ask Xaero's World Map to refresh regions when the claim map actually changed,
  only for fully loaded idle regions, and never once the map session is finalizing. Upstream
  refreshed every loaded region on every packet, and Xaero's World Map 1.45 asserts on that
  during disconnect ("Detected non-loadstate 2 region with refreshing value being true"),
  crashing the client on every quit.
- Server: resend the claim map only when it changed (or on login), not on every chunk-section
  crossing. Null-guard the delete-colony hook when the owner is offline.
- Removed the per-packet stdout dump.

Build: GitHub Actions on push (`./gradlew build`, jar as an artifact). Releases on this fork
are what the server and client pack pin. Drop the fork the day upstream ships a fix.

Mod info
==========
Repository of Minecraft mod that adds compatibility between MineColonies and Xaero's map mods by adding territories of colony to map.
=======
Mod Author's Discord: https://discord.gg/GxGHskDRW2


Репозиторий мода Майнкрафт, который добавляет совместимость между MineColonies и модами на карту от Xaero путём отображения территории колонии на карте.
=======
Дискорд-сервер автора мода: https://discord.gg/GxGHskDRW2
