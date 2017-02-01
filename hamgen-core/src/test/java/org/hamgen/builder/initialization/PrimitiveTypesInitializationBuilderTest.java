package org.hamgen.builder.initialization;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;
import org.hamgen.HamProperties;
import org.hamgen.builder.initialization.PrimitiveTypesInitializationBuilder;
import org.hamgen.model.MatcherField;
import org.hamgen.testtools.CodeModelTestDataBuilder;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.hamcrest.Matchers.is;
import static org.hamgen.testtools.CodeModelUtil.generableToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PrimitiveTypesInitializationBuilderTest {

    private static final String MATCHER_PRE_FIX = HamProperties.Key.MATCHER_PRE_FIX.getDefaultValue();
    private static final String PACKAGE_POST_FIX = HamProperties.Key.PACKAGE_POST_FIX.getDefaultValue();

    @Test
    public void t0800_doubleMatcherInitializing() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        MatcherField matcherField = new MatcherField("getMyDouble", "myDouble", "Matcher", double.class);

        PrimitiveTypesInitializationBuilder builder = new PrimitiveTypesInitializationBuilder();
        JVar matcherParam = testDataBuilder.buildJVar(Matcher.class, "doubleMatcher");
        JVar expectedParam = testDataBuilder.buildJVar(double.class, "expected");

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
