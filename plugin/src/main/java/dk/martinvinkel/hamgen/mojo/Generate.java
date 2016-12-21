package dk.martinvinkel.hamgen.mojo;

import dk.martinvinkel.hamgen.HamProperties;
import dk.martinvinkel.hamgen.HamcrestGenerator;
import dk.martinvinkel.hamgen.log.MavenLogger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static dk.martinvinkel.hamgen.log.Logger.getLogger;

/**
 * Generates Hamcrest Matchers from generated JAXB classes
 */
@Mojo(name = "generate", aggregator = true)
public class Generate extends AbstractMojo {

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
            HamcrestGenerator generator = new HamcrestGenerator(properties);
            generator.generateMatchers();
        } catch (Exception e) {
            throw new MojoExecutionException("Buhuuu it failed :(", e);
        }
    }
}
