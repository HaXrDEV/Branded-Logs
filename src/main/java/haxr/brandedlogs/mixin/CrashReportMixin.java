package haxr.brandedlogs.mixin;

import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import haxr.brandedlogs.BrandedLogsMod;

import java.nio.file.Path;
import java.util.List;

/**
 * Adapted from CrashBrander under LGPL-3.0
 * <a href="https://github.com/Deftu/CrashBrander/blob/main/LICENSE">LICENSE</a>
 */
@Mixin({CrashReport.class})
public class CrashReportMixin {
    @Shadow @Final private List<CrashReportSection> otherSections;

    @Inject(method = "writeToFile", at = @At("HEAD"))
    private void crashbrander$addSection(Path path, ReportType type, List<String> extraInfo, CallbackInfoReturnable callbackInfoReturnable) {
        CrashReportSection section = new CrashReportSection("Modpack Branding");
        BrandedLogsMod.crashBranding(section);
        otherSections.add(section);
    }
}