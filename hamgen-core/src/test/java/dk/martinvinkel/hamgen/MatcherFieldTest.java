package dk.martinvinkel.hamgen;

import com.squareup.javapoet.CodeBlock;
import org.junit.Test;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static dk.martinvinkel.hamgen.matcher.MatcherFieldMatcher.isMatcherField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void t0403_MatcherAsArgument() {
        //Arrange
        MatcherField expected = new MatcherField();
        expected.setName("something");
        expected.setGetterName("getSomething");
        expected.setFieldPostFix("Match");
        expected.setType(String.class);

        //Act
        MatcherField result = MatcherField.builder(expected).build();

        //Assert
        assertThat(result, isMatcherField(expected));
    }

    @Test
    public void t0404_buildDescriptionToFirstMatcher() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(String.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "desc.appendText(\"somethingMatch \");\n" +
                "desc.appendDescriptionOf(somethingMatch);\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildDescriptionTo(true, "desc");

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0405_buildDescriptionToNotFirstMatcher() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(String.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "desc.appendText(\", \");\n" +
                "desc.appendText(\"somethingMatch \");\n" +
                "desc.appendDescriptionOf(somethingMatch);\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildDescriptionTo(false, "desc");

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0406_buildMatcherInitializationString() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(String.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "this.somethingMatch = expected.getSomething() == null || expected.getSomething().isEmpty() ? isEmptyOrNullString() : is(expected.getSomething());\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization("expected");

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0407_buildMatcherInitializationOtherType() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(double.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "this.somethingMatch = is(expected.getSomething());\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization("expected");

        //Assert
        assertEquals(expected, result.toString());
    }


}
