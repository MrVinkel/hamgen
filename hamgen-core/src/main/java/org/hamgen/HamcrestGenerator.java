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

    public void generateMatchers(Collection<Class<?>> classes, List<Class<?>> excludeClasses) throws Exception {
        LOGGER.info("Generating matchers..");
        File outputDir = createOutputDir();

        checkClasses(classes);

        JCodeModel codeModel = new JCodeModel();
        for (Class<?> clazz : classes) {
            if (clazz.isEnum() || clazz.isPrimitive() || ClassUtil.isPrimitiveWrapper(clazz)) {
                LOGGER.info("Skipping enum/primitive/primitiveWrapper class <" + clazz.getName() + ">");
                continue;
            }

            LOGGER.debug("Building matcher for " + clazz.getName());
            MatcherBuilder matcherClassBuilder = new MatcherBuilder()
                    .withCodeModel(codeModel)
                    .withClass(clazz)
                    .withExcludeTypes(excludeClasses)
                    .withMatcherPrefix(properties.getProperty(MATCHER_PRE_FIX))
                    .withMatcherNamePostfix(properties.getProperty(MATCHER_POST_FIX))
                    .withPackagePostFix(properties.getProperty(PACKAGE_POST_FIX))
                    .matchFields(clazz.getMethods());

            codeModel = matcherClassBuilder.build();
        }

        LOGGER.info("Writing files..");
        codeModel.build(outputDir);

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
            LOGGER.info("Found " + classes.size() + " classes");
        }
    }

    private File createOutputDir() {
        File outputDir = new File(properties.getProperty(OUTPUT_DIR));
        LOGGER.debug("Creating output dir: " + outputDir.getAbsolutePath());
        boolean mkdirs = outputDir.mkdirs();
        LOGGER.debug("Mkdirs: " + mkdirs);
        return outputDir;
    }

}
