package haxr.brandedlogs.mixin;

import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import haxr.brandedlogs.BrandedLogsMod;


import java.io.File;
import java.util.List;

/**
 * Adapted from CrashBrander under LGPL-3.0
 * <a href="https://github.com/Deftu/CrashBrander/blob/main/LICENSE">LICENSE</a>
 */
@Mixin({CrashReport.class})
public class CrashReportMixin {
    @Shadow @Final private List<CrashReportSection> otherSections;

    @Inject(method = "writeToFile", at = @At("HEAD"))
    private void crashbrander$addSection(File file, CallbackInfoReturnable<Boolean> cir) {
        CrashReportSection section = new CrashReportSection("Modpack Branding");
        BrandedLogsMod.crashBranding(section);
        otherSections.add(section);
    }

}