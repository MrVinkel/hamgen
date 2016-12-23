package dk.martinvinkel.hamgen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dk.martinvinkel.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.AbstractMap;
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
                "public class TestClassMatcher extends dk.martinvinkel.hamgen.HamGenDiagnosingMatcher {\n" +
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
                "public class TestClassMyPost extends dk.martinvinkel.hamgen.HamGenDiagnosingMatcher {\n" +
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
                "public class TestClassMyPost extends dk.martinvinkel.hamgen.HamGenDiagnosingMatcher {\n" +
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
                "public class MatcherBuilderTestDataSomethingElseMatcher extends dk.martinvinkel.hamgen.HamGenDiagnosingMatcher {\n" +
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

}
