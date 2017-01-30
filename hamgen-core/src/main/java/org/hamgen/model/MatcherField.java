package org.hamgen.model;

import org.hamgen.HamProperties;
import org.hamgen.builder.MatcherFieldBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MatcherField {
    private String getterName;
    private String name;
    private Type type;
    private String fieldPostFix = HamProperties.Key.MATCHER_POST_FIX.getDefaultValue();

    public MatcherField(String getterName, String name, String fieldPostFix, Type type) {
        this.getterName = getterName;
        this.name = name;
        this.fieldPostFix = fieldPostFix;
        this.type = type;
    }

    public static MatcherFieldBuilder builder(Type type, String getterName) {
        return new MatcherFieldBuilder(type, getterName);
    }

    public static MatcherFieldBuilder builder(MatcherField matcherField) {
        return new MatcherFieldBuilder(matcherField);
    }

    public String getGetterName() {
        return getterName;
    }

    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    public String getOrigName() {
        return name;
    }

    public String getName() {
        return name + fieldPostFix;
    }

    public void setOrigName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getTypeClass() {
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        return (Class) type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFieldPostFix() {
        return fieldPostFix;
    }

    public void setFieldPostFix(String fieldPostFix) {
        this.fieldPostFix = fieldPostFix;
    }
}
