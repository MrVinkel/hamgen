/*
package org.hamgen.builder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;
import org.hamcrest.Matcher;
import org.hamgen.HamProperties;
import org.hamgen.model.MatcherField;
import org.hamgen.testtools.CodeModelTestDataBuilder;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.hamgen.testtools.CodeModelUtil.generableToString;

public class CollectionMatcherInitializationBuilderTest {

    private static final String MATCHER_PRE_FIX = HamProperties.Key.MATCHER_PRE_FIX.getDefaultValue();
    private static final String PACKAGE_POST_FIX = HamProperties.Key.PACKAGE_POST_FIX.getDefaultValue();

    @Test
    public void t0802_arrayMatcherInitializing() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        MatcherField matcherField = new MatcherField("getMyDouble", "myDouble", "Matcher", double[].class);

        CollectionMatcherInitializationBuilder builder = new CollectionMatcherInitializationBuilder();
        JVar matcherParam = testDataBuilder.buildJVar(Matcher.class, "doubleMatcher");
        JVar expectedParam = testDataBuilder.buildJVar(double[].class, "expected");

        builder.withMatcher(matcherParam)
                .withExpected(expectedParam)
                .withMatcherField(matcherField)
                .withPackagePostFix(PACKAGE_POST_FIX)
                .withMatcherPreFix(MATCHER_PRE_FIX)
                .withConstructorBody(new JBlock())
                .withCodeModel(codeModel);

        String expected = "{\r\n" +
                "    doubleMatcher = org.hamcrest.Matchers.is(expected.getMyDouble());\r\n" +
                "}";

        //Act
        JBlock result = builder.build();

        //Assert
        assertEquals(expected, generableToString(result));
    }
}
*/
