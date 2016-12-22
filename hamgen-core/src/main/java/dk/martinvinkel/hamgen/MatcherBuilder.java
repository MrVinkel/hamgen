package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_PRE_FIX;
import static dk.martinvinkel.hamgen.HamProperties.Key.PACKAGE_POST_FIX;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class MatcherBuilder {

    private String originalClassName;
    private String matcherNamePostFix = MATCHER_POST_FIX.getDefaultValue();
    private String originalPackageName;
    private String packagePostFix = PACKAGE_POST_FIX.getDefaultValue();
    private String matcherPreFix = MATCHER_PRE_FIX.getDefaultValue();
    private List<Entry<TypeName, String>> matcherFields = new ArrayList<>();

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

    public MatcherBuilder matchField(Class<?> type, String name) {
        TypeName typeName = ClassName.get(type);
        matcherFields.add(new SimpleEntry<>(typeName, name.trim() + matcherNamePostFix));
        return this;
    }

    public MatcherBuilder matchFields(List<Entry<Class<?>, String>> fields) {
        for(Entry field : fields) {
            matchField((Class<?>)field.getKey(), (String)field.getValue());
        }
        return this;
    }

    public TypeSpec build() {
        String matcherName = originalClassName + matcherNamePostFix;
        String matcherPackage = originalPackageName + packagePostFix;
        TypeName originalClass = ClassName.get(originalPackageName, originalClassName);
        TypeName matcherClass = ClassName.get(matcherPackage, matcherName);
        TypeName matcher = ClassName.get(Matcher.class);
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

        boolean firstField = true;
        for (Entry matcherField : matcherFields) {
            String matcherFieldName = (String) matcherField.getValue();

            // matcher fields
            FieldSpec field = FieldSpec.builder(matcher, matcherFieldName)
                    .addModifiers(PROTECTED)
                    .build();
            matcherClassBuilder.addField(field);

            // description to
            if(!firstField) {
                descriptionToBuilder.addStatement("$N.appendText(\", \")", descriptionParameter);
            }
            descriptionToBuilder
                    .addStatement("$N.appendText($S)", descriptionParameter, matcherFieldName + " ")
                    .addStatement("$N.appendDescriptionOf($N)", descriptionParameter, matcherFieldName);

            firstField = false;
        }


        /**
         * if (!color.matches(lightsaber.getColor())) {
         reportMismatch("color", color, lightsaber.getColor(), mismatchDescription, matches);
         matches = false;
         }
         */

        MethodSpec descriptionToMethod = descriptionToBuilder
                .addStatement("$N.appendText($S)", descriptionParameter ,"}")
                .build();

        matcherClassBuilder.addMethod(factoryMethod);
        matcherClassBuilder.addMethod(descriptionToMethod);
        return matcherClassBuilder.build();
    }
}
