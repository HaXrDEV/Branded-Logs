package haxr.brandedlogs;

import com.google.gson.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import net.minecraft.util.SystemDetails;
import net.minecraft.util.crash.CrashReportSection;


public class BrandedLogsMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("BrandedLogs");
    public static JsonObject MODPACKINFO = modpackInfoObject();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        writeResourceTextFile("./resources/modpack/modpackversion.txt", "modpackVersion");
        writeResourceTextFile("./resources/modpack/modpackname.txt", "modpackName");

        String sysDetails = new SystemDetails().collect();

        sysDetails = sysDetails.replaceFirst("Java Version: (\\d+)", "Java Version: $1 ");

        LOGGER.info("\n----------------={ Branded Logs }=----------------\n" + "Modpack: " + modpackInfo() + "\n" + sysDetails + "\n--------------------------------------------------");

    }


    public static void crashBranding(CrashReportSection section) {
        section.add("Modpack", modpackInfo());
    }


    private static JsonObject modpackInfoObject() {
        try {
            JsonElement json = JsonParser.parseReader(new FileReader("./config/bcc.json"));
            JsonObject obj = json.getAsJsonObject();
            return obj;
        } catch (JsonIOException e) {
        } catch (JsonSyntaxException e) {
        } catch (FileNotFoundException e) {
        } catch (NullPointerException e) {
        }
        return null;
    }


    private static String modpackInfo() {
        try {
            JsonObject obj = MODPACKINFO;
            return obj.get("modpackName").getAsString() + " version " + obj.get("modpackVersion").getAsString();
        } catch (JsonIOException e) {
        } catch (JsonSyntaxException e) {
        } catch (NullPointerException e) {
        }
        return "";
    }


    public static void writeResourceTextFile(String pathString, String objectKey) {
        Path path = Paths.get(pathString);
        try {
            JsonObject obj = MODPACKINFO;
            String content = obj.get(objectKey).getAsString();
            Files.writeString(path, content);

            LOGGER.info("File created and content written successfully! (" + objectKey + ")");

        } catch (JsonIOException e) {
        } catch (JsonSyntaxException e) {
        } catch (NullPointerException e) {
        } catch (IOException e) {
            LOGGER.info("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}