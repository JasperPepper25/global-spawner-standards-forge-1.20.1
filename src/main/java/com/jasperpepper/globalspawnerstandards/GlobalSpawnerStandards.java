package com.jasperpepper.globalspawnerstandards;

import com.jasperpepper.globalspawnerstandards.config.GssConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(GlobalSpawnerStandards.MOD_ID)
public final class GlobalSpawnerStandards {
    public static final String MOD_ID = "global_spawner_standards";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GlobalSpawnerStandards() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                GssConfig.SPEC,
                "global_spawner_standards-common.toml"
        );

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Global Spawner Standards initialized for Forge 1.20.1.");

        if (ModList.get().isLoaded("spawnercontrol")) {
            LOGGER.warn("Spawner Control is also installed. Both mods can alter vanilla spawner attributes; " +
                    "for clean testing, disable Spawner Control or keep its values aligned with Global Spawner Standards.");
        }
    }
}
