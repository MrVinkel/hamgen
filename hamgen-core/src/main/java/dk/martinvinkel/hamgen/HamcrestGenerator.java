package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import dk.martinvinkel.hamgen.log.Logger;
import org.hamcrest.Factory;
import org.reflections.Reflections;

import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static dk.martinvinkel.hamgen.HamProperties.Key.*;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class HamcrestGenerator {
    private static final Logger LOGGER = Logger.getLogger();

    private HamProperties properties;

    public HamcrestGenerator(HamProperties properties) {
        this.properties = properties;
    }

    public void generateMatchers() throws IOException {
        LOGGER.info("Generate Matchers start");

        File outputDir = new File(properties.getProperty(OUTPUT_DIR, "target/generated-sources"));
        outputDir.mkdirs();

        Reflections reflections = new Reflections(properties.getProperty(PACKAGE_NAME));
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(XmlType.class);

        for(Class<?> clazz : annotatedClasses) {
            TypeSpec matcherClass = buildMatcherClass(clazz);
            writeFile(clazz.getPackage().getName(), matcherClass, outputDir);
        }

        LOGGER.info("Generate Matchers stop");
    }

    private TypeSpec buildMatcherClass(Class<?> clazz) {
        MethodSpec factoryMethod = buildFactoryMethod(clazz);

        return TypeSpec.classBuilder(clazz.getSimpleName() + "Matcher")
                .addModifiers(PUBLIC)
                .addMethod(factoryMethod)
                .build();
    }

    private MethodSpec buildFactoryMethod(Class<?> clazz) {
        TypeName matcherClass = ClassName.get(clazz.getPackage().getName(), clazz.getSimpleName());

        return MethodSpec.methodBuilder(properties.getProperty(MATCHER_PRE_FIX, "Is") + clazz.getSimpleName())
                    .addAnnotation(Factory.class)
                    .returns(matcherClass)
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(clazz, "expected")
                    .addStatement("return new $T(expected)", matcherClass)
                    .build();
    }

    private void writeFile(String packageName, TypeSpec matcherClass, File outputDir) throws IOException {
        JavaFile file = JavaFile.builder(packageName + properties.getProperty(PACKAGE_POST_FIX, ".matcher"), matcherClass).build();
        file.writeTo(outputDir);
    }

}
