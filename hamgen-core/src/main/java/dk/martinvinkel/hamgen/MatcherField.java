package dk.martinvinkel.hamgen;

import dk.martinvinkel.hamgen.util.StringUtil;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;

public class MatcherField {
    private String getterName;
    private String name;
    private Class<?> type;
    private String fieldPostFix = MATCHER_POST_FIX.getDefaultValue();

    public MatcherField() {
    }

    private MatcherField(String getterName, String name, String fieldPostFix, Class<?> type) {
        this.getterName = getterName;
        this.name = name;
        this.fieldPostFix = fieldPostFix;
        this.type = type;
    }

    public static Builder builder(Class<?> type, String getterName) {
        return new Builder(type, getterName);
    }

    public String getGetterName() {
        return getterName;
    }

    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    private String getOrigName() {
        return name;
    }

    public String getName() {
        return name + fieldPostFix;
    }

    void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getFieldPostFix() {
        return fieldPostFix;
    }

    public void setFieldPostFix(String fieldPostFix) {
        this.fieldPostFix = fieldPostFix;
    }


    public static class Builder {
        private MatcherField matcherField = new MatcherField();

        private Builder(Class<?> type, String getterName) {
            withType(type);
            withGetterName(getterName);
            withPostFix(MATCHER_POST_FIX.getDefaultValue());
        }

        private Builder withName(String name) {
            this.matcherField.setName(name);
            return this;
        }

        public Builder withPostFix(String postFix) {
            matcherField.setFieldPostFix(postFix);
            return this;
        }

        public Builder withType(Class<?> type) {
            matcherField.setType(type);
            return this;
        }

        public Builder withGetterName(String getterName) {
            if(getterName == null) {
                return this;
            }
            if (!getterName.toLowerCase().startsWith("get")) {
                throw new IllegalStateException("Not a getter function " + getterName);
            }
            this.matcherField.setGetterName(getterName);
            withName(StringUtil.decapitalize(getterName.substring(3)));
            return this;
        }

        public MatcherField build() {
            return new MatcherField(matcherField.getGetterName(), matcherField.getOrigName(), matcherField.fieldPostFix, matcherField.getType());
        }


    }

}
