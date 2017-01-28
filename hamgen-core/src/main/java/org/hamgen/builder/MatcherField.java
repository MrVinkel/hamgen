package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;
import org.hamgen.log.Logger;
import org.hamgen.util.StringUtil;
import org.hamgen.HamProperties;
import org.hamgen.util.ClassUtil;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.sun.codemodel.JExpr.FALSE;

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
        private JCodeModel codeModel;

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

        public Builder withCodeModel(JCodeModel codeModel) {
            this.codeModel = codeModel;
            return this;
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

        public JFieldVar buildFieldSpec(JDefinedClass matcherClass, JClass hamcrestMatcherClass) {
            return matcherClass.field(Modifier.PROTECTED, hamcrestMatcherClass, matcherField.getName());
        }

        public JBlock buildDescribeTo(JBlock describeToBody, JVar descriptionParam, JVar matcher, boolean first) {
            if (!first) {
                describeToBody.invoke(descriptionParam, "appendText").arg(", ");
            }
            describeToBody.invoke(descriptionParam, "appendText").arg(matcherField.getOrigName() + " ");
            describeToBody.invoke(descriptionParam, "appendDescriptionOf").arg(matcher);
            return describeToBody;
        }

        public JBlock buildMatchesSafely(JBlock matcherSafelyBody, JVar matcher, JVar actual, JVar matches, JVar mismatchDescription, JClass hamcrestMatcherClass) {
            JBlock matchFailedBlock = matcherSafelyBody._if(matcher.invoke("matches").arg(actual.invoke(matcherField.getGetterName())).not())._then();
            matchFailedBlock.staticInvoke(hamcrestMatcherClass, "reportMismatch").arg(matcherField.getOrigName()).arg(matcher).arg(actual.invoke(matcherField.getGetterName())).arg(mismatchDescription).arg(matches);
            matchFailedBlock.assign(matches, FALSE);
            return matcherSafelyBody;
        }

        // todo refactor this mess
        public JBlock buildMatcherInitialization(JBlock constructorBody, JVar matcher, JVar expected) {
            JClass matchers = codeModel.ref(Matchers.class);
            JInvocation invokeMatcherIs = matchers.staticInvoke("is").arg(expected.invoke(matcherField.getGetterName()));

            JExpression assignmentExpression;

            if (matcherField.getType() == String.class) {
                JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null()).cor(expected.invoke(matcherField.getGetterName()).invoke("isEmpty"));
                JInvocation invokeMatcherIsEmptyOrNullString = matchers.staticInvoke("isEmptyOrNullString");
                assignmentExpression = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);
            } else if (matcherField.getTypeClass().isPrimitive() || ClassUtil.isPrimitiveWrapper(matcherField.getTypeClass()) || matcherField.getTypeClass().isEnum()) {
                //todo make this the default assignmentExpression
                assignmentExpression = invokeMatcherIs;
            } else if(Collection.class.isAssignableFrom(matcherField.getTypeClass())) {
                assignmentExpression = null;
//                return buildCollectionMatcher(matcherPreFix, packagePostFix);
            } else {
                // todo make this explicit if
                // Assume a matcher is generated for the type
                JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null());
                JInvocation invokeMatcherNullValue = matchers.staticInvoke("nullValue");
                assignmentExpression = JOp.cond(condition, invokeMatcherNullValue, invokeMatcherIs);
            }
            return constructorBody.assign(matcher, assignmentExpression);
        }

        /*private CodeBlock buildCollectionMatcher(String matcherPreFix, String packagePostFix) {
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
        }*/

    }

}
