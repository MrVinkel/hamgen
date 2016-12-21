package dk.martinvinkel.hamgen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_PRE_FIX;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HamcrestGenerator.class)
public class HamcrestGeneratorTest {


    @Test
    public void t0101_buildFactoryMethod() throws Exception {
        HamProperties properties = new HamProperties();
        properties.setProperty(MATCHER_PRE_FIX, "Is");
        HamcrestGenerator generator = new HamcrestGenerator(properties);

    }
}
