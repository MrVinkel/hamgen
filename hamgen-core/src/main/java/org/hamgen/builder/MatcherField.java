package org.hamgen.builder;

import com.squareup.javapoet.*;
import org.hamgen.log.Logger;
import org.hamgen.util.StringUtil;
import org.hamcrest.Matcher;
import org.hamgen.HamProperties;
import org.hamgen.util.ClassUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static javax.lang.model.element.Modifier.PROTECTED;

public class MatcherField {
    private String getterName;
    private String name;
    private Type type;
    private String fieldPostFix = HamProperties.Key.MATCHER_POST_FIX.getDefaultValue();

    MatcherField() {
        //Use builder
    }

    private MatcherField(String getterName, String name, String fieldPostFix, Type type) {
        this.getterName = getterName;
        this.name = name;
        this.fieldPostFix = fieldPostFix;
        this.type = type;
    }

    public static Builder builder(Type type, String getterName) {
        return new Builder(type, getterName);
    }

    public static Builder builder(MatcherField matcherField) {
        return new Builder(matcherField);
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

    void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getTypeClass() {
        if(type instanceof ParameterizedType) {
            return (Class)((ParameterizedType) type).getRawType();
        }
        return (Class)type;
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


    public static class Builder {
        private static final Logger LOGGER = Logger.getLogger();
        private MatcherField matcherField = new MatcherField();
        private Map<ClassName, String> staticImports = new HashMap<>();

        private Builder(Type type, String getterName) {
            withType(type);
            withGetterName(getterName);
            withPostFix(HamProperties.Key.MATCHER_POST_FIX.getDefaultValue());
        }

        private Builder(MatcherField matcherField) {
            withType(matcherField.getType());
            withGetterName(matcherField.getGetterName());
            withPostFix(matcherField.getFieldPostFix());
        }

        private Builder withName(String name) {
            this.matcherField.setName(name);
            return this;
        }

        public Builder withPostFix(String postFix) {
            matcherField.setFieldPostFix(postFix);
            return this;
        }

        public Builder withType(Type type) {
            matcherField.setType(type);
            return this;
        }

        public Builder withGetterName(String getterName) {
            if (getterName == null) {
                return this;
            }
            if (!getterName.toLowerCase().startsWith("get")) {
                throw new IllegalStateException("Not a getter function " + getterName);
            }
            this.matcherField.setGetterName(getterName);
            withName(StringUtil.deCapitalizeFirstLetter(getterName.substring(3)));
            return this;
        }

        public MatcherField build() {
            return new MatcherField(matcherField.getGetterName(), matcherField.getOrigName(), matcherField.fieldPostFix, matcherField.getType());
        }

        public FieldSpec buildFieldSpec() {
            TypeName matcher = ClassName.get(Matcher.class);
            return FieldSpec.builder(matcher, matcherField.getName())
                    .addModifiers(PROTECTED)
                    .build();
        }

        public CodeBlock buildDescriptionTo(boolean firstField, String descriptionParameter) {
            CodeBlock.Builder descriptionTo = CodeBlock.builder();

            if (!firstField) {
                descriptionTo.addStatement("$N.appendText($S)", descriptionParameter, ", ");
            }

            return descriptionTo.addStatement("$N.appendText($S)", descriptionParameter, matcherField.getOrigName() + " ")
                    .addStatement("$N.appendDescriptionOf($N)", descriptionParameter, matcherField.getName())
                    .build();
        }

        public CodeBlock buildMatchesSafely(ParameterSpec actualParameter, ParameterSpec mismatchDescriptionParameter, ParameterSpec matchesLocalField) {
            return CodeBlock.builder()
                    .beginControlFlow("if (!$N.matches($N.$N()))", matcherField.getName(), actualParameter, matcherField.getGetterName())
                    .addStatement("reportMismatch($S, $N, $N.$N(), $N, $N)", matcherField.getOrigName(), matcherField.getName(), actualParameter, matcherField.getGetterName(), mismatchDescriptionParameter, matchesLocalField)
                    .addStatement("$N = false", matchesLocalField)
                    .endControlFlow()
                    .build();
        }

        // todo refactor this mess
        public CodeBlock buildMatcherInitialization(String expectedName, String matcherPreFix, String packagePostFix) {
            LOGGER.debug(matcherField.getType().toString());
            if (matcherField.getType() == String.class) {
                return CodeBlock.builder().addStatement("this.$N = $N.$N() == null || $N.$N().isEmpty() ? isEmptyOrNullString() : is($N.$N())",
                        matcherField.getName(), expectedName, matcherField.getGetterName(),
                        expectedName, matcherField.getGetterName(),
                        expectedName, matcherField.getGetterName())
                        .build();
            } else if (matcherField.getTypeClass().isPrimitive() || ClassUtil.isPrimitiveWrapper(matcherField.getTypeClass()) || matcherField.getTypeClass().isEnum()) {
                return CodeBlock.builder().addStatement("this.$N = is($N.$N())",
                        matcherField.getName(), expectedName, matcherField.getGetterName())
                        .build();
            } else if(Collection.class.isAssignableFrom(matcherField.getTypeClass())) {
                return buildCollectionMatcher(matcherPreFix, packagePostFix);
            } else {
                // Assume a matcher is generated for the type
                String matcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(matcherField.getTypeClass().getSimpleName());
                addStaticImport(packagePostFix, matcherPreFix, matcherField.getTypeClass());
                return CodeBlock.builder().addStatement("this.$N = $N.$N() == null ? nullValue() : $N($N.$N())",
                        matcherField.getName(), expectedName, matcherField.getGetterName(),
                        matcherFactoryName, expectedName, matcherField.getGetterName())
                        .build();
            }
        }

        private CodeBlock buildCollectionMatcher(String matcherPreFix, String packagePostFix) {
            CodeBlock.Builder builder = CodeBlock.builder();

            ParameterizedType parameterizedType = (ParameterizedType) matcherField.getType();
            Type collectionType = parameterizedType.getActualTypeArguments()[0];
            Class<?> collectionClass = (Class<?>) collectionType;

            builder.addStatement("$T<$T> items = expected.$N()", List.class, collectionClass, matcherField.getGetterName());
            builder.beginControlFlow("if (items == null)");
            builder.addStatement("this.$N = nullValue()", matcherField.getName());
            builder.nextControlFlow("else");
            builder.addStatement("$T<$T> matchers = new $T<>()", List.class, Matcher.class, ArrayList.class);
            builder.beginControlFlow("for ($T item : items)", collectionClass);
            if(collectionType == String.class) {
                builder.addStatement("$T matcher = item == null ||item.isEmpty() ? isEmptyOrNullString() : is(item)", Matcher.class);
            } else if(collectionClass.isPrimitive() || ClassUtil.isPrimitiveWrapper(collectionClass) || collectionClass.isEnum()) {
                builder.addStatement("$T matcher = is(item)", Matcher.class);
            } else {
                addStaticImport(packagePostFix, matcherPreFix, collectionClass);
                builder.addStatement("$T matcher = item == null ? nullValue() : is$N(item)", Matcher.class, collectionClass.getSimpleName());
            }
            builder.addStatement("matchers.add(matcher)");
            builder.endControlFlow();
            builder.addStatement("this.$N = contains(matchers.toArray(new $T[matchers.size()]))", matcherField.getName(), Matcher.class);
            builder.endControlFlow();
            return builder.build();
        }

        private void addStaticImport(String packagePostFix, String matcherPreFix, Class clazz) {
            String matcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(clazz.getSimpleName());
            ClassName matcherClass = ClassName.get(clazz.getPackage().getName() + packagePostFix, clazz.getSimpleName() + matcherField.getFieldPostFix());
            staticImports.put(matcherClass, matcherFactoryName);
        }

        public Map<ClassName, String> buildStaticImports() {
            return staticImports;
        }

    }

}
