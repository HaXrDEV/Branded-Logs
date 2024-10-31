package dev.haxr.brandedlogs;

import com.google.gson.*;
import dev.haxr.brandedlogs.config.BrandedLogsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.CrashReportCategory;
import net.minecraft.SystemReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class BrandedLogsCommon {


    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("BrandedLogs");
    public static final String MOD_ID = "brandedlogs";
    public static JsonObject modpackInfoObj;
    public static BrandedLogsConfig config;
    public static final String INSTANCE_FILE_PATH = "./minecraftinstance.json";
    public static final String BCC_FILE_PATH = "./config/bcc.json";

    //@Override
    public static void init() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Registers the config screen.
        AutoConfig.register(BrandedLogsConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BrandedLogsConfig.class).getConfig();

        // Gets json object containing modpack information.
        modpackInfoObj = getModpackInfoObject();

        // Writes information to resource text files if enabled in config.
        if (config.doWriteToResourceTextFile && modpackInfoObj != null) {
            createResourceDirectory("./resources/modpack");
            writeResourceTextFile("./resources/modpack/modpackversion.txt", "modpackVersion");
            writeResourceTextFile("./resources/modpack/modpackname.txt", "modpackName");
        }

        // Gets system information.
        String sysDetails = new SystemReport().toLineSeparatedString();
        sysDetails = sysDetails.replaceFirst("Java Version: (\\d+)", "Java Version: $1 ");

        // Prints information into the logs.
        String header = "\n----------------={ Branded Logs }=----------------";
        String footer = "\n--------------------------------------------------";
        String content = modpackInfoObj != null
                ? String.format("\nModpack: %s\n%s", modpackInfo(modpackInfoObj), sysDetails)
                : "\n" + sysDetails;

        LOGGER.info("{}{}{}", header, content, footer);

    }
    /**
    * Prints modpack branding into the crash logs.
    */
    public static void crashBranding(CrashReportCategory category) {
        category.setDetail("Modpack", modpackInfo(modpackInfoObj));
    }


//    private static boolean usingInstanceFile(){
//        return config.parseMinecraftInstanceJson && new File(INSTANCE_FILE_PATH).isFile();
//    }

    /**
     * Gets JSON object containing modpack name and version information.
     */
    private static JsonObject getModpackInfoObject() {
        String filePath;

        if (config.parseMinecraftInstanceJson && new File(INSTANCE_FILE_PATH).isFile()) {
            filePath = INSTANCE_FILE_PATH;
        } else {
            filePath = BCC_FILE_PATH;
        }
        LOGGER.info("Reading {}", filePath); // Helpful for debugging

        try {
            JsonElement json = JsonParser.parseReader(new FileReader(filePath));
            JsonObject obj = json.getAsJsonObject();

            // Extract the relevant fields and create a new JsonObject
            String nameKey;
            String versionKey;
            JsonObject result = new JsonObject();
            if (filePath.equals(BCC_FILE_PATH)) {
                nameKey = "modpackName";
                versionKey = "modpackVersion";
                if(config.parseMinecraftInstanceJson && new File(INSTANCE_FILE_PATH).isFile()){
                    LOGGER.info("Falling back to {}", filePath);
                }
            } else {
                obj = obj.getAsJsonObject("manifest");
                nameKey = "name";
                versionKey = "version";
            }

            result.add("modpackName", obj.get(nameKey));
            result.add("modpackVersion", obj.get(versionKey));
            return result.getAsJsonObject();

        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException | NullPointerException ignored) {
        }
        LOGGER.error("An error occurred while reading the {} file.", filePath);
        return null;
    }


    /**
     * Takes json object as argument and returns the modpack information as a string.
     */
    public static String modpackInfo(JsonObject objArg) {
        try {
            return "'" + objArg.get("modpackName").getAsString() + "' v" + objArg.get("modpackVersion").getAsString();
        } catch (JsonIOException | JsonSyntaxException | NullPointerException ignored) {
            return "";
        }
    }


    public static void writeResourceTextFile(String pathString, String objectKey) {
        Path path = Paths.get(pathString);
        try {
            JsonObject obj = modpackInfoObj;
            String content = obj.get(objectKey).getAsString();
            Files.writeString(path, content);

            LOGGER.info("File created and content written successfully! ({})", objectKey);

        } catch (JsonIOException | JsonSyntaxException | NullPointerException ignored) {
        } catch (IOException e) {
            LOGGER.info("An error occurred while writing to the file: {}", e.getMessage());
        }
    }

    /**
     * Creates a modpack resource directory and any necessary parent directories.
     * Returns silently if directory already exists.
     */
    public static void createResourceDirectory(String pathString) {
        // Specify the directory path
        Path directoryPath = Paths.get(pathString);

        try {
            // Create the directory and any necessary parent directories
            Files.createDirectories(directoryPath);
            LOGGER.info("Directory created successfully: {}", directoryPath);
        } catch (IOException e) {
            // throw new RuntimeException(e); // Uncomment for debugging
            LOGGER.error("Failed to create directory: {}", e.getMessage());
        }
    }
}