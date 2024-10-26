package dev.haxr.brandedlogs.neoforge;

import dev.haxr.brandedlogs.BrandedLogsCommon;
import dev.haxr.brandedlogs.config.BrandedLogsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(BrandedLogsCommon.MOD_ID)
public class BrandedLogsNeoForge {
	public BrandedLogsNeoForge() {
		registerConfig();
		BrandedLogsCommon.init();
	}
	public static void registerConfig() {
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> AutoConfig.getConfigScreen(BrandedLogsConfig.class, parent).get());
	}
}
