package dk.martinvinkel.hamgen;

import dk.martinvinkel.hamgen.log.BufferLogger;
import dk.martinvinkel.hamgen.log.Logger.LogLevel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;

import static dk.martinvinkel.hamgen.HamProperties.Key.FAIL_ON_NO_CLASSES_FOUND;
import static dk.martinvinkel.hamgen.HamProperties.Key.PACKAGE_NAME;
import static dk.martinvinkel.hamgen.log.Logger.LogLevel.INFO;
import static dk.martinvinkel.hamgen.log.Logger.LogLevel.WARN;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HamcrestGeneratorTest {

    @BeforeClass
    public static void setupClass() {
        new BufferLogger(INFO);
    }

    @Test
    public void t0101_exceptionOnNoClassesFound() {
        //Arrange
        HamProperties properties = new HamProperties();
        properties.setProperty(FAIL_ON_NO_CLASSES_FOUND, "true");
        HamcrestGenerator hamcrestGenerator = new HamcrestGenerator(properties);

        //Act
        try {
            hamcrestGenerator.generateMatchers(Collections.<Class<?>>emptyList());
            fail();
        } catch (Exception e) {
            //Assert
            assertTrue(e instanceof IllegalStateException);
            assertEquals("No classes found!", e.getMessage());
        }
    }

    @Test
    public void t0102_logOnNoClassesFoundGetsLogged() throws Exception{
        //Arrange
        BufferLogger.clear();

        HamProperties properties = new HamProperties();
        properties.setProperty(FAIL_ON_NO_CLASSES_FOUND, "false");
        HamcrestGenerator hamcrestGenerator = new HamcrestGenerator(properties);

        SimpleEntry<LogLevel, String> expected = new SimpleEntry<>(WARN, "No classes found!");

        //Act
        hamcrestGenerator.generateMatchers(Collections.<Class<?>>emptyList());

        //Assert
        assertThat(BufferLogger.getLog(), hasItem(is(expected)));
    }

}
