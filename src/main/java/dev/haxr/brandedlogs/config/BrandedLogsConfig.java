package dev.haxr.brandedlogs.config;

import dev.haxr.brandedlogs.BrandedLogsCommon;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = BrandedLogsCommon.MOD_ID)
public class BrandedLogsConfig implements ConfigData {
    //@Comment("Useful for showing modpack name/version in FancyMenu.")
    public boolean doWriteToResourceTextFile = false;
}
