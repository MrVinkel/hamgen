package dk.martinvinkel.hamgen;

import org.junit.Test;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static dk.martinvinkel.hamgen.matcher.MatcherFieldMatcher.isMatcherField;
import static org.hamcrest.MatcherAssert.assertThat;

public class MatcherFieldTest {

    @Test
    public void t0401_matcherField() {
        //Arrange
        MatcherField expected = new MatcherField();
        expected.setName("something");
        expected.setGetterName("getSomething");
        expected.setFieldPostFix("Match");
        expected.setType(String.class);

        //Act
        MatcherField result = MatcherField.builder(String.class, "getSomething")
                .withPostFix("Match")
                .build();

        //Assert
        assertThat(result, isMatcherField(expected));
    }

    @Test
    public void t0402_DefaultPostFix() {
        //Arrange
        MatcherField expected = new MatcherField();
        expected.setName("something");
        expected.setGetterName("getSomething");
        expected.setFieldPostFix(MATCHER_POST_FIX.getDefaultValue());
        expected.setType(String.class);

        //Act
        MatcherField result = MatcherField.builder(String.class, "getSomething").build();

        //Assert
        assertThat(result, isMatcherField(expected));
    }

    @Test
    public void t0403_nullValues() {
        //Arrange
        MatcherField expected = new MatcherField();
        expected.setName(null);
        expected.setGetterName(null);
        expected.setFieldPostFix(null);
        expected.setType(null);

        //Act
        MatcherField result = MatcherField.builder(null, null)
                .withPostFix(null)
                .build();

        //Assert
        assertThat(result, isMatcherField(expected));
    }



}
