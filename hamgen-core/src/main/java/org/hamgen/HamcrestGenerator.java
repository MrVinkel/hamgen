package org.hamgen;

import com.sun.codemodel.JCodeModel;
import org.hamgen.builder.MatcherBuilder;
import org.hamgen.log.Logger;
import org.hamgen.util.ClassUtil;

import java.io.File;
import java.util.*;

import static org.hamgen.HamProperties.Key.*;

public class HamcrestGenerator {
    private static final Logger LOGGER = Logger.getLogger();

    private HamProperties properties;

    public HamcrestGenerator(HamProperties properties) {
        this.properties = properties;
    }

    public void generateMatchers(Collection<Class<?>> classes) throws Exception {
        LOGGER.info("Generating matchers..");
        File outputDir = createOutputDir();

        checkClasses(classes);

        for (Class<?> clazz : classes) {
            if (clazz.isEnum() || clazz.isPrimitive() || ClassUtil.isPrimitiveWrapper(clazz)) {
                LOGGER.info("Skipping enum/primitive/primitiveWrapper class <" + clazz.getName() + ">");
                continue;
            }

            LOGGER.info("Building matcher for " + clazz.getName());
            MatcherBuilder matcherClassBuilder = MatcherBuilder.matcherBuild(clazz.getPackage().getName(), clazz.getSimpleName())
                    .withMatcherPrefix(properties.getProperty(MATCHER_PRE_FIX))
                    .withMatcherNamePostfix(properties.getProperty(MATCHER_POST_FIX))
                    .withPackagePostFix(properties.getProperty(PACKAGE_POST_FIX))
                    .matchFields(clazz.getMethods());
            // todo check if all needed matchers are generated for nesting
            writeFile(matcherClassBuilder, outputDir);
        }

        LOGGER.info("Done!");
    }

    private void checkClasses(Collection<Class<?>> classes) {
        boolean failOnNoClasses = Boolean.parseBoolean(properties.getProperty(FAIL_ON_NO_CLASSES_FOUND));
        if (classes.size() == 0) {
            if (failOnNoClasses) {
                throw new IllegalStateException("No classes found!");
            }
            LOGGER.warn("No classes found!");
        } else {
            LOGGER.debug("Found " + classes.size() + " classes");
        }
    }

    private File createOutputDir() {
        File outputDir = new File(properties.getProperty(OUTPUT_DIR));
        LOGGER.debug("Creating output dir: " + outputDir.getAbsolutePath());
        outputDir.mkdirs();
        return outputDir;
    }

    private void writeFile(MatcherBuilder matcherClassBuilder, File outputDir) throws Exception {
        JCodeModel codeModel = matcherClassBuilder.build();
        codeModel.build(outputDir);
    }

}
