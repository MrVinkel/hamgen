package dk.martinvinkel.hamgen;

import java.util.Properties;

import static dk.martinvinkel.hamgen.HamProperties.Key.toKey;

public class HamProperties extends Properties {

    @Override
    public synchronized Object setProperty(String key, String value) {
        return setProperty(toKey(key), value);
    }

    public synchronized Object setProperty(Key key, String value) {
        return super.setProperty(key.getName(), value);
    }

    @Override
    public String getProperty(String key) {
        return getProperty(toKey(key));
    }

    public String getProperty(Key key) {
        if(containsKey(key.getName())) {
            // todo return default if is null or empty?
            return super.getProperty(key.getName());
        }
        return key.getDefaultValue();
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getProperty(toKey(key));
    }

    public String getProperty(Key key, String defaultValue) {
        return getProperty(key.getName(), defaultValue);
    }

    public enum Key {
        OUTPUT_DIR("outputdir", "target/generate-sources"),
        PACKAGE_NAME("packagename", null),
        FAIL_ON_NO_CLASSES_FOUND("failOnNoClasses", "true"),
        PACKAGE_POST_FIX("packagepostfix", ".matcher"),
        MATCHER_PRE_FIX("matcherprefix", "Is"),
        MATCHER_POST_FIX("matcherpostfix", "Matcher"),
        ;

        private String name;
        private String defaultValue;

        Key(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public static Key toKey(String keyToParse) {
            for(Key key : values()) {
                if(key.getName().toLowerCase().equals(keyToParse.toLowerCase())) {
                    return key;
                }
            }
            throw new IllegalStateException("Unknown property: " + keyToParse);
        }
    }
}

