package dev.haxr.brandedlogs.mixin;

import dev.haxr.brandedlogs.BrandedLogsCommon;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.21 {
import net.minecraft.ReportType;
import java.nio.file.Path;
//?} else {
/*import java.io.File;
*///?}

import java.util.List;

/**
 * Adapted from CrashBrander under LGPL-3.0
 * <a href="https://github.com/Deftu/CrashBrander/blob/main/LICENSE">LICENSE</a>
 */
@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Shadow @Final
    private List<CrashReportCategory> details;

    //? if >=1.21 {
    @Inject(method = "saveToFile*", at = @At(value = "HEAD", target = "Lnet/minecraft/CrashReport;saveToFile(Ljava/nio/file/Path;Lnet/minecraft/ReportType;Ljava/util/List;)Z"))
    private void crashbrander$addCategory(Path path, ReportType reportType, List<String> list, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*@Inject(method = "saveToFile", at = @At(value = "HEAD"))
    private void crashbrander$addCategory(File file, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        CrashReportCategory category = new CrashReportCategory("Modpack Branding");
        BrandedLogsCommon.crashBranding(category);
        details.add(category);
    }
}