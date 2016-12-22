package dk.martinvinkel.hamgen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.hamcrest.Factory;

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
        if(!packagePostFix.substring(0, 1).equals(".")) {
            packagePostFix = "." + packagePostFix;
        }
        this.packagePostFix = packagePostFix;
        return this;
    }

    public TypeSpec build() {
        String matcherName = originalClassName + matcherNamePostFix;
        String matcherPackage = originalPackageName + packagePostFix;
        TypeName originalClass = ClassName.get(originalPackageName, originalClassName);
        TypeName matcherClass = ClassName.get(matcherPackage, matcherName);

        MethodSpec factoryMethod = MethodSpec.methodBuilder(matcherPreFix + originalClassName)
                .addAnnotation(Factory.class)
                .returns(matcherClass)
                .addModifiers(PUBLIC, STATIC)
                .addParameter(originalClass, "expected")
                .addStatement("return new $T(expected)", matcherClass)
                .build();

        return TypeSpec.classBuilder(matcherName)
                .addModifiers(PUBLIC)
                .addMethod(factoryMethod)
                .build();

    }
}
