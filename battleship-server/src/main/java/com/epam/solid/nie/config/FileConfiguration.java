package com.epam.solid.nie.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Reads configuration from the file.
 */
public class FileConfiguration implements Configuration {

    private EnumMap<ConfigProperty, String> map = new EnumMap(ConfigProperty.class);

    /**
     * Return the config map which is used in the game.
     * The map is filled based on the configuration file.
     * @return
     */
    public Map<ConfigProperty, String> provide() {
        if (map.isEmpty()) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(Configuration.CONFIGURATION);
            Stream.of(ConfigProperty.values()).forEach(consume(resourceBundle));
        }
        return map;
    }

    private Consumer<ConfigProperty> consume(ResourceBundle resourceBundle) {
        return element -> map.put(element, resourceBundle.getString(element.name()));
    }
}