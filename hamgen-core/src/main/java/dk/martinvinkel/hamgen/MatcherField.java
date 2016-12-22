package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import dk.martinvinkel.hamgen.util.StringUtil;
import org.hamcrest.Matcher;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static javax.lang.model.element.Modifier.PROTECTED;

public class MatcherField {
    private String getterName;
    private String name;
    private Class<?> type;
    private String fieldPostFix = MATCHER_POST_FIX.getDefaultValue();

    MatcherField() {
        //Use builder
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

    public static Builder builder(MatcherField matcherField) {
        return new Builder(matcherField);
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

        public Builder withType(Class<?> type) {
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

            return descriptionTo.addStatement("$N.appendText($S)", descriptionParameter, matcherField.getName() + " ")
                    .addStatement("$N.appendDescriptionOf($N)", descriptionParameter, matcherField.getName())
                    .build();
        }

        public CodeBlock buildMatchesSafely(ParameterSpec actualParameter, ParameterSpec mismatchDescriptionParameter, ParameterSpec matchesLocalField) {
            return CodeBlock.builder()
                    .beginControlFlow("if(!$N.matches($N.$N()))", matcherField.getName(), actualParameter, matcherField.getGetterName())
                    .addStatement("reportMismatch($S, $N, $N.$N(), $N, $N)", matcherField.getName(), matcherField.getName(), actualParameter, matcherField.getGetterName(), mismatchDescriptionParameter, matchesLocalField)
                    .addStatement("$N = false", matchesLocalField)
                    .endControlFlow()
                    .build();
        }

        public CodeBlock buildMatcherInitialization(String expectedName) {
            if (matcherField.getType() == String.class) {
                return CodeBlock.builder().addStatement("this.$N = $N.$N() == null || $N.$N().isEmpty() ? isEmptyOrNullString() : is($N.$N())",
                        matcherField.getName(), expectedName, matcherField.getGetterName(),
                        expectedName, matcherField.getGetterName(),
                        expectedName, matcherField.getGetterName())
                        .build();
            } else {
                return CodeBlock.builder().addStatement("this.$N = $N.$N() == null ? nullValue() : is($N.$N())",
                        matcherField.getName(), expectedName, matcherField.getGetterName(),
                        expectedName, matcherField.getGetterName())
                        .build();
            }
        }

    }

}
