package haxr.brandedlogs;

import com.google.gson.*;
import haxr.brandedlogs.config.BrandedLogsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.nio.file.FileAlreadyExistsException;
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
    public static final String MOD_ID = "brandedlogs";
    public static JsonObject MODPACK_INFO = modpackInfoObject();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Registers the config screen.
        AutoConfig.register(BrandedLogsConfig.class, JanksonConfigSerializer::new);

        // Writes information to resource text files if enabled in config.
        if (BrandedLogsConfig.getInstance().doWriteToResourceTextFile)
            createResourceDirectory("./resources");
            createResourceDirectory("./resources/modpack");
            writeResourceTextFile("./resources/modpack/modpackversion.txt", "modpackVersion");
            writeResourceTextFile("./resources/modpack/modpackname.txt", "modpackName");

        String sysDetails = new SystemDetails().collect();

        sysDetails = sysDetails.replaceFirst("Java Version: (\\d+)", "Java Version: $1 ");

        if (MODPACK_INFO != null) {
            LOGGER.info("\n----------------={ Branded Logs }=----------------\n" + "Modpack: " + modpackInfo() + "\n" + sysDetails + "\n--------------------------------------------------");
        }
        else {
            LOGGER.info("\n----------------={ Branded Logs }=----------------\n" + sysDetails + "\n--------------------------------------------------");
        }

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
        LOGGER.info("An error occurred while reading the bcc.json file.");
        return null;
    }


    private static String modpackInfo() {
        try {
            JsonObject obj = MODPACK_INFO;
            return "'" + obj.get("modpackName").getAsString() + "' v" + obj.get("modpackVersion").getAsString();
        } catch (JsonIOException e) {
        } catch (JsonSyntaxException e) {
        } catch (NullPointerException e) {
        }
        return "";
    }


    public static void writeResourceTextFile(String pathString, String objectKey) {
        Path path = Paths.get(pathString);
        try {
            JsonObject obj = MODPACK_INFO;
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

    // Tries to create modpack resource directory in case it doesn't already exist.
    public static void createResourceDirectory(String pathString) {
        // Specify the directory path
        Path directoryPath = Paths.get(pathString);

        try {
            // Create the directory
            Files.createDirectory(directoryPath);
            System.out.println("Directory created successfully");
        } catch (FileAlreadyExistsException e) {
            System.err.println("Directory already exists: " + directoryPath);
        } catch (IOException e) {
            //throw new RuntimeException(e); // Uncomment for debugging
            System.err.println("Failed to create directory: " + e.getMessage());
        }
    }
}