package com.epam.solid.nie.config.language;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CommunicateProviderImpl implements CommunicateProvider {
    private Map<Communicate, String> communicates = new HashMap<>();

    public CommunicateProviderImpl populate(Language language) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(language.name());
        Stream.of(Communicate.values()).forEach(consume(resourceBundle));
        return this;
    }

    private Consumer<Communicate> consume(ResourceBundle resourceBundle) {
        return element -> communicates.put(element, resourceBundle.getString(element
                .name()));
    }

    public String getCommunicate(Communicate communicate) {
        return communicates.get(communicate);
    }

    public boolean isMapEmpty(){
        return communicates.isEmpty();
    }
}