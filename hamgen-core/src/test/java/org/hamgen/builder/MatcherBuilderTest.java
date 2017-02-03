
package org.hamgen.builder;

import com.sun.codemodel.JCodeModel;
import com.test.TestClass;
import org.hamgen.testdata.MatcherBuilderTestDataListSomething;
import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import static org.hamgen.testtools.CodeModelUtil.codeModelToString;
import static org.junit.Assert.assertEquals;

public class MatcherBuilderTest {

    @Test
    public void t0201_MatcherBuilder() throws Exception {
        // Arrange
        String expected =
                "-----------------------------------com.test.matcher.TestClassMatcher.java-----------------------------------\r\n" +
                        "\r\n" +
                        "package com.test.matcher;\r\n" +
                        "\r\n" +
                        "import com.test.TestClass;\r\n" +
                        "import org.hamcrest.Description;\r\n" +
                        "import org.hamcrest.Factory;\r\n" +
                        "import org.hamgen.HamGenDiagnosingMatcher;\r\n" +
                        "\r\n" +
                        "public class TestClassMatcher\r\n" +
                        "    extends HamGenDiagnosingMatcher\r\n" +
                        "{\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "    public TestClassMatcher(TestClass expected) {\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public void describeTo(Description desc) {\r\n" +
                        "        desc.appendText(\"{\");\r\n" +
                        "        desc.appendText(\"}\");\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public boolean matchesSafely(Object item, Description mismatchDesc) {\r\n" +
                        "        TestClass actual = ((TestClass) item);\r\n" +
                        "        boolean matches = true;\r\n" +
                        "        mismatchDesc.appendText(\"{\");\r\n" +
                        "        mismatchDesc.appendText(\"}\");\r\n" +
                        "        return matches;\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    @Factory\r\n" +
                        "    public static TestClassMatcher isTestClass(TestClass expected) {\r\n" +
                        "        return new TestClassMatcher(expected);\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "}\r\n";
        // Act
        JCodeModel result = new MatcherBuilder().withClass(TestClass.class).build();

        // Assert
        assertEquals(expected, codeModelToString(result));
    }

    @Test
    public void t0202_MatcherBuilderCustomPrePostFixes() throws Exception {
        // Arrange
        String expected =
                "-----------------------------------com.test.My.Package.TestClassMyPost.java-----------------------------------\r\n" +
                        "\r\n" +
                        "package com.test.My.Package;\r\n" +
                        "\r\n" +
                        "import com.test.TestClass;\r\n" +
                        "import org.hamcrest.Description;\r\n" +
                        "import org.hamcrest.Factory;\r\n" +
                        "import org.hamgen.HamGenDiagnosingMatcher;\r\n" +
                        "\r\n" +
                        "public class TestClassMyPost\r\n" +
                        "    extends HamGenDiagnosingMatcher\r\n" +
                        "{\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "    public TestClassMyPost(TestClass expected) {\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public void describeTo(Description desc) {\r\n" +
                        "        desc.appendText(\"{\");\r\n" +
                        "        desc.appendText(\"}\");\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public boolean matchesSafely(Object item, Description mismatchDesc) {\r\n" +
                        "        TestClass actual = ((TestClass) item);\r\n" +
                        "        boolean matches = true;\r\n" +
                        "        mismatchDesc.appendText(\"{\");\r\n" +
                        "        mismatchDesc.appendText(\"}\");\r\n" +
                        "        return matches;\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    @Factory\r\n" +
                        "    public static TestClassMyPost MyPreTestClass(TestClass expected) {\r\n" +
                        "        return new TestClassMyPost(expected);\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "}\r\n";

        // Act
        JCodeModel result = new MatcherBuilder()
                .withClass(TestClass.class)
                .withMatcherNamePostfix("MyPost")
                .withMatcherPrefix("MyPre")
                .withPackagePostFix("My.Package")
                .build();

        // Assert
        assertEquals(expected, codeModelToString(result));
    }


    @Test
    public void t0202_MatcherBuilderCustomPrePostFixesPackageWithDots() throws Exception {
        // Arrange
        String expected =
                "-----------------------------------com.test.My.Package.TestClassMyPost.java-----------------------------------\r\n" +
                        "\r\n" +
                        "package com.test.My.Package;\r\n" +
                        "\r\n" +
                        "import com.test.TestClass;\r\n" +
                        "import org.hamcrest.Description;\r\n" +
                        "import org.hamcrest.Factory;\r\n" +
                        "import org.hamgen.HamGenDiagnosingMatcher;\r\n" +
                        "\r\n" +
                        "public class TestClassMyPost\r\n" +
                        "    extends HamGenDiagnosingMatcher\r\n" +
                        "{\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "    public TestClassMyPost(TestClass expected) {\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public void describeTo(Description desc) {\r\n" +
                        "        desc.appendText(\"{\");\r\n" +
                        "        desc.appendText(\"}\");\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public boolean matchesSafely(Object item, Description mismatchDesc) {\r\n" +
                        "        TestClass actual = ((TestClass) item);\r\n" +
                        "        boolean matches = true;\r\n" +
                        "        mismatchDesc.appendText(\"{\");\r\n" +
                        "        mismatchDesc.appendText(\"}\");\r\n" +
                        "        return matches;\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    @Factory\r\n" +
                        "    public static TestClassMyPost MyPreTestClass(TestClass expected) {\r\n" +
                        "        return new TestClassMyPost(expected);\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "}\r\n";

        // Act
        JCodeModel result = new MatcherBuilder()
                .withClass(TestClass.class)
                .withMatcherNamePostfix("MyPost")
                .withMatcherPrefix("MyPre")
                .withPackagePostFix(".My.Package")
                .build();

        // Assert
        assertEquals(expected, codeModelToString(result));
    }


    @Test
    public void t0203_WithFields() throws Exception {
        // Arrange
        String expected =
                "-----------------------------------org.hamgen.testdata.matcher.MatcherBuilderTestDataSomethingElseMatcher.java-----------------------------------\r\n" +
                        "\r\n" +
                        "package org.hamgen.testdata.matcher;\r\n" +
                        "\r\n" +
                        "import org.hamcrest.Description;\r\n" +
                        "import org.hamcrest.Factory;\r\n" +
                        "import org.hamcrest.Matcher;\r\n" +
                        "import org.hamcrest.Matchers;\r\n" +
                        "import org.hamgen.HamGenDiagnosingMatcher;\r\n" +
                        "import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;\r\n" +
                        "\r\n" +
                        "public class MatcherBuilderTestDataSomethingElseMatcher\r\n" +
                        "    extends HamGenDiagnosingMatcher\r\n" +
                        "{\r\n" +
                        "\r\n" +
                        "    private Matcher myEnumMatcher;\r\n" +
                        "    private Matcher mySecondFieldMatcher;\r\n" +
                        "    private Matcher myFieldMatcher;\r\n" +
                        "\r\n" +
                        "    public MatcherBuilderTestDataSomethingElseMatcher(MatcherBuilderTestDataSomethingElse expected) {\r\n" +
                        "        myEnumMatcher = Matchers.is(expected.getMyEnum());\r\n" +
                        "        mySecondFieldMatcher = Matchers.is(expected.getMySecondField());\r\n" +
                        "        myFieldMatcher = (((expected.getMyField() == null)||expected.getMyField().isEmpty())?Matchers.isEmptyOrNullString():Matchers.is(expected.getMyField()));\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public void describeTo(Description desc) {\r\n" +
                        "        desc.appendText(\"{\");\r\n" +
                        "        desc.appendText(\"myEnum \");\r\n" +
                        "        desc.appendDescriptionOf(myEnumMatcher);\r\n" +
                        "        desc.appendText(\", \");\r\n" +
                        "        desc.appendText(\"mySecondField \");\r\n" +
                        "        desc.appendDescriptionOf(mySecondFieldMatcher);\r\n" +
                        "        desc.appendText(\", \");\r\n" +
                        "        desc.appendText(\"myField \");\r\n" +
                        "        desc.appendDescriptionOf(myFieldMatcher);\r\n" +
                        "        desc.appendText(\"}\");\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public boolean matchesSafely(Object item, Description mismatchDesc) {\r\n" +
                        "        MatcherBuilderTestDataSomethingElse actual = ((MatcherBuilderTestDataSomethingElse) item);\r\n" +
                        "        boolean matches = true;\r\n" +
                        "        mismatchDesc.appendText(\"{\");\r\n" +
                        "        if (!myEnumMatcher.matches(actual.getMyEnum())) {\r\n" +
                        "            HamGenDiagnosingMatcher.reportMismatch(\"myEnum\", myEnumMatcher, actual.getMyEnum(), mismatchDesc, matches);\r\n" +
                        "            matches = false;\r\n" +
                        "        }\r\n" +
                        "        if (!mySecondFieldMatcher.matches(actual.getMySecondField())) {\r\n" +
                        "            HamGenDiagnosingMatcher.reportMismatch(\"mySecondField\", mySecondFieldMatcher, actual.getMySecondField(), mismatchDesc, matches);\r\n" +
                        "            matches = false;\r\n" +
                        "        }\r\n" +
                        "        if (!myFieldMatcher.matches(actual.getMyField())) {\r\n" +
                        "            HamGenDiagnosingMatcher.reportMismatch(\"myField\", myFieldMatcher, actual.getMyField(), mismatchDesc, matches);\r\n" +
                        "            matches = false;\r\n" +
                        "        }\r\n" +
                        "        mismatchDesc.appendText(\"}\");\r\n" +
                        "        return matches;\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    @Factory\r\n" +
                        "    public static MatcherBuilderTestDataSomethingElseMatcher isMatcherBuilderTestDataSomethingElse(MatcherBuilderTestDataSomethingElse expected) {\r\n" +
                        "        return new MatcherBuilderTestDataSomethingElseMatcher(expected);\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "}\r\n";


        // Act
        //getMethods() returns a random order each time.. so we have to get the methods individually in the right order to make sure the test parses
        //I wanted to stub out the Method class, but it is not possible with PowerMock/Mockito because they themselves rely on it
        MatcherBuilder matcherBuilder = new MatcherBuilder()
                .withClass(MatcherBuilderTestDataSomethingElse.class)
                .matchFields(MatcherBuilderTestDataSomethingElse.class.getMethod("myRandomFunction"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMyEnum"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMySecondField"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMyField"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getClass"));
        JCodeModel result = matcherBuilder.build();

        // Assert
        assertEquals(expected, codeModelToString(result));
    }

    @Test
    public void t0204_ListField() throws Exception {
        // Arrange
        String expected =
                "-----------------------------------org.hamgen.testdata.matcher.MatcherBuilderTestDataListSomethingMatcher.java-----------------------------------\r\n" +
                        "\r\n" +
                        "package org.hamgen.testdata.matcher;\r\n" +
                        "\r\n" +
                        "import java.util.ArrayList;\r\n" +
                        "import java.util.List;\r\n" +
                        "import org.hamcrest.Description;\r\n" +
                        "import org.hamcrest.Factory;\r\n" +
                        "import org.hamcrest.Matcher;\r\n" +
                        "import org.hamcrest.Matchers;\r\n" +
                        "import org.hamgen.HamGenDiagnosingMatcher;\r\n" +
                        "import org.hamgen.testdata.MatcherBuilderTestDataListSomething;\r\n" +
                        "import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;\r\n" +
                        "\r\n" +
                        "public class MatcherBuilderTestDataListSomethingMatcher\r\n" +
                        "    extends HamGenDiagnosingMatcher\r\n" +
                        "{\r\n" +
                        "\r\n" +
                        "    private Matcher someListMatcher;\r\n" +
                        "\r\n" +
                        "    public MatcherBuilderTestDataListSomethingMatcher(MatcherBuilderTestDataListSomething expected) {\r\n" +
                        "        List<MatcherBuilderTestDataSomethingElse> items = expected.getSomeList();\r\n" +
                        "        if (items == null) {\r\n" +
                        "            someListMatcher = Matchers.nullValue();\r\n" +
                        "        } else {\r\n" +
                        "            List<Matcher> matchers = new ArrayList<Matcher>();\r\n" +
                        "            for (MatcherBuilderTestDataSomethingElse item: items) {\r\n" +
                        "                Matcher itemMatcher = ((item == null)?Matchers.nullValue():MatcherBuilderTestDataSomethingElseMatcher.isMatcherBuilderTestDataSomethingElse(item));\r\n" +
                        "                matchers.add(itemMatcher);\r\n" +
                        "            }\r\n" +
                        "            someListMatcher = Matchers.contains(matchers.toArray(new Matcher[matchers.size()] ));\r\n" +
                        "        }\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public void describeTo(Description desc) {\r\n" +
                        "        desc.appendText(\"{\");\r\n" +
                        "        desc.appendText(\"someList \");\r\n" +
                        "        desc.appendDescriptionOf(someListMatcher);\r\n" +
                        "        desc.appendText(\"}\");\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public boolean matchesSafely(Object item, Description mismatchDesc) {\r\n" +
                        "        MatcherBuilderTestDataListSomething actual = ((MatcherBuilderTestDataListSomething) item);\r\n" +
                        "        boolean matches = true;\r\n" +
                        "        mismatchDesc.appendText(\"{\");\r\n" +
                        "        if (!someListMatcher.matches(actual.getSomeList())) {\r\n" +
                        "            HamGenDiagnosingMatcher.reportMismatch(\"someList\", someListMatcher, actual.getSomeList(), mismatchDesc, matches);\r\n" +
                        "            matches = false;\r\n" +
                        "        }\r\n" +
                        "        mismatchDesc.appendText(\"}\");\r\n" +
                        "        return matches;\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    @Factory\r\n" +
                        "    public static MatcherBuilderTestDataListSomethingMatcher isMatcherBuilderTestDataListSomething(MatcherBuilderTestDataListSomething expected) {\r\n" +
                        "        return new MatcherBuilderTestDataListSomethingMatcher(expected);\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "}\r\n";

        // Act
        //getMethods() returns a random order each time.. so we have to get the methods individually in the right order to make sure the test parses
        //I wanted to stub out the Method class, but it is not possible with PowerMock/Mockito because they themselves rely on it
        MatcherBuilder matcherBuilder = new MatcherBuilder()
                .withClass(MatcherBuilderTestDataListSomething.class)
                .matchFields(MatcherBuilderTestDataListSomething.class.getMethods());
        JCodeModel result = matcherBuilder.build();

        // Assert
        assertEquals(expected, codeModelToString(result));
    }


    @Test
    public void t0205_ExcludedFields() throws Exception {
        // Arrange
        String expected =
                "-----------------------------------org.hamgen.testdata.matcher.MatcherBuilderTestDataSomethingElseMatcher.java-----------------------------------\r\n" +
                        "\r\n" +
                        "package org.hamgen.testdata.matcher;\r\n" +
                        "\r\n" +
                        "import org.hamcrest.Description;\r\n" +
                        "import org.hamcrest.Factory;\r\n" +
                        "import org.hamcrest.Matcher;\r\n" +
                        "import org.hamcrest.Matchers;\r\n" +
                        "import org.hamgen.HamGenDiagnosingMatcher;\r\n" +
                        "import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;\r\n" +
                        "\r\n" +
                        "public class MatcherBuilderTestDataSomethingElseMatcher\r\n" +
                        "    extends HamGenDiagnosingMatcher\r\n" +
                        "{\r\n" +
                        "\r\n" +
                        "    private Matcher myEnumMatcher;\r\n" +
                        "    private Matcher mySecondFieldMatcher;\r\n" +
                        "\r\n" +
                        "    public MatcherBuilderTestDataSomethingElseMatcher(MatcherBuilderTestDataSomethingElse expected) {\r\n" +
                        "        myEnumMatcher = Matchers.is(expected.getMyEnum());\r\n" +
                        "        mySecondFieldMatcher = Matchers.is(expected.getMySecondField());\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public void describeTo(Description desc) {\r\n" +
                        "        desc.appendText(\"{\");\r\n" +
                        "        desc.appendText(\"myEnum \");\r\n" +
                        "        desc.appendDescriptionOf(myEnumMatcher);\r\n" +
                        "        desc.appendText(\", \");\r\n" +
                        "        desc.appendText(\"mySecondField \");\r\n" +
                        "        desc.appendDescriptionOf(mySecondFieldMatcher);\r\n" +
                        "        desc.appendText(\"}\");\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    public boolean matchesSafely(Object item, Description mismatchDesc) {\r\n" +
                        "        MatcherBuilderTestDataSomethingElse actual = ((MatcherBuilderTestDataSomethingElse) item);\r\n" +
                        "        boolean matches = true;\r\n" +
                        "        mismatchDesc.appendText(\"{\");\r\n" +
                        "        if (!myEnumMatcher.matches(actual.getMyEnum())) {\r\n" +
                        "            HamGenDiagnosingMatcher.reportMismatch(\"myEnum\", myEnumMatcher, actual.getMyEnum(), mismatchDesc, matches);\r\n" +
                        "            matches = false;\r\n" +
                        "        }\r\n" +
                        "        if (!mySecondFieldMatcher.matches(actual.getMySecondField())) {\r\n" +
                        "            HamGenDiagnosingMatcher.reportMismatch(\"mySecondField\", mySecondFieldMatcher, actual.getMySecondField(), mismatchDesc, matches);\r\n" +
                        "            matches = false;\r\n" +
                        "        }\r\n" +
                        "        mismatchDesc.appendText(\"}\");\r\n" +
                        "        return matches;\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "    @Factory\r\n" +
                        "    public static MatcherBuilderTestDataSomethingElseMatcher isMatcherBuilderTestDataSomethingElse(MatcherBuilderTestDataSomethingElse expected) {\r\n" +
                        "        return new MatcherBuilderTestDataSomethingElseMatcher(expected);\r\n" +
                        "    }\r\n" +
                        "\r\n" +
                        "}\r\n";

        List<Class<?>> excludedTypes = new ArrayList<Class<?>>();
        excludedTypes.add(String.class);


        // Act
        //getMethods() returns a random order each time.. so we have to get the methods individually in the right order to make sure the test parses
        //I wanted to stub out the Method class, but it is not possible with PowerMock/Mockito because they themselves rely on it
        MatcherBuilder matcherBuilder = new MatcherBuilder()
                .withClass(MatcherBuilderTestDataSomethingElse.class)
                .withExcludeTypes(excludedTypes)
                .matchFields(MatcherBuilderTestDataSomethingElse.class.getMethod("myRandomFunction"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMyEnum"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMySecondField"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMyField"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getClass"));

        JCodeModel result = matcherBuilder.build();

        // Assert
        assertEquals(expected, codeModelToString(result));
    }

}

