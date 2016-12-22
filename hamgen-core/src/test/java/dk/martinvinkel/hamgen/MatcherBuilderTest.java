package dk.martinvinkel.hamgen;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatcherBuilderTest {

    @Test
    public void t0201_MatcherBuilder() throws Exception {
        // Arrange
        String expected =
                "public class TestClassMatcher extends dk.martinvinkel.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(com.test.TestClass actual, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = false;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    mismatchDesc.appendTest(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void descriptionTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.matcher.TestClassMatcher IsTestClass(com.test.TestClass expected) {\n" +
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
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(com.test.TestClass actual, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = false;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    mismatchDesc.appendTest(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void descriptionTo(org.hamcrest.Description desc) {\n" +
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
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(com.test.TestClass actual, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = false;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    mismatchDesc.appendTest(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void descriptionTo(org.hamcrest.Description desc) {\n" +
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
                "public class TestClassMatcher extends dk.martinvinkel.hamgen.HamGenDiagnosingMatcher {\n" +
                        "  protected org.hamcrest.Matcher myFieldMatcher;\n" +
                        "\n" +
                        "  protected org.hamcrest.Matcher mySecondFieldMatcher;\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public boolean matchesSafely(com.test.TestClass actual, org.hamcrest.Description mismatchDesc) {\n" +
                        "    boolean matches = false;\n" +
                        "    mismatchDesc.appendText(\"{\");\n" +
                        "    if(!myFieldMatcher.matches(actual.someMethod())) {\n" +
                        "      reportMismatch(\"myFieldMatcher\", myFieldMatcher, actual.someMethod(), mismatchDesc, matches);\n" +
                        "      matches = false;\n" +
                        "    }\n" +
                        "    if(!mySecondFieldMatcher.matches(actual.someMethod())) {\n" +
                        "      reportMismatch(\"mySecondFieldMatcher\", mySecondFieldMatcher, actual.someMethod(), mismatchDesc, matches);\n" +
                        "      matches = false;\n" +
                        "    }\n" +
                        "    mismatchDesc.appendTest(\"}\");\n" +
                        "    return matches;\n" +
                        "  }\n" +
                        "\n" +
                        "  @java.lang.Override\n" +
                        "  public void descriptionTo(org.hamcrest.Description desc) {\n" +
                        "    desc.appendText(\"{\");\n" + "" +
                        "    desc.appendText(\"myFieldMatcher \");\n" +
                        "    desc.appendDescriptionOf(myFieldMatcher);\n" +
                        "    desc.appendText(\", \");\n" +
                        "    desc.appendText(\"mySecondFieldMatcher \");\n" +
                        "    desc.appendDescriptionOf(mySecondFieldMatcher);\n" +
                        "    desc.appendText(\"}\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @org.hamcrest.Factory\n" +
                        "  public static com.test.matcher.TestClassMatcher IsTestClass(com.test.TestClass expected) {\n" +
                        "    return new com.test.matcher.TestClassMatcher(expected);\n" +
                        "  }\n" +
                        "}\n";
        // Act
        TypeSpec result = MatcherBuilder.matcherBuild("com.test", "TestClass")
                .matchField(String.class, "myField")
                .matchField(Double.class, "mySecondField")
                .build();

        // Assert
        assertEquals(expected, result.toString());
    }
}
