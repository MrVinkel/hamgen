package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import org.hamcrest.Description;
import org.hamcrest.Factory;

import java.lang.reflect.Method;
import java.util.*;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_PRE_FIX;
import static dk.martinvinkel.hamgen.HamProperties.Key.PACKAGE_POST_FIX;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class MatcherBuilder {

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
        Class<?> type = getterMethod.getReturnType();
        String name = getterMethod.getName();
        MatcherField matcherField = MatcherField.builder(type, name).withPostFix(matcherNamePostFix).build();
        matcherFields.add(matcherField);
        return this;
    }

    public MatcherBuilder matchFields(Method ...getterMethods) {
        for(Method method : getterMethods) {
            if(isGetterMethod(method)) {
                matchField(method);
            }
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

        MethodSpec factoryMethod = MethodSpec.methodBuilder(matcherPreFix + originalClassName)
                .addAnnotation(Factory.class)
                .returns(matcherClass)
                .addModifiers(PUBLIC, STATIC)
                .addParameter(originalClass, "expected")
                .addStatement("return new $T(expected)", matcherClass)
                .build();

        ParameterSpec descriptionParameter = ParameterSpec.builder(description, "desc").build();
        MethodSpec.Builder descriptionToBuilder = MethodSpec.methodBuilder("descriptionTo")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(descriptionParameter)
                .addStatement("$N.appendText($S)", descriptionParameter ,"{");

        ParameterSpec actualParameter = ParameterSpec.builder(originalClass, "actual").build();
        ParameterSpec mismatchDescriptionParameter = ParameterSpec.builder(description, "mismatchDesc").build();
        ParameterSpec matchesLocalField = ParameterSpec.builder(TypeName.BOOLEAN, "matches").build();
        MethodSpec.Builder matchesSafelyBuilder = MethodSpec.methodBuilder("matchesSafely")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(actualParameter)
                .addParameter(mismatchDescriptionParameter)
                .addStatement("$T $N = false", matchesLocalField.type, matchesLocalField)
                .addStatement("$N.appendText($S)", mismatchDescriptionParameter, "{");

        boolean firstField = true;
        for (MatcherField matcherField : matcherFields) {
            MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

            FieldSpec field = matcherFieldBuilder.buildFieldSpec();
            matcherClassBuilder.addField(field);

            CodeBlock descriptionBlock = matcherFieldBuilder.buildDescriptionTo(firstField, descriptionParameter.name);
            descriptionToBuilder.addCode(descriptionBlock);

            CodeBlock matcherSafelyBlock = matcherFieldBuilder.buildMatchesSafely(actualParameter, mismatchDescriptionParameter, matchesLocalField);
            matchesSafelyBuilder.addCode(matcherSafelyBlock);

            firstField = false;
        }

        MethodSpec descriptionToMethod = descriptionToBuilder
                .addStatement("$N.appendText($S)", descriptionParameter ,"}")
                .build();

        MethodSpec matchesSafelyMethod = matchesSafelyBuilder
                .addStatement("$N.appendText($S)", mismatchDescriptionParameter, "}")
                .addStatement("return $N", matchesLocalField)
                .build();

        matcherClassBuilder.addMethod(matchesSafelyMethod);
        matcherClassBuilder.addMethod(descriptionToMethod);
        matcherClassBuilder.addMethod(factoryMethod);
        return matcherClassBuilder.build();
    }

    private boolean isGetterMethod(Method method) {
        return method.getName().toLowerCase().startsWith("get");
    }
}
