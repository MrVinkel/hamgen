package dk.martinvinkel.hamgen;

import com.squareup.javapoet.*;
import dk.martinvinkel.hamgen.log.Logger;
import dk.martinvinkel.hamgen.util.StringUtil;
import org.reflections.Reflections;

import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static dk.martinvinkel.hamgen.HamProperties.Key.*;

public class HamcrestGenerator {
    private static final Logger LOGGER = Logger.getLogger();

    private HamProperties properties;

    public HamcrestGenerator(HamProperties properties) {
        this.properties = properties;
    }

    public void generateMatchers() throws IOException {
        LOGGER.info("Generate Matchers start");

        File outputDir = createOutputDir();

        Reflections reflections = new Reflections(properties.getProperty(PACKAGE_NAME));
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(XmlType.class);

        for (Class<?> clazz : annotatedClasses) {
            TypeSpec matcherClass = MatcherBuilder.matcherBuild(clazz.getPackage().getName(), clazz.getSimpleName())
                    .withMatcherPrefix(properties.getProperty(MATCHER_PRE_FIX))
                    .withMatcherNamePostfix(properties.getProperty(MATCHER_POST_FIX))
                    .withPackagePostFix(properties.getProperty(PACKAGE_POST_FIX))
                    .matchFields(clazz.getMethods())
                    .build();
            writeFile(clazz.getPackage().getName(), matcherClass, outputDir);
        }

        LOGGER.info("Generate Matchers stop");
    }

    private File createOutputDir() {
        File outputDir = new File(properties.getProperty(OUTPUT_DIR));
        outputDir.mkdirs();
        return outputDir;
    }

    private void writeFile(String packageName, TypeSpec matcherClass, File outputDir) throws IOException {
        JavaFile file = JavaFile.builder(packageName + properties.getProperty(PACKAGE_POST_FIX), matcherClass).build();
        file.writeTo(outputDir);
    }

}
