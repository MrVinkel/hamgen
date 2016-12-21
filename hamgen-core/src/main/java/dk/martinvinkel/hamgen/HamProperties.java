package dk.martinvinkel.hamgen;

import java.util.Properties;

import static dk.martinvinkel.hamgen.HamProperties.Key.toKey;

public class HamProperties extends Properties {

    @Override
    public synchronized Object setProperty(String key, String value) {
        return setProperty(toKey(key), value);
    }

    public synchronized Object setProperty(Key key, String value) {
        return super.setProperty(key.getValue(), value);
    }

    @Override
    public String getProperty(String key) {
        return getProperty(toKey(key));
    }

    public String getProperty(Key key) {
        return super.getProperty(key.getValue());
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getProperty(toKey(key), defaultValue);
    }

    public String getProperty(Key key, String defaultValue) {
        return super.getProperty(key.getValue(), defaultValue);
    }

    enum Key {
        OUTPUT_DIR("outputdir"),
        PACKAGE_NAME("packagename"),
        PACKAGE_POST_FIX("packagepostfix"),
        MATCHER_PRE_FIX("matcherprefix");

        private String value;

        Key(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Key toKey(String keyToParse) {
            for(Key key : values()) {
                if(key.getValue().toLowerCase().equals(keyToParse.toLowerCase())) {
                    return key;
                }
            }
            throw new IllegalStateException("Unknown property: " + keyToParse);
        }
    }
}

