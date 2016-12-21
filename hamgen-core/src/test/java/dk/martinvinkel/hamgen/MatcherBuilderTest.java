package dk.martinvinkel.hamgen;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

public class MatcherBuilderTest {

    @Test
    public void t0201_MatcherBuilder() throws Exception {
        TypeSpec result = MatcherBuilder.matcherBuild("com.test", "TestClass")
                .build();

        System.out.println(result.toString());
    }
}
