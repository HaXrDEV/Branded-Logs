package dev.haxr.brandedlogs;

import com.google.gson.*;
import dev.haxr.brandedlogs.config.BrandedLogsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.CrashReportCategory;
import net.minecraft.SystemReport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class BrandedLogsCommon {


    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("BrandedLogs");
    public static final String MOD_ID = "brandedlogs";
    public static JsonObject modpackInfoObj;
    public static BrandedLogsConfig config;
    public static final String BCC_FILE_PATH = "./config/bcc.json";
    public static final String CF_INSTANCE_FILE_PATH = "./minecraftinstance.json";
    public static final String MMC_INSTANCE_FILE_PATH = "../instance.cfg";  // Uses two dots to go back one folder further than the game instance.
    public static final String AT_GD_INSTANCE_FILE_PATH = "./instance.json";

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

    /**
     * Gets JSON object containing modpack name and version information.
     * Parses the first in prioritized order of BCC_FILE_PATH, CF_INSTANCE_FILE_PATH or MMC_INSTANCE_FILE_PATH.
     */
    private static JsonObject getModpackInfoObject() {
        final String filePath = getFilePath();

        try {
            JsonObject result = new JsonObject();

            if (filePath.endsWith(".cfg")) {
                // Handle .cfg file
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    props.load(fis);

                    String packName = props.getProperty("ManagedPackName");
                    String packVersion = props.getProperty("ManagedPackVersionName");

                    if (packName != null && packVersion != null) {
                        result.addProperty("modpackName", packName);
                        result.addProperty("modpackVersion", packVersion);
                        LOGGER.info("Reading {} as CFG file", filePath);
                        return result;
                    }
                }
            } else {
                // Handle JSON files
                JsonElement json = JsonParser.parseReader(new FileReader(filePath));
                JsonObject obj = json.getAsJsonObject();

                // Extract the relevant fields and create a new JsonObject
                String nameKey = "";
                String versionKey = "";

                switch (filePath) {
                    case CF_INSTANCE_FILE_PATH -> {
                        obj = obj.getAsJsonObject("manifest");
                        nameKey = "name";
                        versionKey = "version";
                    }
                    case AT_GD_INSTANCE_FILE_PATH -> {
                        if (obj.has("launcher")) { // "launcher" section only exists in ATLaunchers instance file.
                            obj = obj.getAsJsonObject("launcher");
                            nameKey = "pack";
                            versionKey = "version";
                        } else {
                            LOGGER.info("'{}' does not belong to ATLauncher, must be GDLauncher.", filePath);
                            nameKey = "name";
                            versionKey = "modpack::version_id"; // GDLauncher
                        }
                    }
                    case BCC_FILE_PATH -> {
                        nameKey = "modpackName";
                        versionKey = "modpackVersion";
                    }
                }

                LOGGER.info("Reading {} as JSON file", filePath);

                result.add("modpackName", getJsonValue(obj, nameKey));
                result.add("modpackVersion", getJsonValue(obj, versionKey));

                return result;
            }

        } catch (JsonIOException | JsonSyntaxException | IOException | NullPointerException e) {

            if (filePath.isEmpty()){
                LOGGER.error("Could not find any BCC config or launcher instance file: '{}', '{}', '{}', '{}'", BCC_FILE_PATH, CF_INSTANCE_FILE_PATH, MMC_INSTANCE_FILE_PATH, AT_GD_INSTANCE_FILE_PATH);
            } else {
                LOGGER.error("An error occurred while reading the {} file: {}", filePath, e.getMessage());
            }
        }

        return null;
    }


    /**
     * Checks in prioritized order which file to parse information from based on whether the file exists or not.
     */
    private static @NotNull String getFilePath() {
        String filePath;
        if (new File(BCC_FILE_PATH).isFile()) {
            filePath = BCC_FILE_PATH;
        } else if (new File(CF_INSTANCE_FILE_PATH).isFile()) {
            filePath = CF_INSTANCE_FILE_PATH;
        } else if (new File(MMC_INSTANCE_FILE_PATH).isFile()) {
            filePath = MMC_INSTANCE_FILE_PATH;
        } else if (new File(AT_GD_INSTANCE_FILE_PATH).isFile()) {
            filePath = AT_GD_INSTANCE_FILE_PATH;
        } else {
            filePath = "";
        }
        return filePath;
    }


    /**
     * Retrieves the value of a JSON object, handling both nested and non-nested keys.
     *
     * @param topLevelObject The top-level JSON object.
     * @param path           The path to the desired value, formatted as "key" for top-level or "key1::key2::...::keyN" for nested.
     * @return The value at the specified path as a JsonElement, or null if not found or if an error occurs.
     */
    public static JsonElement getJsonValue(JsonObject topLevelObject, String path) {
        if (topLevelObject == null || path == null || path.isEmpty()) {
            return null;
        }

        // Split path to check for nested or single key
        String[] keys = path.split("::");

        try {
            if (keys.length == 1) {
                // Handle non-nested (single key) request
                if (topLevelObject.has(keys[0])) {
                    return topLevelObject.get(keys[0]);
                } else {
                    return null; // Key not found
                }
            } else {
                // Handle nested key request
                JsonObject jsonObject = topLevelObject;
                for (String key : keys) {
                    if (jsonObject.has(key)) {
                        JsonElement value = jsonObject.get(key);
                        if (keys[keys.length - 1].equals(key)) {
                            // If this is the last key, return its value
                            return value;
                        } else {
                            // Move into the nested JSON object
                            if (value.isJsonObject()) {
                                jsonObject = value.getAsJsonObject();
                            } else {
                                // Path leads to a non-object, return null
                                return null;
                            }
                        }
                    } else {
                        // Key not found, return null
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            // Handle exceptions, return null for safety
            System.err.println("Exception occurred while navigating JSON: " + e.getMessage());
            return null;
        }

        // If we've reached this point without returning, something went wrong
        return null;
    }



    /**
     * Takes json object as argument and returns the modpack information as a string.
     */
    public static String modpackInfo(JsonObject objArg) {
        try {
            String modpackName = objArg.has("modpackName") && !objArg.get("modpackName").isJsonNull()
                    ? objArg.get("modpackName").getAsString()
                    : "Unknown";
            String modpackVersion = objArg.has("modpackVersion") && !objArg.get("modpackVersion").isJsonNull()
                    ? objArg.get("modpackVersion").getAsString()
                    : "Unknown";
            return "'" + modpackName + "' " + modpackVersion;
        } catch (JsonIOException | JsonSyntaxException e) {
            return "";
        }
    }

    /**
     * Writes the content of a specific JSON key from the `modpackInfoObj` JsonObject to a file at the specified path.
     *
     * @param pathString  The file path where the content will be written.
     * @param objectKey   The key in the `modpackInfoObj` JsonObject whose value will be written to the file.
     */
    public static void writeResourceTextFile(String pathString, String objectKey) {
        Path path = Paths.get(pathString);
        try {
            JsonObject obj = modpackInfoObj;
            String content = "";
            if (obj.has(objectKey) && !obj.get(objectKey).isJsonNull()) {
                content = obj.get(objectKey).getAsString();
                Files.writeString(path, content);
                LOGGER.info("File written successfully! (\'{}\')", pathString);
            } else {
                LOGGER.info("The requested JSON key '{}' does not exist or is empty. No file written.", objectKey);
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            LOGGER.info("An error occurred while processing the JSON data: {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.info("An error occurred while writing to the file: {}", e.getMessage());
        }
    }

    /**
     * Creates a modpack resource directory and any necessary parent directories.
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