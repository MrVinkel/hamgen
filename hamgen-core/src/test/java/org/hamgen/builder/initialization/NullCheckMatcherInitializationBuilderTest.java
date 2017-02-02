package org.hamgen.builder.initialization;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;
import org.hamgen.HamProperties;
import org.hamgen.model.MatcherField;
import org.hamgen.testtools.CodeModelTestDataBuilder;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.hamgen.testtools.CodeModelUtil.generableToString;
import static org.junit.Assert.assertEquals;

public class NullCheckMatcherInitializationBuilderTest {

    private static final String MATCHER_PRE_FIX = HamProperties.Key.MATCHER_PRE_FIX.getDefaultValue();
    private static final String PACKAGE_POST_FIX = HamProperties.Key.PACKAGE_POST_FIX.getDefaultValue();

    @Test
    public void t0900_matchObject() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        MatcherField matcherField = new MatcherField("getMyObject", "myObject", "Matcher", Object.class);

        NullCheckMatcherInitializationBuilder builder = new NullCheckMatcherInitializationBuilder();
        JVar matcherParam = testDataBuilder.buildJVar(Matcher.class, "objectMatcher");
        JVar expectedParam = testDataBuilder.buildJVar(double.class, "expected");

        builder.withMatcher(matcherParam)
                .withExpected(expectedParam)
                .withMatcherField(matcherField)
                .withPackagePostFix(PACKAGE_POST_FIX)
                .withMatcherPreFix(MATCHER_PRE_FIX)
                .withConstructorBody(new JBlock())
                .withCodeModel(codeModel);

        String expected = "{\r\n" +
                "    objectMatcher = ((expected.getMyObject() == null)?org.hamcrest.Matchers.nullValue():org.hamcrest.Matchers.is(expected.getMyObject()));\r\n" +
                "}";

        //Act
        JBlock result = builder.build();

        //Assert
        assertEquals(expected, generableToString(result));
    }
}
