package dev.haxr.brandedlogs.forge;

import dev.haxr.brandedlogs.BrandedLogsCommon;
import dev.haxr.brandedlogs.config.BrandedLogsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(BrandedLogsCommon.MOD_ID)
public class BrandedLogsForge {
	public BrandedLogsForge() {
		registerConfig();
		BrandedLogsCommon.init();
	}

	public static void registerConfig() {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> AutoConfig.getConfigScreen(BrandedLogsConfig.class, parent).get()));
	}
}
