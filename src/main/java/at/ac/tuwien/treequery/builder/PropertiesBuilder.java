package at.ac.tuwien.treequery.builder;

import at.ac.tuwien.treequery.annotation.PublicApi;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a utility class for building a properties {@code Map}.
 */
@PublicApi
public class PropertiesBuilder {

    private final Map<String, Object> properties = new LinkedHashMap<>();

    private PropertiesBuilder() {
    }

    /**
     * Creates a new builder with empty properties.
     *
     * @return The new builder instance
     */
    @PublicApi
    public static PropertiesBuilder props() {
        return new PropertiesBuilder();
    }

    /**
     * Sets a property in the map to the given value.
     * A previously existing property with the same key is overwritten.
     *
     * @param key The key of the property
     * @param value The new value for the property
     * @return This builder instance
     */
    @PublicApi
    public PropertiesBuilder set(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    /**
     * Obtains the final properties from the builder.
     * This builder instance should not be used anymore after this method was called.
     *
     * @return The finalized properties <code>Map</code>
     */
    @PublicApi
    public Map<String, Object> build() {
        return properties;
    }
}
