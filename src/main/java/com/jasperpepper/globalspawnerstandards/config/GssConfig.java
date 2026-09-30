package com.jasperpepper.globalspawnerstandards.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class GssConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue STANDARDIZATION_REVISION;
    public static final ForgeConfigSpec.IntValue INITIAL_DELAY;
    public static final ForgeConfigSpec.IntValue MIN_SPAWN_DELAY;
    public static final ForgeConfigSpec.IntValue MAX_SPAWN_DELAY;
    public static final ForgeConfigSpec.IntValue SPAWN_COUNT;
    public static final ForgeConfigSpec.IntValue MAX_NEARBY_ENTITIES;
    public static final ForgeConfigSpec.IntValue REQUIRED_PLAYER_RANGE;
    public static final ForgeConfigSpec.IntValue SPAWN_RANGE;

    public static final ForgeConfigSpec.ConfigValue<String> STRUCTURE_FILTER_MODE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STRUCTURE_IDS;
    public static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;

    private static final int SHORT_MAX = Short.MAX_VALUE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "Global Spawner Standards applies these values once to each vanilla mob spawner after its NBT is loaded.",
                "This intentionally overrides structure-authored spawner timing/count/range NBT while leaving SpawnData and later upgrades alone.",
                "When changing defaults or the structure filter for an existing world, increment standardizationRevision to re-evaluate loaded spawners once."
        ).push("standardization");

        STANDARDIZATION_REVISION = builder
                .comment(
                        "Revision marker stored per spawner.",
                        "Increase this integer whenever you want already-evaluated spawners to be processed again.",
                        "Do NOT change it for normal play; keeping it stable prevents later Apotheosis upgrades from being overwritten."
                )
                .defineInRange("standardizationRevision", 1, 1, Integer.MAX_VALUE);

        INITIAL_DELAY = builder
                .comment("Initial Delay in ticks applied when the spawner is standardized.")
                .defineInRange("initialDelay", 20, 0, SHORT_MAX);

        MIN_SPAWN_DELAY = builder
                .comment("Minimum delay between successful spawn cycles, in ticks.")
                .defineInRange("minSpawnDelay", 100, 0, SHORT_MAX);

        MAX_SPAWN_DELAY = builder
                .comment("Maximum delay between successful spawn cycles, in ticks. Values below minSpawnDelay are clamped up at runtime.")
                .defineInRange("maxSpawnDelay", 300, 0, SHORT_MAX);

        SPAWN_COUNT = builder
                .comment("Number of spawn attempts per spawn cycle.")
                .defineInRange("spawnCount", 6, 1, SHORT_MAX);

        MAX_NEARBY_ENTITIES = builder
                .comment("Maximum nearby entities of the spawned class before the spawner pauses.")
                .defineInRange("maxNearbyEntities", 20, 1, SHORT_MAX);

        REQUIRED_PLAYER_RANGE = builder
                .comment("Distance in blocks within which a living player activates the spawner.")
                .defineInRange("requiredPlayerRange", 45, 1, SHORT_MAX);

        SPAWN_RANGE = builder
                .comment("Horizontal/vertical spawn attempt range around the spawner.")
                .defineInRange("spawnRange", 4, 0, SHORT_MAX);

        builder.pop();

        builder.comment(
                "Optional structure-based filtering.",
                "BLACKLIST: standardize every spawner EXCEPT spawners located inside a listed generated structure piece.",
                "WHITELIST: standardize ONLY spawners located inside a listed generated structure piece.",
                "An empty BLACKLIST affects all spawners. An empty WHITELIST affects none.",
                "Structure IDs use registry names such as minecraft:stronghold or dungeons_arise:example_structure."
        ).push("structure_filter");

        STRUCTURE_FILTER_MODE = builder
                .comment("Allowed values: BLACKLIST or WHITELIST.")
                .define("mode", "BLACKLIST", GssConfig::validateFilterMode);

        STRUCTURE_IDS = builder
                .comment("Structure registry IDs used by the selected filter mode.")
                .defineListAllowEmpty("structures", List.of(), GssConfig::validateResourceLocation);

        builder.pop();

        builder.push("debug");
        DEBUG_LOGGING = builder
                .comment("Log each one-time standardization/filter decision. Useful for testing; leave false for normal play.")
                .define("logDecisions", false);
        builder.pop();

        SPEC = builder.build();
    }

    private GssConfig() {}

    private static boolean validateFilterMode(Object value) {
        if (!(value instanceof String string)) return false;
        return "BLACKLIST".equalsIgnoreCase(string) || "WHITELIST".equalsIgnoreCase(string);
    }

    private static boolean validateResourceLocation(Object value) {
        return value instanceof String string && ResourceLocation.tryParse(string) != null;
    }

    public static StructureFilterMode filterMode() {
        return StructureFilterMode.fromConfig(STRUCTURE_FILTER_MODE.get());
    }
}
