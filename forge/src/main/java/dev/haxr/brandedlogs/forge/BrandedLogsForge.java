package dev.haxr.brandedlogs.forge;

import dev.haxr.brandedlogs.BrandedLogsCommon;
import net.minecraftforge.fml.common.Mod;

@Mod(BrandedLogsCommon.MOD_ID)
public class BrandedLogsForge {
	public BrandedLogsForge() {
		BrandedLogsCommon.init();
	}
}
