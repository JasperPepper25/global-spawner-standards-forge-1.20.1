package com.jasperpepper.globalspawnerstandards.standardization;

import com.jasperpepper.globalspawnerstandards.GlobalSpawnerStandards;
import com.jasperpepper.globalspawnerstandards.config.GssConfig;
import com.jasperpepper.globalspawnerstandards.mixin.BaseSpawnerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class SpawnerStandardizer {
    private static final String DATA_ROOT = "GlobalSpawnerStandards";
    private static final String KEY_EVALUATED_REVISION = "EvaluatedRevision";
    private static final String KEY_APPLIED = "Applied";
    private static final String KEY_FILTER_MODE = "FilterMode";
    private static final String KEY_MATCHED_STRUCTURE = "MatchedStructure";

    private SpawnerStandardizer() {}

    public static void evaluate(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            SpawnerBlockEntity blockEntity
    ) {
        int configuredRevision = GssConfig.STANDARDIZATION_REVISION.get();
        CompoundTag persistentData = blockEntity.getPersistentData();
        CompoundTag marker = persistentData.getCompound(DATA_ROOT);

        if (marker.getInt(KEY_EVALUATED_REVISION) >= configuredRevision) {
            return;
        }

        StructureFilter.Result filter = StructureFilter.evaluate(level, pos);
        boolean applied = false;

        if (filter.shouldStandardize()) {
            applyConfiguredValues(blockEntity);
            applied = true;
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }

        marker.putInt(KEY_EVALUATED_REVISION, configuredRevision);
        marker.putBoolean(KEY_APPLIED, applied);
        marker.putString(KEY_FILTER_MODE, filter.mode().name());
        if (filter.matchedStructure() != null) {
            marker.putString(KEY_MATCHED_STRUCTURE, filter.matchedStructure().toString());
        } else {
            marker.remove(KEY_MATCHED_STRUCTURE);
        }
        persistentData.put(DATA_ROOT, marker);
        blockEntity.setChanged();

        if (GssConfig.DEBUG_LOGGING.get()) {
            if (applied) {
                GlobalSpawnerStandards.LOGGER.info(
                        "Standardized spawner at {} in {} (revision {}).",
                        pos, level.dimension().location(), configuredRevision
                );
            } else {
                GlobalSpawnerStandards.LOGGER.info(
                        "Skipped spawner at {} in {} by {} filter{} (revision {}).",
                        pos,
                        level.dimension().location(),
                        filter.mode(),
                        filter.matchedStructure() == null ? "" : " matched " + filter.matchedStructure(),
                        configuredRevision
                );
            }
        }
    }

    private static void applyConfiguredValues(SpawnerBlockEntity blockEntity) {
        BaseSpawnerAccessor spawner = (BaseSpawnerAccessor) blockEntity.getSpawner();

        int minDelay = GssConfig.MIN_SPAWN_DELAY.get();
        int maxDelay = Math.max(minDelay, GssConfig.MAX_SPAWN_DELAY.get());

        spawner.globalSpawnerStandards$setSpawnDelay(GssConfig.INITIAL_DELAY.get());
        spawner.globalSpawnerStandards$setMinSpawnDelay(minDelay);
        spawner.globalSpawnerStandards$setMaxSpawnDelay(maxDelay);
        spawner.globalSpawnerStandards$setSpawnCount(GssConfig.SPAWN_COUNT.get());
        spawner.globalSpawnerStandards$setMaxNearbyEntities(GssConfig.MAX_NEARBY_ENTITIES.get());
        spawner.globalSpawnerStandards$setRequiredPlayerRange(GssConfig.REQUIRED_PLAYER_RANGE.get());
        spawner.globalSpawnerStandards$setSpawnRange(GssConfig.SPAWN_RANGE.get());
    }
}
