# Development notes

## Core lifecycle

`SpawnerBlockEntity.serverTick` is injected at `HEAD` on the logical server. On the first tick for a spawner at a config revision:

1. Read the Forge persistent evaluation marker.
2. Evaluate the structure filter against configured structure registry IDs.
3. If eligible, update only the seven vanilla `BaseSpawner` fields through a Mixin accessor.
4. Store the evaluated revision and filter result in persistent block-entity data.
5. Mark the block entity changed and sync the block to clients.

No continuous enforcement occurs after the marker is written.

## Persistent marker

Stored under Forge block-entity persistent data:

```text
GlobalSpawnerStandards: {
  EvaluatedRevision: int,
  Applied: byte,
  FilterMode: string,
  MatchedStructure: string?
}
```

Excluded spawners are also marked as evaluated for that revision so they are not structure-queried every tick. Incrementing `standardizationRevision` reevaluates both applied and excluded spawners.

## Structure matching

For each configured structure ID, the mod creates a `ResourceKey<Structure>` and calls:

```java
level.structureManager().getStructureWithPieceAt(pos, structureKey).isValid()
```

This checks whether the spawner is actually inside a generated piece for that structure.
