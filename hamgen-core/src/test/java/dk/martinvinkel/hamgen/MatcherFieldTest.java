package dk.martinvinkel.hamgen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataMyEnum;
import dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static dk.martinvinkel.hamgen.HamProperties.Key.MATCHER_POST_FIX;
import static dk.martinvinkel.hamgen.matcher.MatcherFieldMatcher.isMatcherField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;

public class MatcherFieldTest {

    private static final String MATCHER_PRE_FIX = HamProperties.Key.MATCHER_PRE_FIX.getDefaultValue();
    private static final String PACKAGE_POST_FIX = HamProperties.Key.PACKAGE_POST_FIX.getDefaultValue();

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

        String expected = "desc.appendText(\"something \");\n" +
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
                "desc.appendText(\"something \");\n" +
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
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization("expected", MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0407_buildMatcherInitializationSimpleTypes() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(double.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "this.somethingMatch = is(expected.getSomething());\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization("expected", MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0408_buildMatcherInitializationOtherTypes() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("somethingElse");
        matcherField.setGetterName("getSomethingElse");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(MatcherBuilderTestDataSomethingElse.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "this.somethingElseMatch = expected.getSomethingElse() == null ? nullValue() : isSomethingElse(expected.getSomethingElse());\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization("expected", MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0409_staticImportsGetsBuild() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("somethingElse");
        matcherField.setGetterName("getSomethingElse");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(MatcherBuilderTestDataSomethingElse.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);


        ClassName matcherTypeName = ClassName.get("dk.martinvinkel.hamgen.testdata.matcher", "MatcherBuilderTestDataSomethingElseMatch");
        SimpleEntry<ClassName, String> expected = new SimpleEntry<>(matcherTypeName, "isSomethingElse");

        //Act
        matcherFieldBuilder.buildMatcherInitialization("expected", MATCHER_PRE_FIX, PACKAGE_POST_FIX);
        Map<ClassName, String> result = matcherFieldBuilder.buildStaticImports();

        //Assert
        assertThat(result.entrySet(), allOf((Matcher)hasItem(expected), hasSize(1)));
    }

    @Test
    public void t0410_buildMatcherInitializationEnum() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myEnum");
        matcherField.setGetterName("getMyEnum");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(MatcherBuilderTestDataMyEnum.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "this.myEnumMatch = is(expected.getMyEnum());\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization("expected", MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

}
