package haxr.brandedlogs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;


public class BrandedLogsMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("BrandedLogs");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        String sysDetails = new SystemDetails().collect();

        sysDetails = sysDetails.replaceFirst("Java Version: (\\d+)", "Java Version: $1 ");

        LOGGER.info("\n----------------={ Branded Logs }=----------------\n" + "Modpack: " + modpackInfo() + "\n" + sysDetails + "\n--------------------------------------------------");
    }

    public static void crashBranding(CrashReportSection section) {
        section.add("Modpack", modpackInfo());
    }

    private static String modpackInfo() {
        try {
            JsonElement name = JsonParser.parseReader(new FileReader("./resources/modpack/modpackname.txt"));
            JsonElement version = JsonParser.parseReader(new FileReader("./resources/modpack/modpackversion.txt"));

            return name.getAsString() + " " + version.getAsString();

        } catch (JsonIOException e) {
        } catch (JsonSyntaxException e) {
        } catch (FileNotFoundException e) {
        } catch (NullPointerException e) {
        }
        return "";
    }
}