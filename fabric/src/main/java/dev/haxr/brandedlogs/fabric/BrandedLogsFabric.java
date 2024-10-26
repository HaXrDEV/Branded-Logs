package dev.haxr.brandedlogs.fabric;

import net.fabricmc.api.ModInitializer;
import dev.haxr.brandedlogs.BrandedLogsCommon;

public class BrandedLogsFabric implements ModInitializer {
	@Override
	public void onInitialize() {

		BrandedLogsCommon.init();
	}
}
