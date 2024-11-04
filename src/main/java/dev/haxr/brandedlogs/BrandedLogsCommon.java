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
    public static final String MMC_INSTANCE_FILE_PATH = "./instance.cfg";
    public static final String AT_INSTANCE_FILE_PATH = "./instance.json";

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
                String nameKey;
                String versionKey;

                if (filePath.equals(CF_INSTANCE_FILE_PATH)) {
                    obj = obj.getAsJsonObject("manifest");
                    nameKey = "name";
                    versionKey = "version";
                } else if (filePath.equals(AT_INSTANCE_FILE_PATH)) {
                    obj = obj.getAsJsonObject("launcher");
                    nameKey = "pack";
                    versionKey = "version";
                }
                else {
                    nameKey = "modpackName";
                    versionKey = "modpackVersion";
                }
                LOGGER.info("Reading {} as JSON file", filePath);

                result.add("modpackName", obj.get(nameKey));
                result.add("modpackVersion", obj.get(versionKey));
                return result;
            }

        } catch (JsonIOException | JsonSyntaxException | IOException | NullPointerException e) {

            if (filePath.isEmpty()){
                LOGGER.error("Could not find any BCC config or launcher instance file: '{}', '{}', '{}'", BCC_FILE_PATH, CF_INSTANCE_FILE_PATH, MMC_INSTANCE_FILE_PATH);
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
        } else if (new File(AT_INSTANCE_FILE_PATH).isFile()) {
            filePath = AT_INSTANCE_FILE_PATH;
        } else {
            filePath = "";
        }
        return filePath;
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

            LOGGER.info("File written successfully! (\'{}\')", pathString);

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