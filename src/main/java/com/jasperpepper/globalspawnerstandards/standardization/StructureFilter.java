package com.jasperpepper.globalspawnerstandards.standardization;

import com.jasperpepper.globalspawnerstandards.config.GssConfig;
import com.jasperpepper.globalspawnerstandards.config.StructureFilterMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public final class StructureFilter {
    private StructureFilter() {}

    public static Result evaluate(ServerLevel level, BlockPos pos) {
        StructureFilterMode mode = GssConfig.filterMode();
        List<? extends String> configuredIds = GssConfig.STRUCTURE_IDS.get();

        ResourceLocation matched = null;
        for (String rawId : configuredIds) {
            ResourceLocation id = ResourceLocation.tryParse(rawId);
            if (id == null) continue;

            ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, id);
            if (level.structureManager().getStructureWithPieceAt(pos, key).isValid()) {
                matched = id;
                break;
            }
        }

        boolean matchedListedStructure = matched != null;
        boolean shouldStandardize = mode == StructureFilterMode.BLACKLIST
                ? !matchedListedStructure
                : matchedListedStructure;

        return new Result(shouldStandardize, mode, matched);
    }

    public record Result(
            boolean shouldStandardize,
            StructureFilterMode mode,
            ResourceLocation matchedStructure
    ) {}
}
