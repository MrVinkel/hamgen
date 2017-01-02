package dk.martinvinkel.hamgen.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import dk.martinvinkel.hamgen.HamProperties;
import dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataMyEnum;
import dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataSomething;
import dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
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
    private static final String PARAM_NAME_EXPECTED = "expected";

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
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

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
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

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

        String expected = "this.somethingElseMatch = expected.getSomethingElse() == null ? nullValue() : isMatcherBuilderTestDataSomethingElse(expected.getSomethingElse());\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

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
        SimpleEntry<ClassName, String> expected = new SimpleEntry<>(matcherTypeName, "isMatcherBuilderTestDataSomethingElse");

        //Act
        matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);
        Map<ClassName, String> result = matcherFieldBuilder.buildStaticImports();

        //Assert
        assertThat(result.entrySet(), allOf((Matcher) hasItem(expected), hasSize(1)));
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
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0411_buildMatcherListString() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myList");
        matcherField.setGetterName("getMyList");
        matcherField.setType(this.getClass().getMethod("getList").getGenericReturnType());
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "java.util.List<java.lang.String> items = expected.getMyList();\n" +
                "if (items == null) {\n" +
                "  this.myListMatcher = nullValue();\n" +
                "} else {\n" +
                "  java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<>();\n" +
                "  for (java.lang.String item : items) {\n" +
                "    org.hamcrest.Matcher matcher = item == null ||item.isEmpty() ? isEmptyOrNullString() : is(item);\n" +
                "    matchers.add(matcher);\n" +
                "  }\n" +
                "  this.myListMatcher = contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()]));\n" +
                "}\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0412_buildMatcherListComplexType() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myList");
        matcherField.setGetterName("getMyList");
        matcherField.setType(this.getClass().getMethod("getSomething").getGenericReturnType());
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "java.util.List<dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataSomething> items = expected.getMyList();\n" +
                "if (items == null) {\n" +
                "  this.myListMatcher = nullValue();\n" +
                "} else {\n" +
                "  java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<>();\n" +
                "  for (dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataSomething item : items) {\n" +
                "    org.hamcrest.Matcher matcher = item == null ? nullValue() : isMatcherBuilderTestDataSomething(item);\n" +
                "    matchers.add(matcher);\n" +
                "  }\n" +
                "  this.myListMatcher = contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()]));\n" +
                "}\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0413_buildMatcherListDouble() throws Exception {
        //Arrange
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myList");
        matcherField.setGetterName("getMyList");
        matcherField.setType(this.getClass().getMethod("getDouble").getGenericReturnType());
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField);

        String expected = "java.util.List<java.lang.Double> items = expected.getMyList();\n" +
                "if (items == null) {\n" +
                "  this.myListMatcher = nullValue();\n" +
                "} else {\n" +
                "  java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<>();\n" +
                "  for (java.lang.Double item : items) {\n" +
                "    org.hamcrest.Matcher matcher = is(item);\n" +
                "    matchers.add(matcher);\n" +
                "  }\n" +
                "  this.myListMatcher = contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()]));\n" +
                "}\n";

        //Act
        CodeBlock result = matcherFieldBuilder.buildMatcherInitialization(PARAM_NAME_EXPECTED, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, result.toString());
    }

    // used by t0411
    public List<String> getList() {
        return Collections.emptyList();
    }

    // used by t0412
    public List<MatcherBuilderTestDataSomething> getSomething() {
        return Collections.emptyList();
    }

    // used by t0413
    public List<Double> getDouble() {
        return Collections.emptyList();
    }

}
