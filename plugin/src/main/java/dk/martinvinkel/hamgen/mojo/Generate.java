package dk.martinvinkel.hamgen.mojo;

import dk.martinvinkel.hamgen.HamProperties;
import dk.martinvinkel.hamgen.HamcrestGenerator;
import dk.martinvinkel.hamgen.log.MavenLogger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static dk.martinvinkel.hamgen.HamProperties.Key.FAIL_ON_NO_CLASSES_FOUND;
import static dk.martinvinkel.hamgen.HamProperties.Key.OUTPUT_DIR;
import static dk.martinvinkel.hamgen.HamProperties.Key.PACKAGE_NAME;
import static dk.martinvinkel.hamgen.log.Logger.getLogger;

/**
 * Generates Hamcrest Matchers from generated JAXB classes
 */
@Mojo(name = "generate", aggregator = true, defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class Generate extends AbstractMojo {

    /**
     * Package name to scan through
     */
    @Parameter(required = true)
    private String packageName;

    /**
     * Output directory - default is ${project.build.directory}/generated-test-sources/hamgen
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/hamgen")
    private String outputDirectory;

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

        if(skip) {
            getLogger().info("Skipping");
        }

        try {
            HamProperties properties = new HamProperties();
            properties.setProperty(PACKAGE_NAME, packageName);
            properties.setProperty(OUTPUT_DIR, outputDirectory);
            properties.setProperty(FAIL_ON_NO_CLASSES_FOUND, Boolean.toString(failOnNoClassesFound));
            HamcrestGenerator generator = new HamcrestGenerator(properties);
            generator.generateMatchers();
        } catch (Exception e) {
            throw new MojoExecutionException("Buhuuu it failed :(", e);
        }
    }
}
