package com.vizja.testweb.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    private static synchronized Properties loadProperties() {
        if (properties != null) {
            return properties;
        }
        properties = new Properties();
        String[] paths = {
                "configuration.properties",
                "backend/configuration.properties"
        };
        for (String pathStr : paths) {
            Path path = Paths.get(pathStr);
            if (Files.isRegularFile(path)) {
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    properties.load(fis);
                    return properties;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load " + pathStr, e);
                }
            }
        }
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("configuration.properties")) {
            if (is != null) {
                properties.load(is);
                return properties;
            }
        } catch (IOException e) {
        }
        throw new RuntimeException(
                "configuration.properties not found. Tried: " + String.join(", ", paths) + " and classpath. " +
                        "Current working directory: " + Paths.get("").toAbsolutePath());
    }

    public static String getProperty(String key) {
        return loadProperties().getProperty(key);
    }
}
