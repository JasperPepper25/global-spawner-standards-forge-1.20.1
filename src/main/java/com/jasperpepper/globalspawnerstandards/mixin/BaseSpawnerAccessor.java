package com.jasperpepper.globalspawnerstandards.mixin;

import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {
    @Accessor("spawnDelay")
    void globalSpawnerStandards$setSpawnDelay(int value);

    @Accessor("minSpawnDelay")
    void globalSpawnerStandards$setMinSpawnDelay(int value);

    @Accessor("maxSpawnDelay")
    void globalSpawnerStandards$setMaxSpawnDelay(int value);

    @Accessor("spawnCount")
    void globalSpawnerStandards$setSpawnCount(int value);

    @Accessor("maxNearbyEntities")
    void globalSpawnerStandards$setMaxNearbyEntities(int value);

    @Accessor("requiredPlayerRange")
    void globalSpawnerStandards$setRequiredPlayerRange(int value);

    @Accessor("spawnRange")
    void globalSpawnerStandards$setSpawnRange(int value);
}
