package dev.haxr.brandedlogs.mixin;

import dev.haxr.brandedlogs.BrandedLogsMod;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12100
import net.minecraft.ReportType;
//#endif

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Adapted from CrashBrander under LGPL-3.0
 * <a href="https://github.com/Deftu/CrashBrander/blob/main/LICENSE">LICENSE</a>
 */
@Mixin({ CrashReport.class })
public class CrashReportMixin {
    @Shadow
    @Final
    private List<CrashReportCategory> details;

    @Inject(method = "saveToFile", at = @At("HEAD"))
    // spotless:off
    //#if MC >= 12100
    private void crashbrander$addSection(Path path, ReportType type, List<String> extraInfo, CallbackInfoReturnable callbackInfoReturnable) {
    //#else
    //$$ private void crashbrander$addCategory(File toFile, CallbackInfoReturnable<Boolean> cir) {
    //#endif
    //spotless:on
        CrashReportCategory category = new CrashReportCategory("Modpack Branding");
        BrandedLogsMod.crashBranding(category);
        details.add(category);
    }
}