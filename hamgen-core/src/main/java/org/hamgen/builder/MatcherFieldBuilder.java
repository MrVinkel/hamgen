package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.Matcher;
import org.hamgen.HamProperties;
import org.hamgen.builder.initialization.MatcherInitializationBuilder;
import org.hamgen.log.Logger;
import org.hamgen.model.MatcherField;
import org.hamgen.util.StringUtil;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import static com.sun.codemodel.JExpr.FALSE;

public class MatcherFieldBuilder {
    private static final Logger LOGGER = Logger.getLogger();

    private static final String METHOD_NAME_APPEND_TEXT = "appendText";
    private static final String METHOD_NAME_APPEND_DESCRIPTION_OF = "appendDescriptionOf";
    private static final String METHOD_NAME_MATCHES = "matches";
    private static final String METHOD_NAME_REPORT_MISMATCH = "reportMismatch";

    private MatcherField matcherField = new MatcherField(null, null, null, null);
    private JCodeModel codeModel;

    public MatcherFieldBuilder(Type type, String getterName) {
        withType(type);
        withGetterName(getterName);
        withPostFix(HamProperties.Key.MATCHER_POST_FIX.getDefaultValue());
    }

    public MatcherFieldBuilder(MatcherField matcherField) {
        withType(matcherField.getType());
        withGetterName(matcherField.getGetterName());
        withPostFix(matcherField.getFieldPostFix());
    }

    public MatcherFieldBuilder withCodeModel(JCodeModel codeModel) {
        this.codeModel = codeModel;
        return this;
    }

    private MatcherFieldBuilder withName(String name) {
        this.matcherField.setOrigName(name);
        return this;
    }

    public MatcherFieldBuilder withPostFix(String postFix) {
        matcherField.setFieldPostFix(postFix);
        return this;
    }

    public MatcherFieldBuilder withType(Type type) {
        matcherField.setType(type);
        return this;
    }

    public MatcherFieldBuilder withGetterName(String getterName) {
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
        return new MatcherField(matcherField.getGetterName(), matcherField.getOrigName(), matcherField.getFieldPostFix(), matcherField.getType());
    }

    public JFieldVar buildFieldSpec(JDefinedClass matcherClass) {
        JClass matcherClazz = codeModel.ref(Matcher.class);
        return matcherClass.field(Modifier.PROTECTED, matcherClazz, matcherField.getName());
    }

    public JBlock buildDescribeTo(JBlock describeToBody, JVar descriptionParam, JVar matcher, boolean first) {
        if (!first) {
            describeToBody.invoke(descriptionParam, METHOD_NAME_APPEND_TEXT).arg(", ");
        }
        describeToBody.invoke(descriptionParam, METHOD_NAME_APPEND_TEXT).arg(matcherField.getOrigName() + " ");
        describeToBody.invoke(descriptionParam, METHOD_NAME_APPEND_DESCRIPTION_OF).arg(matcher);
        return describeToBody;
    }

    public JBlock buildMatchesSafely(JBlock matcherSafelyBody, JVar matcher, JVar actual, JVar matches, JVar mismatchDescription, JClass hamcrestMatcherClass) {
        JBlock matchFailedBlock = matcherSafelyBody._if(matcher.invoke(METHOD_NAME_MATCHES).arg(actual.invoke(matcherField.getGetterName())).not())._then();
        matchFailedBlock.staticInvoke(hamcrestMatcherClass, METHOD_NAME_REPORT_MISMATCH).arg(matcherField.getOrigName()).arg(matcher).arg(actual.invoke(matcherField.getGetterName())).arg(mismatchDescription).arg(matches);
        matchFailedBlock.assign(matches, FALSE);
        return matcherSafelyBody;
    }

    //todo this could be moved to MatcherBuilder
    public JBlock buildMatcherInitialization(JBlock constructorBody, JVar matcher, JVar expected, String matcherPreFix, String packagePostFix) {
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(matcherField.getType());
        return builder.withCodeModel(codeModel)
                .withConstructorBody(constructorBody)
                .withExpected(expected)
                .withMatcher(matcher)
                .withMatcherField(matcherField)
                .withMatcherPreFix(matcherPreFix)
                .withPackagePostFix(packagePostFix)
                .build();
    }

}
