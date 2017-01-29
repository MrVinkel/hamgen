package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.Matcher;
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

        public JFieldVar buildFieldSpec(JDefinedClass matcherClass) {
            JClass matcherClazz = codeModel.ref(Matcher.class);
            return matcherClass.field(Modifier.PROTECTED, matcherClazz, matcherField.getName());
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
        public JBlock buildMatcherInitialization(JBlock constructorBody, JVar matcher, JVar expected, String matcherPreFix, String packagePostFix) {
            JClass matchersClazz = codeModel.ref(Matchers.class);
            JInvocation invokeMatcherIs = matchersClazz.staticInvoke("is").arg(expected.invoke(matcherField.getGetterName()));

            JExpression assignmentExpression;

            if (matcherField.getType() == String.class) {
                JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null()).cor(expected.invoke(matcherField.getGetterName()).invoke("isEmpty"));
                JInvocation invokeMatcherIsEmptyOrNullString = matchersClazz.staticInvoke("isEmptyOrNullString");
                assignmentExpression = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);
            } else if (matcherField.getTypeClass().isPrimitive() || ClassUtil.isPrimitiveWrapper(matcherField.getTypeClass()) || matcherField.getTypeClass().isEnum()) {
                assignmentExpression = invokeMatcherIs;
            } else if (Collection.class.isAssignableFrom(matcherField.getTypeClass())) {
                return buildCollectionMatcher(constructorBody, matcher, expected, matcherPreFix, packagePostFix);
            } else {
                // Assume a matcher is generated for the type
                JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null());
                JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke("nullValue");

                String generatedMatcherName = matcherField.getTypeClass().getPackage().getName() + packagePostFix + "." + matcherField.getTypeClass().getSimpleName() + matcherField.getFieldPostFix();
                String generatedMatcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(matcherField.getTypeClass().getSimpleName());
                JClass generatedMatcherClass = codeModel.ref(generatedMatcherName);
                JInvocation invokeGeneratedMatcher = generatedMatcherClass.staticInvoke(generatedMatcherFactoryName).arg(expected.invoke(matcherField.getGetterName()));

                assignmentExpression = JOp.cond(condition, invokeMatcherNullValue, invokeGeneratedMatcher);
            }
            return constructorBody.assign(matcher, assignmentExpression);
        }

        private JBlock buildCollectionMatcher(JBlock constructorBody, JVar matcher, JVar expected, String matcherPreFix, String packagePostFix) {
            JClass matchersClazz = codeModel.ref(Matchers.class);
            JClass matcherClazz = codeModel.ref(Matcher.class);

            ParameterizedType parameterizedType = (ParameterizedType) matcherField.getType();
            Type collectionType = parameterizedType.getActualTypeArguments()[0];
            Class<?> collectionClass = (Class<?>) collectionType;

            JClass rawListClazz = codeModel.ref(List.class);
            JClass genericClazz = codeModel.ref(collectionClass);
            JClass itemsListClazz = rawListClazz.narrow(genericClazz);
            JVar itemsList = constructorBody.decl(itemsListClazz, "items", expected.invoke(matcherField.getGetterName()));

            JConditional isListNull = constructorBody._if(itemsList.eq(JExpr._null()));
            isListNull._then().assign(matcher, matchersClazz.staticInvoke("nullValue"));
            JBlock listNotEmptyBlock = isListNull._else();

            JClass matcherListClazz = rawListClazz.narrow(matcherClazz);
            JClass arrayListClass = codeModel.ref(ArrayList.class).narrow(matcherClazz);
            JVar matchersList = listNotEmptyBlock.decl(matcherListClazz, "matchers", JExpr._new(arrayListClass));

            JForEach itemLoop = listNotEmptyBlock.forEach(genericClazz, "item", itemsList);
            JVar item = itemLoop.var();
            JBlock itemLoopBlock = itemLoop.body();
            JInvocation invokeMatcherIs = matchersClazz.staticInvoke("is").arg(item);

            JExpression matcherInitialization;
            if (collectionType == String.class) {
//                item == null ||item.isEmpty() ? isEmptyOrNullString() : is(item)", Matcher.class);
                JExpression condition = item.eq(JExpr._null()).cor(item.invoke("isEmpty"));
                JInvocation invokeMatcherIsEmptyOrNullString = matchersClazz.staticInvoke("isEmptyOrNullString");
                matcherInitialization = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);
            } else if (collectionClass.isPrimitive() || ClassUtil.isPrimitiveWrapper(collectionClass) || collectionClass.isEnum()) {
                //builder.addStatement("$T matcher = is(item)", Matcher.class);
                matcherInitialization = invokeMatcherIs;
            } else {
                //builder.addStatement("$T matcher = item == null ? nullValue() : is$N(item)", Matcher.class, collectionClass.getSimpleName());
                JExpression condition = item.eq(JExpr._null());
                JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke("nullValue");

                String generatedMatcherName = collectionClass.getPackage().getName() + packagePostFix + "." + collectionClass.getSimpleName() + matcherField.getFieldPostFix();
                String generatedMatcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(collectionClass.getSimpleName());
                JClass generatedMatcherClass = codeModel.ref(generatedMatcherName);
                JInvocation invokeGeneratedMatcher = generatedMatcherClass.staticInvoke(generatedMatcherFactoryName).arg(item);

                matcherInitialization = JOp.cond(condition, invokeMatcherNullValue, invokeGeneratedMatcher);
            }
            JVar itemMatcher = itemLoopBlock.decl(matcherClazz, "itemMatcher", matcherInitialization);
            itemLoopBlock.add(matchersList.invoke("add").arg(itemMatcher));

            listNotEmptyBlock.assign(matcher, matcherClazz.staticInvoke("contains").arg(matchersList.invoke("toArray").arg(JExpr.newArray(matcherClazz, matchersList.invoke("size")))));

            return constructorBody;
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
