package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.*;
import org.hamgen.HamGenDiagnosingMatcher;
import org.hamgen.log.Logger;
import org.hamgen.HamProperties;
import org.hamgen.model.MatcherField;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static com.sun.codemodel.JExpr.TRUE;
import static java.lang.reflect.Modifier.PUBLIC;

public class MatcherBuilder {
    private static final Logger LOGGER = Logger.getLogger();

    private static final String PARAM_NAME_EXPECTED = "expected";
    private static final String PARAM_NAME_DESCRIPTION = "desc";
    private static final String PARAM_NAME_ACTUAL_ITEM = "item";
    private static final String PARAM_NAME_MISMATCH_DESCRIPTION = "mismatchDesc";
    private static final String METHOD_NAME_DESCRIPTION_TO = "describeTo";
    private static final String METHOD_NAME_MATCHES_SAFELY = "matchesSafely";

    private Class<?> originalClazz;
    private String originalClassName;
    private String matcherNamePostFix = HamProperties.Key.MATCHER_POST_FIX.getDefaultValue();
    private String originalPackageName;
    private String packagePostFix = HamProperties.Key.PACKAGE_POST_FIX.getDefaultValue();
    private String matcherPreFix = HamProperties.Key.MATCHER_PRE_FIX.getDefaultValue();
    private List<MatcherField> matcherFields = new ArrayList<MatcherField>();
    private JCodeModel codeModel;

    public MatcherBuilder() {
        this.codeModel = new JCodeModel();
    }

    public MatcherBuilder withClass(Class<?> originalClazz) {
        this.originalClazz = originalClazz;
        this.originalPackageName = originalClazz.getPackage().getName().trim();
        this.originalClassName = originalClazz.getSimpleName().trim();
        return this;
    }

    public MatcherBuilder withMatcherPrefix(String matcherPreFix) {
        this.matcherPreFix = matcherPreFix.trim();
        return this;
    }

    public MatcherBuilder withMatcherNamePostfix(String matcherNamePostFix) {
        this.matcherNamePostFix = matcherNamePostFix.trim();
        return this;
    }

    public MatcherBuilder withPackagePostFix(String packagePostFix) {
        packagePostFix = packagePostFix.trim();
        if (!packagePostFix.substring(0, 1).equals(".")) {
            packagePostFix = "." + packagePostFix;
        }
        this.packagePostFix = packagePostFix;
        return this;
    }

    public MatcherBuilder matchField(Method getterMethod) {
        if (!isGetterMethod(getterMethod)) {
            LOGGER.debug("Not a getter method: " + getterMethod.getName());
            return this;
        }

        Type type = getterMethod.getGenericReturnType();
        String name = getterMethod.getName();
        MatcherField matcherField = MatcherField.builder(type, name).withPostFix(matcherNamePostFix).build();
        matcherFields.add(matcherField);
        return this;
    }

    public MatcherBuilder matchFields(Method... getterMethods) {
        for (Method method : getterMethods) {
            matchField(method);
        }
        return this;
    }

    public JCodeModel build() throws Exception {
        JClass hamcrestMatcherClass = codeModel.ref(HamGenDiagnosingMatcher.class);

        // Create class
        String matcherName = originalClassName + matcherNamePostFix;
        String matcherPackage = originalPackageName + packagePostFix;
        JClass originalClass = codeModel.ref(originalClazz);
        JPackage packageName = codeModel._package(matcherPackage);
        JDefinedClass matcherClass = packageName._class(matcherName);
        matcherClass._extends(hamcrestMatcherClass);

        // Create constructor
        JMethod matcherClassConstructor = matcherClass.constructor(PUBLIC);
        JVar constructorParameterExpectedItem = matcherClassConstructor.param(originalClass, PARAM_NAME_EXPECTED);
        JBlock constructorBody = matcherClassConstructor.body();

        // create description method
        JMethod describeTo = matcherClass.method(PUBLIC, void.class, METHOD_NAME_DESCRIPTION_TO);
        JVar descriptionParam = describeTo.param(Description.class, PARAM_NAME_DESCRIPTION);
        JBlock describeToBody = describeTo.body();
        describeToBody.invoke(descriptionParam, "appendText").arg("{");

        // create match method
        JMethod matchesSafely = matcherClass.method(PUBLIC, boolean.class, METHOD_NAME_MATCHES_SAFELY);
        JBlock matchtSafelyBody = matchesSafely.body();
        JVar itemParam = matchesSafely.param(Object.class, PARAM_NAME_ACTUAL_ITEM);
        JVar mismatchDescriptionParam = matchesSafely.param(Description.class, PARAM_NAME_MISMATCH_DESCRIPTION);
        JVar actual = matchtSafelyBody.decl(originalClass, "actual", JExpr.cast(originalClass, itemParam));
        JVar matches = matchtSafelyBody.decl(codeModel.BOOLEAN, "matches", TRUE);
        matchtSafelyBody.invoke(mismatchDescriptionParam, "appendText").arg("{");

        // create factory method
        JMethod jmFactory = matcherClass.method(PUBLIC | JMod.STATIC, matcherClass, matcherPreFix + originalClassName);
        JVar expectedItemParam = jmFactory.param(originalClass, PARAM_NAME_EXPECTED);
        jmFactory.annotate(Factory.class);
        JBlock body = jmFactory.body();
        body._return(JExpr._new(matcherClass).arg(expectedItemParam));

        boolean firstField = true;
        for (MatcherField matcherField : matcherFields) {
            MatcherFieldBuilder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

            JFieldVar matcher = matcherFieldBuilder.buildFieldSpec(matcherClass);
            matcherFieldBuilder.buildMatcherInitialization(constructorBody, matcher, constructorParameterExpectedItem, matcherPreFix, packagePostFix);
            matcherFieldBuilder.buildMatchesSafely(matchtSafelyBody, matcher, actual, matches, mismatchDescriptionParam, hamcrestMatcherClass);
            matcherFieldBuilder.buildDescribeTo(describeToBody, descriptionParam, matcher, firstField);

            firstField = false;
        }

        // finalize description
        describeToBody.invoke(descriptionParam, "appendText").arg("}");

        // finalize match method
        matchtSafelyBody.invoke(mismatchDescriptionParam, "appendText").arg("}");
        matchtSafelyBody._return(matches);

        return codeModel;
    }

    private boolean isGetterMethod(Method method) {
        return method.getName().toLowerCase().startsWith("get") && !method.getName().equals("getClass");
    }
}
