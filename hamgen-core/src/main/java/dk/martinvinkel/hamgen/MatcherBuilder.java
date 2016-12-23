package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import org.hamcrest.Description;
import org.hamcrest.Factory;

import java.lang.reflect.Method;
import java.util.*;

import static com.squareup.javapoet.TypeName.OBJECT;
import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_PRE_FIX;
import static dk.martinvinkel.hamgen.HamProperties.Key.PACKAGE_POST_FIX;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class MatcherBuilder {
    private static final String PARAM_NAME_EXPECTED = "expected";
    private static final String PARAM_NAME_DESCRIPTION = "desc";
    private static final String PARAM_NAME_ACTUAL_ITEM = "item";
    private static final String PARAM_NAME_MISMATCH_DESCRIPTION = "mismatchDesc";

    private static final String METHOD_NAME_DESCRIPTION_TO = "describeTo";
    private static final String METHOD_NAME_MATCHES_SAFELY = "matchesSafely";

    private String originalClassName;
    private String matcherNamePostFix = MATCHER_POST_FIX.getDefaultValue();
    private String originalPackageName;
    private String packagePostFix = PACKAGE_POST_FIX.getDefaultValue();
    private String matcherPreFix = MATCHER_PRE_FIX.getDefaultValue();
    private List<MatcherField> matcherFields = new ArrayList<>();

    private MatcherBuilder(String originalPackageName, String originalClassName) {
        this.originalPackageName = originalPackageName.trim();
        this.originalClassName = originalClassName.trim();
    }

    public static MatcherBuilder matcherBuild(String packageName, String className) {
        return new MatcherBuilder(packageName, className);
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
            return this;
        }

        Class<?> type = getterMethod.getReturnType();
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

    public TypeSpec build() {
        String matcherName = originalClassName + matcherNamePostFix;
        String matcherPackage = originalPackageName + packagePostFix;
        TypeName originalClass = ClassName.get(originalPackageName, originalClassName);
        TypeName matcherClass = ClassName.get(matcherPackage, matcherName);
        TypeName description = ClassName.get(Description.class);
        TypeName hamGenDiagnosingMatcher = ClassName.get(HamGenDiagnosingMatcher.class);

        TypeSpec.Builder matcherClassBuilder = TypeSpec.classBuilder(matcherName)
                .superclass(hamGenDiagnosingMatcher)
                .addModifiers(PUBLIC);

        ParameterSpec expectedConstructorLocalParam = ParameterSpec.builder(originalClass, PARAM_NAME_EXPECTED).build();
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(expectedConstructorLocalParam);

        ParameterSpec expectedFactoryLocalParam = ParameterSpec.builder(originalClass, PARAM_NAME_EXPECTED).build();
        MethodSpec factoryMethod = MethodSpec.methodBuilder(matcherPreFix + originalClassName)
                .addAnnotation(Factory.class)
                .returns(matcherClass)
                .addModifiers(PUBLIC, STATIC)
                .addParameter(expectedFactoryLocalParam)
                .addStatement("return new $T($N)", matcherClass, expectedFactoryLocalParam)
                .build();

        ParameterSpec descriptionParameter = ParameterSpec.builder(description, PARAM_NAME_DESCRIPTION).build();
        MethodSpec.Builder descriptionToBuilder = MethodSpec.methodBuilder(METHOD_NAME_DESCRIPTION_TO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(descriptionParameter)
                .addStatement("$N.appendText($S)", descriptionParameter, "{");

        ParameterSpec itemParameter = ParameterSpec.builder(OBJECT, PARAM_NAME_ACTUAL_ITEM).build();
        ParameterSpec actualLocalParameter = ParameterSpec.builder(originalClass, "actual").build();
        ParameterSpec mismatchDescriptionParameter = ParameterSpec.builder(description, PARAM_NAME_MISMATCH_DESCRIPTION).build();
        ParameterSpec matchesLocalField = ParameterSpec.builder(TypeName.BOOLEAN, "matches").build();
        MethodSpec.Builder matchesSafelyBuilder = MethodSpec.methodBuilder(METHOD_NAME_MATCHES_SAFELY)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(itemParameter)
                .addParameter(mismatchDescriptionParameter)
                .addStatement("$T $N = true", matchesLocalField.type, matchesLocalField)
                .addStatement("$T $N = ($T) $N", actualLocalParameter.type, actualLocalParameter, actualLocalParameter.type, itemParameter)
                .addStatement("$N.appendText($S)", mismatchDescriptionParameter, "{");

        boolean firstField = true;
        for (MatcherField matcherField : matcherFields) {
            MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

            FieldSpec field = matcherFieldBuilder.buildFieldSpec();
            matcherClassBuilder.addField(field);

            CodeBlock constructorBlock = matcherFieldBuilder.buildMatcherInitialization(expectedConstructorLocalParam.name, matcherPreFix);
            constructorBuilder.addCode(constructorBlock);

            CodeBlock matcherSafelyBlock = matcherFieldBuilder.buildMatchesSafely(actualLocalParameter, mismatchDescriptionParameter, matchesLocalField);
            matchesSafelyBuilder.addCode(matcherSafelyBlock);

            CodeBlock descriptionBlock = matcherFieldBuilder.buildDescriptionTo(firstField, descriptionParameter.name);
            descriptionToBuilder.addCode(descriptionBlock);

            firstField = false;
        }

        MethodSpec descriptionToMethod = descriptionToBuilder
                .addStatement("$N.appendText($S)", descriptionParameter, "}")
                .build();

        MethodSpec matchesSafelyMethod = matchesSafelyBuilder
                .addStatement("$N.appendText($S)", mismatchDescriptionParameter, "}")
                .addStatement("return $N", matchesLocalField)
                .build();

        MethodSpec constructorMethod = constructorBuilder.build();

        matcherClassBuilder.addMethod(constructorMethod);
        matcherClassBuilder.addMethod(matchesSafelyMethod);
        matcherClassBuilder.addMethod(descriptionToMethod);
        matcherClassBuilder.addMethod(factoryMethod);
        return matcherClassBuilder.build();
    }

    private boolean isGetterMethod(Method method) {
        return method.getName().toLowerCase().startsWith("get") && !method.getName().equals("getClass");
    }
}
