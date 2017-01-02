package org.hamgen.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.hamgen.testdata.MatcherBuilderTestDataListSomething;
import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

public class MatcherBuilderTest {

    @Test
    public void t0201_MatcherBuilder() throws Exception {
        // Arrange
        String expected =
                "public class TestClassMatcher extends org.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  public TestClassMatcher(com.test.TestClass expected) {\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(java.lang.Object item, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = true;\n" +
                        "    com.test.TestClass actual = (com.test.TestClass) item;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    mismatchDesc.appendText(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void describeTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.matcher.TestClassMatcher isTestClass(com.test.TestClass expected) {\n" +
                        "    return new com.test.matcher.TestClassMatcher(expected);\n" +
                        "  }\n" +
                        "}\n";
        // Act
        TypeSpec result = MatcherBuilder.matcherBuild("com.test", "TestClass").build();

        // Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0202_MatcherBuilderCustomPrePostFixes() throws Exception {
        // Arrange
        String expected =
                "public class TestClassMyPost extends org.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  public TestClassMyPost(com.test.TestClass expected) {\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(java.lang.Object item, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = true;\n" +
                        "    com.test.TestClass actual = (com.test.TestClass) item;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    mismatchDesc.appendText(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void describeTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.My.Package.TestClassMyPost MyPreTestClass(com.test.TestClass expected) {\n" +
                        "    return new com.test.My.Package.TestClassMyPost(expected);\n" +
                        "  }\n" +
                        "}\n";
        // Act
        TypeSpec result = MatcherBuilder.matcherBuild("com.test", "TestClass")
                .withMatcherNamePostfix("MyPost")
                .withMatcherPrefix("MyPre")
                .withPackagePostFix("My.Package")
                .build();

        // Assert
        assertEquals(expected, result.toString());
    }

    @Test
    public void t0202_MatcherBuilderCustomPrePostFixesPackageWithDots() throws Exception {
        // Arrange
        String expected =
                "public class TestClassMyPost extends org.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  public TestClassMyPost(com.test.TestClass expected) {\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(java.lang.Object item, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = true;\n" +
                        "    com.test.TestClass actual = (com.test.TestClass) item;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    mismatchDesc.appendText(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void describeTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.My.Package.TestClassMyPost MyPreTestClass(com.test.TestClass expected) {\n" +
                        "    return new com.test.My.Package.TestClassMyPost(expected);\n" +
                        "  }\n" +
                        "}\n";
        // Act
        TypeSpec result = MatcherBuilder.matcherBuild("com.test", "TestClass")
                .withMatcherNamePostfix("MyPost")
                .withMatcherPrefix("MyPre")
                .withPackagePostFix(".My.Package")
                .build();

        // Assert
        assertEquals(expected, result.toString());
    }


    @Test
    public void t0203_WithFields() throws Exception {
        // Arrange
        String expected =
                "public class MatcherBuilderTestDataSomethingElseMatcher extends org.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  protected org.hamcrest.Matcher myEnumMatcher;\n" +
                        "\n" +
                        "  protected org.hamcrest.Matcher mySecondFieldMatcher;\n" +
                        "\n" +
                        "  protected org.hamcrest.Matcher myFieldMatcher;\n" +
                        "\n" +
                        "  public MatcherBuilderTestDataSomethingElseMatcher(com.test.MatcherBuilderTestDataSomethingElse expected) {\n" +
                        "    this.myEnumMatcher = is(expected.getMyEnum());\n" +
                        "    this.mySecondFieldMatcher = is(expected.getMySecondField());\n" +
                        "    this.myFieldMatcher = expected.getMyField() == null || expected.getMyField().isEmpty() ? isEmptyOrNullString() : is(expected.getMyField());\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(java.lang.Object item, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = true;\n" +
                        "    com.test.MatcherBuilderTestDataSomethingElse actual = (com.test.MatcherBuilderTestDataSomethingElse) item;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    if (!myEnumMatcher.matches(actual.getMyEnum())) {\n" +
                        "      reportMismatch(\"myEnum\", myEnumMatcher, actual.getMyEnum(), mismatchDesc, matches);\n" +
                        "      matches = false;\n" +
                        "    }\n" +
                        "    if (!mySecondFieldMatcher.matches(actual.getMySecondField())) {\n" +
                        "      reportMismatch(\"mySecondField\", mySecondFieldMatcher, actual.getMySecondField(), mismatchDesc, matches);\n" +
                        "      matches = false;\n" +
                        "    }\n" +
                        "    if (!myFieldMatcher.matches(actual.getMyField())) {\n" +
                        "      reportMismatch(\"myField\", myFieldMatcher, actual.getMyField(), mismatchDesc, matches);\n" +
                        "      matches = false;\n" +
                        "    }\n" +
                        "    mismatchDesc.appendText(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void describeTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" + "" +
                        "    desc.appendText(\"myEnum \");\n" +
                        "    desc.appendDescriptionOf(myEnumMatcher);\n" +
                        "    desc.appendText(\", \");\n" +
                        "    desc.appendText(\"mySecondField \");\n" +
                        "    desc.appendDescriptionOf(mySecondFieldMatcher);\n" +
                        "    desc.appendText(\", \");\n" +
                        "    desc.appendText(\"myField \");\n" +
                        "    desc.appendDescriptionOf(myFieldMatcher);\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.matcher.MatcherBuilderTestDataSomethingElseMatcher isMatcherBuilderTestDataSomethingElse(com.test.MatcherBuilderTestDataSomethingElse expected) {\n" +
                        "    return new com.test.matcher.MatcherBuilderTestDataSomethingElseMatcher(expected);\n" +
                        "  }\n" +
                        "}\n";

        SimpleEntry<ClassName, String> expectedStaticImport = new SimpleEntry<>(ClassName.get(Matchers.class), "*");

        // Act
        //getMethods() returns a random order each time.. so we have to get the methods individually in the right order to make sure the test parses
        //I wanted to stub out the Method class, but it is not possible with PowerMock/Mockito because they themselves rely on it
        MatcherBuilder matcherBuilder = MatcherBuilder.matcherBuild("com.test", "MatcherBuilderTestDataSomethingElse")
                .matchFields(MatcherBuilderTestDataSomethingElse.class.getMethod("myRandomFunction"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMyEnum"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMySecondField"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getMyField"),
                        MatcherBuilderTestDataSomethingElse.class.getMethod("getClass"));
        TypeSpec result = matcherBuilder.build();

        Map<ClassName, String> staticImportsResult = matcherBuilder.buildStaticImports();

        // Assert
        assertEquals(expected, result.toString());
        assertThat(staticImportsResult.entrySet(), allOf(hasSize(1), (Matcher)hasItem(expectedStaticImport)));
    }

    @Test
    public void t0204_ListField() throws Exception {
        // Arrange
        String expected =
                "public class MatcherBuilderTestDataListSomethingMatcher extends org.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  protected org.hamcrest.Matcher someListMatcher;\n" +
                        "\n" +
                        "  public MatcherBuilderTestDataListSomethingMatcher(com.test.MatcherBuilderTestDataListSomething expected) {\n" +
                        "    java.util.List<org.hamgen.testdata.MatcherBuilderTestDataSomethingElse> items = expected.getSomeList();\n" +
                        "    if (items == null) {\n" +
                        "      this.someListMatcher = nullValue();\n" +
                        "    } else {\n" +
                        "      java.util.List<org.hamcrest.Matcher> matchers = new java.util.ArrayList<>();\n" +
                        "      for (org.hamgen.testdata.MatcherBuilderTestDataSomethingElse item : items) {\n" +
                        "        org.hamcrest.Matcher matcher = item == null ? nullValue() : isMatcherBuilderTestDataSomethingElse(item);\n" +
                        "        matchers.add(matcher);\n" +
                        "      }\n" +
                        "      this.someListMatcher = contains(matchers.toArray(new org.hamcrest.Matcher[matchers.size()]));\n" +
                        "    }\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(java.lang.Object item, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = true;\n" +
                        "    com.test.MatcherBuilderTestDataListSomething actual = (com.test.MatcherBuilderTestDataListSomething) item;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    if (!someListMatcher.matches(actual.getSomeList())) {\n" +
                        "      reportMismatch(\"someList\", someListMatcher, actual.getSomeList(), mismatchDesc, matches);\n" +
                        "      matches = false;\n" +
                        "    }\n" +
                        "    mismatchDesc.appendText(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void describeTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" +
                        "    desc.appendText(\"someList \");\n" +
                        "    desc.appendDescriptionOf(someListMatcher);\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.matcher.MatcherBuilderTestDataListSomethingMatcher isMatcherBuilderTestDataListSomething(com.test.MatcherBuilderTestDataListSomething expected) {\n" +
                        "    return new com.test.matcher.MatcherBuilderTestDataListSomethingMatcher(expected);\n" +
                        "  }\n" +
                        "}\n";

        SimpleEntry<ClassName, String> expectedStaticImport1 = new SimpleEntry<>(ClassName.get(Matchers.class), "*");
        SimpleEntry<ClassName, String> expectedStaticImport2 = new SimpleEntry<>(ClassName.get("org.hamgen.testdata.matcher", "MatcherBuilderTestDataSomethingElseMatcher"), "isMatcherBuilderTestDataSomethingElse");

        // Act
        //getMethods() returns a random order each time.. so we have to get the methods individually in the right order to make sure the test parses
        //I wanted to stub out the Method class, but it is not possible with PowerMock/Mockito because they themselves rely on it
        MatcherBuilder matcherBuilder = MatcherBuilder.matcherBuild("com.test", "MatcherBuilderTestDataListSomething")
                .matchFields(MatcherBuilderTestDataListSomething.class.getMethod("getSomeList"));
        TypeSpec result = matcherBuilder.build();

        Map<ClassName, String> staticImportsResult = matcherBuilder.buildStaticImports();

        // Assert
        assertEquals(expected, result.toString());
        assertThat(staticImportsResult.entrySet(), allOf(hasSize(2), (Matcher)hasItem(expectedStaticImport1), hasItem(expectedStaticImport2)));
    }

}
