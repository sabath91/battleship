package com.epam.solid.nie.config.language;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * It provides relevant messages to the game.
 */
public class CommunicateProviderImpl implements CommunicateProvider {
    private Map<Communicate, String> communicates = new HashMap<>();

    /**
     * Fills the map with messages in the appropriate language.
     * @param language
     * @return actual instance enriched of messages in given languages
     */
    public CommunicateProviderImpl populate(Language language) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(language.name());
        Stream.of(Communicate.values()).forEach(consume(resourceBundle));
        return this;
    }

    private Consumer<Communicate> consume(ResourceBundle resourceBundle) {
        return element -> communicates.put(element, resourceBundle.getString(element
                .name()));
    }

    /**
     * Retrieves the message in the appropriate language from the map.
     * @param communicate
     * @return specified message from the map
     */
    public String getCommunicate(Communicate communicate) {
        return communicates.get(communicate);
    }

    /**
     * Determines whether the map is empty.
     * @return true if the map is empty
     */
    public boolean isMapEmpty(){
        return communicates.isEmpty();
    }
}
