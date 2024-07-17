package dev.haxr.brandedlogs.config;

import dev.haxr.brandedlogs.BrandedLogsMod;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BrandedLogsMod.MOD_ID)
public class BrandedLogsConfig implements ConfigData {
    // @Comment("")
    public boolean doWriteToResourceTextFile = false;

    public static BrandedLogsConfig getInstance() {
        return AutoConfig.getConfigHolder(BrandedLogsConfig.class).getConfig();
    }
}
