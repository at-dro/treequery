package at.ac.tuwien.treequery.builder;

import java.util.LinkedHashMap;
import java.util.Map;

public class PropertiesBuilder {

    private final Map<String, Object> properties = new LinkedHashMap<>();

    private PropertiesBuilder() {
    }

    public static PropertiesBuilder props() {
        return new PropertiesBuilder();
    }

    public PropertiesBuilder set(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return properties;
    }
}
