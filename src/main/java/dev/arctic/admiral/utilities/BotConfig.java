package dev.arctic.admiral.utilities;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class BotConfig {
    public static String aiToken;

    public static String mysqlHost;
    public static int mysqlPort;
    public static String mysqlDatabase;
    public static String mysqlUsername;
    public static String mysqlPassword;

    public static final Map<String, Double> thresholds = new HashMap<>();


    private static final Path CONFIG_PATH = Paths.get("config.toml");

    public static boolean loadConfig() {
        if (Files.notExists(CONFIG_PATH)) {
            try (InputStream in = BotConfig.class.getClassLoader().getResourceAsStream("config.toml")) {
                if (in == null) {
                    System.err.println("[ERROR] Default config.toml missing in resources!");
                    return false;
                }
                Files.copy(in, CONFIG_PATH);
                System.out.println("[INFO] config.toml not found. Default created. Please configure it and restart.");
                return false;
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to create default config.toml: " + e.getMessage());
                return false;
            }
        }

        try {
            TomlParseResult config = Toml.parse(Files.readString(CONFIG_PATH));

            aiToken = config.getString("openai.key");

            mysqlHost = config.getString("mysql.host");
            mysqlPort = Math.toIntExact(config.getLong("mysql.port"));
            mysqlDatabase = config.getString("mysql.database");
            mysqlUsername = config.getString("mysql.username");
            mysqlPassword = config.getString("mysql.password");

            var tree = config.getTable("openai.thresholds");
            if (tree != null) {
                for (String key : tree.keySet()) {
                    Double val = tree.getDouble(key);
                    if (val != null) {
                        thresholds.put(key, val);
                    }
                }
            }


            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse config.toml: " + e.getMessage());
            return false;
        }
    }
}
