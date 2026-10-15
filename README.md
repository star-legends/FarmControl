# FarmControl
[Hangar](https://hangar.papermc.io/froobynooby/FarmControl) | [Modrinth](https://modrinth.com/plugin/farmcontrol) | [Spigot](https://www.spigotmc.org/resources/86923/)

## About
FarmControl is a Bukkit plugin that allows you to control certain properties of farms on your server.

**Features**:
* Disable breeding in oversized animal farms and villager breeders.
* Reduce unnecessary random movement within mob farms.
* Disable the AI of mobs in farms.
* Limit the number of entities allowed in an area.
* Highly configurable - allowing you to tailor the plugin to your needs.
* Low impact - with the brunt of the plugin's processing performed asynchronously.

## Building
If you would like to build the plugin yourself you can follow these steps.

1\. Clone FarmControl and build
```bash
git clone https://github.com/froobynooby/FarmControl
cd FarmControl
./gradlew clean build
```

2\. Find jar in `FarmControl/build/libs`
# Leaf / Paper 26.2 adaptation

This branch targets Leaf/Paper 26.2 and Java 25. Build with `./gradlew build`
(Windows: `gradlew.bat build`). The shaded plugin is written to `build/libs/`.

- Legacy exclusion names `chest_boat` and `minecart_chest` remain accepted.
  The first protects every chest boat/raft variant; unknown names produce a warning.
- When installed and enabled, MythicMobs and ModelEngine are queried through their
  public UUID lookup APIs. Their entities are excluded, including a final check on
  the owning thread immediately before new actions. Neither plugin is required.
- If an installed integration cannot be queried, a severe console message explains
  the problem and new actions are skipped for all entities until restart. Undoing
  previous actions remains possible.
- The random-movement hook checks all required NMS classes and field types before
  making the action available. Removed wrappers are restored with their priorities.
- Fresh installations use 32 animals for the proactive soft-nerf profile. Existing
  `profiles.yml` files are preserved: change `profiles.soft-nerf-animal-farms.group.count`
  to `32` manually if desired. This threshold describes a nearby group, not a global
  player or chunk limit. No kill or breeding-limit profile is activated by this update.
- Existing `config.yml` files remain valid; no reset is needed. Restart the server
  to replace the JAR and load optional integrations.

Validation: 9 regression tests pass. An isolated inspection of the official Leaf
26.2-124 server classes also verified the goal fields, movement classes and mutation/
restoration of Leaf's BinaryGoalSet. Live startup on Leaf 26.2-123 with MythicMobs and
ModelEngine R4.1.0 has not been tested; the commercial plugin JARs were not available.
