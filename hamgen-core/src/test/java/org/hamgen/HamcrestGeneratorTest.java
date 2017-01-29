package org.hamgen;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsCollectionContaining;
import org.hamgen.log.BufferLogger;
import org.hamgen.log.Logger.LogLevel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamgen.log.Logger.LogLevel.INFO;
import static org.hamgen.log.Logger.LogLevel.WARN;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        properties.setProperty(HamProperties.Key.FAIL_ON_NO_CLASSES_FOUND, "true");
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
        properties.setProperty(HamProperties.Key.FAIL_ON_NO_CLASSES_FOUND, "false");
        HamcrestGenerator hamcrestGenerator = new HamcrestGenerator(properties);

        SimpleEntry<LogLevel, String> expected = new SimpleEntry<LogLevel, String>(WARN, "No classes found!");

        //Act
        hamcrestGenerator.generateMatchers(Collections.<Class<?>>emptyList());

        //Assert
        assertThat(BufferLogger.getLog(), (Matcher) hasItem(is(expected)));
    }

}
