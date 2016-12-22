package dk.martinvinkel.hamgen;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatcherBuilderTest {

    @Test
    public void t0201_MatcherBuilder() throws Exception {
        // Arrange
        String expected =
                "public class TestClassMatcher {\n" +
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
                "public class TestClassMyPost {\n" +
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
                "public class TestClassMyPost {\n" +
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
}
