# Global Spawner Standards

A lightweight native Forge **1.20.1** mod that applies configurable baseline attributes to vanilla mob spawners **after their NBT has been loaded**. This lets the configured values override structure-authored spawner NBT while preserving the spawner's mob/SpawnData and later per-spawner upgrades.

Built for a heavily modified ATM9-based Forge 1.20.1 pack, but the implementation is intentionally small and general-purpose.

## Target environment

- Minecraft 1.20.1
- Forge 47.4.16+
- Java 17+

## Default standardized values

| Attribute | Default |
| --- | ---: |
| Initial Delay | 20 ticks |
| Min Spawn Delay | 100 ticks |
| Max Spawn Delay | 300 ticks |
| Spawn Count | 6 |
| Max Nearby Entities | 20 |
| Required Player Range | 45 blocks |
| Spawn Range | 4 blocks |

The mod **does not change** `SpawnData`, `SpawnPotentials`, the spawned mob ID, spawned-mob equipment/NBT, or custom data owned by other mods.

## One-time standardization

Each spawner is evaluated once for the configured `standardizationRevision`. The revision is stored in Forge persistent block-entity data.

This is deliberate:

1. Structure-authored NBT is already present before standardization runs.
2. The global baseline is applied once.
3. Later changes made by mods such as Apotheosis are left alone.
4. Restarting/reloading the world does not continually erase those upgrades.

If you intentionally change the global defaults or the structure filter for an existing world, increase `standardizationRevision` by one. Previously evaluated spawners will then be processed once at the new revision.

## Structure whitelist / blacklist

The common config supports two modes:

- `BLACKLIST` — standardize every spawner **except** spawners located inside one of the listed structures.
- `WHITELIST` — standardize **only** spawners located inside one of the listed structures.

An empty blacklist means all spawners are eligible. An empty whitelist means none are eligible.

Structure matching uses Minecraft's actual generated **structure pieces** at the spawner's block position rather than only the structure's overall bounding box. IDs use the structure registry, for example:

```toml
[structure_filter]
  mode = "BLACKLIST"
  structures = ["minecraft:stronghold", "minecraft:ancient_city"]
```

When changing this list in an existing world, increment `standardizationRevision` so previously evaluated spawners are checked again.

## Example config

Forge creates `config/global_spawner_standards-common.toml` on first launch.

```toml
[standardization]
  standardizationRevision = 1
  initialDelay = 20
  minSpawnDelay = 100
  maxSpawnDelay = 300
  spawnCount = 6
  maxNearbyEntities = 20
  requiredPlayerRange = 45
  spawnRange = 4

[structure_filter]
  mode = "BLACKLIST"
  structures = []

[debug]
  logDecisions = false
```

## Compatibility notes

### Apotheosis / Apothic Spawners

The mod is designed so Apotheosis can still alter an individual spawner **after** the global baseline is applied. The revision marker prevents Global Spawner Standards from continuously resetting the values.

### Cursed Spawners

Global Spawner Standards only changes vanilla `BaseSpawner` timing/count/range fields. It does not touch Cursed Spawners' action, reforge, break, mimic, loot, or particle data.

### Spawner Control

Spawner Control can modify some of the same vanilla spawner attributes. Running both is redundant and may create order-dependent behavior. For clean testing, remove/disable Spawner Control or keep its values aligned with this mod.

## CI validation

The GitHub Actions workflow:

1. Builds against Forge 47.4.16 / Java 17.
2. Boots a dedicated Forge server.
3. Places a spawner containing custom `200/400/2/6/12/4` NBT to simulate a structure-authored spawner.
4. Verifies it becomes `100/300/6/20/45/4` plus the configured initial delay.
5. Verifies a manual post-standardization edit survives subsequent ticks at the same revision.
6. Boots a headless Forge client to catch mixin/client-load regressions.
7. Uploads the built jar as a CI artifact.

## License

MIT. See [LICENSE](LICENSE).
