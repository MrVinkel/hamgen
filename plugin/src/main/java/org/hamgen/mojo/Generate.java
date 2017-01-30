package org.hamgen.mojo;

import org.hamgen.HamProperties;
import org.hamgen.HamcrestGenerator;
import org.hamgen.ReflectiveClassFinder;
import org.hamgen.log.MavenLogger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamgen.HamProperties.Key.*;
import static org.hamgen.log.Logger.getLogger;

/**
 * Generates Hamcrest Matchers from generated JAXB classes
 */
@Mojo(name = "generate", aggregator = true, defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class Generate extends AbstractMojo {

    /**
     * Package name to scan through
     */
    @Parameter(required = true)
    private List<String> packageNames;

    /**
     * Annotation to scan for - E.g: javax.xml.bind.annotation.XmlType
     */
    @Parameter
    private String annotation;

    /**
     * Output directory - default is ${project.build.directory}/generated-test-sources/hamgen
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/hamgen")
    private String outputDirectory;

    /**
     * Names of classes to generate matchers for - don't include those found by the annotation
     */
    @Parameter
    private List<String> classNames;

    /**
     * Fail if no classes are found - default is true
     */
    @Parameter(defaultValue = "true")
    private boolean failOnNoClassesFound;

    /**
     * Skip execution - default is false
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    public void execute() throws MojoExecutionException, MojoFailureException {
        new MavenLogger(getLog());

        if (skip) {
            getLogger().info("Skipping");
        }

        try {
            HamProperties properties = new HamProperties();

            properties.setProperty(OUTPUT_DIR, outputDirectory);
            properties.setProperty(FAIL_ON_NO_CLASSES_FOUND, Boolean.toString(failOnNoClassesFound));

            Collection<Class<?>> classes = new ArrayList<Class<?>>();

            if(annotation != null && !annotation.isEmpty()) {
                properties.setProperty(ANNOTATION, annotation);
                ReflectiveClassFinder classFinder = new ReflectiveClassFinder();
                classes.addAll(classFinder.findClassesWithAnnotation(packageNames, properties.getProperty(ANNOTATION, ANNOTATION.getDefaultValue())));
            }

            if(classNames != null && classNames.size() > 0) {
                for(String name : classNames) {
                    Class<?> clazz = Class.forName(name);
                    classes.add(clazz);
                }
            }

            HamcrestGenerator generator = new HamcrestGenerator(properties);
            generator.generateMatchers(classes);
        } catch (Exception e) {
            throw new MojoExecutionException("Buhuuu it failed :(", e);
        }
    }
}
