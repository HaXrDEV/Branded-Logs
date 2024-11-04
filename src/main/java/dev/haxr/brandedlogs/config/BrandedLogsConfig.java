package dev.haxr.brandedlogs.config;

import dev.haxr.brandedlogs.BrandedLogsCommon;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BrandedLogsCommon.MOD_ID)
public class BrandedLogsConfig implements ConfigData {
    // @Comment("")
    public boolean doWriteToResourceTextFile = false;
}
