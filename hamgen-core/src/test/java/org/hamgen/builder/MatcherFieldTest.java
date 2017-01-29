package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.Description;
import org.hamgen.HamProperties;
import org.hamgen.testdata.MatcherBuilderTestDataMyEnum;
import org.hamgen.testdata.MatcherBuilderTestDataSomething;
import org.hamcrest.Matcher;
import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamgen.matcher.MatcherFieldMatcher.isMatcherField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamgen.util.CodeModelUtil.generableToString;
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
        expected.setFieldPostFix(HamProperties.Key.MATCHER_POST_FIX.getDefaultValue());
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

        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(new JCodeModel());
        JBlock describeToBody = new JBlock();
        JVar description = testDataBuilder.buildJVar(Description.class, "desc");
        JVar matcher = testDataBuilder.buildJVar(String.class, "somethingMatch");

        String expected =
                "{\r\n" +
                        "    desc.appendText(\"something \");\r\n" +
                        "    desc.appendDescriptionOf(somethingMatch);\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildDescribeTo(describeToBody, description, matcher, true);

        //Assert
        assertEquals(expected, generableToString(result));
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

        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(new JCodeModel());
        JBlock describeToBody = new JBlock();
        JVar description = testDataBuilder.buildJVar(Description.class, "desc");
        JVar matcher = testDataBuilder.buildJVar(String.class, "somethingMatch");

        String expected =
                "{\r\n" +
                        "    desc.appendText(\", \");\r\n" +
                        "    desc.appendText(\"something \");\r\n" +
                        "    desc.appendDescriptionOf(somethingMatch);\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildDescribeTo(describeToBody, description, matcher, false);

        //Assert
        assertEquals(expected, generableToString(result));
    }

    @Test
    public void t0406_buildMatcherInitializationString() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(String.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "somethingMatch");
        JVar expectedJVar = testDataBuilder.buildJVar(String.class, PARAM_NAME_EXPECTED);

        String expected =
                "{\r\n" +
                        "    somethingMatch = (((expected.getSomething() == null)||expected.getSomething().isEmpty())?org.hamcrest.Matchers.isEmptyOrNullString():org.hamcrest.Matchers.is(expected.getSomething()));\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
    }


    @Test
    public void t0407_buildMatcherInitializationSimpleTypes() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("something");
        matcherField.setGetterName("getSomething");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(double.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "somethingMatch");
        JVar expectedJVar = testDataBuilder.buildJVar(double.class, PARAM_NAME_EXPECTED);

        String expected =
                "{\r\n" +
                        "    somethingMatch = org.hamcrest.Matchers.is(expected.getSomething());\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
    }

    @Test
    public void t0408_buildMatcherInitializationOtherTypes() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("somethingElse");
        matcherField.setGetterName("getSomethingElse");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(MatcherBuilderTestDataSomethingElse.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "somethingElseMatch");
        JVar expectedJVar = testDataBuilder.buildJVar(MatcherBuilderTestDataSomethingElse.class, PARAM_NAME_EXPECTED);

        String expected =
                "{\r\n" +
                        "    somethingElseMatch = ((expected.getSomethingElse() == null)?org.hamcrest.Matchers.nullValue():org.hamgen.testdata.matcher.MatcherBuilderTestDataSomethingElseMatch.isMatcherBuilderTestDataSomethingElse(expected.getSomethingElse()));\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
    }

    @Test
    public void t0410_buildMatcherInitializationEnum() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myEnum");
        matcherField.setGetterName("getMyEnum");
        matcherField.setFieldPostFix("Match");
        matcherField.setType(MatcherBuilderTestDataMyEnum.class);
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "myEnumMatch");
        JVar expectedJVar = testDataBuilder.buildJVar(MatcherBuilderTestDataMyEnum.class, PARAM_NAME_EXPECTED);

        String expected =
                "{\r\n" +
                        "    myEnumMatch = org.hamcrest.Matchers.is(expected.getMyEnum());\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
    }

    @Test
    public void t0411_buildMatcherListString() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myList");
        matcherField.setGetterName("getMyList");
        matcherField.setType(this.getClass().getMethod("getList").getGenericReturnType());
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "myListMatcher");
        JVar expectedJVar = testDataBuilder.buildJVar(ArrayList.class, PARAM_NAME_EXPECTED);

        String expected =
                "{\r\n" +
                        "    java.util.List<java.lang.String> items = expected.getMyList();\r\n" +
                        "    if (items == null) {\r\n" +
                        "        myListMatcher = org.hamcrest.Matchers.nullValue();\r\n" +
                        "    } else {\r\n" +
                        "        java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<org.hamcrest.Matcher>();\r\n" +
                        "        for (java.lang.String item: items) {\r\n" +
                        "            org.hamcrest.Matcher itemMatcher = (((item == null)||item.isEmpty())?org.hamcrest.Matchers.isEmptyOrNullString():org.hamcrest.Matchers.is(item));\r\n" +
                        "            matchers.add(itemMatcher);\r\n" +
                        "        }\r\n" +
                        "        myListMatcher = org.hamcrest.Matcher.contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()] ));\r\n" +
                        "    }\r\n" +
                        "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
    }

    @Test
    public void t0412_buildMatcherListComplexType() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myList");
        matcherField.setGetterName("getMyList");
        matcherField.setType(this.getClass().getMethod("getSomething").getGenericReturnType());
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "myListMatcher");
        JVar expectedJVar = testDataBuilder.buildJVar(ArrayList.class, PARAM_NAME_EXPECTED);

        String expected = "{\r\n" +
                "    java.util.List<org.hamgen.testdata.MatcherBuilderTestDataSomething> items = expected.getMyList();\r\n" +
                "    if (items == null) {\r\n" +
                "        myListMatcher = org.hamcrest.Matchers.nullValue();\r\n" +
                "    } else {\r\n" +
                "        java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<org.hamcrest.Matcher>();\r\n" +
                "        for (org.hamgen.testdata.MatcherBuilderTestDataSomething item: items) {\r\n" +
                "            org.hamcrest.Matcher itemMatcher = ((item == null)?org.hamcrest.Matchers.nullValue():org.hamgen.testdata.matcher.MatcherBuilderTestDataSomethingMatcher.isMatcherBuilderTestDataSomething(item));\r\n" +
                "            matchers.add(itemMatcher);\r\n" +
                "        }\r\n" +
                "        myListMatcher = org.hamcrest.Matcher.contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()] ));\r\n" +
                "    }\r\n" +
                "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
    }

    @Test
    public void t0413_buildMatcherListDouble() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        MatcherField matcherField = new MatcherField();
        matcherField.setName("myList");
        matcherField.setGetterName("getMyList");
        matcherField.setType(this.getClass().getMethod("getDouble").getGenericReturnType());
        MatcherField.Builder matcherFieldBuilder = MatcherField.builder(matcherField).withCodeModel(codeModel);

        JBlock constructorBlock = new JBlock();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        JVar matcherJVar = testDataBuilder.buildJVar(Matcher.class, "myListMatcher");
        JVar expectedJVar = testDataBuilder.buildJVar(ArrayList.class, PARAM_NAME_EXPECTED);

        String expected = "{\r\n" +
                "    java.util.List<java.lang.Double> items = expected.getMyList();\r\n" +
                "    if (items == null) {\r\n" +
                "        myListMatcher = org.hamcrest.Matchers.nullValue();\r\n" +
                "    } else {\r\n" +
                "        java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<org.hamcrest.Matcher>();\r\n" +
                "        for (java.lang.Double item: items) {\r\n" +
                "            org.hamcrest.Matcher itemMatcher = org.hamcrest.Matchers.is(item);\r\n" +
                "            matchers.add(itemMatcher);\r\n" +
                "        }\r\n" +
                "        myListMatcher = org.hamcrest.Matcher.contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()] ));\r\n" +
                "    }\r\n" +
                "}";

        //Act
        JBlock result = matcherFieldBuilder.buildMatcherInitialization(constructorBlock, matcherJVar, expectedJVar, MATCHER_PRE_FIX, PACKAGE_POST_FIX);

        //Assert
        assertEquals(expected, generableToString(result));
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
