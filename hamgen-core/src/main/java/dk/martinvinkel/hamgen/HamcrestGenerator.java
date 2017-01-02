package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import dk.martinvinkel.hamgen.builder.MatcherBuilder;
import dk.martinvinkel.hamgen.log.Logger;
import org.reflections.Reflections;

import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static dk.martinvinkel.hamgen.HamProperties.Key.*;
import static dk.martinvinkel.hamgen.util.ClassUtil.isPrimitiveWrapper;

public class HamcrestGenerator {
    private static final Logger LOGGER = Logger.getLogger();

    private HamProperties properties;

    public HamcrestGenerator(HamProperties properties) {
        this.properties = properties;
    }

    public void generateMatchers() throws IOException {
        LOGGER.info("Generating matchers..");

        File outputDir = createOutputDir();

        Reflections reflections = new Reflections(properties.getProperty(PACKAGE_NAME));
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(XmlType.class);

        checkClasses(annotatedClasses);

        for (Class<?> clazz : annotatedClasses) {
            if(clazz.isEnum() || clazz.isPrimitive() || isPrimitiveWrapper(clazz)) {
                LOGGER.debug("Skipping enum/primitive/primitiveWrapper class");
                continue;
            }

            LOGGER.info("Building matcher for " + clazz.getName());
            MatcherBuilder matcherClassBuilder = MatcherBuilder.matcherBuild(clazz.getPackage().getName(), clazz.getSimpleName())
                    .withMatcherPrefix(properties.getProperty(MATCHER_PRE_FIX))
                    .withMatcherNamePostfix(properties.getProperty(MATCHER_POST_FIX))
                    .withPackagePostFix(properties.getProperty(PACKAGE_POST_FIX))
                    .matchFields(clazz.getMethods());
            writeFile(clazz.getPackage().getName(), matcherClassBuilder, outputDir);
        }

        LOGGER.info("Done!");
    }

    private void checkClasses(Set<Class<?>> annotatedClasses) {
        boolean failOnNoClasses = Boolean.parseBoolean(properties.getProperty(FAIL_ON_NO_CLASSES_FOUND));
        if(annotatedClasses.size() == 0) {
            if(failOnNoClasses) {
                throw new IllegalStateException("No classes found!");
            }
            LOGGER.warn("No classes found!");
        } else {
            LOGGER.debug("Found " + annotatedClasses.size() + " classes");
        }
    }

    private File createOutputDir() {
        File outputDir = new File(properties.getProperty(OUTPUT_DIR));
        LOGGER.debug("Creating output dir: " + outputDir.getAbsolutePath());
        outputDir.mkdirs();
        return outputDir;
    }

    private void writeFile(String packageName, MatcherBuilder matcherClassBuilder, File outputDir) throws IOException {
        TypeSpec matcherClass = matcherClassBuilder.build();
        JavaFile.Builder fileBuilder = JavaFile.builder(packageName + properties.getProperty(PACKAGE_POST_FIX), matcherClass).indent("    ");
        for(Map.Entry<ClassName, String> staticImport : matcherClassBuilder.buildStaticImports().entrySet()) {
            fileBuilder.addStaticImport(staticImport.getKey(), staticImport.getValue());
        }
        LOGGER.debug("Writing file " + matcherClass.name + " to " + outputDir.getAbsolutePath());
        JavaFile file = fileBuilder.build();
        file.writeTo(outputDir);
    }

}
